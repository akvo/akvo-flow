#!/usr/bin/env bash

if [ -z "${ZULIP_CLI_TOKEN}" ]; then
  echo "You need a env var ZULIP_CLI_TOKEN with a valid Zulip API key"
  echo "See https://zulipchat.com/api/api-keys for instructions"
  echo "Create the env variable and rerun this script ($0)"
  echo "Environment variable format is YOUR_EMAIL:API_KEY"
  exit 1
fi

zulip_txt=$(git --no-pager log --reverse --oneline --no-merges 20210508t163928..0233d84d5ca2f3db34608c7857b49a523a23822b | cut -f 2- -d\  | sed 's/\[#\([0-9]*\)\]/\[#\1\]\(https:\/\/github.com\/akvo\/akvo-flow\/issues\/\1\)/' | tr \" \')

curl --request POST https://akvo.zulipchat.com/api/v1/messages     --user "${ZULIP_CLI_TOKEN}"     --data "type=stream"     --data "to=K2 Engine"     --data "topic=Releases"     --data-urlencode "content=Promoting akvo-flow to dark production (aka https://uat1.akvoflow.org). [Full diff](https://github.com/akvo/akvo-flow/compare/20210508t163928..0233d84d5ca2f3db34608c7857b49a523a23822b).

${zulip_txt}"

