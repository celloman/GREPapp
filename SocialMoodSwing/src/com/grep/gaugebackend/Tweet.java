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
	public long id;
	// the username of the author
	public String user;
	// if this is a retweet, the id of the original tweet
	public long originalID;
	// the text of the tweet
	public String text;
	// the keyword in the tweet
	public String keyword;
	// the weight of the tweet
	public long weight;
	// the sentiment of the tweet
	public int sentiment;
	// the number of followers of the user who tweeted this
	public int followers;
	// the number of retweets this tweet has
	public long retweets;
	// whether or not this tweet is a retweet
	public boolean isRetweet;
	// the language code of the user who tweeted this
	public String lang;
	
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
		user = another.user;
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
		user = status.getUser().getScreenName();
		keyword = "";
		
		// sentiment and weight will get filled in later
		sentiment = 0;
		weight = 0;
		
		// if this isn't a retweet, the originalID will be 0
		originalID = isRetweet ? status.getRetweetedStatus().getId() : 0;
	}
	
	

}
