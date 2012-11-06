/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* get_weight.cpp
*
******************************************************/

#include <stdint.h>
#include <deque>

#include "filter/filter.h"
#include "tweet/tweet.h"
#include "config/config.h"

using namespace std;


int main(int argc, char **argv)
{
	INFO_LOG << "starting get_weight process\n";

	deque<int64_t> tweet_ids;
	int weight;
	string line = "";

	while(getline(cin, line))
	{
		tweet t = tweet(line.c_str());
		
		// filtering: have we seen this tweet already?
		if(filter::in_deque(t.m_id, tweet_ids))
		{
			INFO_LOG << "repeated tweet: filtered out\n";
			continue;
		}
		// filtering: does it have exactly one keyword?
		if(!filter::has_one_keyword(t.m_text))
		{
			INFO_LOG << "bad number of keywords: filtered out\n";
			continue;
		}

		// weighting algorithm
		if(t.m_is_retweet)
		{
			if(filter::in_deque(t.m_original_id, tweet_ids))
			{
				if(t.m_retweets > 1)
				{
					t.m_retweets -= 1;
					filter::store_in_deque(t.m_id, tweet_ids);
				}
				else
				{
					t.m_followers -= AVG_FOLLOWERS;
				}
			}
			else
			{
				filter::store_in_deque(t.m_id, tweet_ids);
			}
		}
		else
		{
			filter::store_in_deque(t.m_id, tweet_ids);
		}

		t.m_weight = t.m_followers + (AVG_FOLLOWERS * t.m_retweets);

		t.print(tweet::TEXT | tweet::WEIGHT);
	}

	return 0;
}
