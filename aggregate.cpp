/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* aggregate.cpp
*
******************************************************/

#include <iostream>
#include <fstream>
#include <stdio.h>
#include <ctime>
#include <deque>

#include "tweet/tweet.h"
#include "config/config.h"

using namespace std;

int main(int argc, char **argv)
{
	INFO_LOG << "starting aggregate process\n";

	deque<tweet> tweets;
	string line = "";
	double liberal_pos, conservative_pos, liberal_neg, conservative_neg, liberal_gauge, conservative_gauge;
	double liberal_gauge_average = -1;
	int tweet_count = 0;
	int avg_update_count = 0;

	time_t raw_time, last_time = 0;
	int hours;

	char ascii_time[9]; // of the form "hh:mm:ss"
	char ascii_date_csv_name[19]; // of the form "csv/mm-dd-yyyy.csv\0"
	char ascii_daily_stat_txt_name[27]; // of the form "daily_stats/mm-dd-yyyy.txt\0"

	FILE *csv_out, *csv_check, *avg_save_file;

	while(getline(cin, line))
	{
		liberal_pos = 0;
		conservative_pos = 0;
		liberal_neg = 0;
		conservative_neg = 0;

		tweet_count++;
		tweet t = tweet(line.c_str());
		
		if(tweets.size() >= TWEET_CAP)
		{
			tweets.pop_back();
		}

		tweets.push_front(t);

		for(int i = 0; i < tweets.size(); i++)
		{
			if(tweets[i].m_liberal > 0)
				liberal_pos += tweets[i].m_liberal;
			else
				liberal_neg += tweets[i].m_liberal;

			if(tweets[i].m_conservative > 0)
				conservative_pos += tweets[i].m_conservative;
			else
				conservative_neg += tweets[i].m_conservative;
		}

		// get current time (UTC)
		time(&raw_time);

		// add (or subtract) offset
		raw_time += 3600 * TIME_ZONE_OFFSET;

		// calculate gauge values
		liberal_gauge = (liberal_pos - conservative_neg) * 100 / (liberal_pos + conservative_pos - liberal_neg - conservative_neg);
		conservative_gauge = 100.0 - liberal_gauge;

		if(liberal_gauge > 100 || liberal_gauge < 0 || liberal_gauge + conservative_gauge != 100)
		{
			ERROR_LOG << "bad gauge calculation: (liberal gauge value = " << liberal_gauge << ")\n";
		}
		else
		{
			printf("{\"gauge\":%.0f, \"liberal\":%.0f, \"conservative\":%.0f, \"tweets\":%d, \"time\":%ld, \"daily_liberal\":\"%.2f\", \"daily_conservative\":\"%.2f\"}\n", 
				liberal_gauge,
				liberal_pos + liberal_neg,
				conservative_pos + conservative_neg,
				tweet_count,
				raw_time * 1000, // output page needs this in milliseconds
				liberal_gauge_average,
				100 - liberal_gauge_average
			);

			fflush(stdout);
		}

		// only every UPDATE_AVG_INTERVAL seconds, and we've reached the TWEET_CAP
		if(raw_time >= last_time + UPDATE_AVG_INTERVAL && tweet_count >= TWEET_CAP)
		{
			last_time = raw_time;

			// output to csv only after we reach the tweet cap
			if(CSV_OUTPUT)
			{
				strftime(ascii_date_csv_name, 19, "csv/%m-%d-%Y.csv\0", gmtime(&raw_time));

				// see if this file exists yet (open for reading)
				csv_check = fopen(ascii_date_csv_name, "r");

				// open for writing
				csv_out = fopen(ascii_date_csv_name, "a");

				if(csv_out == NULL)
				{
					ERROR_LOG << "couldn't open csv file for writing\n";
				}
				else
				{
					// if it doesn't exist yet, write the first row with column headings
					if(csv_check == NULL)
					{
						INFO_LOG << "starting new csv file\n";
						fprintf(csv_out, 
							"\"time\", "
							"\"liberal percentage\", "
							"\"conservative percentage\", "
							"\"liberal positive\", "
							"\"liberal negative\", "
							"\"conservative positive\", "
							"\"conservative negative\"\n"
						);
					}
					else
					{
						fclose(csv_check);
					}

					strftime(ascii_time, 9, "%H:%M:%S\0", gmtime(&raw_time));

					fprintf(csv_out, "\"%s\", \"%f\", \"%f\", \"%f\", \"%f\", \"%f\", \"%f\"\n",
						ascii_time,
						liberal_gauge,
						conservative_gauge,
						liberal_pos,
						liberal_neg,
						conservative_pos,
						conservative_neg
					);

					fclose(csv_out);
				}
			}

			// calculate daily average and save
			avg_update_count++;

			strftime(ascii_daily_stat_txt_name, 27, "daily_stats/%m-%d-%Y.txt\0", gmtime(&raw_time));

			FILE *avg_save_file_check = fopen(ascii_daily_stat_txt_name, "r");

			if(avg_save_file_check != NULL)
			{
				int saved_avg_count, saved_tweet_count;

				if(!fscanf(avg_save_file_check, "%f", (float*)&liberal_gauge_average))
					ERROR_LOG << "could not read saved liberal gauge average\n";
				if(!fscanf(avg_save_file_check, "%d", &saved_avg_count))
					ERROR_LOG << "could not read saved average save count\n";

				DEBUG_LOG << liberal_gauge_average << "\n\n";

				// we are recovering
				if(saved_avg_count != avg_update_count - 1)
				{
					INFO_LOG << "recovering daily average and count\n";
					DEBUG_LOG << "recovering daily average and count\n";
					avg_update_count = saved_avg_count;
					if(!fscanf(avg_save_file_check, "%d", &saved_tweet_count))
						ERROR_LOG << "could not read saved tweet count\n";
					tweet_count += saved_tweet_count;
				}

				fclose(avg_save_file_check);

				liberal_gauge_average = ((liberal_gauge_average * (avg_update_count - 1)) + liberal_gauge) / avg_update_count;
			}
			else
			{
				liberal_gauge_average = liberal_gauge;
				avg_update_count = 1;
				tweet_count = 1;
			}

			avg_save_file = fopen(ascii_daily_stat_txt_name, "w");

			if(avg_save_file == NULL)
			{
				ERROR_LOG << "couldn't open daily average file for writing\n";
			}
			else
			{
				INFO_LOG << "writing to daily average file\n";
				fprintf(avg_save_file, "%f\n%d\n%d\n", liberal_gauge_average, avg_update_count, tweet_count);
				fclose(avg_save_file);
			}

		}

	}

	return 0;
}
