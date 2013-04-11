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
import android.widget.Toast;

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

	DatabaseHandler dh = new DatabaseHandler(this); // Is this how to initiate the database in an activity?
	int topic_id = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);

		dh.open();
		
		//retrieve the topicId as passed to this intent from the TopicListActivity, default return is -1
		topic_id = getIntent().getIntExtra("topicId", -1);

		setTitle(getResources().getString(R.string.title_activity_topic) + " - " + dh.getTopic(topic_id).getTopicName());
		
		if (topic_id == -1) {
			//Show user an error if the topic id is not properly retrieved... something went wrong
			//Should not ever really get here
			Toast.makeText(this, "Error: Could not find topic in database", Toast.LENGTH_LONG).show();
			this.finish();
		}

	}

	private void drawGraph() {
		// Create lists to pass to javascript of session values and session times (theoretically)
		Random generator = new Random();
		
		dh.open();
		
// 		Get a list of session values
		List<Session> analysisSessions = dh.getAllSessions(topic_id); // Figure out how to get list of sessions from db
		final List<Integer> analysisValues = new ArrayList<Integer>();
		final List<String> analysisTimes = new ArrayList<String>();
				
		// Create 40 random fake analysis sessions
/*		for(int i = 0; i < 40; i++)
			analysisSessions.add(new Session(topic_id, generator.nextInt(4000), generator.nextInt(1000), generator.nextInt() % 100, generator.nextInt() % 100));
	*/
		//Don't display a graph if there are no analysis sessions in history 
		//Only show the last 15 analysis sessions
		int length = 0;
		if(analysisSessions.size() > 15)
			length = analysisSessions.size() - 15;
		
		for(int i = length; i < analysisSessions.size(); i++) {
			analysisTimes.add(analysisSessions.get(i).getStartTime()); // Is this somewhat correct?
			
			// Are we storing negative sentiment as a negative number?
			if(-1 * analysisSessions.get(i).getAvgNegSentiment() > analysisSessions.get(i).getAvgPosSentiment())
				analysisValues.add(analysisSessions.get(i).getAvgNegSentiment());
			else
				analysisValues.add(analysisSessions.get(i).getAvgPosSentiment());
		}
		final WebView historyGraphWebView = (WebView) findViewById(R.id.graph);

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
		
		EditText info = (EditText) findViewById(R.id.topicInfo);
		
		int totalTweets = 0;
		int totalTime = 0;
		int avgSentiment = 0;
		
		for(int i = 0; i < analysisSessions.size(); i++) {
			totalTweets += analysisSessions.get(i).getNumTweetsProcessed();
			totalTime += analysisSessions.get(i).getDuration();
			// Check to see if this is right at some point
			if(analysisSessions.get(i).getAvgPosSentiment() > (-1) * analysisSessions.get(i).getAvgNegSentiment())
				avgSentiment += analysisSessions.get(i).getAvgPosSentiment();
			else
				avgSentiment -= analysisSessions.get(i).getAvgNegSentiment();
		}
		
		info.setText("Tweets Processed:\t" + totalTweets + "\n");
		info.append("Hours Running:\t\t\t" + totalTime + "\n");
		info.append("Avg. Sentiment:\t\t\t" + avgSentiment + "\n");
		
		if(analysisSessions.size() == 0)
			info.setText("There are no analysis sessions in the database." +
					" \n\nEnter a duration above and click \"To Gauge\"" +
					" in order to begin an analysis session");
	} // end drawGraph();

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic, menu);
		return true;
	}
	*/
	
	@Override
	protected void onResume() {
		drawGraph();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		dh.close();
		super.onPause();
	}
	
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
		EditText hoursEntry = (EditText) findViewById(R.id.hours);
		EditText minutesEntry = (EditText) findViewById(R.id.minutes);
		
		int hours = 0;
		int minutes = 0;
		
		if(hoursEntry.getText().length() != 0 || minutesEntry.getText().length() != 0) {
			if(hoursEntry.getText().length() != 0) {
				hours = Integer.parseInt(hoursEntry.getText().toString());
			}
			if(minutesEntry.getText().length() != 0) {
				minutes = Integer.parseInt(minutesEntry.getText().toString());
			}
			int time = hours * 3600 + minutes * 60;
			
			Intent intent = new Intent(this, GaugeActivity.class);
			intent.putExtra("analysisDuration", time);
			startActivity(intent);
		}
		else {
			Toast.makeText(this, "Please enter an Analysis Session Duration", Toast.LENGTH_LONG).show();
			hoursEntry.setHintTextColor(getResources().getColor(R.color.red));
			minutesEntry.setHintTextColor(getResources().getColor(R.color.red));
		}
	}
}
