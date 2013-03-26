package com.grep.ui;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

/**
 * TopicActivity displays the currently selected topic's run history
 * and specifications for a new run. Additionally, Twitter credentials
 * can be changed from here by going to the Login menu item.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class TopicActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		setTitle(R.string.title_activity_topic);
		
		WebView myWebView = (WebView) findViewById(R.id.graph);
		myWebView.loadUrl("file:///android_asset/graph.html");
		myWebView.setHorizontalScrollBarEnabled(false);
		//myWebView.addJavascriptInterface(new JsObject(), "injectedObject");
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic, menu);
		return true;
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
		Intent intent = new Intent(this, GaugeActivity.class);
		startActivity(intent);
	}
}
