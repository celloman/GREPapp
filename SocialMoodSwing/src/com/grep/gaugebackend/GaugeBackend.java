package com.grep.gaugebackend;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GaugeBackend {
	
	static protected Thread m_getterThread;
	static protected Thread m_weighterThread;
	static protected Thread m_sentimenterThread;
	static protected Thread m_printerThread;

	public static void start() {
		
		BlockingQueue<Tweet> fetch_queue = new ArrayBlockingQueue<Tweet>(100);
		BlockingQueue<Tweet> weight_queue = new ArrayBlockingQueue<Tweet>(100);
		BlockingQueue<Tweet> sentiment_queue = new ArrayBlockingQueue<Tweet>(100);
		
		String[] keywords = {"obama", "clinton", "politics", "administration", "liberal", "conservative"};
		
		GetTweets getter = new GetTweets(fetch_queue, keywords);
		GetWeight weighter = new GetWeight(fetch_queue, weight_queue, keywords);
		GetSentiment sentimenter = new GetSentiment(weight_queue, sentiment_queue, keywords);
		StatusPrinter printer = new StatusPrinter(sentiment_queue);
		
		m_getterThread = new Thread(getter);
		m_weighterThread = new Thread(weighter);
		m_sentimenterThread = new Thread(sentimenter);
		m_printerThread = new Thread(printer);
		
		m_getterThread.start();
		m_weighterThread.start();
		m_sentimenterThread.start();
		m_printerThread.start();
	}
	
	public static void stop() {
		System.out.println("Trying to stop the threads...");
		try {
			m_getterThread.interrupt();
			m_weighterThread.interrupt();
			m_sentimenterThread.interrupt();
			m_printerThread.interrupt();
			
			m_getterThread.join();
			m_weighterThread.join();
			m_sentimenterThread.join();
			m_printerThread.join();
		} catch (InterruptedException ex) {
			Logger.getLogger(GaugeBackend.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}

