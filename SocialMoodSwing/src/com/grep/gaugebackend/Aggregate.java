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
	protected BlockingQueue<WebToast> m_webToasts = null;
	// outgoing queue of gauge values
	protected BlockingQueue<Gauge> m_outGauge = null;
	// internal queue of latest tweets (tweet wave)
	protected CircularFifoBuffer m_tweetWaveQueue = new CircularFifoBuffer(500);
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
						BlockingQueue<WebToast> outWebToasts,
						BlockingQueue<Gauge> outGauge) {
		m_inQueue = inQueue;
		m_webToasts = outWebToasts;
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
		Boolean sizeReset = false;
		int tweetFrequency = 0;
		long beginTime = System.currentTimeMillis();
		
		// loop while the thread isn't interrupted
		while(!Thread.currentThread().isInterrupted()) {
			
			if(!sizeReset){
				long current = System.currentTimeMillis();
				if(current < beginTime + 30000)
					tweetFrequency++;
				else{
					CircularFifoBuffer tmp = m_tweetWaveQueue;
					if(tweetFrequency == 0) tweetFrequency = 1;
					System.out.println(String.format("frequency: %s", tweetFrequency));
					m_tweetWaveQueue = new CircularFifoBuffer(tweetFrequency);
					m_tweetWaveQueue.addAll(tmp);
					tmp = null;
					sizeReset = true;
					if(tweetFrequency <= 6)
						m_webToasts.offer(new WebToast("warning", "We aren't finding many tweets that contain the keywords you entered. You might want to add more keywords.", "Warning", 0, 0, 0));
				}
			}
			
			//System.out.println(String.format("aggregate thread: (%d, %d)", this.m_inQueue.size(), this.m_outGauge.size()));
			
			try {
				// get from prev module
				Tweet t = m_inQueue.take();
				// increment our tweet counter
				m_tweetCount++;
				// store in tweet wave (removing oldest if necessary)
				saveTweet(t);
				// check for 'sentimental' tweets
				if(t.sentiment != 0) {
					// use offer(), not put() so that it won't block, because this
					// isn't exactly mission critical
					m_webToasts.offer(new WebToast("info", t.displayText, t.user, t.followers, (int)t.retweets, t.sentiment));
				}
				
				// once we get tweets start aggregating
				if(m_tweetWaveQueue.size() > 0) {
					// calculate gauge values
					Gauge g = new Gauge(m_tweetWaveQueue, m_Positive, m_Negative, m_tweetCount);
					
					// update positive and negative session totals
					m_Positive += g.m_Positive;
					m_Negative += g.m_Negative;
					
					// send the gauge values out
					m_outGauge.offer(g);
				}
				
			} catch (InterruptedException e) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
			
		}
	}
}
