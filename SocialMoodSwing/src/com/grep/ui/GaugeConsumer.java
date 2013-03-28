/**
 * GaugeConsumer.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.ui;

import android.webkit.WebView;
import com.grep.gaugebackend.Gauge;
import java.util.concurrent.BlockingQueue;

/**
 * public class GaugeConsumer implements Runnable
 */
public class GaugeConsumer implements Runnable {
	
	// incoming gauge values queue
	protected BlockingQueue<Gauge> m_inQueue = null;
	// app context
	protected GaugeActivity m_activity = null;
	// webview that needs updating
	protected WebView m_wv = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueue, GaugeActivity a, WebView wv) {
		m_inQueue = inQueue;
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
				Gauge g = m_inQueue.take();
				Integer gaugeVal = 50;
				
				if((g.m_Positive - g.m_Negative) != 0)
					gaugeVal = (int)(g.m_Positive*100)/(g.m_Positive - g.m_Negative);
				
				System.out.println("tweet count: " + Integer.toString(g.m_tweetCount));
				
				m_wv.loadUrl( String.format("javascript:refresh_gauge(%d, %d, %.2f)",
					gaugeVal,
					g.m_tweetCount,
					g.m_sessionAverage*100
				));
				
				//m_activity.showToast(Integer.toString(gaugeVal));
				
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
