package com.grep.ui;

import com.grep.database.Keyword;
import com.grep.gaugebackend.GaugeBackend;
import com.grep.database.DatabaseHandler;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
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
	
		//Create keyword list from database
		final List<Keyword> keywordList = dh.getAllKeywords(topic_id);
		String[] keywords = new String[keywordList.size()];//{"doma", "defense of marriage act", "traditional marriage", "marriage", "conservative marriage", "biblical marriage"};
		for(int i = 0; i < keywordList.size(); i++) {
			keywords[i] = keywordList.get(i).getKeyword();
			System.out.println("Keyword: " + keywordList.get(i).getKeyword());
		}
		BlockingQueue<WebToast> webToasts = new ArrayBlockingQueue<WebToast>(100);
		BlockingQueue<Gauge> gaugeValues = new ArrayBlockingQueue<Gauge>(100);
		GaugeBackend.start(keywords, webToasts, gaugeValues, duration, this);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		webView.loadUrl("file:///android_asset/gauge.html");
		
		// start another thread to process gauge values TODO add another to process
		// the popular Tweets
		m_gaugeConsumer = new GaugeConsumer(gaugeValues, webToasts, webView);
		m_gaugeConsumerThread = new Thread(m_gaugeConsumer);
		m_gaugeConsumerThread.start();
		System.out.println("Before timing");
/*		Timer refreshTime = new Timer();
		refreshTime.schedule(new TimerTask() {
			int remainingTime = duration;
			@Override
			public void run() {
				System.out.println("Timing");
				refreshTime(remainingTime);
				remainingTime--;
			}
		}, 0, 1000);
		refreshTime(duration);*/
	}
	
	public void refreshTime(int remainingTime) {
		final TextView textView = (TextView) findViewById(R.id.time_left);
		if(remainingTime > 3600) // If duration is greater than an hour
			textView.setText(remainingTime/3600 + " hours " + (remainingTime - (remainingTime/3600)*3600)/60 + " minutes remaining");
		else if(remainingTime > 60) // If duration is greater than a minute (but less than an hour)
			textView.setText(remainingTime/60 + " minutes " + (remainingTime- (remainingTime/60)*60) + " seconds remaining");
		else
			textView.setText(remainingTime + " seconds remaining");
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
	   dh.close();
	   finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// get latest gauge value from consumer and save to database
		//if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null) {
			
		//}
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
