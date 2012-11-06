#include "filter.h"

namespace filter {

	bool has_one_keyword(string text)
	{
		static vector<string> keywords;
		if(keywords.size() == 0)
		{
		    keywords::load_liberal(keywords);
		    keywords::load_conservative(keywords);
		}

		int keyword_count = 0;

		for(int i = 0; i < keywords.size(); i++)
		{
			if(string::npos != text.find(keywords[i])) keyword_count++;
		}

		return (keyword_count == 1);
	}

	bool in_deque(int64_t id, deque<int64_t> &ids)
	{
		for(int i = 0; i < ids.size(); i++)
		{
			if(id == ids[i]) return true;
		}
		return false;
	}

	void store_in_deque(int64_t id, deque<int64_t> &ids)
	{
		if(ids.size() >= TWEET_CAP)
		{
			ids.pop_back();
		}

		ids.push_front(id);
	}

}