#!/bin/bash

echo "{\"gauge\":50, \"tweets\":0, \"liberal\":0, \"conservative\":0, \"time\":0}" > "html/out.txt"
echo "{\"id\":0, \"text\":\"\", \"title\":\"\", \"type\":\"\"}" > "html/out.txt"

while read json; do
	echo $json > "html/out.txt"
done
