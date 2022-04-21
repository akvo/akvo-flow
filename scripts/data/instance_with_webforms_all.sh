#!/usr/bin/env bash
date_now=$(date '+%Y-%m-%d')

echo 'alias,instances,count' > /tmp/tmp.csv

# Expecting akvo-flow-server-config as sibling folder for this repo
list=$(find ../../../akvo-flow-server-config -type f -name appengine-web.xml \
    -maxdepth 2 -mindepth 2 \
    -exec echo {} \; 2>/dev/null)
for word in ${list};
do
    service=$(grep "<application>" "${word}"\
        | sed 's/<.*>\(.*\)<.*>/\1/' \
        | sed 's/\ //g')
    alias=$(grep "alias" "${word}"\
        | sed 's/.*value="\([^"]*\).*/\1/' \
        | cut -d '.' -f 1)
    count=$(./instance_with_webforms.sh "${service}" "${date_now}")
    echo "${alias},${count}" \
        | sed 's/\ //g' >> /tmp/tmp.csv;
done;

sed 's/done//g' /tmp/tmp.csv \
    | sed 's/[s|e]~//g' \
    | sed '/^$/d' >> "./webform-result-count-${date_now}.csv"

# To check the progress: tail -f /tmp/tmp.csv
