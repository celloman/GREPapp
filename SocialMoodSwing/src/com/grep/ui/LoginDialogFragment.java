package com.grep.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;


/**
 * LoginDialogFragment creates a pop-up dialog that prompts the user to
 * enter their Twitter authentication credentials.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class LoginDialogFragment extends DialogFragment {
	
	/**
	 * Constructor
	 */
	public LoginDialogFragment() {
		// Empty
	}
	
	/**
	 * */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.login_dialog, null);
        
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("Twitter Login")
        	   .setView(view)
        	   // Add action buttons
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   LoginDialogFragment.this.getDialog().cancel();
                   }
               });
        return builder.create();
    }
	
	/**
	 * {@code public void viewTwitterSite}
	 * Creates a new browser activity taking the user to twitter.com
	 * 
	 * @param v (View) - text view specified from onClick
	 */
	public void toTwitterSite(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://twitter.com"));
		startActivity(intent);
	}
}