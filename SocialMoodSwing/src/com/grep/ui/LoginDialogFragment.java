package com.grep.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;


/**
 * LoginDialogFragment creates a pop-up dialog that notifies the user
 * that they will be redirected to Twitter to login.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class LoginDialogFragment extends DialogFragment {
    
	// Listen interface between dialog and host activity
	LoginDialogListener mListener;
	
	/**
	 * Constructor
	 */
	public LoginDialogFragment() {
		// Empty
	}
	
	// Methods to be called in host activity for starting Twitter authentication
	public interface LoginDialogListener {
		public void onLoginDialogClick();
	}
	
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
               // Open Twitter authentication page in browser
        	   .setPositiveButton("OK", new DialogInterface.OnClickListener() {    
            	   public void onClick(DialogInterface dialog, int id) {
					   mListener.onLoginDialogClick();
					   LoginDialogFragment.this.getDialog().cancel();
                   }
               })
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
	
	// Override the Fragment.onAttach() method to instantiate the LoginDialogListener
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
}