package com.grep.ui;

import com.grep.database.Keyword;
import com.grep.database.Session;
import com.grep.gaugebackend.GaugeBackend;
import com.grep.database.DatabaseHandler;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.grep.database.Credentials;
import com.grep.gaugebackend.Gauge;
import com.grep.gaugebackend.WebToast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * GaugeActivity displays the results of the current analysis session in real time, via
 * a gauge, statistical data, and popular Tweets. 
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

public class GaugeActivity extends FragmentActivity {
	
	static public Thread m_gaugeConsumerThread;
	static protected GaugeConsumer m_gaugeConsumer = null;
	DatabaseHandler dh = new DatabaseHandler(this);
	int topic_id = -1;
	Thread countdown;
	int sessionDuration;
	
	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(GaugeActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gauge);
		setTitle(R.string.title_activity_gauge);
		
		dh.open();
		topic_id = getIntent().getIntExtra("topicId", -1);
		
		if (topic_id == -1) {
			//Show user an error if the topic id is not properly retrieved... something went wrong
			//Should not ever really get here
			Toast.makeText(this, "Error: Could not find topic in database", Toast.LENGTH_LONG).show();
			this.finish();
		}
		
		final int duration = getIntent().getIntExtra("analysisDuration", 10);
		sessionDuration = duration;
		
		//Create keyword list from database
		final List<Keyword> keywordList = dh.getAllKeywords(topic_id);
		String[] keywords = new String[keywordList.size()];//{"doma", "defense of marriage act", "traditional marriage", "marriage", "conservative marriage", "biblical marriage"};
		for(int i = 0; i < keywordList.size(); i++) {
			keywords[i] = keywordList.get(i).getKeyword();
			System.out.println("Keyword: " + keywordList.get(i).getKeyword());
		}
		BlockingQueue<WebToast> webToasts = new ArrayBlockingQueue<WebToast>(100);
		BlockingQueue<Gauge> gaugeValues = new ArrayBlockingQueue<Gauge>(100);

		Credentials c = dh.getCredentials();
		
		GaugeBackend.start(keywords, c.getConsumerKey(), c.getConsumerSecret(), webToasts, gaugeValues, duration, this);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		webView.setLongClickable(false);
		webView.loadUrl("file:///android_asset/gauge.html");
		
		// start another thread to process gauge values TODO add another to process
		// the popular Tweets
		m_gaugeConsumer = new GaugeConsumer(gaugeValues, webToasts, webView);
		m_gaugeConsumerThread = new Thread(m_gaugeConsumer);
		m_gaugeConsumerThread.start();
		
		countdown = new Thread() {
			int remainingTime = duration;
			  @Override
			  public void run() {
			    try {
			      while (!isInterrupted()) {
			        runOnUiThread(new Runnable() {
			      		@Override
			      		public void run() {
			      			refreshTime(remainingTime);
			      			remainingTime--;
			      		}
			        });
			        Thread.sleep(1000); // This should possibly be more like 997... It loses ~1 sec every 5 mins
			      }
			    } catch (InterruptedException e) {
			    }
			  }
			};

		countdown.start();
	}
	
	public void refreshTime(int remainingTime) {
		TextView textView = (TextView) findViewById(R.id.time_left);
		if(remainingTime > 3600) // If duration is greater than an hour
			textView.setText(String.format("%02d", remainingTime/3600) + ":" + String.format("%02d", (remainingTime - (remainingTime/3600)*3600)/60) + ":" + String.format("%02d", (remainingTime- (remainingTime/60)*60)) + " remaining");
		else if(remainingTime >= 60) // If duration is greater than a minute (but less than an hour)
			textView.setText(String.format("%02d", (remainingTime - (remainingTime/3600)*3600)/60) + ":" + String.format("%02d", (remainingTime- (remainingTime/60)*60)) + " remaining");
		else
			textView.setText((remainingTime- (remainingTime/60)*60) + " seconds remaining");
	}
	
	public void stopGauge() {
	   // stop the threads (hopefully...)
	   GaugeBackend.stop();
	   GaugeActivity.m_gaugeConsumerThread.interrupt();
	   try {
		   GaugeActivity.m_gaugeConsumerThread.join();
	   } catch (InterruptedException ex) {
		   System.out.println("something went wrong while killing the gauge consumer thread");
	   }
	   finish();
	}
	
	@Override
	public void onDestroy() {
		countdown.interrupt();
		super.onDestroy();
		dh.open();
		// get latest gauge value from consumer and save to database
		System.out.println("Values: " + m_gaugeConsumer + m_gaugeConsumer.m_latestGauge);
		if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null) {
			if(m_gaugeConsumer.m_latestGauge.m_sessionAverage > 0) {
				dh.addSession(new Session(topic_id, 
						sessionDuration, 
						m_gaugeConsumer.m_latestGauge.m_tweetCount, 
						(int)(m_gaugeConsumer.m_latestGauge.m_sessionAverage * 100), 
						(int)((m_gaugeConsumer.m_latestGauge.m_sessionAverage - 1) * 100))); // Need to figure out these numbers
			} else {
				dh.addSession(new Session(topic_id, 
						sessionDuration, 
						m_gaugeConsumer.m_latestGauge.m_tweetCount, 
						(int)((m_gaugeConsumer.m_latestGauge.m_sessionAverage + 1) * 100), 
						(int)(m_gaugeConsumer.m_latestGauge.m_sessionAverage * 100))); // Need to figure out these numbers
			}
		}
		dh.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gauge, menu);
		return true;
	}
	
	public void showWarningMessage(View v) {
		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new WarningDialogFragment();
        dialog.show(getSupportFragmentManager(), "WarningDialogFragment");
	}
	
	@Override
	public void onBackPressed() {
		//display the Warning dialog, don't let user exit GaugeActivity with back button alone
	    showWarningMessage(getCurrentFocus());
	}
}
