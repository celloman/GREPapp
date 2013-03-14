/**
 * Gauge.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * public class Gauge
 */
public class Gauge {
	
	// total positive value of this tweet wave
	public int m_Positive = 0;
	// total negative value of this tweet wave
	public int m_Negative = 0;
	// average for the entire analysis session
	public double m_sessionAverage = 0.0;
	// tweet count
	public int m_tweetCount = 0;
	
	/**
	 * Constructor
	 * @param tweetWave (Queue<Tweet>)
	 * @param prevPositive (int)
	 * @param prevNegative  (int)
	 */
	public Gauge(CircularFifoBuffer tweetWave, int prevPositive, int prevNegative, int tweetCount) {
		// TODO assert that tweetWave size is correct
		
		Tweet[] tweetWaveArray = (Tweet[])tweetWave.toArray(new Tweet[tweetWave.size()]);
		
		for(int i = 0; i < tweetWave.size(); i++) {
			if(tweetWaveArray[i].sentiment > 0) {
				m_Positive += tweetWaveArray[i].sentiment * tweetWaveArray[i].weight;
			}
			else if(tweetWaveArray[i].sentiment < 0) {
				m_Negative += tweetWaveArray[i].sentiment * tweetWaveArray[i].weight;
			}
			// note that neutral tweets have no effect (sentiment = 0)
		}
		
		// TODO assert that m_Positive and prevPositive are >= 0, and that
		// m_Negative and prevNegative are <= 0
		
		m_sessionAverage = ((m_Positive + prevPositive) * 1.0) /
					((m_Positive + prevPositive - m_Negative - prevNegative) * 1.0);
		
		// the absolute value of the session will always be greater than 0.5
		// if the average is more negative, it will get the negative percentage,
		// if more positive it will get the positive percentage.
		if(m_sessionAverage < 0.5) {
			m_sessionAverage  = m_sessionAverage - 1;
		}
		
		m_tweetCount = tweetCount;
	}
}
