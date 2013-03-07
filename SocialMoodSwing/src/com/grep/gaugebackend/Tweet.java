package com.grep.gaugebackend;

import twitter4j.Status;

public class Tweet {
	
	// the id of the tweet
	long id;
	// if this is a retweet, the id of the original tweet
	long original_id;
	// the text of the tweet
	String text;
	// the keyword in the tweet
	String keyword;
	// the weight of the tweet
	long weight;
	// the sentiment of the tweet
	float sentiment;
	// the number of followers of the user who tweeted this
	int followers;
	// the number of retweets this tweet has
	long retweets;
	// whether or not this tweet is a retweet
	boolean is_retweet;
	// the language code of the user who tweeted this
	String lang;
	
	public Tweet(Status status)
	{
		// fill in the info from the 'Status'
		id = status.getId();
		retweets = (status.getRetweetCount() < 0) ? 0 : status.getRetweetCount();
		text = status.getText();
		is_retweet = status.isRetweet();
		lang = status.getUser().getLang();
		followers = status.getUser().getFollowersCount();
		keyword = "";
		
		// sentiment and weight will get filled in later
		sentiment = 0;
		weight = 0;
		
		// if this isn't a retweet, the original_id will be 0
		if(is_retweet)
			original_id = status.getRetweetedStatus().getId();
		else
			original_id = 0;
	}
	
	

}
