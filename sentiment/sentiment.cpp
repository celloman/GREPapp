/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* sentiment.cpp
*
******************************************************/

#include "sentiment.h"

bool sentiment::m_alchemy_available = !USE_SENTIMENT140;

size_t sentiment::alchemy_write_function(char *data, size_t size, size_t nmemb, void *usrdata)
{ 
	double *sentiment = (double*)usrdata;

	Json::Value root;
	Json::Reader reader;
	if(data == NULL || !reader.parse(data, root))
	{
		ERROR_LOG << "failed to decode alchemy JSON!\n" << reader.getFormattedErrorMessages() << data << "\n\n";
		*sentiment = 0;
	}
	else if(root["status"].asString() == "ERROR")
	{
		if(root["statusInfo"].asString() == "unsupported-text-language")
		{
			ERROR_LOG << "alchemy error: unsupported-text-language (" << root["language"].asString() << ")\n";
		}
		else if(root["statusInfo"].asString() == "daily-transaction-limit-exceeded")
		{
			ERROR_LOG << "alchemy error: " << root["statusInfo"].asString() << "\n";
			WEB_LOG("Uhoh...", "Looks like you hit the daily Alchemy API request limit.", "error");
		}
		else
		{
			ERROR_LOG << "alchemy error: " << root["statusInfo"].asString() << "\n";
		}

		*sentiment = 0;
	}
	else
	{
		INFO_LOG << "alchemy success\n";
		*sentiment = atof(root["docSentiment"]["score"].asString().c_str());
	}

	return size * nmemb;
}

size_t sentiment::sentiment140_write_function(char *data, size_t size, size_t nmemb, void *usrdata)
{ 
	double *sentiment = (double*)usrdata;

	Json::Value root;
	Json::Reader reader;
	if(data == NULL || !reader.parse(data, root))
	{
		ERROR_LOG << "failed to decode sentiment140 JSON!\n" << reader.getFormattedErrorMessages() << data << "\n\n";
		*sentiment = 0;
	}
	else
	{
		INFO_LOG << "sentiment140 success\n";
		*sentiment = root["results"]["polarity"].asDouble();
		*sentiment -= 2.0;
		*sentiment /= 2.0;
	}

	return size * nmemb;
}


sentiment::sentiment()
{
	m_curl = curl_multi_init();
	m_requests = 0;
}

sentiment::~sentiment()
{
	curl_multi_cleanup(m_curl);
}

double sentiment::get(string text, string subject)
{
	double sentiment;

	#if FAKE_SENTIMENT

	sentiment = (double) ((rand() % 200000) - 100000) / 100000.0;

	#else

	CURL* curl = curl_easy_init();

	if(curl)
	{
		string url = "";

		if(m_alchemy_available)
		{
			url += "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey="ALCHEMY_KEY"&outputMode=json&text=";
			url += curl_easy_escape(curl, text.c_str(), text.length());
			url += "&target=";
		}
		else // use sentiment140
		{
			url += "http://www.sentiment140.com/api/classify?&text=";
			url += curl_easy_escape(curl, text.c_str(), text.length());
			url += "&query=";
		}

		url += curl_easy_escape(curl, subject.c_str(), subject.length());	

		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &sentiment);

		if(m_alchemy_available)
		{
			curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, alchemy_write_function);
		}
		else
		{
			curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, sentiment140_write_function);
		}

		curl_multi_add_handle(m_curl, curl);
		curl_multi_perform(m_curl, &m_requests);
	}

	#endif

	return sentiment;
}