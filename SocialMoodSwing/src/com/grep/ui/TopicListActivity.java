package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * TopicListActivity displays a list of all of the topics created by the
 * user. Each topic can be viewed or edited by clicking on either the topic
 * or the edit button. A new topic may be created by clicking the Create 
 * Topic button.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class TopicListActivity extends FragmentActivity {
	ListView topicsListView;
	ListItemAdapter adapter;
	List<ListItem> rows = new ArrayList<ListItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_topic_list);
		setTitle(R.string.title_activity_topic_list);
        
		rows.add(new ListItem(R.drawable.edit_pencil, "Minnesota Vikings are the best team in the NFL! And they are going to get Jennings!"));
        rows.add(new ListItem(R.drawable.edit_pencil, "Gun Contjjjjjjjjjjjjjjffffffffffffrol"));
        rows.add(new ListItem(R.drawable.edit_pencil, "Abortionaaaaaaaaaaaaaaaaaaaaaaaffffffffff"));
        rows.add(new ListItem(R.drawable.edit_pencil, "Politicaffffffffffffaaaaaaaaaaaacccccccccccccs"));

        //create an adapter which defines the data/format of each element of our listview
        adapter = new ListItemAdapter(this, R.layout.listview_item_row, rows, ListItemAdapter.listItemType.TOPIC);
              
        //listview we will populate
        topicsListView = (ListView)findViewById(R.id.topicsListView);
       
        //set our adapter for the listview so that we can know what each list element (row) will be like
        topicsListView.setAdapter(adapter);
        
        //setup the click listener for when a list item (row) in list is clicked
        topicsListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View listRow, int position, long arg3)
            {
            	goToTopicActivity();
            }
        });		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic_list, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_login:
	            showLoginDialog();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	/**
	 * Upon Edit Button (pencil) being clicked, create an
	 * instance of the keywords dialog fragment for the
	 * user to add a new topic name with new keywords.
	 */
    public void onClickEditTopicButton(View v)
    {
    	showTopicKeywordsDialog(); //TODO should be calling version that takes list
    	//v.getTag() returns the topic for the edit button that was clicked, Tag is set in *Adapter.java class
    	//TODO add logic for launching the edit topic dialogue
    	//do we need a separate action from when someone hits the add topic button?
    }

	
	/**
	 * Upon "Add Topic" button being clicked, create an
	 * instance of the keywords dialog fragment for the
	 * user to add a new topic name with new keywords.
	 */
    public void onClickAddTopicButton(View v)
    {
    	//showTopicKeywordsDialog();
    	rows.add(new ListItem(R.drawable.edit_pencil, "Minnesota Vikings are the best team in the NFL! And they are going to get Jennings!"));
        rows.add(new ListItem(R.drawable.edit_pencil, "Gun Contjjjjjjjjjjjjjjffffffffffffrol"));
    	adapter.notifyDataSetChanged();
    	//TODO add logic for launching the new topic dialogue
    	//do we need a separate action from when someone hits the edit topic button?
    }
    
	/**
	 * TODO
	 */
    
	public void onClickDeleteKeywordButton(View v)
	{
		int button_row = (Integer) v.getTag();
		TopicKeywordsDialogFragment.rows.remove(button_row);
		TopicKeywordsDialogFragment.adapter.notifyDataSetChanged();
	}
	
	

    /**
	 * Creates an instance of the Login dialog fragment for the user to
	 * enter Twitter authentication credentials.
	 */
	public void showLoginDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }
		
	
	/**
	 * Creates an instance of the Topic Keywords dialog fragment so the user
	 * may create a new topic, or edit a topic's keywords.
	 */
	public void showTopicKeywordsDialog() {
		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new TopicKeywordsDialogFragment();
        dialog.show(getSupportFragmentManager(), "TopicKeywordsDialogFragment");
	}

	/**
	 * Creates an intent to change to the Topic activity corresponding to the
	 * topic selected in the list.
	 */
	public void goToTopicActivity(){
		Intent intent = new Intent(this, TopicActivity.class);
		startActivity(intent);
	}
}
