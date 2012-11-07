#!/bin/bash

x=1234

if [ $(($x % 100)) -eq 35 ] ; then
	echo "hey"
fi
