/**
 * GetTweets.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;

import twitter4j.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.conf.ConfigurationBuilder;

/**
 * public class GetTweets implements Runnable
 */
public class GetTweets implements Runnable {
	
	// outgoing m_outQueue of tweets
	protected BlockingQueue<Tweet> m_outQueue = null;
	// m_Keywords to search for
	protected String[] m_Keywords = null;
	
	/**
	 * Constructor
	 * @param m_outQueue (BlockingQueue<Tweet>)
	 * @param m_Keywords (String[])
	 */
	public GetTweets(BlockingQueue<Tweet> queue, String[] keywords) {
		m_outQueue = queue;
		m_Keywords = keywords;
	}
	
	/**
	 * public void run
	 */
    public void run() {
        
		// login info
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setUser("greshamschlect")
                .setPassword("saxon93g");
    	
		// create the stream
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        
		// status listener for twitter4J streaming
        final class StatusListenerQueueing implements StatusListener {
        	
        	protected BlockingQueue<Tweet> queue = null;
        	
        	public StatusListenerQueueing(BlockingQueue<Tweet> queue) {
            	this.queue = queue;
            }
        	
            @Override
            public void onStatus(Status status) {
				//System.out.println(String.format("getter thread running... %d", this.queue.size()));
				
                try {
					this.queue.put(new Tweet(status));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
				Thread.currentThread().interrupt();
            	System.out.println(ex.toString());
            }
        };

		// start listening and queueing up outgoing tweets
        twitterStream.addListener(new StatusListenerQueueing(this.m_outQueue));
		// add the keyword filtering
        twitterStream.filter(new FilterQuery(0, null, this.m_Keywords));
		
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException ex) {
				// immediately reset interrupt flag
				Thread.currentThread().interrupt();
			}
		}
		
		twitterStream.cleanUp();
		twitterStream.shutdown();
    }
}
