/**
 * GaugeConsumer.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.ui;

import android.webkit.WebView;
import com.grep.gaugebackend.Gauge;
import com.grep.gaugebackend.WebToast;
import java.util.concurrent.BlockingQueue;

/**
 * public class GaugeConsumer implements Runnable
 */
public class GaugeConsumer implements Runnable {
	
	// incoming gauge values queue
	protected BlockingQueue<Gauge> m_inQueueGauge = null;
	// incoming popular tweets queue
	protected BlockingQueue<WebToast> m_inWebToasts = null;
	// webview that needs updating
	protected WebView m_wv = null;
	// latest gauge values
	public Gauge m_latestGauge = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueueGauge, BlockingQueue<WebToast> inWebToasts, WebView wv) {
		m_inQueueGauge = inQueueGauge;
		m_inWebToasts = inWebToasts;
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
				m_latestGauge = g;
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
				WebToast t = m_inWebToasts.poll();
				if(t != null) {
					//m_activity.showToast(t.text);
					m_wv.loadUrl( String.format("javascript:makeToast('%s','%s','%s', %d, %d, %d)",
						t.m_type,
						t.m_message,
						t.m_heading,
						t.m_data1,
						t.m_data2,
						t.m_data3
					));
				}
				
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
