#include "tweet.h"

// constructor (takes json data)
tweet::tweet(const char *json_data)
{
	Json::Value root;
	Json::Reader reader;
	if(json_data == NULL || !reader.parse(json_data, root))
	{
		cerr << "failed to decode tweet JSON!\n";
		cerr << json_data << "\n";
		return;
	}
	else
	{
		m_text = filter(root["text"].asString());
		m_liberal_sentiment = atof(root["liberal_sentiment"].asString().c_str());
		m_conservative_sentiment = atof(root["conservative_sentiment"].asString().c_str());
		m_ranking = atoi(root["ranking"].asString().c_str());
	}
}

// convert a string to lowercase and replace newlines with spaces
string tweet::filter(string text)
{
	for(int i = 0; i < text.length(); ++i)
	{
		text[i] = tolower(text[i]);
		if(text[i] == '\n') text[i] = ' ';
	}

	return text;
}

// print the json of the tweet
void tweet::print()
{
	cout << "{\"text\":\"" << m_text << 
			"\",\"liberal_sentiment\":\"" << m_liberal_sentiment << 
			"\",\"conservative_sentiment\":\"" << m_conservative_sentiment << 
			"\",\"ranking\":\"" << m_ranking << 
			"\"}\n" << flush;
}