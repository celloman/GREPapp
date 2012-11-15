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

CC = g++
CFLAGS = -I.
LDFLAGS = -lcurl -ljsoncpp

all: release

debug: CFLAGS += -ggdb -O0 -DDEBUG=true
debug: executables

info: CFLAGS += -O2 -DINFO=true
info: executables

release: CFLAGS += -O2
release: executables

executables: get_tweets get_weight get_sentiment aggregate clean


get_tweets: get_tweets.o twitter_stream.o tweet.o keywords.o
	$(CC) -o $@ $^ $(LDFLAGS)

get_tweets.o: get_tweets.cpp
	$(CC) -c $(CFLAGS) $<

twitter_stream.o: twitter_stream/twitter_stream.cpp twitter_stream/twitter_stream.h
	$(CC) -c $(CFLAGS) $<


get_weight: get_weight.o tweet.o keywords.o filter.o
	$(CC) -o $@ $^ $(LDFLAGS)

get_weight.o: get_weight.cpp
	$(CC) -c $(CFLAGS) $<


get_sentiment: get_sentiment.o sentiment.o tweet.o keywords.o
	$(CC) -o $@ $^ $(LDFLAGS)

get_sentiment.o: get_sentiment.cpp
	$(CC) -c $(CFLAGS) $<

sentiment.o: sentiment/sentiment.cpp sentiment/sentiment.h
	$(CC) -c $(CFLAGS) $<


aggregate: aggregate.o tweet.o
	$(CC) -o $@ $^ $(LDFLAGS)

aggregate.o: aggregate.cpp
	$(CC) -c $(CFLAGS) $<


tweet.o: tweet/tweet.cpp tweet/tweet.h
	$(CC) -c $(CFLAGS) $<

keywords.o: keywords/keywords.cpp keywords/keywords.h
	$(CC) -c $(CFLAGS) $<

filter.o: filter/filter.cpp filter/filter.h
	$(CC) -c $(CFLAGS) $<


clean:
	rm -f *.o html/out.txt html/log.txt

cleanall: clean
	rm -f get_sentiment get_weight get_tweets aggregate

test:
	@tests/iotests.sh
