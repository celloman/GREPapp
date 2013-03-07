package com.grep.ui;

import com.grep.gaugebackend.VikingsPort;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

/**
 * GaugeActivity displays the results of the current analysis session in real time, via
 * a gauge, statistical data, and popular Tweets. 
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

public class GaugeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gauge);
	
		VikingsPort.go();
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
