package com.grep.ui;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		setTitle("Social Mood Swing");
		final WebView helpView = (WebView) findViewById(R.id.helpview);
		
		helpView.loadUrl("file:///android_asset/help.html");
	}
}
