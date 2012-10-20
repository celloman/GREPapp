#*********************************************************************************
# Team Vikings
# 9/19/2012
# CS383 Team Project
#
# Makefile 
#
# $@ -- refers to name of target
# $^ -- refers to name of all prerequisites with duplicates removed
# $< -- name of first prerequisite#
# @  -- preprending @ to a recipe hides the command during the make process 
# 
# rm -f -- removes the specified files, suppresses error message when
#	   file you are attempting to remove doesn't exist
#
#*********************************************************************************

CC 	= g++
CFLAGS = -I.                                            
LDFLAGS = -lcurl -ljsoncpp

all: get_tweets get_sentiment clean


get_tweets: get_tweets.o twitter_stream.o tweet.o keywords.o
	$(CC) -o $@ $^ $(LDFLAGS)

get_tweets.o: get_tweets.cpp
	$(CC) -c $(CFLAGS) $<

twitter_stream.o: twitter_stream/twitter_stream.cpp twitter_stream/twitter_stream.h
	$(CC) -c $(CFLAGS) $<


get_sentiment: get_sentiment.o sentiment.o tweet.o keywords.o
	$(CC) -o $@ $^ $(LDFLAGS)

get_sentiment.o: get_sentiment.cpp
	$(CC) -c $(CFLAGS) $<

sentiment.o: sentiment/sentiment.cpp sentiment/sentiment.h
	$(CC) -c $(CFLAGS) $<


tweet.o: tweet/tweet.cpp tweet/tweet.h
	$(CC) -c $(CFLAGS) $<

keywords.o: keywords/keywords.cpp keywords/keywords.h
	$(CC) -c $(CFLAGS) $<


clean:
	rm -f *.o

cleanall: clean
	rm -f get_sentiment get_tweets
