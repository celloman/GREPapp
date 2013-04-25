
package com.grep.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;


/**
 * LoadingDialogFragment creates a pop-up dialog that notifies the user
 * that they will be redirected to Twitter to login.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class LoadingDialogFragment extends DialogFragment {
	
	/**
	 * Constructor
	 */
	public LoadingDialogFragment() {
		// Empty
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.loading_dialog, null);
        
        // set view and on back press functionality
        builder.setView(view)
               // Check for back press, close application if back is pressed
               .setOnKeyListener(new DialogInterface.OnKeyListener() {
            	   @Override
            	   public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            		   if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            			   getActivity().finish();
            		   return true;
            	   }
               });
        
        return builder.create();
	}
}