package com.grep.ui;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.util.Log;
import com.grep.database.*;

public class DBUnitTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dbunittest);

		Log.d("DBHandler: ", "Instantiating...");
		System.out.println("DBHandler: Instantiating...");
		DatabaseHandler dh = new DatabaseHandler(this);
		System.out.println("DBHandler: Created");
		Log.d("DBHandler: ", "Created");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dbunittest, menu);
		return true;
	}

}
