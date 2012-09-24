#include <iostream>

#include "twitter_stream/twitter_stream.h"
#include "sentiment/sentiment.h"

using namespace std;

void callback(tweet t)
{
	static double republican = 0, democrat = 0, sentiment;
	static int tweets = 0;
	tweets++;

	cout << t.m_text.substr(0, 15) << "...  ";

	if (string::npos != t.m_text.find("obama"))
	{
		sentiment = sentiment::get(t.m_text, "obama");
		democrat += sentiment;
	}
	if (string::npos != t.m_text.find("romney"))
	{
		sentiment = sentiment::get(t.m_text, "romney");
		republican += sentiment;
	}

	cout << "R: " << republican << ", O: " << democrat << " (" << tweets << ")\n";
}

int main(int argc, char **argv)
{
	twitter_stream ts = twitter_stream(&callback);
	ts.start();

	return 0;
}
