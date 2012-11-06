#include "tweet.h"

// constructor (takes json data)
tweet::tweet(const char *json_data)
{
	Json::Value root;
	Json::Reader reader;
	if(json_data == NULL || !reader.parse(json_data, root))
	{
		ERROR_LOG << "failed to decode tweet JSON!\n" << reader.getFormattedErrorMessages() << json_data << "\n\n";
		return;
	}
	else
	{
		m_id = root.get("id", 0).asInt64();
		m_text = filter(root.get("text", "").asString());

		// is this json straight from twitter? (ie does it have the created_at field?)
		if(root.get("created_at", "").asString().length() > 0)
		{
			m_followers = root["user"].get("followers_count", 0).asInt();
			m_retweets = root.get("retweet_count", 0).asInt();
			m_is_retweet = !root["retweeted_status"].empty();
			if(m_is_retweet)
			{
				m_original_id = root["retweeted_status"].get("id", 0).asInt64();
			}
			else
			{
				m_original_id = 0;
			}
		}
		else // internal json
		{
			m_followers = root.get("followers", 0).asInt();
			m_retweets = root.get("retweets", 0).asInt();
			m_weight = root.get("weight", 0).asInt();
			m_conservative = root.get("conservative", 0).asDouble();
			m_liberal = root.get("liberal", 0).asDouble();
			m_is_retweet = root.get("is_retweet", false).asBool();
			m_original_id = root.get("original_id", 0).asInt64();
		}
	}
}

// convert a string to lowercase, replace newlines with spaces,
// and replace single quotes with double quotes
string tweet::filter(string text)
{
	for(int i = 0; i < text.length(); ++i)
	{
		text[i] = tolower(text[i]);
		if(text[i] == '\n') text[i] = ' ';
		if(text[i] == '"') text[i] = '\'';
	}

	return text;
}

// print the json of the tweet
void tweet::print(unsigned int fields)
{
	cout << "{";

	cout << "\"id\":" << m_id;

	if(fields & TEXT)
		cout << ",\"text\":\"" << m_text << "\"";
	if(fields & LIBERAL)
		cout << ",\"liberal\":" << m_liberal;
	if(fields & CONSERVATIVE)
		cout << ",\"conservative\":" << m_conservative;
	if(fields & WEIGHT)
		cout << ",\"weight\":" << m_weight;
	if(fields & FOLLOWERS)
		cout << ",\"followers\":" << m_followers;
	if(fields & RETWEETS)
		cout << ",\"retweets\":" << m_retweets;
	if(fields & IS_RETWEET)
		cout << ",\"is_retweet\":" << m_is_retweet;
	if(fields & ORIGINAL_ID)
		cout << ",\"original_id\":" << m_original_id;

	cout << "}\n" << flush;
}