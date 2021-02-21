#!/usr/bin/env bash

docker run --rm -e ZULIP_CLI_TOKEN -v ~/.config:/home/akvo/.config -v "$(pwd)":/app \
  -it akvo/akvo-devops:20210211.183324.b615f87 \
  flow-promote-test-to-prod.sh
