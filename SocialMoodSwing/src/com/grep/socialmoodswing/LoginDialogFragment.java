package com.grep.socialmoodswing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;


/**
 * LoginDialogFragment creates a pop-up dialog that prompts the user to
 * enter their Twitter authentication credentials.
 * 
 * @author Everett
 *
 */
public class LoginDialogFragment extends DialogFragment {
	
	public LoginDialogFragment() {
		// Empty
	}
	
/* 	
 * Commented section allows for communication between dialog and calling activity, may not need.
 * 
	// Methods to be called in host activity
	public interface LoginDialogListener {
        public void onDialogPositiveClick(String msg);
    }
	
	// Listen interface between dialog and host activity
	LoginDialogListener mListener;
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
 *
 */	
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
               .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                    
                   public void onClick(DialogInterface dialog, int id) {
/*
 * Will need to change this to authenticate credentials
 *  
                       // Send the positive button event back to the host activity
                	   
                	   // Email specified in username field
                       EditText username = (EditText) view.findViewById(R.id.username);
                       
                       // Password specified in password field
                       EditText password = (EditText) view.findViewById(R.id.password);
                	   
                       // Send message to host activity
                       mListener.onDialogPositiveClick("Username: \"" + username.getText().toString() 
                			   				  + "\"\n" + "Password: \"" + password.getText().toString() + "\"");  
  *
  */               }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   LoginDialogFragment.this.getDialog().cancel();
                   }
               });
        return builder.create();
    }
}