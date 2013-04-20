package com.grep.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

/**
 * WarningDialogFragment displays a warning to the user.
 * 
 * @author Gresham, Ryan, Everett, Pierce 
 *
 */
public class WarningDialogFragment extends DialogFragment {
	
	public WarningDialogFragment() {
		//default constructor, for new topic
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Determine if there are values to save to db, if not will disable saving option
        Bundle arguments = this.getArguments();
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.warning_dialog, null);
        final CheckBox saveSession = (CheckBox) view.findViewById(R.id.saveCheckBox);
        
        // if no Tweets were processed, do not allow user to save session
        if(!arguments.getBoolean("hasValues")) {
        	saveSession.setChecked(false);
        	saveSession.setEnabled(false);
        }
        
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("WARNING!")
        	   .setView(view)
        	   	// Add action buttons
               .setPositiveButton("Stop", new DialogInterface.OnClickListener() {    
            	   public void onClick(DialogInterface dialog, int id) {
					   GaugeActivity a = (GaugeActivity) WarningDialogFragment.this.getActivity();
					   
            		   //stop the analysis session and return to TopicActivity, finish() calls onDestroy() for
            		   //this activity where results from session need to be stored in database
            		   a.saveResults(saveSession.isChecked());
            		   a.stopGaugeThreads();
                   }
               })
               .setNegativeButton("Continue Running", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   WarningDialogFragment.this.getDialog().cancel();
                   }
               });
        return builder.create();
    }
}
