/**
 * Aggregate.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * public class Aggregate implements Runnable
 */
public class Aggregate implements Runnable {
	
	// incoming queue of tweets
	protected BlockingQueue<Tweet> m_inQueue = null;
	// outgoing queue of popular tweets
	protected BlockingQueue<Tweet> m_outPopularQueue = null;
	// outgoing queue of gauge values
	protected BlockingQueue<Gauge> m_outGauge = null;
	// internal queue of latest tweets (tweet wave)
	protected CircularFifoBuffer m_tweetWaveQueue = new CircularFifoBuffer(10);
	// total number of tweets processed
	protected int m_tweetCount = 0;
	// total positive value
	protected int m_Positive = 0;
	// total negative value
	protected int m_Negative = 0;
	
	/**
	 * Constructor
	 * @param inQueue (BlockingQueue<Tweet>)
	 * @param outPopularQueue (BlockingQueue<Tweet>)
	 */
	public Aggregate(	BlockingQueue<Tweet> inQueue, 
						BlockingQueue<Tweet> outPopularQueue,
						BlockingQueue<Gauge> outGauge ) {
		m_inQueue = inQueue;
		m_outPopularQueue = outPopularQueue;
		m_outGauge = outGauge;
	}
	
	/**
	 * private void saveTweet
	 * @param t (Tweet)
	 */
	private void saveTweet(Tweet t) {
		m_tweetWaveQueue.add(t);
	}

	/**
	 * public void run
	 */
	public void run() {
		// loop while the thread isn't interrupted
		while(!Thread.currentThread().isInterrupted()) {
			
			System.out.println("aggregator thread running...");
			
			try {
				// get from prev module
				Tweet t = m_inQueue.take();
				// increment our tweet counter
				m_tweetCount++;
				// store in tweet wave (removing oldest if necessary)
				saveTweet(t);
				// check for popular tweets
				if(t.weight > 1000) {
					m_outPopularQueue.put(t);
				}
				
				System.out.println(m_tweetWaveQueue.size());
				
				// once our tweet wave is full, get aggregating
				if(m_tweetWaveQueue.size() == 10) {
					// calculate gauge values
					Gauge g = new Gauge(m_tweetWaveQueue, m_Positive, m_Negative, m_tweetCount);
					
					// update positive and negative session totals
					m_Positive += g.m_Positive;
					m_Negative += g.m_Negative;
					
					// send the gauge values out
					m_outGauge.put(g);
				}
				
			} catch (InterruptedException e) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
			
		}
	}
}
