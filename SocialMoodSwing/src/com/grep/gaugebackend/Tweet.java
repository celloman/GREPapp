/**
 * Tweet.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.gaugebackend;

import twitter4j.Status;

/**
 * public class Tweet
 */
public class Tweet {
	
	// the id of the tweet
	long id;
	// if this is a retweet, the id of the original tweet
	long originalID;
	// the text of the tweet
	String text;
	// the keyword in the tweet
	String keyword;
	// the weight of the tweet
	long weight;
	// the sentiment of the tweet
	int sentiment;
	// the number of followers of the user who tweeted this
	int followers;
	// the number of retweets this tweet has
	long retweets;
	// whether or not this tweet is a retweet
	boolean isRetweet;
	// the language code of the user who tweeted this
	String lang;
	
	/**
	 * Copy Constructor
	 * @param another (Tweet)
	 */
	public Tweet(Tweet another) {
		id = another.id;
		retweets = another.retweets;
		text = another.text;
		isRetweet = another.isRetweet;
		lang = another.lang;
		followers = another.followers;
		keyword = another.keyword;
		sentiment = another.sentiment;
		weight = another.weight;
		originalID = another.originalID;
	}
	
	/**
	 * Constructor
	 * @param status (Status)
	 */
	public Tweet(Status status) {
		// fill in the info from the 'Status'
		id = status.getId();
		retweets = (status.getRetweetCount() < 0) ? 0 : status.getRetweetCount();
		text = status.getText();
		isRetweet = status.isRetweet();
		lang = status.getUser().getLang();
		followers = status.getUser().getFollowersCount();
		keyword = "";
		
		// sentiment and weight will get filled in later
		sentiment = 0;
		weight = 0;
		
		// if this isn't a retweet, the originalID will be 0
		originalID = isRetweet ? status.getRetweetedStatus().getId() : 0;
	}
	
	

}
