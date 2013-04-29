package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.grep.database.Credentials;
import com.grep.database.DatabaseHandler;
import com.grep.database.Keyword;
import com.grep.database.Topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class TopicKeywordsActivity extends FragmentActivity {
	static ListView keywordsListView;
	static EditText newKeywordEditText;
	static ListItemAdapter adapter;
	static List<ListItem> rows = new ArrayList<ListItem>();
	List<Keyword> keywords = new ArrayList<Keyword>();
	EditText topicTitle;
	static boolean isNewTopic;
	static DatabaseHandler dh;
	boolean buttonHeightSet = false;
	static int topicId = -1;
	static List<Keyword> keywordTracker = new ArrayList<Keyword>();
	
	// OAuth information for checking Internet access
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	private final static String consumerKey = "2RKMlxcy1cf1WGFfHJvpg";
	private final static String consumerSecret = "35Ege9Yk1vkoZmk4koDDZj07e9CJZtkRaLycXZepqA";
	private final String CALLBACKURL = "socialmoodswing://credentials";
	private int TWITTER_AUTH;
	private String verifier;
	
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
		
		//get screen width
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		//set button width to 33% (truncated to int) of the screen width
		double buttonWidth = metrics.widthPixels*.33;
		
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		Button saveButton = (Button) findViewById(R.id.saveButton);
		Button deleteButton = (Button) findViewById(R.id.deleteButton);
		cancelButton.setWidth((int)buttonWidth);
		saveButton.setWidth((int)buttonWidth);
		deleteButton.setWidth((int)buttonWidth);
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
		//Create an instance of the delete topic warning dialog fragment and show it
		DialogFragment dialog = new DeleteTopicWarningDialogFragment();
		dialog.show(getSupportFragmentManager(), "DeleteTopicWarningDialogFragment");
	}
	
	public static void DeleteTopic()
	{
		//if not a new topic (we are editing existing topic) we need to actually delete it; if new topic just cancel w/out saving 
		if(!isNewTopic) {
			
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
		
		//TopicKeywordsActivity.this.finish();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_topic_keywords, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    // Handle item selection
	    switch (item.getItemId())
	    {
	    	case R.id.menu_login:        	
	    		Credentials c = dh.getCredentials();
	    		// remove current credentials from database
	    		if(c != null) {
	    			dh.deleteCredentials(c.getId());
	    		}	
	    		// start new thread for Twitter OAuth
	    		newOAuthThread();
	    		return true;
	        case R.id.menu_help:
	        	showHelpActivity();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Creates an instance of the Help Activity for the user to
	 * view application help page
	 */
	public void showHelpActivity() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Make a new thread for Twitter OAuth and run startOAuth
	 */
	public void newOAuthThread() {
    	
    	// run authentication process in new thread
		Runnable runnable = new Runnable() {
		    @Override
		    public void run() {
		    	Looper.prepare();
				// show loading dialog
				DialogFragment dialog = new LoadingDialogFragment();
				dialog.show(getSupportFragmentManager(), "LoadingDialogFragment");
		    	
		    	// start Twitter OAuth
		    	startOAuth();
		        
		    	// after finishing, close the progress bar
		        dialog.dismiss();
		        Looper.loop();
		    }
		};
		
		//start thread
		new Thread(runnable).start();
	}
	
	/**
	 * Starts user authentication using Twitter OAuth
	 */
	public void startOAuth() {
		
		//Attempt to open Twitter OAuth in webview
		try {
		    httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		    httpOauthprovider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token",
		                                            "https://api.twitter.com/oauth/access_token",
		                                            "https://api.twitter.com/oauth/authorize");
		    String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
		    
		    // open web view with for twitter authentication
		    Intent intent = new Intent(this, TwitterWebviewActivity.class);
		    intent.putExtra("URL", authUrl);
		    startActivityForResult(intent, TWITTER_AUTH);
		} catch (Exception e) {
			DialogFragment dialog = new ConnectToNetworkDialogFragment();
			dialog.show(getSupportFragmentManager(), "ConnectToNetworkDialogFragment");
		}
	}
	
	/**
	 * Add credential results returned from Twitter webview to database.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// check if results come from webview
        if (requestCode == TWITTER_AUTH)
        {
        	//check if results are "OK" from webview
            if (resultCode == Activity.RESULT_OK)
            {
                verifier = (String) data.getExtras().get("oauth_verifier");

                // Twitter authentication needs to be run in separate thread for newer
                // versions of android
            	Runnable runnable = new Runnable() {
            		@Override
        		    public void run() {
        		    	Looper.prepare();
        		    	try
        		    	{
            		    	httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
            		    
            		
            		        String user_key = httpOauthConsumer.getToken();
            		        String user_secret = httpOauthConsumer.getTokenSecret();

            		        // Save user_key and user_secret in database
            		        Credentials c = new Credentials(user_key, user_secret);
            		        dh.open();
            		        dh.addCredentials(c);
        		    	}
        		    	catch (Exception e)
        		    	{
        		    		// cancel button pressed in webview
        		    	} 
        		    	Looper.loop();
            		}
            	};           	
            	//start thread
        		new Thread(runnable).start();
            }else {
            	// user uses back press to leave activity
            }
        }
        else
        {
        	// non twitter auth request code
        }
    }
}



