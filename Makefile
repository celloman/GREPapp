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
#CFLAGS = #none at this time                                                
LDFLAGS = -lcurl -ljson

all: political clean

political: main.o twitter_stream.o
	$(CC) -o $@ $^ $(LDFLAGS)

main.o: main.cpp
	$(CC) -c $(CFLAGS) $<

twitter_stream.o: twitter_stream/twitter_stream.cpp twitter_stream/twitter_stream.h
	$(CC) -c $(CFLAGS) $<

clean:
	@rm -f *.o

cleanall: clean
	@rm -f political
