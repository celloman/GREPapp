package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * TopicKeywordsDialogFragment allows for the creation of a new topic,
 * with specified keywords, or the editing of a topic's keywords.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class TopicKeywordsDialogFragment extends DialogFragment {
	ListView keywordsListView;	
	static ListItemAdapter adapter;
	static List<ListItem> rows = new ArrayList<ListItem>();
	String [] keywords = null;
		
	public TopicKeywordsDialogFragment() {
		//default constructor, for new topic
	}
		
	public TopicKeywordsDialogFragment(String [] keywords /* contains all keywords of topic */) {
		//overloaded constructor, for editing existing topic
		this.keywords = keywords;
	}
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
	        
		// Get view from inflater
		final View view = inflater.inflate(R.layout.keyword_dialog, null);
			
		//populate the keywords list
		rows.add(new ListItem(R.drawable.delete_x, "Ryan Sacksteder"));
		rows.add(new ListItem(R.drawable.delete_x, "Gresham"));
		rows.add(new ListItem(R.drawable.delete_x, "Everett Bloch"));
		rows.add(new ListItem(R.drawable.delete_x, "Pierce"));
	        
		//create an adapter which defines the data/format of each element of our listview
		adapter = new ListItemAdapter(view.getContext(), R.layout.keywords_item_row, rows, ListItemAdapter.listItemType.KEYWORD);
	              
		//listview we will populate
		keywordsListView = (ListView)view.findViewById(R.id.keywordsListView);
	       
		//set our adapter for the listview so that we can know what each list element (row) will be like
		keywordsListView.setAdapter(adapter);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setMessage("Topic Keywords")
			.setView(view)
			// Add action buttons
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {    
				public void onClick(DialogInterface dialog, int id) {
					//Action performed when done
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					TopicKeywordsDialogFragment.this.getDialog().cancel();
				}
			});
		
		return builder.create();
	}		
}
