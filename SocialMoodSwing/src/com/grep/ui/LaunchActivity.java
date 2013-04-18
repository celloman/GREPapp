/**
 * LaunchActivity.java
 * 
 * This activity is for launching Social Mood Swing
 * 
 * This activity initializes the database and starts the 
 * TopicListActivity.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.ui;

import com.grep.database.DatabaseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;


public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		// initialize database
		new DatabaseHandler(this);
		
		Intent intent = new Intent(this, TopicListActivity.class);
        startActivity(intent);
	}

}
