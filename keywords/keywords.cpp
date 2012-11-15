#include "keywords.h"

namespace keywords {

	void load(const char *filename, vector<string> &keywords)
	{
		ifstream fin;
		string str;

		fin.open(filename, fstream::in);

		// Check the file opened successfully.
		if ( ! fin.is_open()) 
		{
			ERROR_LOG << "can't find keywords file\n";
	    }

		while(getline(fin, str))
		{
			keywords.push_back(str);
		}

		fin.close();
	}

	void load_conservative(vector<string> &keywords)
	{
		load("keywords/conservative.txt", keywords);
	}

	void load_liberal(vector<string> &keywords)
	{
		load("keywords/liberal.txt", keywords);
	}

}