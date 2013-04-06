package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import com.grep.database.DatabaseHandler;
import com.grep.database.Keyword;
import com.grep.database.Topic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


/**
 * TopicKeywordsDialogFragment allows for the creation of a new topic,
 * with specified keywords, or the editing of a topic's keywords.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

//load original database into list (load id's), if I delete a keyword change the id tag to negative of that id
//if I add a keyword, give it an id tag of 0
//if I edit a keyword, leave it's id as it
//in the end, loop through secondary list of keywords
//	if negative, remove keyword from database
//  if 0, add keyword to database
//  if positive, compare with the keyword in the database with that id and see if the text has changed, if so update text
public class TopicKeywordsDialogFragment extends DialogFragment {
	ListView keywordsListView;
	EditText topicTitle;
	static EditText newKeywordEditText;
	static ListItemAdapter adapter;
	static List<ListItem> rows;
	int topicId = -1; //invalid topicId, only change value if we are launching a keywords dialog for existing topic
	List<Keyword> keywords = null;
	boolean isNewTopic;
	DatabaseHandler db;
		
	public TopicKeywordsDialogFragment() {
		this.isNewTopic = true;
		//default constructor, for new topic
	}
		
	public TopicKeywordsDialogFragment(int topicId, List<Keyword> keywords) {
		//overloaded constructor, for editing existing topic
		this.topicId = topicId;
		this.keywords = keywords;
		this.isNewTopic = false;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		db.open();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		db.close();
	}
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		db = new DatabaseHandler(getActivity());
		db.open(); //do I have to call this here as well, was getting null pointer exceptions with database when this wasn't here
		
		rows = new ArrayList<ListItem>();
		
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
	        
		// Get view from inflater
		final View view = inflater.inflate(R.layout.keyword_dialog, null);
		
		newKeywordEditText = (EditText) view.findViewById(R.id.newKeywordEditText);
		/* 		
	    newKeywordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {      	
	        	//if we lost focus and there is text in the new keyword edit box, add the keyword to the list
	        	if(!hasFocus && !newKeywordEditText.getText().toString().isEmpty()) {
	        		rows.add(new ListItem(R.drawable.delete_x, newKeywordEditText.getText().toString()));
	        		newKeywordEditText.setText("");
	        		newKeywordEditText.setHintTextColor(getResources().getColor(R.color.black));
	        	}
	        }
	    });
	    
	    */
	    
	    //TODO this is not quite working, need to get this working or figure out a better way of doing
	    //this, maybe a checkmark button or something. Also need to validate that at least one keyword
	    //was given, else highlight new keyword in red and pop a toast
	    //the below works on Jason's phone but listview items don't pop up until exiting keyboard

		/* Use enter key to add keyword in addKeywordEditText to the keyword list, some issues
		 Currently works to use enter to add keyword to list, but only for first two keywords, then has issues

		newKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        	//if done button hit on keyboard
	        	if(actionId == EditorInfo.IME_ACTION_DONE) { 
		        	//if there is text in the new keyword edit box, add the keyword to the list
		        	if(!newKeywordEditText.getText().toString().isEmpty()) {
		        		rows.add(new ListItem(R.drawable.delete_x, newKeywordEditText.getText().toString()));
		        		newKeywordEditText.setText("");
		        		newKeywordEditText.setHintTextColor(getResources().getColor(R.color.black));		        		
		        	}
		        	

	                return true;
	            }
	            return false;
	        }
	    });
		 */		
		
		//listview we will populate and the edittext for the topic title
		keywordsListView = (ListView) view.findViewById(R.id.keywordsListView);
		topicTitle = (EditText) view.findViewById(R.id.topicEditText);
		
		//populate the keywords list with the keywords for this topic
	    if (!isNewTopic) {
	    	topicTitle.setText(db.getTopic(this.topicId).getTopicName());
	    	
			if(keywords != null) { //shouldn't ever be null, but if this is the case, keywords.size() throws exception
				for (int i = 0; i < keywords.size(); i++)
		    	{	
		      		rows.add(new ListItem(R.drawable.delete_x, keywords.get(i).getKeyword(), keywords.get(i).getId() )); //TODO do some testing to make sure this keyword id is correct
		    	}
			}
		    	
	    }
   
		//create an adapter which defines the data/format of each element of our listview
		adapter = new ListItemAdapter(view.getContext(), R.layout.keywords_item_row, rows, ListItemAdapter.listItemType.KEYWORD);
	       
		//set our adapter for the listview so that we can know what each list element (row) will be like
		keywordsListView.setAdapter(adapter);

		
		/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
builder.setMessage("Test for preventing dialog close");
AlertDialog dialog = builder.create();
dialog.show();
//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
      {            
          @Override
          public void onClick(View v)
          {
              Boolean wantToCloseDialog = false;
              //Do stuff, possibly set wantToCloseDialog to true then...
              if(wantToCloseDialog)
                  dismiss();
              //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
          }
      });
*/
		
		
		
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setMessage("Topic Keywords")
			.setView(view)
			// Add action buttons
			.setPositiveButton("Save Topic", null)
			/*.setPositiveButton("Save Topic", new DialogInterface.OnClickListener() {    
				@Override
				public void onClick(DialogInterface dialog, int id) {
					//TODO save the topic and keywords as is
					String topicText = topicTitle.getText().toString();

					if (topicText.isEmpty()) {
						topicTitle.setHintTextColor(getResources().getColor(R.color.red));//Toast.makeText(view.getContext(), "You need to specify a topic title!", Toast.LENGTH_SHORT).show();
					}
					
					else {
						Topic topic = new Topic(topicTitle.getText().toString());
						db.addTopic(topic);
						TopicListActivity.rows.add(new ListItem(R.drawable.edit_pencil, topic.getTopicName()));
						//TODO get string value from topic title text edit, if no topic pop warning, else save topic
						TopicListActivity.adapter.notifyDataSetChanged();
						TopicKeywordsDialogFragment.this.getDialog().cancel();						
					}
				}
			})*/
			.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					TopicKeywordsDialogFragment.this.getDialog().cancel();
				}
			})
			.setNegativeButton("Delete Topic", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					TopicKeywordsDialogFragment.this.getDialog().cancel();
				}
			});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	        	//TODO save the topic and keywords as is
					String topicText = topicTitle.getText().toString();

					if (topicText.isEmpty()) {
						topicTitle.setHintTextColor(getResources().getColor(R.color.red));
						Toast.makeText(view.getContext(), "You need to specify a topic title!", Toast.LENGTH_SHORT).show();
					}
					
					else {
						
						if (rows.isEmpty()) {
							newKeywordEditText.setHintTextColor(getResources().getColor(R.color.red));
							Toast.makeText(view.getContext(), "You must add at least one keyword!", Toast.LENGTH_SHORT).show();
						}
						
						else {
							Topic topic = new Topic(topicTitle.getText().toString());
							int topic_id = db.addTopic(topic);
							TopicListActivity.rows.add(new ListItem(R.drawable.edit_pencil, topic.getTopicName(), topic_id));
							TopicListActivity.adapter.notifyDataSetChanged();
							TopicKeywordsDialogFragment.this.getDialog().cancel();			
						}
					}
	          }
	      });
		//return builder.create();
		return dialog;
	}		
}
