#!/usr/bin/env bash

docker run --rm -e SLACK_CLI_TOKEN -v ~/.config:/home/akvo/.config -v "$(pwd)":/app \
  -it akvo/akvo-devops:20200525.103249.2b55a8b \
  flow-promote-test-to-prod.sh