package com.grep.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * EndSessionDialogFragment displays a message to the user containing
 * info on the recently finished analysis Session.
 * 
 * @author Gresham, Ryan, Everett, Pierce 
 *
 */
public class EndSessionDialogFragment extends DialogFragment {
	
	public EndSessionDialogFragment() {
		//default constructor, for new topic
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        Bundle arguments = this.getArguments();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.end_session_dialog, null);
        final CheckBox saveSession = (CheckBox) view.findViewById(R.id.saveSessionCheckBox);
        final TextView sessionInfo = (TextView) view.findViewById(R.id.endSessionTextView);
        
        // If Tweets were processed during session
        if(arguments.getBoolean("hasValues")) {
        	// Display number of Tweets processed
        	sessionInfo.append("\nTweets Processed: " + arguments.getInt("numTweets") + "\n");
        	// Display duration of analysis session
        	// No need for seconds only, as user cannot enter less than a minute
        	if(arguments.getInt("runTime") >= 3600)
        		sessionInfo.append("Session Duration: \t" + String.format("%02d", arguments.getInt("runTime")/3600) + "h " + String.format("%02d", (arguments.getInt("runTime") - (arguments.getInt("runTime")/3600)*3600)/60) + "m\n");
        	else if (arguments.getInt("runTime") >=60)
        		sessionInfo.append("Session Duration: \t" + String.format("%02d", (arguments.getInt("runTime") - (arguments.getInt("runTime")/3600)*3600)/60) + "m\n");
        	// Display average sentiment for this session
        	if(arguments.getInt("sessionAverage") > 0)
        		sessionInfo.append("Avg. Sentiment: \t\t+" + arguments.getInt("sessionAverage") + "%\n");
        	else
        		sessionInfo.append("Avg. Sentiment: \t\t" + arguments.getInt("sessionAverage") + "%\n");
        } else {
        	// If no Tweets were processed, inform user
        	// Their keywords may be bad, or there may have been a communication error with Twitter
        	sessionInfo.append("Error: No Tweets were processed during this analysis session!\n" +
        			"There may have been a communication error with Twitter, or there may not be any Tweets with your keyword set");
        	// Don't allow them to save a null session
        	saveSession.setChecked(false);
        	saveSession.setEnabled(false);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("Analysis Session Ended")
        	   .setView(view)
        	   	// Add action buttons
               .setPositiveButton("Continue", new DialogInterface.OnClickListener() {    
            	   public void onClick(DialogInterface dialog, int id) {
					   GaugeActivity a = (GaugeActivity) EndSessionDialogFragment.this.getActivity();
					   
            		   //stop the analysis session and return to TopicActivity, finish() calls onDestroy() for
            		   //this activity where results from session need to be stored in database
            		   a.saveResults(saveSession.isChecked());
                   }
               });
        return builder.create();
    }
}
