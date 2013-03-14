/**
 * GaugeConsumer.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.ui;

import com.grep.gaugebackend.Gauge;
import java.util.concurrent.BlockingQueue;

/**
 * public class GaugeConsumer implements Runnable
 */
public class GaugeConsumer implements Runnable {
	
	// incoming gauge values queue
	protected BlockingQueue<Gauge> m_inQueue = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueue) {
		this.m_inQueue = inQueue;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			System.out.println("gauge consumer thread running...");
			
			try {
				Gauge g = m_inQueue.take();
				System.out.println("+"+g.m_Positive+", -"+g.m_Negative+" ("+g.m_sessionAverage+")");
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
