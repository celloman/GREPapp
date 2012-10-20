#ifndef TWEET_H
#define TWEET_H

#include <iostream>
#include <string>
#include <stdlib.h>
#include <jsoncpp/json/json.h>

using namespace std;

class tweet {
private:
	string filter(string text);
public:
	string m_text;
	double m_liberal_sentiment;
	double m_conservative_sentiment;
	int m_ranking;

	tweet(const char *json_data);
	void print();
};

#endif