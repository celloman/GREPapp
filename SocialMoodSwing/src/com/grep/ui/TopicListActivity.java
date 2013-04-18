package com.grep.ui;

import java.util.ArrayList;
import java.util.List;
import com.grep.database.DatabaseHandler;
import com.grep.database.Topic;
import com.grep.ui.ListItemAdapter.TopicListItemHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug.FlagToString;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * TopicListActivity displays a list of all of the topics created by the
 * user. Each topic can be viewed or edited by clicking on either the topic
 * or the edit button. A new topic may be created by clicking the Create 
 * Topic button.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class TopicListActivity extends FragmentActivity
{
	static ListView topicsListView;
	static ListItemAdapter adapter;
	static List<ListItem> rows = new ArrayList<ListItem>();
	List<Topic> topics = new ArrayList<Topic>();
	DatabaseHandler dh;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		dh.open();	
		
		// check database for credentials
		if(dh.getCredentials() == null) {
			// send user to Twitter login activity
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("change_credentials", true);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		dh.close();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		dh = new DatabaseHandler(this);
		dh.open();
		
		setContentView(R.layout.activity_topic_list);
		setTitle(R.string.title_activity_topic_list);

        //create an adapter which defines the data/format of each element of our listview
        adapter = new ListItemAdapter(this, R.layout.topics_list_item_row, rows, ListItemAdapter.listItemType.TOPIC);
              
        //listview we will populate
        topicsListView = (ListView)findViewById(R.id.topicsListView);
       
        //set our adapter for the listview so that we can know what each list element (row) will be like
        topicsListView.setAdapter(adapter);
        
        //setup the click listener for when a list item (row) in the topics list is clicked
        topicsListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View listRow, int position, long arg3)
            {
            	TopicListItemHolder holder = (TopicListItemHolder) listRow.getTag();      	
            	goToTopicActivity(holder.itemId);
            }
        });
        
        //since rows is static, it may need to be cleared if there were existing topic rows left over from last view of activity
		rows.clear();
		topics = dh.getAllTopics();
		
		if(topics != null) {
			for(int i=0; i < topics.size(); i++)
			{
				rows.add(new ListItem(R.drawable.edit_pencil, topics.get(i).getTopicName(), topics.get(i).getId()));
				adapter.notifyDataSetChanged();
			}
		}	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic_list, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    // Handle item selection
	    switch (item.getItemId())
	    {
	        case R.id.menu_login:
	        	launchLoginActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	/**
	 * Upon Edit Button (pencil) being clicked, get the topicId
	 * associated with that button and create an instance of the
	 * keywords dialog fragment with associated topic name and
	 * keywords loaded into the dialog fragment.
	 */
    public void onClickEditTopicButton(View v)
    {
    	//the edit button has a tag set in the background corresponding to the topicId for the listview topic element
    	int topicId = (Integer) v.getTag();
    	launchExistingTopicKeywordsActivity(topicId);
    }

	
	/**
	 * Upon "Add Topic" button being clicked, create an
	 * instance of the keywords dialog fragment for the
	 * user to add a new topic name with new keywords.
	 */
    public void onClickAddTopicButton(View v)
    {
    	launchNewTopicKeywordsActivity();
    }


	/**
	 * Creates an instance of the Login Activity for the user to
	 * enter Twitter authentication credentials.
	 */
	public void launchLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
    }
		
	
	/**
	 * Creates an instance of the Topic Keywords dialog fragment so the user
	 * may create a new topic.
	 */
	public void launchNewTopicKeywordsActivity()
	{
		//create intent and pass in true for isNewTopic
        Intent intent = new Intent(this, TopicKeywordsActivity.class);
        intent.putExtra("isNewTopic", true);
        startActivity(intent);
	}
	
	
	/**
	 * Creates an instance of the Topic Keywords dialog fragment so the user
	 * may edit an existing topic.
	 */
	public void launchExistingTopicKeywordsActivity(int topicId)
	{
		//create intent and pass in false for isNewTopic, and the topicId
        Intent intent = new Intent(this, TopicKeywordsActivity.class);
        intent.putExtra("isNewTopic", false);
        intent.putExtra("topicId", topicId);
        startActivity(intent);
	}
	

	/**
	 * Creates an intent to change to the Topic activity corresponding to the
	 * topic selected in the list. Pass the topicId to the next intent so that
	 * it can load in the session info for that topic.
	 */
	public void goToTopicActivity(int topicId)
	{	
		Intent intent = new Intent(this, TopicActivity.class);
		intent.putExtra("topicId", topicId);
		startActivity(intent);
	}
}
