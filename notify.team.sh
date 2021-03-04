#!/usr/bin/env bash

if [ -z "${ZULIP_CLI_TOKEN}" ]; then
  echo "You need a env var ZULIP_CLI_TOKEN with a valid Zulip API key"
  echo "See https://zulipchat.com/api/api-keys for instructions"
  echo "Create the env variable and rerun this script ($0)"
  echo "Environment variable format is YOUR_EMAIL:API_KEY"
  exit 1
fi

slack_txt=$(git --no-pager log --oneline --no-merges 403c49fce56fadeb468b4897d5482d91b42ada5c..d080c9dd5396f9c203b05985d39e5d9ac6357f90 | cut -f 2- -d\  | sed 's/\[#\([0-9]*\)\]/\[#\1\]\(https:\/\/github.com\/akvo\/akvo-flow\/issues\/\1\)/' | tr \" \')

curl -X POST https://akvo.zulipchat.com/api/v1/messages     -u "${ZULIP_CLI_TOKEN}"     -d "type=stream"     -d "to=K2 Engine"     -d "topic=Releases"     -d "content=Flipping *FLOW PROD!!!*. [Full diff](https://github.com/akvo/akvo-flow/compare/403c49fce56fadeb468b4897d5482d91b42ada5c..d080c9dd5396f9c203b05985d39e5d9ac6357f90).

$slack_txt"

