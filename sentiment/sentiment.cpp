#include "sentiment.h"

size_t alchemy_write_function(char *data, size_t size, size_t nmemb, void *usrdata)
{
	if(data != NULL)
	{
		sentiment *s = (sentiment*)(usrdata);
		s->m_response_buffer = "";
		s->m_response_buffer.append((char*) data, size * nmemb);
	}
	else
	{
		cerr << "null json string returned from alchemy...\n";
	}
	
	return size * nmemb;
}

CURL *sentiment::m_curl = NULL;
string sentiment::m_response_buffer = "";

double sentiment::str_to_double(string x)
{
	stringstream s;
	s << x;
	double res;
	s >> res;
	return res;
}

double sentiment::get(string text, string subject)
{
	CURLcode res;

	m_curl = curl_easy_init();

	if(m_curl)
	{
		string url = "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey=3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab&outputMode=json&text=";
		url += curl_easy_escape(m_curl, text.c_str(), text.length());
		url += "&target=";
		url += subject;

		curl_easy_setopt(m_curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(m_curl, CURLOPT_WRITEFUNCTION, alchemy_write_function);
		// Perform the request, res will get the return code
		res = curl_easy_perform(m_curl);
		// Check for errors
		if(res != CURLE_OK)
			fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
		else
		{
			Json::Value root;
			Json::Reader reader;

			if(!reader.parse(m_response_buffer.c_str(), root) && root["status"].asString() == "ERROR")
				cout << "Sentiment JSON fail!\n";
			else
			{
				curl_easy_cleanup(m_curl);
				return str_to_double(root["docSentiment"]["score"].asString());
			}
		}

		curl_easy_cleanup(m_curl);
	}
	return 0;
}