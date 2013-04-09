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
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
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
	static List<ListItem> rows = new ArrayList<ListItem>();
	static int topicId;
	List<Keyword> keywords = new ArrayList<Keyword>();
	static List<Keyword> keywordTracker = new ArrayList<Keyword>();
	boolean isNewTopic;
	DatabaseHandler db;
	boolean buttonHeightSet;
		
	//default constructor, for new topic
	public TopicKeywordsDialogFragment()
	{
		this.isNewTopic = true;
		this.buttonHeightSet = false;
	}
		
	public TopicKeywordsDialogFragment(int topicId, List<Keyword> keywords) 
	{
		//overloaded constructor, for editing existing topic
		this.topicId = topicId;
		this.keywords = keywords;
		this.keywordTracker = keywords;
		this.isNewTopic = false;
		this.buttonHeightSet = false;	
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		db.open();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		db.close();
	}
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		db = new DatabaseHandler(getActivity());
		db.open(); //do I have to call this here as well, was getting null pointer exceptions with database when this wasn't here
				
		//Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
	        
		//Get view from inflater
		final View view = inflater.inflate(R.layout.keyword_dialog, null);
		
		
		//listview of keywords we will populate, edittext for the topic title, edittext for new keywords
		keywordsListView   = (ListView) view.findViewById(R.id.keywordsListView);
		topicTitle         = (EditText) view.findViewById(R.id.topicEditText);
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
		
		//since rows is static, it may need to be cleared if there were existing keyword rows left over from last view of activity
		rows.clear();
			
		//if existing topic, populate the keywords list with the keywords for this topic
	    if (!isNewTopic) {
	    	topicTitle.setText(db.getTopic(this.topicId).getTopicName());
	    	
			if(keywords != null) { //shouldn't ever be null, but if this is the case, keywords.size() throws exception
				for (int i = 0; i < keywords.size(); i++)
		    	{
		      		rows.add(new ListItem(R.drawable.delete_x, keywords.get(i).getKeyword(), keywords.get(i).getId() ));
		    	}
			}		    	
	    }
   
		//create an adapter which defines the data/format of each element of our listview
		adapter = new ListItemAdapter(view.getContext(), R.layout.keywords_item_row, rows, ListItemAdapter.listItemType.KEYWORD);
	       
		//set our adapter for the listview so that we can know what each list element (row) will be like
		keywordsListView.setAdapter(adapter);
		
		//Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());		
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setMessage("Topic Keywords")
			.setView(view)
			// Add action buttons
			.setPositiveButton("Save Topic", null)
			.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					TopicKeywordsDialogFragment.this.getDialog().cancel();
				}
			})
			.setNegativeButton("Delete Topic", null);
		
		final AlertDialog dialog = builder.create();		
		dialog.show();
		
		//used to set all dialog fragment buttons to the same height, it's the button with most text
		final Button deleteTopicButton = (Button) dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		
		//set the height of the dialog fragment buttons to all be the same
		deleteTopicButton.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	            if (!buttonHeightSet) {
	                // Here button is already laid out and measured for the first time, so we can use height to set other buttons
	            	dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setHeight(deleteTopicButton.getHeight());
	            	dialog.getButton(AlertDialog.BUTTON_POSITIVE).setHeight(deleteTopicButton.getHeight());
	            	buttonHeightSet = true;
	                
	            }
	        }
	    });

		//onClick for Save Topic button
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				//TODO need to do something special depending on if this is a new topic or not? like only saving vs updating
				String topicText = topicTitle.getText().toString();

				//if no topic name provided, highlight textedit and show warning message
				if (topicText.isEmpty()) {
					topicTitle.setHintTextColor(getResources().getColor(R.color.red));
					Toast.makeText(view.getContext(), "You need to specify a topic title!", Toast.LENGTH_SHORT).show();
				}
				else {		
					//if no keywords provided, highlight textedit and show warning message
					if (rows.isEmpty()) {
						newKeywordEditText.setHintTextColor(getResources().getColor(R.color.red));
						Toast.makeText(view.getContext(), "You must add at least one keyword!", Toast.LENGTH_SHORT).show();
					}	
					else {
						//if editing an existing topic
						if (!isNewTopic) {
							Topic topic = db.getTopic(topicId);
							
							//if topic name has been changed in the dialog, update in db and in TopicListActivity listview
							if (!topic.getTopicName().equals(topicText)) {
								for(int i = 0; i < TopicListActivity.rows.size(); i++)
								{
									//find the changed topic by topicId in the TopicActivity listview
									if (TopicListActivity.rows.get(i).getItemId() == topicId) {
										TopicListActivity.rows.get(i).setText(topicText);
										break;
									}								
								}
								
								//update all edits to this topic
								topic.setTopicName(topicText);
								db.updateTopic(topic);
								TopicListActivity.adapter.notifyDataSetChanged();
							}
							
							//check for newly added keywords
							for (int i = 0; i < rows.size(); i++)
							{
								int keywordId = rows.get(i).getItemId();
								if (keywordId == 0) {
									Keyword keyword = new Keyword(topicId, rows.get(i).getText());
									db.addKeyword(keyword);
								}
							}
							
							//check for edited or deleted keywords
							if (keywords != null) {
								boolean found;
								int keywordId;
								
								//for every keyword initially loaded see if it was edited or deleted
								for(int i = 0; i < keywords.size(); i++)
								{
									found = false;
								
									for (int j = 0; j < rows.size(); j++)
									{
										keywordId = keywords.get(i).getId();

										//if the keyword is found, see if the text was edited
										if (keywords.get(i).getId() == rows.get(j).getItemId()) {
											found = true;
											System.out.println("stored: " + keywords.get(i).getKeyword());
											System.out.println("textedit: " + rows.get(j).getText());
											
											//if text is different, update the keyword in the database
											//TODO this if statement below check compares keyword text with original text upon loading listview, need to getText() from edittext here
											if (!keywords.get(i).getKeyword().equals(rows.get(j).getText())) {
												Keyword keyword = db.getKeyword(keywordId);
												keyword.setKeyword(rows.get(j).getText());
												db.updateKeyword(keyword);
											}
											
											break;
										}
									}
									
									//if the keyword no longer exists in the listview, it must have been deleted so remove from db
									if (!found) {
										db.deleteKeyword(keywords.get(i).getId());
									}
								}
							}
							
							TopicKeywordsDialogFragment.this.getDialog().cancel();
						}
						else {
							//if new topic, add to db and update TopicListActivity listview
							Topic topic = new Topic(topicTitle.getText().toString());
							int topic_id = db.addTopic(topic);
							
							for (int i = 0; i < rows.size(); i++)
							{
								Keyword keyword = new Keyword(topic_id, rows.get(i).getText());
								db.addKeyword(keyword);
							}
														
							TopicListActivity.rows.add(new ListItem(R.drawable.edit_pencil, topic.getTopicName(), topic_id));
							TopicListActivity.adapter.notifyDataSetChanged();
							TopicKeywordsDialogFragment.this.getDialog().cancel();	
						}
					}
				}
	         }
		});
		
		//set up the action for when the delete topic button is clicked
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				//if not a new topic (we are editing existing topic) we need to actually delete it; if new topic just cancel w/out saving 
				if(!isNewTopic) {
					//TODO should we pop up a warning dialog confirming that they want to delete the topic?
					//delete topic from the database
					db.deleteTopic(topicId);
					
					//delete topic from the topics listview and update the listview
					for(int i = 0; i < TopicListActivity.rows.size(); i++)
					{
						if (TopicListActivity.rows.get(i).getItemId() == topicId) {
							TopicListActivity.rows.remove(i);
							TopicListActivity.adapter.notifyDataSetChanged();
							break;
						}
					}
				}
				
				TopicKeywordsDialogFragment.this.getDialog().cancel();
			}
		});

		return dialog;
	}		
}
