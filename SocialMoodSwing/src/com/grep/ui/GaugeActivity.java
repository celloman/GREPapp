package com.grep.ui;

import com.grep.database.Keyword;
import com.grep.database.Session;
import com.grep.gaugebackend.GaugeBackend;
import com.grep.database.DatabaseHandler;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
	int elapsedTime;
	WebView m_webView;
	
	// TODO Why on earth is this here, is it ever used?
	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(GaugeActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}

	@SuppressLint("SetJavaScriptEnabled") // Prevents warnings about using javascript in webview
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gauge);

		dh.open();
		topic_id = getIntent().getIntExtra("topicId", -1); // Get topic id
		
		// Set title of activity to "Analysis Session - <Topic Name>"
		setTitle(getResources().getString(R.string.title_activity_gauge) + " - " + dh.getTopic(topic_id).getTopicName());
		
		if (topic_id == -1) {
			//Show user an error if the topic id is not properly retrieved... something went wrong
			//Should not ever get here, as all topics on list should be in db
			Toast.makeText(this, "Error: Could not find topic in database", Toast.LENGTH_LONG).show();
			this.finish();
		}
		// Retrieve the analysis session duration passed from the topic activity
		final int duration = getIntent().getIntExtra("analysisDuration", 10);
		sessionDuration = duration;
		
		//Create keyword list from database
		final List<Keyword> keywordList = dh.getAllKeywords(topic_id);
		String[] keywords = new String[keywordList.size()];
		for(int i = 0; i < keywordList.size(); i++) {
			keywords[i] = keywordList.get(i).getKeyword();
		}
		BlockingQueue<WebToast> webToasts = new ArrayBlockingQueue<WebToast>(100);
		BlockingQueue<Gauge> gaugeValues = new ArrayBlockingQueue<Gauge>(100);

		// Get Twitter OAuth credentials from database (for Tweet retrieval)
		Credentials c = dh.getCredentials();

		GaugeBackend.start(keywords, c.getConsumerKey(), c.getConsumerSecret(), webToasts, gaugeValues);

		m_webView = (WebView) findViewById(R.id.webview);
		m_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		m_webView.getSettings().setJavaScriptEnabled(true);
		m_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		m_webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true; // Fix crashing on long click
			}
		});
		
		m_webView.setLongClickable(false);
		m_webView.loadUrl("file:///android_asset/gauge.html");
		
		// start another thread to process gauge values
		m_gaugeConsumer = new GaugeConsumer(gaugeValues, webToasts, m_webView);
		m_gaugeConsumerThread = new Thread(m_gaugeConsumer);
		m_gaugeConsumerThread.start();
		
		// Thread handling the updating of the countdown timer on Gauge Activity
		// Also handles terminating threads at end of duration, and showing
		// of dialog at end of session
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
			      			// Don't allow counter to go below 0
			      			if(remainingTime > 0) {
			      				remainingTime--;
			      				elapsedTime++; // Keep track of elapsed time... could modify so this isn't needed...
			      			} else if (remainingTime <= 0) {
			      				showEndSessionMessage(); // Show end session dialog when duration is up
			      			}
			      		}
			        });
			        Thread.sleep(998); // Sleep for a second before updating count
			      }
			    } catch (InterruptedException e) {
					Thread.currentThread().interrupt();
			    	Log.e(getName(), "Error in timer thread");
			    }
			  }
			};
		countdown.start(); // Start the countdown timer
	}
	
	// Updates countdown display
	public void refreshTime(int remainingTime) {
		TextView textView = (TextView) findViewById(R.id.time_left);
		if(remainingTime >= 3600) // If duration is greater than an hour
			textView.setText(String.format("%02d", remainingTime/3600) + ":" 
					+ String.format("%02d", (remainingTime - (remainingTime/3600)*3600)/60) + ":" 
					+ String.format("%02d", (remainingTime- (remainingTime/60)*60)) + " remaining");
		else if(remainingTime >= 60) // If duration is greater than a minute (but less than an hour)
			textView.setText(String.format("%02d", (remainingTime - (remainingTime/3600)*3600)/60) + ":" 
					+ String.format("%02d", (remainingTime- (remainingTime/60)*60)) + " remaining");
		else // If duration is less than a minute, display seconds
			textView.setText((remainingTime- (remainingTime/60)*60) + " seconds remaining");
	}
	
	// Save results if user checked box in dialog (warning or end session)
	public void saveResults(boolean isChecked) {
		dh.open();
		// get latest gauge value from consumer and save to database
		System.out.println("Values: " + m_gaugeConsumer + m_gaugeConsumer.m_latestGauge);
		if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null && isChecked) {
			// Determine whether average in gauge is positive or negative
			if(m_gaugeConsumer.m_latestGauge.m_sessionAverage > 0) {
				// if positive, save appropriate values
				dh.addSession(new Session(topic_id, 
						elapsedTime, 
						m_gaugeConsumer.m_latestGauge.m_tweetCount, 
						(int)(m_gaugeConsumer.m_latestGauge.m_sessionAverage * 100), 
						(int)((m_gaugeConsumer.m_latestGauge.m_sessionAverage - 1) * 100)));
			} else {
				// if negative, save appropriate values
				dh.addSession(new Session(topic_id, 
						elapsedTime, 
						m_gaugeConsumer.m_latestGauge.m_tweetCount, 
						(int)((m_gaugeConsumer.m_latestGauge.m_sessionAverage + 1) * 100), 
						(int)(m_gaugeConsumer.m_latestGauge.m_sessionAverage * 100)));
			}
		}
		dh.close();
		//finish the activity TODO is this in the right place?
		finish();
	}

	// Stop all gauge threads
	public void stopGaugeThreads() {
		countdown.interrupt(); // Stop timer in background
		GaugeBackend.stop();
		GaugeActivity.m_gaugeConsumerThread.interrupt();
		try {
			GaugeActivity.m_gaugeConsumerThread.join();
		} catch (InterruptedException ex) {
			System.out.println("something went wrong while killing the gauge consumer thread");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Make sure to end countdown thread (when ending early) so doesn't crash topic activity
		if(countdown.isAlive())
			countdown.interrupt();
		
		m_webView.destroy();
		m_webView = null;
	}

	// TODO Remove?
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gauge, menu);
		return true;
	}
	
	// Show message alerting user that they pressed stop, and that continuing will
	// end current analysis session
	// Offers opportunity to save current results if they terminate the session
	public void showWarningMessage(View v) {
		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new WarningDialogFragment();
        Bundle sessionValues = new Bundle();
        if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null) {
        	sessionValues.putBoolean("hasValues", true);
        } else {
        	sessionValues.putBoolean("hasValues", false);
        }
        dialog.setArguments(sessionValues);
        dialog.show(getSupportFragmentManager(), "WarningDialogFragment");
	}
	
	// Show dialog when the duration is up
	public void showEndSessionMessage() {
		elapsedTime = sessionDuration; // Ensure time added to db is what is expected

		// Stop the gauge threads so gauge does not continue running in background
		stopGaugeThreads();

		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new EndSessionDialogFragment();
        Bundle sessionValues = new Bundle();
        // Send values to be displayed to dialog
        if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null) {
        	sessionValues.putInt("numTweets", m_gaugeConsumer.m_latestGauge.m_tweetCount);
        	sessionValues.putInt("runTime", sessionDuration);
        	sessionValues.putInt("sessionAverage", (int)(m_gaugeConsumer.m_latestGauge.m_sessionAverage * 100));
        	sessionValues.putBoolean("hasValues", true);
        } else {
        	// No Tweets were processed, don't allow user to attempt to save
        	sessionValues.putBoolean("hasValues", false);
        }
        dialog.setArguments(sessionValues);
        dialog.show(getSupportFragmentManager(), "EndSessionDialogFragment");
	}
	
	@Override
	public void onBackPressed() {
		//display the Warning dialog, don't let user exit GaugeActivity with back button alone
	    showWarningMessage(getCurrentFocus());
	}
}
