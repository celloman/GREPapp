/**
 * GaugeConsumer.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.ui;

import android.webkit.WebView;
import com.grep.gaugebackend.Gauge;
import com.grep.gaugebackend.Tweet;
import java.util.concurrent.BlockingQueue;

/**
 * public class GaugeConsumer implements Runnable
 */
public class GaugeConsumer implements Runnable {
	
	// incoming gauge values queue
	protected BlockingQueue<Gauge> m_inQueueGauge = null;
	// incoming popular tweets queue
	protected BlockingQueue<Tweet> m_inQueueTweets = null;
	// app context
	protected GaugeActivity m_activity = null;
	// webview that needs updating
	protected WebView m_wv = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueueGauge, BlockingQueue<Tweet> inQueueTweets, GaugeActivity a, WebView wv) {
		m_inQueueGauge = inQueueGauge;
		m_inQueueTweets = inQueueTweets;
		m_activity = a;
		m_wv = wv;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			//System.out.println("gauge consumer thread running...");
			
			try {
				Gauge g = m_inQueueGauge.take();
				Integer gaugeVal = 50;
				
				if((g.m_Positive - g.m_Negative) != 0)
					gaugeVal = (int)(g.m_Positive*100)/(g.m_Positive - g.m_Negative);
				
				System.out.println("tweet count: " + Integer.toString(g.m_tweetCount));
				
				m_wv.loadUrl( String.format("javascript:refresh_gauge(%d, %d, %.1f)",
					gaugeVal,
					g.m_tweetCount,
					g.m_sessionAverage*100
				));
				
				// check for popular tweets, toast if we have one
				Tweet t = m_inQueueTweets.poll();
				if(t != null) {
					//m_activity.showToast(t.text);
					m_wv.loadUrl( String.format("javascript:toastr.info('%s')",
						t.text
					));
				}
				
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
