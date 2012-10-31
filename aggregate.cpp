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

#include <stdio.h>
#include <deque>

#include "tweet/tweet.h"
#include "logger/logger.h"

using namespace std;

int main(int argc, char **argv)
{
	INFO_LOG << "starting aggregate process\n";

	deque<tweet> tweets;
	string line = "";
	double liberal, conservative;
	int tweet_count = 0;

	while(getline(cin, line))
	{
		tweet_count++;
		tweet t = tweet(line.c_str());
		
		if(tweets.size() >= 100)
		{
			tweets.pop_back();
		}

		tweets.push_front(t);

		for(int i = 0; i < tweets.size(); i++)
		{
			liberal += tweets[i].m_liberal;
			conservative += tweets[i].m_conservative;
		}
			
		printf("{\"gauge\":%.0f,\"tweets\":%d}\n", liberal*100 / (liberal+conservative), tweet_count);
		fflush(stdout);
	}

	return 0;
}
