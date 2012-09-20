#include <cstdlib>
#include <iostream>
#include <sstream>
#include <cstring>
#include <curl/curl.h>
#include <json/json.h>

#include "twitter_stream.h"

using namespace std;

string buffer;

double str_to_double(string x){
	stringstream s;
	s << x;
	double res;
	s >> res;
	return res;
}

string to_lowercase(string text) //convert a string to lowercase
{
	for(int i = 0; i < text.length(); ++i)
		text[i] = tolower(text[i]);

	return text;
}

size_t write_data(char *data, size_t size, size_t nmemb, void *usrdata)
{
	buffer = "";
	buffer.append((char*) data, size * nmemb);
	return size * nmemb;
}

double get_sentiment(string text, string subject)
{
	CURL *curl;
	CURLcode res;

	curl = curl_easy_init();

	if(curl)
	{
		string url = "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey=3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab&outputMode=json&text=";
		url += curl_easy_escape(curl, text.c_str(), text.length());
		url += "&target=";
		url += subject;

		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
		// Perform the request, res will get the return code
		res = curl_easy_perform(curl);
		// Check for errors
		if(res != CURLE_OK)
			fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
		else
		{
			Json::Value root;
			Json::Reader reader;

			if(!reader.parse(buffer.c_str(), root) && root["status"].asString() == "ERROR")
				cout << "Sentiment JSON fail!\n";
			else
			{
				curl_easy_cleanup(curl);
				return str_to_double(root["docSentiment"]["score"].asString()) + 1;
			}
		}

		curl_easy_cleanup(curl);
	}
	return 1;
}

void callback(tweet t)
{
	static double republican = 0, democrat = 0;

	double sentiment;

	cout << t.m_text << "\n";

	//TODO: once we add more keywords we may want to move this functionality to a separate function
	//search the lowercase tweet for the following strings
	if (string::npos != t.m_text.find("obama"))
	{
		sentiment = get_sentiment(t.m_text, "obama");
		democrat += sentiment;
	}
	if (string::npos != t.m_text.find("romney"))
	{
		sentiment = get_sentiment(t.m_text, "romney");
		republican += sentiment;
	}

	cout << "Romney: " << republican << ", Obama: " << democrat << "\n";
}

int main(int argc, char **argv)
{

	twitter_stream ts = twitter_stream(&callback);
	ts.start();

	return 0;
}
