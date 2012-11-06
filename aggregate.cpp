/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* aggregate.cpp
*
******************************************************/

#include <stdio.h>
#include <ctime>
#include <deque>

#include "tweet/tweet.h"
#include "config/config.h"

using namespace std;

int main(int argc, char **argv)
{
	INFO_LOG << "starting aggregate process\n";

	deque<tweet> tweets;
	string line = "";
	double liberal, conservative, liberal_gauge, conservative_gauge;
	int tweet_count = 0;

	while(getline(cin, line))
	{
		liberal = 0;
		conservative = 0;
		liberal_gauge = 0;
		conservative_gauge = 0;

		tweet_count++;
		tweet t = tweet(line.c_str());
		
		if(tweets.size() >= TWEET_CAP)
		{
			tweets.pop_back();
		}

		tweets.push_front(t);

		for(int i = 0; i < tweets.size(); i++)
		{
			if(tweets[i].m_liberal > 0)
				liberal_gauge += tweets[i].m_liberal;
			else
				conservative_gauge -= tweets[i].m_liberal;

			if(tweets[i].m_conservative > 0)
				conservative_gauge += tweets[i].m_conservative;
			else
				liberal_gauge -= tweets[i].m_conservative;

			liberal += tweets[i].m_liberal;
			conservative += tweets[i].m_conservative;
		}

		printf("{\"gauge\":%.0f, \"liberal\":%.0f, \"conservative\":%.0f, \"tweets\":%d, \"time\":%ld}\n", 
			liberal_gauge*100 / (liberal_gauge+conservative_gauge),
			liberal,
			conservative,
			tweet_count,
			time(NULL)
		);

		fflush(stdout);
	}

	return 0;
}
