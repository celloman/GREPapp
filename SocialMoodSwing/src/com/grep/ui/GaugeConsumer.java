/**
 * GaugeConsumer.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.ui;

import android.app.Activity;
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
	// last popular tweet time
	long m_Time = 0;
	// last guage update time
	long m_UpdateTime = 0;
	// activity context
	Activity m_gaugeActivity = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueueGauge, BlockingQueue<WebToast> inWebToasts, WebView wv, Activity a) {
		m_inQueueGauge = inQueueGauge;
		m_inWebToasts = inWebToasts;
		m_wv = wv;
		m_gaugeActivity = a;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			//System.out.println("gauge consumer thread running...");
			
			try {
				final Gauge g = m_inQueueGauge.take();
				m_latestGauge = g;
				final Integer gaugeVal;
				
				if((g.m_Positive - g.m_Negative) != 0)
					gaugeVal = (int)(g.m_Positive*100)/(g.m_Positive - g.m_Negative);
				else
					gaugeVal = 50;
				
				long currentTime = System.currentTimeMillis();
				if(currentTime > m_UpdateTime+1000){
					m_gaugeActivity.runOnUiThread(new Runnable(){
						public void run() {
							m_wv.loadUrl( String.format("javascript:refresh_gauge(%d, %d, %.1f)",
								gaugeVal,
								g.m_tweetCount,
								g.m_sessionAverage*100
							));	
						}
					});
					m_UpdateTime = currentTime;
				}
				
				// check for popular tweets, toast if we have one
				final WebToast t = m_inWebToasts.poll();
				if(t != null) {
					if(t.m_type.equals("warning")){		
						m_gaugeActivity.runOnUiThread(new Runnable(){
							public void run() {
								m_wv.loadUrl( String.format("javascript:makeModal('%s','%s','%s', %d, %d, %d)",
									t.m_type,
									t.m_heading,
									t.m_message,
									t.m_data1,
									t.m_data2,
									t.m_data3
								));
							}
						});
					}
					else {
						currentTime = System.currentTimeMillis();
						if(currentTime > m_Time+1500){
							m_gaugeActivity.runOnUiThread(new Runnable(){
								public void run() {
									m_wv.loadUrl( String.format("javascript:makeToast('%s','%s','%s', %d, %d, %d)",
										t.m_type,
										t.m_message,
										t.m_heading,
										t.m_data1,
										t.m_data2,
										t.m_data3
									));	
								}
							});
							m_Time = currentTime;
						}
						else{
							System.out.println("too much!!!!");
						}
					}
				}
				
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
