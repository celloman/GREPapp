package com.grep.socialmoodswing;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TopicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic, menu);
		return true;
	}

}
