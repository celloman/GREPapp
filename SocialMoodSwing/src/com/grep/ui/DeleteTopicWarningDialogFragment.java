package com.grep.ui;

import com.grep.ui.TopicKeywordsActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * WarningDialogFragment displays a warning to the user.
 * 
 * @author Gresham, Ryan, Everett, Pierce 
 *
 */
public class DeleteTopicWarningDialogFragment extends DialogFragment {
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.delete_topic_warning_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("Deleting Topic")
        	   .setView(view)
        	   	// Add action buttons
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {    
            	   public void onClick(DialogInterface dialog, int id) {
					   TopicKeywordsActivity.DeleteTopic();//GaugeActivity a = (GaugeActivity) DeleteTopicWarningDialogFragment.this.getActivity();
					   DeleteTopicWarningDialogFragment.this.getActivity().finish();
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if (this != null) {
                		   DeleteTopicWarningDialogFragment.this.getDialog().cancel();
                	   }
                   }
               });
        return builder.create();
    }
}
