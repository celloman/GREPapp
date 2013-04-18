package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import com.grep.database.DatabaseHandler;
import com.grep.database.Keyword;
import com.grep.database.Topic;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
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

public class TopicKeywordsActivity extends FragmentActivity {
	static ListView keywordsListView;
	static EditText newKeywordEditText;
	static ListItemAdapter adapter;
	static List<ListItem> rows = new ArrayList<ListItem>();
	List<Keyword> keywords = new ArrayList<Keyword>();
	EditText topicTitle;
	boolean isNewTopic;
	DatabaseHandler dh;
	boolean buttonHeightSet = false;
	static int topicId = -1;
	static List<Keyword> keywordTracker = new ArrayList<Keyword>();
	
	@Override
	public void onResume()
	{
		super.onResume();
		dh.open();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		dh.close();
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		dh = new DatabaseHandler(this);
		dh.open(); //do I have to call this here as well, was getting null pointer exceptions with database when this wasn't here
		
		//retrieve the topicId as passed to this intent from the TopicListActivity, default return is -1
		isNewTopic = getIntent().getBooleanExtra("isNewTopic", false);
		
		if (!isNewTopic) {
			topicId = getIntent().getIntExtra("topicId", -1);

			if (topicId == -1) {
				//Show user an error if the topic id is not properly retrieved... something went wrong
				//Should not ever really get here
				Toast.makeText(this, "Error: Could not find topic in database", Toast.LENGTH_LONG).show();
				this.finish();
			}
			
			keywords = dh.getAllKeywords(topicId);
			setTitle(getResources().getString(R.string.title_activity_topic_keywords) + " - " + dh.getTopic(topicId).getTopicName());
		}
		else
			setTitle(getResources().getString(R.string.title_activity_topic_keywords) + " - " + getResources().getString(R.string.new_topic));
		
		setContentView(R.layout.activity_topic_keywords);
				
		
		//listview of keywords we will populate, edittext for the topic title, edittext for new keywords
		keywordsListView   = (ListView)findViewById(R.id.keywordsListView);
		topicTitle         = (EditText)findViewById(R.id.topicEditText);
		newKeywordEditText = (EditText)findViewById(R.id.newKeywordEditText);
				
		//since rows is static, it may need to be cleared if there were existing keyword rows left over from last view of activity
		rows.clear();
			
		//if existing topic, populate the keywords list with the keywords for this topic
	    if (!isNewTopic) {
	    	topicTitle.setText(dh.getTopic(topicId).getTopicName());
	    	
			if(keywords != null) { //shouldn't ever be null, but if this is the case, keywords.size() throws exception
				for (int i = 0; i < keywords.size(); i++)
		    	{
		      		rows.add(new ListItem(R.drawable.x, keywords.get(i).getKeyword(), keywords.get(i).getId() ));
		    	}
			}		    	
	    }
   
		//create an adapter which defines the data/format of each element of our listview
		adapter = new ListItemAdapter(this, R.layout.keywords_list_item_row, rows, ListItemAdapter.listItemType.KEYWORD);
	       
		//set our adapter for the listview so that we can know what each list element (row) will be like
		keywordsListView.setAdapter(adapter);
		
		//bring the keyboard up ready to type for new topic, not for existing topic
		if (!isNewTopic) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}
	
	
	/**
	 * When add keyword button is clicked, validate that there is a keyword to add. If no keyword, provide notification
	 * to the user. If keyword is provided, add it to the beginning of the keywords listview, and update the display.
	 */
	public void onClickAddKeywordButton(View v)
	{
		String keywordText = newKeywordEditText.getText().toString(); 
		
		if(!keywordText.isEmpty()) {
			//the last arg of the ListItem constructor is the keyword id, for new keywords set it to 0 initially
			rows.add(0, new ListItem(R.drawable.x, keywordText, 0));
			newKeywordEditText.setText("");
			newKeywordEditText.setHintTextColor(getResources().getColor(R.color.black));
			ListItemAdapter.keywordJustAdded = true;
			adapter.notifyDataSetChanged();
			keywordsListView.smoothScrollToPosition(0);
		}
		else {
			newKeywordEditText.setHintTextColor(getResources().getColor(R.color.red));
			Toast.makeText(this, "Please enter a keyword to add to the list!", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * Upon tapping delete keyword button, get the position of
	 * the button in the list, remove it, and update the listview. 
	 * The database will be updated upon clicking Save Topic
	 */
	public void onClickDeleteKeywordButton(View v)
	{
		//the delete keyword button has a tag set in the background for identifying which position in the listview it is
		int buttonRow = (Integer) v.getTag();

		rows.remove(buttonRow);
		ListItemAdapter.keywordDeleted = buttonRow;
		adapter.notifyDataSetChanged();
	}	
	
	
	/**
	 * Upon tapping cancel button in keywords activity,
	 * ignore changes and return to topic list activity
	 */
	public void onClickCancelButton(View v)
	{
		TopicKeywordsActivity.this.finish();
	}
	
	
	/**
	 * Upon tapping delete topic button, if this is a new topic,
	 * just ignore changes and go back to topic list activity.
	 * If this is an existing topic, remove it from the topic list
	 * activity listview and remove it from the database.
	 */
	public void onClickDeleteTopicButton(View v)
	{
		//if not a new topic (we are editing existing topic) we need to actually delete it; if new topic just cancel w/out saving 
		if(!isNewTopic) {
			
			//TODO should we pop up a warning dialog confirming that they want to delete the topic?
			//delete topic from the database
			dh.deleteTopic(topicId);
			
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
		
		TopicKeywordsActivity.this.finish();
	}
		
	
	/**
	 * Upon tapping save topic button, do some error checking to see
	 * if there are any required fields left blank. Then see if it is
	 * a new or existing topic. If new topic, get topic name and keyword
	 * and create the topic, update topic list activity with this topic 
	 * added to the listview, and add topic to databse. If existing topic
	 * figure out what modifications have been made and save to database.
	 * If the topic title was changed, also update in the topic list activity.
	 */
	public void onClickSaveTopicButton(View v)
	{
		String topicText = topicTitle.getText().toString();
 
		//if no topic name provided, highlight textedit and show warning message
		if (topicText.isEmpty()) {
			topicTitle.setHintTextColor(getResources().getColor(R.color.red));
			Toast.makeText(this, "You need to specify a topic title!", Toast.LENGTH_SHORT).show();
		}
		else {		
			//if no keywords provided, highlight textedit and show warning message
			if (rows.isEmpty()) {
				newKeywordEditText.setHintTextColor(getResources().getColor(R.color.red));
				Toast.makeText(this, "You must add at least one keyword!", Toast.LENGTH_SHORT).show();
			}	
			else {
				//if editing an existing topic
				if (!isNewTopic) {
					Topic topic = dh.getTopic(topicId);
					
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
						dh.updateTopic(topic);
						TopicListActivity.adapter.notifyDataSetChanged();
					}
					
					//check for newly added keywords
					for (int i = 0; i < rows.size(); i++)
					{
						int keywordId = rows.get(i).getItemId();
						if (keywordId == 0) {
							Keyword keyword = new Keyword(topicId, rows.get(i).getText());
							dh.addKeyword(keyword);
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
									
									//get the corresponding EditText for this keyword item in the listview in order to get the current text
									ListItem item = (ListItem) keywordsListView.getItemAtPosition(j);
									//EditText keywordEdit = (EditText) holder.textEdit;
									String keywordText = item.getText();
									
									//if text is different, update the keyword in the database
									if (!keywords.get(i).getKeyword().equals(keywordText)) {
										Keyword keyword = dh.getKeyword(keywordId);
										keyword.setKeyword(keywordText);
										dh.updateKeyword(keyword);
									}
									
									break;
								}
							}
							
							//if the keyword no longer exists in the listview, it must have been deleted so remove from db
							if (!found) {
								dh.deleteKeyword(keywords.get(i).getId());
							}
						}
					}
					
					TopicKeywordsActivity.this.finish();
				}
				else {
					//if new topic, add to db and update TopicListActivity listview
					Topic topic = new Topic(topicTitle.getText().toString());
					int topic_id = dh.addTopic(topic);
					
					for (int i = 0; i < rows.size(); i++)
					{
						Keyword keyword = new Keyword(topic_id, rows.get(i).getText());
						dh.addKeyword(keyword);
					}
												
					TopicListActivity.rows.add(new ListItem(R.drawable.edit_pencil, topic.getTopicName(), topic_id));
					TopicListActivity.adapter.notifyDataSetChanged();
					TopicListActivity.topicsListView.smoothScrollToPosition(TopicListActivity.topicsListView.getCount());
					TopicKeywordsActivity.this.finish();	
				}
			}
		}
     }
}



