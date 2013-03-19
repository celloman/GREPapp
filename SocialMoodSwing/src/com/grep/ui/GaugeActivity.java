package com.grep.ui;

import com.grep.gaugebackend.GaugeBackend;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.grep.gaugebackend.Gauge;
import com.grep.gaugebackend.Tweet;
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
	
	static public Thread m_gaugeConsumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gauge);
	
		String[] keywords = {"obama", "clinton", "politics", "administration", "liberal", "conservative"};
		BlockingQueue<Tweet> popularTweets = new ArrayBlockingQueue<Tweet>(100);
		BlockingQueue<Gauge> gaugeValues = new ArrayBlockingQueue<Gauge>(100);
		GaugeBackend.start(keywords, popularTweets, gaugeValues);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/gauge.html");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		GaugeInterface gi = new GaugeInterface();
		
		webView.addJavascriptInterface(gi, "Android");
		
		// start another thread to process gauge values TODO add another to process
		// the popular Tweets
		m_gaugeConsumer = new Thread(new GaugeConsumer(gaugeValues, gi));
		m_gaugeConsumer.start();
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
