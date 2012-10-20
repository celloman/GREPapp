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

using namespace std;

int main(int argc, char **argv)
{

    vector<string> c_keywords, l_keywords;

    keywords::load_conservative(c_keywords);
    keywords::load_liberal(l_keywords);

	string line = "";
	double sentiment = 0;

	while(getline(cin, line))
	{
		tweet t = tweet(line.c_str());

		//l_keywords is the vector for liberal
		for (int i = 0; i < l_keywords.size(); i++)
		{
			if (string::npos != t.m_text.find(l_keywords[i]))
			{
				sentiment = sentiment::get(t.m_text, l_keywords[i]);
				if(sentiment > 0)
					t.m_liberal_sentiment += sentiment;
				else
					t.m_conservative_sentiment += sentiment;
			}
		}
		//c_keywords is the vector for conservative
		for (int i = 0; i < c_keywords.size(); i++)
		{	
			if (string::npos != t.m_text.find(c_keywords[i]))
			{
				sentiment = sentiment::get(t.m_text, c_keywords[i]);
				if(sentiment > 0)
					t.m_conservative_sentiment += sentiment;
				else
					t.m_liberal_sentiment += sentiment;
			}
		}

		t.print();
	}

	return 0;
}
