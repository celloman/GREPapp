/**
 * GaugeBackend.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;
import java.util.concurrent.*;

/**
 * public class GaugeBackend
 */
public class GaugeBackend {
	
	// threads for each module
	static protected Thread m_getterThread;
	static protected Thread m_weighterThread;
	static protected Thread m_sentimenterThread;
	static protected Thread m_aggregatorThread;
	
	// queues
	static protected BlockingQueue<Tweet> m_fetchQueue;
	static protected BlockingQueue<Tweet> m_weightQueue;
	static protected BlockingQueue<Tweet> m_sentimentQueue;

	public static void start(String[] keywords, String accessToken, String accessTokenSecret, BlockingQueue<WebToast> webToasts, BlockingQueue<Gauge> gaugeValues) {
		// interprocess communication structures
		m_fetchQueue = new ArrayBlockingQueue<Tweet>(5);
		m_weightQueue = new ArrayBlockingQueue<Tweet>(5);
		m_sentimentQueue = new ArrayBlockingQueue<Tweet>(5);
		
		// convert the keywords to lower case
		for(String keyword : keywords)
			keyword = keyword.toLowerCase();
		
		// create the threads
		GetTweets getter = new GetTweets(m_fetchQueue, webToasts, keywords, accessToken, accessTokenSecret);
		GetWeight weighter = new GetWeight(m_fetchQueue, m_weightQueue, keywords);
		GetSentiment sentimenter = new GetSentiment(m_weightQueue, m_sentimentQueue, webToasts);
		Aggregate aggregator = new Aggregate(m_sentimentQueue, webToasts, gaugeValues);
		
		m_getterThread = new Thread(getter);
		m_weighterThread = new Thread(weighter);
		m_sentimenterThread = new Thread(sentimenter);
		m_aggregatorThread = new Thread(aggregator);
		
		// start the threads
		m_getterThread.start();
		m_weighterThread.start();
		m_sentimenterThread.start();
		m_aggregatorThread.start();
	}
	
	public static void stop() {
		System.out.println("trying to stop the threads...");
		
		try {
			// send interrupt signals
			m_getterThread.interrupt();
			m_weighterThread.interrupt();
			m_sentimenterThread.interrupt();
			m_aggregatorThread.interrupt();
			// wait for threads to finish
			m_getterThread.join();
			m_weighterThread.join();
			m_sentimenterThread.join();
			m_aggregatorThread.join();
			
			m_fetchQueue = null;
			m_weightQueue = null;
			m_sentimentQueue = null;
			
		} catch (InterruptedException ex) {
			System.out.println("something went wrong while killing the threads");
		}
		
		System.out.println("successfully stopped gauge threads!!");
	}

}
