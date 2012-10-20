#!/bin/bash

echo "{\"gauge\":50, \"tweets\":0}" > "out.txt"

while read json; do
	echo $json > "out.txt"
	echo $json >> "all.txt"
done
