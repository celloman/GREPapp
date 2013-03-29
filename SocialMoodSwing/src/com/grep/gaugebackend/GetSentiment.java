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
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 * @param outQueue (BlockingQueue<Tweet>)
	 */
	public GetSentiment(BlockingQueue<Tweet> inQueue, BlockingQueue<Tweet> outQueue) {
		m_inQueue = inQueue;
		m_outQueue = outQueue;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			
			//System.out.println("sentiment thread running...");
			
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
						}
					});
				
			} catch (InterruptedException ex) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
		}
	}
}
