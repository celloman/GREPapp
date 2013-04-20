/**
 * TwitterWebviewActivity.java
 * 
 * This activity creates a webview for the user to 
 * login to Twitter using OAuth.
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */
package com.grep.ui;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterWebviewActivity extends Activity {
	
	// Twitter callback url for application
	private final String CALLBACKURL = "socialmoodswing://credentials";
	
	// intent from calling activity
	private Intent callingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_web_view);
		
		//get url from calling activity's intent
		callingIntent = getIntent();
        String url = (String)callingIntent.getExtras().get("URL");
		
        // setup webview functionality
		WebView twitterWebView = (WebView)findViewById(R.id.twitter_webview);
	    twitterWebView.setWebViewClient(new WebViewClient() {
	    	@Override
	    	public boolean shouldOverrideUrlLoading(WebView webView, String url) { 
	    	
	    		if (url != null && url.startsWith(CALLBACKURL)) {
	    			Uri uri = Uri.parse( url );
                    String oauthVerifier = uri.getQueryParameter( "oauth_verifier" );
                    
                    // return results topic list ativity
                    callingIntent.putExtra("oauth_verifier", oauthVerifier);
                    setResult( RESULT_OK, callingIntent );  
                    finish();
	    			return true;            
	    		}
	    		return false;
	    	}		
	    }); 
	    
	    // go to Twitter OAuth site in webview
	    twitterWebView.loadUrl(url);
	}

}
