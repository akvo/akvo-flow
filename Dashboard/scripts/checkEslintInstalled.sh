#!/bin/bash

echo "blah"
npm list | grep eslint || exit 1;
echo "blah 2"

exit 0;
