package com.grep.ui;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.grep.database.Credentials;
import com.grep.database.DatabaseHandler;
import com.grep.database.Topic;
import com.grep.ui.ListItemAdapter.TopicListItemHolder;
import com.grep.ui.LoginDialogFragment.LoginDialogListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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
public class TopicListActivity extends FragmentActivity implements LoginDialogListener
{
	//OAuth variables
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	public final static String consumerKey = "2RKMlxcy1cf1WGFfHJvpg";
	public final static String consumerSecret = "35Ege9Yk1vkoZmk4koDDZj07e9CJZtkRaLycXZepqA";
	private final String CALLBACKURL = "socialmoodswing://credentials";
	
	// ListView variables
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
		
		// check database for credentials
		if(dh.getCredentials() == null) {
			// Display Twitter login dialog
			DialogFragment dialog = new LoginDialogFragment();
	        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
		}
		
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
	
	/**
	 * Captures return values from Twitter OAuth
	 * 
	 * @param intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);

	    Uri uri = intent.getData();

	    //Check if you got NewIntent event due to Twitter Call back only

	    if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

	        String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

	        try {
	            // this will populate token and token_secret in consumer

	            httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
	            String user_key = httpOauthConsumer.getToken();
	            String user_secret = httpOauthConsumer.getTokenSecret();

	            // Save user_key and user_secret in database
	            Credentials c = new Credentials(user_key, user_secret);
	            dh.open();
	            dh.addCredentials(c);

	        } catch(Exception e){
	        	Log.e("OAuth", "OAuth Fail" + e.getMessage());
	        }
	    } else {
	        // Do something if the callback comes from elsewhere
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
	        	Credentials c = dh.getCredentials();
	        	dh.deleteCredentials(c.getId());
	        	this.recreate();
	            return true;
	        case R.id.menu_help:
	        	showHelpActivity();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Method from Login dialog, when "OK" button is clicked it calls this.
	 * This method calls startOAuth().
	 */
	public void onLoginDialogClick() {
		startOAuth();
	}
	
	/**
	 * Starts user authentication using Twitter OAuth
	 */
	public void startOAuth() {
		//Attempt to open Twitter OAuth in browser
		try {
		    httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		    httpOauthprovider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token",
		                                            "https://api.twitter.com/oauth/access_token",
		                                            "https://api.twitter.com/oauth/authorize");
		    String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
		    // Open the browser
		    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
		    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    startActivity(intent);
		} catch (Exception e) {
		    Toast.makeText(this, "Cannot connect to Twitter, make sure your time is correct" +
		    		" and you have internet access.", Toast.LENGTH_LONG).show();
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
	 * Creates an instance of the Help Activity for the user to
	 * view application help page
	 */
	public void showHelpActivity() {
		Intent intent = new Intent(this, HelpActivity.class);
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
	
	/**
	 * Test function on toast button for testing retrieving
	 * credentials from twitter oauth site. Can be removed
	 * when successful.
	 * @param v
	 */
	public void toastCredentials(View v) {
		Credentials c = dh.getCredentials();
		if(c==null) {
			Toast.makeText(this, "No credentials in database!" , Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Key: " + c.getConsumerKey() + "; Secret: " + c.getConsumerSecret() , Toast.LENGTH_LONG).show();
		}	
	}
}
