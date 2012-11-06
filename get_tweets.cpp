/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* get_tweets.cpp
*
******************************************************/

#include <vector>

#include "twitter_stream/twitter_stream.h"
#include "tweet/tweet.h"
#include "keywords/keywords.h"
#include "config/config.h"

using namespace std;

void callback(tweet t)
{
	t.print(tweet::ID | tweet::ORIGINAL_ID | tweet::TEXT | tweet::FOLLOWERS | tweet::RETWEETS | tweet::IS_RETWEET);
}

int main(int argc, char **argv)
{
	INFO_LOG << "starting get_tweets process\n";

    vector<string> keywords; // will contain all political keywords

    keywords::load_liberal(keywords);
    keywords::load_conservative(keywords);

	twitter_stream ts = twitter_stream(&callback, keywords);
	
	ts.start();

	return 0;
}
