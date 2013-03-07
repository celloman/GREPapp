package com.grep.gaugebackend;

import java.util.concurrent.*;

public class VikingsPort {

	public static void go() {
		
		BlockingQueue<Tweet> fetch_queue = new ArrayBlockingQueue<Tweet>(100);
		BlockingQueue<Tweet> weight_queue = new ArrayBlockingQueue<Tweet>(100);
		//BlockingQueue<Tweet> sentiment_queue = new ArrayBlockingQueue<Tweet>(100);
		
		String[] keywords = {"obama", "clinton", "politics", "administration", "liberal", "conservative"};
		
		GetTweets getter = new GetTweets(fetch_queue, keywords);
		GetWeight weighter = new GetWeight(fetch_queue, weight_queue, keywords);
		//GetSentiment sentimenter = new GetSentiment(weight_queue, sentiment_queue, keywords);
		StatusPrinter printer = new StatusPrinter(weight_queue);
		
		new Thread(getter).start();
		new Thread(weighter).start();
		//new Thread(sentimenter).start();
		new Thread(printer).start();
		
	}

}

