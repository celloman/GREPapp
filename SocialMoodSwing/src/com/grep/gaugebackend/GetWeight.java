/**
 * GetWeight.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * public class GetWeight implements Runnable
 */
public class GetWeight implements Runnable {
	
	// incoming tweet queue
	protected BlockingQueue<Tweet> m_inQueue = null;
	// outgoing tweet queue
	protected BlockingQueue<Tweet> m_outQueue = null;
	// keywords that we are searching for
	protected String[] m_Keywords = null;
	// queue to hold previously seen tweet ids
	protected CircularFifoBuffer m_seenIDs = new CircularFifoBuffer(100);
	
	/**
	 * Constructor
	 * @param in_queue (BlockingQueue<Tweet>)
	 * @param out_queue (BlockingQueue<Tweet>)
	 * @param keywords (String[])
	 */
	public GetWeight(BlockingQueue<Tweet> in_queue, BlockingQueue<Tweet> out_queue, String[] keywords) {
		m_inQueue = in_queue;
		m_outQueue = out_queue;
		m_Keywords = keywords;
		
		// get the longest keywords at the front
		Arrays.sort(m_Keywords, new Comparator<String>() {
			public int compare(String s1, String s2) {
				if(s1.length() > s2.length())
					return -1;
				else if(s1.length() < s2.length())
					return 1;
				else
					return 0;
			}
		});
		
		for(String keyword: m_Keywords)
			System.out.println(keyword);
	}
        
	/**
	 * private void saveID
	 * @param id (long)
	 */
	private void saveID(long id) {
		m_seenIDs.add(id);
	}

	/**
	 * private int getKeyword
	 * @param t (Tweet)
	 * @return (int) number of keywords in tweet text
	 */
	private int getKeyword(Tweet t) {
		int count = 0;
		// loop through the m_Keywords
		for(int i = 0; i < m_Keywords.length; i++) {
			if(t.text.contains(m_Keywords[i])) {
				// count matches
				count++;
				// save the match
				t.keyword = m_Keywords[i];
				// break
				break;
			}
		}
		return count;
	}
	
	/**
	 * public void run
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			
			//System.out.println(String.format("weighting thread: (%d, %d)", this.m_inQueue.size(), this.m_outQueue.size()));
			
			try {
				// get from prev module
				Tweet t = m_inQueue.take();
				
				// make sure we only get english
				if(!t.lang.equals("en"))
					continue;
				
				// check for repeated tweets
				if(m_seenIDs.contains(t.id))
					continue;
				
				// check to make sure it just has one keyword, and store that keyword in the tweet
				if(getKeyword(t) == 0)
					continue;
				
				// weighting algorithm (believe it or not, this is equivalent)
				if(t.isRetweet && m_seenIDs.contains(t.originalID))
					t.retweets--;
					
				// save this tweet id
				saveID(t.id);
				
				// calculate the weight
				t.weight = t.followers + (208 * t.retweets);

				// send to the next module
				m_outQueue.offer(t);
				
			} catch (InterruptedException e) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
			
		}
	}

}
