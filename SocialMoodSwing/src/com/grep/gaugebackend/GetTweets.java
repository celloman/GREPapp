package com.grep.gaugebackend;

import twitter4j.*;
import java.util.concurrent.*;
import twitter4j.conf.ConfigurationBuilder;

public class GetTweets implements Runnable {
	
	protected BlockingQueue<Tweet> queue = null;
	protected String[] keywords = null;
	
	public GetTweets(BlockingQueue<Tweet> queue, String[] keywords) {
		this.queue = queue;
		this.keywords = keywords;
	}
	
    public void run() {
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setUser("vikings383")
                .setPassword("383vikings");
    	
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        
        final class StatusListenerQueueing implements StatusListener {
        	
        	protected BlockingQueue<Tweet> queue = null;
        	
        	public StatusListenerQueueing(BlockingQueue<Tweet> queue) {
            	this.queue = queue;
            }
        	
            @Override
            public void onStatus(Status status) {
				System.out.println("getter thread running...");
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
            	System.out.println("error!!");
            	System.out.println(ex.toString());
                ex.printStackTrace();
            }
        };
        
        twitterStream.addListener(new StatusListenerQueueing(this.queue));
        twitterStream.filter(new FilterQuery(0, null, this.keywords));
    }
}