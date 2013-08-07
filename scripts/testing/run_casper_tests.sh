#!/bin/bash

cd "`dirname $0`"

# check whether virtualenv_path parameter exists
if [ -z "$1" ]; then
	echo "Usage: run_casper_tests <virtualenv_path> [ci_mode]"
	exit -1
fi

VIRTUALENV_PATH=$1

# check whether virtualenv path exists

if [ -r $VIRTUALENV_PATH ]; then
	source $VIRTUALENV_PATH/bin/activate
else
	printf ">> Akvo virtual environment [%s] not found\n" $VIRTUALENV_PATH
	exit 1
fi

cd ../../akvo

printf "\n>> Running CasperJS Tests:\n"

casperjs test ./Dashboard/tests/casperjs

