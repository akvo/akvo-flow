#!/bin/bash

OUT_ROOT=../../../GAE/war/admin/frames/

echo "Copying users.html"
mkdir -p ${OUT_ROOT}
cp users.html ${OUT_ROOT}users.html
