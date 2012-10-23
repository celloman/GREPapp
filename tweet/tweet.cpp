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

		// is this json straight from twitter? (ie does it have the created_at field?)
		if(root.get("created_at", "").asString().length() > 0)
		{
			m_text = filter(root.get("text", "").asString());
			m_followers = root["user"].get("followers_count", 0).asInt();
			m_retweets = root.get("retweet_count", 0).asInt();
			m_is_retweet = !root["retweeted_status"].empty();
		}
		else // internal json
		{
			m_text = root.get("text", "").asString();
			m_followers = root.get("followers", 0).asInt();
			m_retweets = root.get("retweets", 0).asInt();
			m_ranking = root.get("ranking", 0).asInt();
			m_conservative = root.get("conservative", 0).asDouble();
			m_liberal = root.get("liberal", 0).asDouble();
			m_is_retweet = root.get("is_retweet", false).asBool();
		}
	}
}

// convert a string to lowercase and replace newlines with spaces
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

void convert(int &out, Json::Value in);
void convert(double &out, Json::Value in);
void convert(bool &out, Json::Value in);

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
	if(fields & RANKING)
		cout << ",\"ranking\":" << m_ranking;
	if(fields & FOLLOWERS)
		cout << ",\"followers\":" << m_followers;
	if(fields & RETWEETS)
		cout << ",\"retweets\":" << m_retweets;
	if(fields & IS_RETWEET)
		cout << ",\"is_retweet\":" << m_is_retweet;

	cout << "}\n" << flush;
}