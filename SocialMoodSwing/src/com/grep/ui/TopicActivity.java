package com.grep.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.grep.database.DatabaseHandler;
import com.grep.database.Session;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * TopicActivity displays the currently selected topic's run history
 * and specifications for a new run. Additionally, Twitter credentials
 * can be changed from here by going to the Login menu item.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class TopicActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		setTitle(R.string.title_activity_topic);
		
//		DatabaseHandler dh = new DatabaseHandler(this); // Is this how to initiate the database in an activity?
//		int topic_id = savedInstanceState.getInt("topic"); // How are we going to get the current topic when this activity is open?
		
// 		Get a list of session values
//		List<Session> analysisSessions = dh.getAllSessions(topic_id); // Figure out how to get list of sessions from db
		final List<Integer> analysisValues = new ArrayList<Integer>();
		final List<String> analysisTimes = new ArrayList<String>();
		
		// Create lists to pass to javascript of session values and session times (theoretically)
		Random generator = new Random();
		
		for(int i = 0; i < 10/*analysisSessions.size()*/; i++) {
			analysisTimes.add("label" + i);
			analysisValues.add(generator.nextInt() % 100);
/*			analysisTimes.add(analysisSessions.get(i).getStartTime()); // Is this somewhat correct?
			
			// Are we storing negative sentiment as a negative number?
			if(-1 * analysisSessions.get(i).getAvgNegSentiment() > analysisSessions.get(i).getAvgPosSentiment())
				analysisValues.add(analysisSessions.get(i).getAvgNegSentiment());
			else
				analysisValues.add(analysisSessions.get(i).getAvgPosSentiment());*/
		}
		System.out.println("Before creating webview");
		final WebView historyGraphWebView = (WebView) findViewById(R.id.graph);
		System.out.println("After creating webview");
		historyGraphWebView.setWebViewClient(new WebViewClient() {  
		    @Override  
		    public void onPageFinished(WebView view, String url)  // Code to be executed after page is loaded (loads graph)
		    {  
				for(int i = 0; i < analysisValues.size(); i++){
					historyGraphWebView.loadUrl("javascript:sessions[" + i + "] = " + analysisValues.get(i) + ";");//, i, analysisValues[i]));
					historyGraphWebView.loadUrl("javascript:timeStamps[" + i + "] = '" + analysisTimes.get(i) + "';");//, i, analysisTimes[i]));
				}
				
				historyGraphWebView.loadUrl("javascript:draw_graph();");
		    }  
		});
		
		historyGraphWebView.loadUrl("file:///android_asset/graph.html");
		historyGraphWebView.setHorizontalScrollBarEnabled(false);
		WebSettings historyGraphWebSettings = historyGraphWebView.getSettings();
		historyGraphWebSettings.setJavaScriptEnabled(true);
		historyGraphWebSettings.setDomStorageEnabled(true);
		historyGraphWebSettings.setLightTouchEnabled(true); // Possibly allow for touching points on graph?
		historyGraphWebSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // disable horizontal scrolling
		
//		EditText hours = (EditText) findViewById(R.id.hours);
//		EditText minutes = (EditText) findViewById(R.id.minutes);
		EditText info = (EditText) findViewById(R.id.topicInfo);
		
		int totalTweets = 0;
		int totalTime = 0;
		int avgSentiment = 0;
		
/*		for(int i = 0; i < analysisSessions.size(); i++) {
			totalTweets += analysisSessions.get(i).getNumTweetsProcessed();
			totalTime += analysisSessions.get(i).getDuration();
			// Check to see if this is right at some point
			if(analysisSessions.get(i).getAvgPosSentiment() > (-1) * analysisSessions.get(i).getAvgNegSentiment())
				avgSentiment += analysisSessions.get(i).getAvgPosSentiment();
			else
				avgSentiment -= analysisSessions.get(i).getAvgNegSentiment();
		}*/
		
		info.setText("Tweets Processed:\t" + totalTweets + "\n");
		info.append("Hours Running:\t\t\t" + totalTime + "\n");
		info.append("Avg. Sentiment:\t\t\t" + avgSentiment + "\n");
		
		}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle menu item selection
	    switch (item.getItemId()) {
	        case R.id.menu_login:
	            showLoginDialog();
	            return true;
	        case R.id.menu_keywords:
	        	showTopicKeywordsDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Creates an instance of the Login dialog fragment for the user to
	 * enter Twitter authentication credentials.
	 */
	public void showLoginDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

	/**
	 * Creates an instance of the Topic Keywords dialog fragment so the user
	 * may create a new topic, or edit a topic's keywords.
	 */
	public void showTopicKeywordsDialog() {
		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new TopicKeywordsDialogFragment();
        dialog.show(getSupportFragmentManager(), "TopicKeywordsDialogFragment");
	}
	
	/**
	 * Creates an intent to change to the Gauge activity corresponding to the
	 * current topic.
	 */
	public void goToGaugeActivity(View v) {
		Intent intent = new Intent(this, GaugeActivity.class);
		startActivity(intent);
	}
}
