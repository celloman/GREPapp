package com.grep.ui;

import com.grep.database.Credentials;
import com.grep.database.DatabaseHandler;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


/**
 * LoginDialogFragment creates a pop-up dialog that prompts the user to
 * enter their Twitter authentication credentials.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
public class LoginActivity extends Activity {
	
	//OAuth variables
	private CommonsHttpOAuthConsumer httpOauthConsumer;
    private OAuthProvider httpOauthprovider;
    public final static String consumerKey = "2RKMlxcy1cf1WGFfHJvpg";
    public final static String consumerSecret = "35Ege9Yk1vkoZmk4koDDZj07e9CJZtkRaLycXZepqA";
    private final String CALLBACKURL = "socialmoodswing://credentials";
    
    //for database interaction
    private DatabaseHandler dh = new DatabaseHandler(this);
	
	/**
	 * Constructor
	 */
	public LoginActivity() {
		// Empty
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(R.string.title_activity_login);
    }
	
	@Override
	protected void onResume() {
		dh.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		dh.close();
		super.onPause();
	}
	
	/**
	 * Starts user authentication using Twitter OAuth
	 */
	public void startOAuth(View v) {
		//Attempt to open Twitter OAuth in browser
		try {
		    httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		    httpOauthprovider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token",
		                                            "https://api.twitter.com/oauth/access_token",
		                                            "https://api.twitter.com/oauth/authorize");
		    String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
		    // Open the browser
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
		    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
	            Toast.makeText(this, "Key: " + c.getConsumerKey() + "; Secret: " + c.getConsumerSecret(), Toast.LENGTH_LONG).show();
	            dh.addCredentials(c);

	        } catch(Exception e){

	        }
	    } else {
	        // Do something if the callback comes from elsewhere
	    }
	}
	
	/**
	 * {@code public void viewTwitterSite}
	 * Creates a new browser activity taking the user to twitter.com
	 * 
	 * @param v (View) - text view specified from onClick
	 */
	public void toTwitterSite(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://twitter.com"));
		startActivity(intent);
	}
}