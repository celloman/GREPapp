package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
import java.net.URLEncoder;
import com.loopj.android.http.*;

public class GetSentiment implements Runnable {

	protected BlockingQueue<Tweet> m_inQueue = null;
	protected BlockingQueue<Tweet> m_outQueue = null;
	protected String[] m_Keywords = null;
	
	public GetSentiment(BlockingQueue<Tweet> inQueue, BlockingQueue<Tweet> outQueue, String[] keywords) {
		m_inQueue = inQueue;
		m_outQueue = outQueue;
		m_Keywords = keywords;
	}
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			System.out.println("sentiment thread running...");
			try {
				final Tweet t = m_inQueue.take();

				String urlString = 
					"http://www.sentiment140.com/api/classify?text=" + 
					URLEncoder.encode(t.text) + 
					"&query=" + 
					URLEncoder.encode(t.keyword);
				
				AsyncHttpClient client = new AsyncHttpClient();
				
				client.get(urlString, 
					new AsyncHttpResponseHandler() {
						
						@Override
						public void onSuccess(String response) {

							t.sentiment = Integer.parseInt((response.split("\"polarity\":")[1]).split(",")[0]);
							
							try {
								m_outQueue.put(t);
							} catch (InterruptedException ex) {
								Thread.currentThread().interrupt();
								System.out.println("sentiment queue put failed...");
							}
						}
						
						@Override
						public void onFailure(Throwable thrwbl, String string) {
							System.out.println("sentiment request failed...");
						}
					});
				
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				System.out.println("sentiment queue take failed...");
			}
		}
	}
}
