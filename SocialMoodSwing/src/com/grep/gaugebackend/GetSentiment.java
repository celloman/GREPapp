/**
 * GetSentiment.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
import java.net.URLEncoder;
import com.loopj.android.http.*;

/**
 * public class GetSentiment implements Runnable
 */
public class GetSentiment implements Runnable {

	// incoming queue of tweets
	protected BlockingQueue<Tweet> m_inQueue = null;
	// outgoing queue of tweets
	protected BlockingQueue<Tweet> m_outQueue = null;
	// outgoing m_outQueue of tweets
	protected BlockingQueue<WebToast> m_webToasts = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 * @param outQueue (BlockingQueue<Tweet>)
	 */
	public GetSentiment(BlockingQueue<Tweet> inQueue, BlockingQueue<Tweet> outQueue, BlockingQueue<WebToast> webToasts) {
		m_inQueue = inQueue;
		m_outQueue = outQueue;
		m_webToasts = webToasts;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			
			//System.out.println(String.format("sentiment thread: (%d, %d)", this.m_inQueue.size(), this.m_outQueue.size()));
			
			
			try {
				final Tweet t = m_inQueue.take();

				// build the url
				String urlString = 
					"http://www.sentiment140.com/api/classify?text=" + 
					URLEncoder.encode(t.text) + 
					"&query=" + 
					URLEncoder.encode(t.keyword);
				
				// send off the request
				AsyncHttpClient client = new AsyncHttpClient();
				
				client.get(urlString, 
					new AsyncHttpResponseHandler() {
						
						@Override
						public void onSuccess(String response) {
							// parse the sentiment from response
							t.sentiment = Integer.parseInt((response.split("\"polarity\":")[1]).split(",")[0]);
							t.sentiment = (t.sentiment / 2) - 1;
							
							// send the tweet to the next module
							try {
								m_outQueue.put(t);
							} catch (InterruptedException ex) {
								// immediately reset interrupt flag
								Thread.currentThread().interrupt();
							}
						}
						
						@Override
						public void onFailure(Throwable thrwbl, String string) {
							System.out.println("sentiment request failed...");
							m_webToasts.offer(new WebToast("warning", "We're having some trouble talking to the sentiment server. We'll keep trying, but you might want to check your internet connection.", "Warning", 0, 0, 0));
						}
					});
				
			} catch (InterruptedException ex) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
		}
	}
}
