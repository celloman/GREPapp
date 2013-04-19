package com.grep.ui;

import java.util.ArrayList;
import java.util.List;
import com.grep.database.DatabaseHandler;
import com.grep.database.Session;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
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

	DatabaseHandler dh = new DatabaseHandler(this);
	int topic_id = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);

		dh.open();
		
		//retrieve the topicId as passed to this intent from the TopicListActivity, default return is -1
		topic_id = getIntent().getIntExtra("topicId", -1);

		// Set title of Activity to "Run History - <Topic Name>"
		setTitle(getResources().getString(R.string.title_activity_topic) + " - " + dh.getTopic(topic_id).getTopicName());
		
		if (topic_id == -1) {
			//Show user an error if the topic id is not properly retrieved... something went wrong
			//Should not ever really get here
			Toast.makeText(this, "Error: Could not find topic in database", Toast.LENGTH_LONG).show();
			this.finish();
		}

	}

	/**
	 * Draw javascript graph in webview
	 * Takes historical analysis session data from database and passes it to javascript
	 * 
	 */
	private void drawGraph() {
		dh.open();
		
		// Get a list of session values
		List<Session> analysisSessions = dh.getAllSessions(topic_id);
		
		// Lists to hold sentiment values and timestamps from historical analysis sessions
		final List<Integer> analysisValues = new ArrayList<Integer>();
		final List<String> analysisTimes = new ArrayList<String>();

		//Only show the last 15 analysis sessions
		int length = 0;
		if(analysisSessions.size() > 15)
			length = analysisSessions.size() - 15;
		
		// Loop through historical analysis sessions and store appropriate values into lists
		for(int i = length; i < analysisSessions.size(); i++) {
			// Save the start time in a list
			analysisTimes.add(analysisSessions.get(i).getStartTime());
			// Calculate real analysis session average sentiment and store
			if(-1 * analysisSessions.get(i).getAvgNegSentiment() > analysisSessions.get(i).getAvgPosSentiment())
				analysisValues.add(analysisSessions.get(i).getAvgNegSentiment());
			else
				analysisValues.add(analysisSessions.get(i).getAvgPosSentiment());
		}
		
		// Historical analysis session graph webview
		final WebView historyGraphWebView = (WebView) findViewById(R.id.graph);

		historyGraphWebView.setWebViewClient(new WebViewClient() {  
			/*
			 * After initial page has finished loading, pass in the values to be displayed on the graph
			 * This is required, because Java code following the loading of the URL will continue on without waiting
			 * for this to finish, causing an error.
			 */
		    @Override  
		    public void onPageFinished(WebView view, String url) {  
				for(int i = 0; i < analysisValues.size(); i++){
					// Values of 100 would throw the scale of the graph off, this makes 100's 99's ... can't tell the difference on graph
					// Also takes care of any possible errors where a value greater than 100 or less than -100 is stored
					if(analysisValues.get(i) >= 100)
						historyGraphWebView.loadUrl("javascript:sessions[" + i + "] = " + 99 + ";");
					else if(analysisValues.get(i) <= -100)
						historyGraphWebView.loadUrl("javascript:sessions[" + i + "] = " + -99 + ";");
					else
						historyGraphWebView.loadUrl("javascript:sessions[" + i + "] = " + analysisValues.get(i) + ";"); // No need to adjust real values
					// Pass time stamp from the database into graph
					historyGraphWebView.loadUrl("javascript:timeStamps[" + i + "] = '" + analysisTimes.get(i) + "';");
				}
				
				// Call javascript function to draw the graph with appropriate data
				historyGraphWebView.loadUrl("javascript:draw_graph();");
		    }  
		}); // End of historyGraphWebView.setWebViewClient(...);
		
		// Load local html page (containing graph) into webview 
		if(analysisValues.size() > 1)
			historyGraphWebView.loadUrl("file:///android_asset/graph.html");
		else {
			historyGraphWebView.loadData("<html><body>There is currently only one analysis session in this topic's history, a graph will" +
					" display once there are at least two sessions in the database.</body></html>", "text/html", null);
		}
		// Prevent scrolling within webview
		// TODO We may want to enable horizontal scrolling for better graph accessibility
		historyGraphWebView.setHorizontalScrollBarEnabled(true);
		historyGraphWebView.setVerticalScrollBarEnabled(false);
		
		// Settings for graph webview
		WebSettings historyGraphWebSettings = historyGraphWebView.getSettings();
		// We need to allow javascript to run in this graph
		historyGraphWebSettings.setJavaScriptEnabled(true);
		historyGraphWebSettings.setDomStorageEnabled(true); // Might not be necessary
		historyGraphWebSettings.setLightTouchEnabled(true); // Possibly allow for touching points on graph? Might not be necessary
		//historyGraphWebSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // disable horizontal scrolling (for sure) TODO check on this
		
		// EditText area that displays statistics for all historical analysis sessions
		EditText info = (EditText) findViewById(R.id.topicInfo);
		
		int totalTweets = 0;
		int totalTime = 0;
		int avgSentiment = 0;
		
		// Gather data from all analysis sessions
		for(int i = 0; i < analysisSessions.size(); i++) {
			// Tally the total number of tweets over all analysis sessions for the current topic
			totalTweets += analysisSessions.get(i).getNumTweetsProcessed();
			// Tally the total number of seconds that the application has spent analyizing the current topic
			totalTime += analysisSessions.get(i).getDuration();
			// Calculate the proper sentiment to retrieve, and retrieve it, adding to overall average sentiment calculation for the current topic
			if(analysisSessions.get(i).getAvgPosSentiment() > (-1) * analysisSessions.get(i).getAvgNegSentiment())
				avgSentiment += analysisSessions.get(i).getAvgPosSentiment() * analysisSessions.get(i).getNumTweetsProcessed();
			else
				avgSentiment += analysisSessions.get(i).getAvgNegSentiment() * analysisSessions.get(i).getNumTweetsProcessed();
		}
		
		// If there are analysis sessions in the database, print out statistics about the analysis session set
		if(analysisSessions.size() > 0) {
			// Finish calculating overall average sentiment, being sure to avoid dividing by 0
			avgSentiment = avgSentiment/totalTweets;
		
			info.setText("Tweets Processed:\t" + totalTweets + "\n");
			
			//Properly format time spent running depending on length (calculated off of number of seconds) (XXh XXm XXs)
			if(totalTime >= 3600) // Greater than or equal to an hour
				info.append("Time Running:\t\t\t\t" + String.format("%02d", totalTime/3600) + "h " 
						+ String.format("%02d", (totalTime - (totalTime/3600)*3600)/60) + "m "
						+ String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			else if(totalTime >= 60) // Greater than or equal to a minute (but less than an hour) (XXm XXs)
				info.append("Time Running:\t\t\t\t" + String.format("%02d", (totalTime - (totalTime/3600)*3600)/60) + "m "
				+ String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			else // Less than one minute (XXs)
				info.append("Time Running:\t\t\t\t" + String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			
			// Format and print average sentiment
			if(avgSentiment > 0)
				info.append("Avg. Sentiment:\t\t\t+" + avgSentiment + "%\n"); // Average is positive
			else
				info.append("Avg. Sentiment:\t\t\t" + avgSentiment + "%\n"); // Average is negative
		}
		
		// Display an instructional message to user if there are no sessions in the topic's history
		if(analysisSessions.size() == 0)
			info.setText("There are no analysis sessions in the database." +
					" \n\nEnter a duration above and click \"To Gauge\"" +
					" in order to begin an analysis session");
	} // end drawGraph();
	
	@Override
	protected void onResume() {
		// Calls function to draw javascript graph in webview
		drawGraph();
		
		// Always reset hint to gray, in case user failed to enter value before last analysis session
		EditText hoursEntry = (EditText) findViewById(R.id.hours);
		EditText minutesEntry = (EditText) findViewById(R.id.minutes);
		hoursEntry.setHintTextColor(getResources().getColor(R.color.gray));
		minutesEntry.setHintTextColor(getResources().getColor(R.color.gray));
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		dh.close();
		super.onPause();
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
		
		
		if(hoursEntry.getText().length() != 0 || minutesEntry.getText().length() != 0 && dh.getCredentials() != null) {
			if(hoursEntry.getText().length() != 0) {
				hours = Integer.parseInt(hoursEntry.getText().toString());
			}
			if(minutesEntry.getText().length() != 0) {
				minutes = Integer.parseInt(minutesEntry.getText().toString());
			}
			int time = hours * 3600 + minutes * 60;
			
			Intent intent = new Intent(this, GaugeActivity.class);
			intent.putExtra("analysisDuration", time);
			intent.putExtra("topicId", topic_id);
			startActivity(intent);
		}
		else if(dh.getCredentials() == null) {
			Toast.makeText(this, "Please log in with Twitter", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, "Please enter an Analysis Session Duration", Toast.LENGTH_LONG).show();
			hoursEntry.setHintTextColor(getResources().getColor(R.color.red));
			minutesEntry.setHintTextColor(getResources().getColor(R.color.red));
		}
	}
}
