/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* get_sentiment.cpp
*
******************************************************/

#include <vector>
#include <string>

#include "sentiment/sentiment.h"
#include "tweet/tweet.h"
#include "keywords/keywords.h"
#include "config/config.h"

using namespace std;

int main(int argc, char **argv)
{
	INFO_LOG << "starting get_sentiment process\n";

	vector<string> c_keywords, l_keywords;

	keywords::load_conservative(c_keywords);
	keywords::load_liberal(l_keywords);

	string line = "";
	sentiment s = sentiment();

	while(getline(cin, line))
	{
		tweet t = tweet(line.c_str());

		//l_keywords is the vector for liberal
		for (int i = 0; i < l_keywords.size(); i++)
		{
			if (string::npos != t.m_text.find(l_keywords[i]))
			{
				t.m_liberal = s.get(t.m_text, l_keywords[i]) * t.m_weight;
				DEBUG_LOG << t.m_liberal << "\n";
			}
		}
		//c_keywords is the vector for conservative
		for (int i = 0; i < c_keywords.size(); i++)
		{	
			if (string::npos != t.m_text.find(c_keywords[i]))
			{
				t.m_conservative = s.get(t.m_text, c_keywords[i]) * t.m_weight;
				DEBUG_LOG << t.m_conservative << "\n";
			}
		}

		t.print(tweet::LIBERAL | tweet::CONSERVATIVE);
	}

	return 0;
}
