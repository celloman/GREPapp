#!/bin/bash

echo "{\"gauge\":50, \"tweets\":0, \"liberal\":0, \"conservative\":0, \"time\":0}" > "out.txt"

x=0

while read json; do
	echo $json > "out.txt"
	if [ $(($x % 1000)) -eq 0 ]; then
		echo $json >> "all.txt"
	fi
	x=$(($x + 1))
done
