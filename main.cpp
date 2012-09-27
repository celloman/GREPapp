#include <iostream>
#include <vector>

#include "twitter_stream/twitter_stream.h"
#include "sentiment/sentiment.h"

using namespace std;

void callback(tweet t, vector<string> c_keywords, vector<string> l_keywords)
{
	// TODO use keywords passed as argument, not the hard-coded strings "obama" and "romney"

	static double republican = 0, democrat = 0, sentiment;
	static int tweets = 0;
	tweets++;

	cout << t.m_text.substr(0, 15) << "...  ";

	//l_keywords is the vector for liberal
	for (int i = 0; i < l_keywords.size(); i++)
	{
		if (string::npos != t.m_text.find(l_keywords[i]))
		{
			sentiment = sentiment::get(t.m_text, l_keywords[i]);
			democrat += sentiment;
		}
	}
	//c_keywords is the vector for conservative
	for (int i = 0; i < c_keywords.size(); i++)
	{	
		if (string::npos != t.m_text.find(c_keywords[i]))
		{
			sentiment = sentiment::get(t.m_text, c_keywords[i]);
			republican += sentiment;
		}
	}

	cout << "R: " << republican << ", D: " << democrat << " (" << tweets << ")\n";
}

int main(int argc, char **argv)
{
	vector<string> c_keywords;
	vector<string> l_keywords;

	c_keywords.push_back("romney");
	l_keywords.push_back("obama");

	twitter_stream ts = twitter_stream(&callback, c_keywords, l_keywords);
	
	ts.start();

	return 0;
}
