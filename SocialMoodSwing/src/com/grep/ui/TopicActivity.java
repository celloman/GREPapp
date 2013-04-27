package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.grep.database.DatabaseHandler;
import com.grep.database.Session;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
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

	// OAuth information for checking Internet access
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	private final static String consumerKey = "2RKMlxcy1cf1WGFfHJvpg";
	private final static String consumerSecret = "35Ege9Yk1vkoZmk4koDDZj07e9CJZtkRaLycXZepqA";
	private final String CALLBACKURL = "socialmoodswing://credentials";
	private boolean connectedToNetwork;
	
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
		final List<String> toolTips = new ArrayList<String>();

		//Only show the last 15 analysis sessions
		int length = 0;
		if(analysisSessions.size() > 15)
			length = analysisSessions.size() - 15;
		
		// Loop through historical analysis sessions and store appropriate values into lists
		for(int i = length; i < analysisSessions.size(); i++) {
			// Save the start time in a list
			analysisTimes.add(analysisSessions.get(i).getStartTime());
			// Calculate real analysis session average sentiment and store
			if(-1 * analysisSessions.get(i).getAvgNegSentiment() > analysisSessions.get(i).getAvgPosSentiment()) {
				analysisValues.add(analysisSessions.get(i).getAvgNegSentiment());
				// Add negative value to toolTip string... no need to add -
				toolTips.add("<span style=\"color: red\">" + analysisSessions.get(i).getAvgNegSentiment() + "%</span>, " +
				analysisSessions.get(i).getNumTweetsProcessed() + " tweets");
			}
			else {
				analysisValues.add(analysisSessions.get(i).getAvgPosSentiment());
				// Add the sentiment values to the toolTip strings to be placed in toolTips on the graph
				if(analysisSessions.get(i).getAvgPosSentiment() == 0) {
					toolTips.add(analysisSessions.get(i).getAvgPosSentiment() + "%, " +
							analysisSessions.get(i).getNumTweetsProcessed() + " tweets"); // If 0, don't add +
				}
				else {
					toolTips.add("<span style=\"color: green\">+" + analysisSessions.get(i).getAvgPosSentiment() + "%</span>, " +
							analysisSessions.get(i).getNumTweetsProcessed() + " tweets"); // Add + for positive
				}
			}
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
					// Pass toolTip string to graph
					historyGraphWebView.loadUrl("javascript:toolTips[" + i + "] = '" + toolTips.get(i) + "';");
				}
				
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int width = metrics.widthPixels;
				if(analysisValues.size() > 1) {
					historyGraphWebView.loadUrl("javascript:resize_graph("+ ((width/metrics.density) - 25) +");");
				}
				
				// Resizes graph if there are more than 8 sessions in the database
				// Reduces graph clutter
				if(analysisValues.size() > 8 && width < 800)
					historyGraphWebView.loadUrl("javascript:resize_graph("+ ((analysisValues.size() - 8)*20 + ((width/metrics.density) - 30)) +");");
				else if(analysisValues.size() > 20 && width > 800)
					historyGraphWebView.loadUrl("javascript:resize_graph("+ ((analysisValues.size() - 20)*20 + ((width/metrics.density) - 30)) +");");
				// Call javascript function to draw the graph with appropriate data
				historyGraphWebView.loadUrl("javascript:draw_graph();");
		    }  
		}); // End of historyGraphWebView.setWebViewClient(...);
		
		// Load local html page (containing graph) into webview 
		if(analysisValues.size() > 1)
			historyGraphWebView.loadUrl("file:///android_asset/graph.html");
		else if (analysisValues.size() == 1){
			// If there is only one analysis session in history, display a message in the webview instead of the graph (as can't see the point)
			historyGraphWebView.loadData("<html><body>There is currently only one analysis session in this topic's history, a graph will" +
					" display once there are at least two sessions in the database.</body></html>", "text/html", null);
		}
		// Allow horizontal scrolling within webview
		historyGraphWebView.setHorizontalScrollBarEnabled(true);
		historyGraphWebView.setVerticalScrollBarEnabled(false);
		
		// Settings for graph webview
		WebSettings historyGraphWebSettings = historyGraphWebView.getSettings();
		// We need to allow javascript to run in this graph
		historyGraphWebSettings.setJavaScriptEnabled(true);
		historyGraphWebSettings.setDomStorageEnabled(true); // Might not be necessary
		historyGraphWebSettings.setLightTouchEnabled(true); // Possibly allow for touching points on graph? Might not be necessary
		
		// EditText area that displays statistics for all historical analysis sessions
		EditText infoLeft= (EditText) findViewById(R.id.topicInfoLeft);
		EditText infoRight = (EditText) findViewById(R.id.topicInfoRight);
		
		int totalTweets = 0;
		int totalTime = 0;
		int avgSentiment = 0;
		
		// Gather data from all analysis sessions
		for(int i = 0; i < analysisSessions.size(); i++) {
			// Tally the total number of Tweets over all analysis sessions for the current topic
			totalTweets += analysisSessions.get(i).getNumTweetsProcessed();
			// Tally the total number of seconds that the application has spent analyzing the current topic
			totalTime += analysisSessions.get(i).getDuration();
			// Calculate the proper sentiment to retrieve, and retrieve it, adding to overall weighted average sentiment calculation for the current topic
			if(analysisSessions.get(i).getAvgPosSentiment() > (-1) * analysisSessions.get(i).getAvgNegSentiment())
				avgSentiment += analysisSessions.get(i).getAvgPosSentiment() * analysisSessions.get(i).getNumTweetsProcessed();
			else
				avgSentiment += analysisSessions.get(i).getAvgNegSentiment() * analysisSessions.get(i).getNumTweetsProcessed();
		}
		
		// If there are analysis sessions in the database, print out statistics about the analysis session set
		if(analysisSessions.size() > 0) {
			// Finish calculating overall average sentiment, being sure to avoid dividing by 0
			avgSentiment = avgSentiment/totalTweets;

			infoLeft.setText("Tweets Processed:\n");
			infoRight.setText(totalTweets + "\n");
			
			infoLeft.append("Sessions:\n");
			infoRight.append(analysisSessions.size() + "\n");
			
			//Properly format time spent running depending on length (calculated off of number of seconds) (XXh XXm XXs)
			if(totalTime >= 3600) {// Greater than or equal to an hour
				infoLeft.append("Time Running:\n");
				infoRight.append(String.format("%02d", totalTime/3600) + "h " 
						+ String.format("%02d", (totalTime - (totalTime/3600)*3600)/60) + "m "
						+ String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			}
			else if(totalTime >= 60) {// Greater than or equal to a minute (but less than an hour) (XXm XXs)
				infoLeft.append("Time Running:\n");
				infoRight.append(String.format("%02d", (totalTime - (totalTime/3600)*3600)/60) + "m "
				+ String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			}
			else {// Less than one minute (XXs)
				infoLeft.append("Time Running:\n");
				infoRight.append(String.format("%02d", totalTime- (totalTime/60)*60) + "s\n");
			}
			
			// Format and print average sentiment
			if(avgSentiment > 0) {
				infoLeft.append("Avg. Sentiment:");
				infoRight.append("+" + avgSentiment + "%"); // Average is positive
			}
			else {
				infoLeft.append("Avg. Sentiment:");
				infoRight.append(avgSentiment + "%"); // Average is negative
			}
		}
		
		// Display an instructional message to user if there are no sessions in the topic's history
		if(analysisSessions.size() == 0)
			infoLeft.setText("There are no analysis sessions in the database." +
					" \n\nEnter a duration above and click \"Start\"" +
					" in order to begin an analysis session.");
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
	 * This method is called when "Start" button is pressed. 
	 * This method checks for network connectivity and
	 * calls goToGuageActivity if connected to network
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		// checking for network connection
		// create new thread for twitter api call
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				connectedToNetwork = true;
				
				try {
					// check access to internet/twitter api
					httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
					httpOauthprovider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token",
							"https://api.twitter.com/oauth/access_token",
							"https://api.twitter.com/oauth/authorize");
					httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
				} catch (Exception e) {
					// Not connected to Internet
					connectedToNetwork = false;
					DialogFragment dialog = new ConnectToNetworkDialogFragment();
					dialog.show(getSupportFragmentManager(), "ConnectToNetworkDialogFragment");
				}
			}
		};
		Thread nThread = new Thread(runnable);
		nThread.start();
		
		// wait for network thread to finish
		try {
			nThread.join();
			goToGaugeActivity(connectedToNetwork);
		} catch (InterruptedException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Creates an intent to change to the Gauge activity corresponding to the
	 * current topic.
	 */
	public void goToGaugeActivity(boolean connectedToNetwork) {
		
		EditText hoursEntry = (EditText) findViewById(R.id.hours);
		EditText minutesEntry = (EditText) findViewById(R.id.minutes);
		
		
		int hours = 0;
		int minutes = 0;
		
		// check for Internet connectivity
		if(connectedToNetwork) {
			// If user entered time and has credentials in the database...
			if(hoursEntry.getText().length() != 0 || minutesEntry.getText().length() != 0 && dh.getCredentials() != null) {
				
				// Calculate time from user input
				if(hoursEntry.getText().length() != 0) {
					hours = Integer.parseInt(hoursEntry.getText().toString());
				}
				if(minutesEntry.getText().length() != 0) {
					minutes = Integer.parseInt(minutesEntry.getText().toString());
				}
				int time = hours * 3600 + minutes * 60;
				
				// Create intent to go to Gauge Activity
				Intent intent = new Intent(this, GaugeActivity.class);
				// Pass analysis session duration to Gauge Activity
				intent.putExtra("analysisDuration", time);
				// Pass the topic id to the Gauge Activity
				intent.putExtra("topicId", topic_id);
				startActivity(intent);
			}
			else if(dh.getCredentials() == null) {
				// If user hasn't logged in with Twitter, don't allow them to go to Gauge Activity
				// Should not ever get here, as should be forced on Topic List Activity
				Toast.makeText(this, "Please log in with Twitter", Toast.LENGTH_LONG).show();
			}
			else {
				// If user fails to enter a duration, inform them that they need to
				Toast.makeText(this, "Please enter an Analysis Session Duration", Toast.LENGTH_LONG).show();
				// Set text color to red in duration boxes to inform the user where to look
				hoursEntry.setHintTextColor(getResources().getColor(R.color.red));
				minutesEntry.setHintTextColor(getResources().getColor(R.color.red));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    // Handle item selection
	    switch (item.getItemId())
	    {
	        case R.id.menu_help:
	        	showHelpActivity();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Creates an instance of the Help Activity for the user to
	 * view application help page
	 */
	public void showHelpActivity() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
}
