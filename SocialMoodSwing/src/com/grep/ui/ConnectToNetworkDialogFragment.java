package com.grep.ui;

import android.os.Bundle;
import android.provider.Settings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class ConnectToNetworkDialogFragment extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Get view from inflater
        final View view = inflater.inflate(R.layout.connect_to_network_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("Connect to Network")
        	   .setView(view)
        	   	// Add action buttons
               .setPositiveButton("Settings", new DialogInterface.OnClickListener() {    
            	   public void onClick(DialogInterface dialog, int id) {
					   Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					   startActivity(intent);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   ConnectToNetworkDialogFragment.this.getDialog().cancel();
                   }
               });
        return builder.create();
    }

}
