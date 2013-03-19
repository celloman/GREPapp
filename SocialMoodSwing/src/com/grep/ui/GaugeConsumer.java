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
	// gauge interface object
	protected GaugeInterface m_interface = null;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 */
	public GaugeConsumer(BlockingQueue<Gauge> inQueue, GaugeInterface iterf) {
		m_inQueue = inQueue;
		m_interface = iterf;
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
				m_interface.setValue((int)(g.m_Positive*100)/(g.m_Positive + g.m_Negative));
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
