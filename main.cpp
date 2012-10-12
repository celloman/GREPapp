/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* main.cpp
*
******************************************************/

#include <iostream>
#include <fstream>
#include <vector>
#include <string>


#include "twitter_stream/twitter_stream.h"
#include "sentiment/sentiment.h"

ifstream l_File;
ifstream c_File;

using namespace std;

void load_c_vector(vector<string> c_keywords)
{
	string str;

	c_File.open("C:\\conservative.txt");
    // Check the file opened successfully.
    if ( ! c_File.is_open()) 
	{
       // cout << "Unable to open input file." << endl;
        //cout << "Press enter to continue...";
        //cin >> out;
       // exit(1);
    }

	while(getline(c_File, str))
	{
		//cout << str << endl;
		c_keywords.push_back(str);
	}
	c_File.close();
}

void load_l_vector(vector<string> l_keywords)
{
	string str;

	l_File.open("C:\\liberal.txt");
    // Check the file opened successfully.
    if ( ! l_File.is_open()) 
	{
       // cout << "Unable to open input file." << endl;
        //cout << "Press enter to continue...";
        //cin >> out;
        //exit(1);
    }

	while(getline(l_File, str))
	{
		//cout << str << endl;
		l_keywords.push_back(str);
	}
	l_File.close();
}

void callback(tweet t, vector<string> c_keywords, vector<string> l_keywords)
{
	static double liberal_bad = 0, liberal_good = 0, conservative_bad = 0, conservative_good = 0;
	double sentiment;
	static int tweets = 0;
	tweets++;

	//cout << t.m_text.substr(0, 15) << "... \n";

	//l_keywords is the vector for liberal
	for (int i = 0; i < l_keywords.size(); i++)
	{
		if (string::npos != t.m_text.find(l_keywords[i]))
		{
			sentiment = sentiment::get(t.m_text, l_keywords[i]);
			if(sentiment > 0)
				liberal_good += sentiment;
			else
				liberal_bad -= sentiment;
		}
	}
	//c_keywords is the vector for conservative
	for (int i = 0; i < c_keywords.size(); i++)
	{	
		if (string::npos != t.m_text.find(c_keywords[i]))
		{
			sentiment = sentiment::get(t.m_text, c_keywords[i]);
			if(sentiment > 0)
				conservative_good += sentiment;
			else
				conservative_bad -= sentiment;
		}
	}

	sentiment = (conservative_bad + liberal_good)*100 / (conservative_good + conservative_bad + liberal_good + liberal_bad);

	cout << sentiment << "\n";
	FILE *fout;
	fout = fopen("out.txt", "w");
	fprintf(fout, "{\"gauge\":%.0f, \"tweets\":%d}", sentiment, tweets);
	fclose(fout);
}

int main(int argc, char **argv)
{
    vector<string> c_keywords; //contains keywords corresponding to conservative party
    vector<string> l_keywords; //contains keywords corresponding to liberal party

  /*  c_keywords.push_back("romney");
    c_keywords.push_back("conservative"); //TODO: decide for sure if we want this keyword, it is an adjective
    c_keywords.push_back("conservatives");
    c_keywords.push_back("republican");
    c_keywords.push_back("republicans");
	c_keywords.push_back("right wing");
	c_keywords.push_back("right-wing");
	c_keywords.push_back("rightwing");
	c_keywords.push_back("gop");

	l_keywords.push_back("obama");
	l_keywords.push_back("liberal"); //TODO: decide for sure if we want this keyword, it is an adjective
	l_keywords.push_back("liberals");
	l_keywords.push_back("democrat");
	l_keywords.push_back("democrats");
	l_keywords.push_back("left wing");
	l_keywords.push_back("left-wing");
	l_keywords.push_back("leftwing");
	l_keywords.push_back("obamacare");
	*/

	twitter_stream ts = twitter_stream(&callback, c_keywords, l_keywords);
	
	ts.start();

	return 0;
}
