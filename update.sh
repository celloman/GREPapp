#!/bin/bash

echo "{\"gauge\":50, \"tweets\":0, \"liberal\":0, \"conservative\":0, \"time\":0}" > "out.txt"

while read json; do
	echo $json > "html/out.txt"
done
