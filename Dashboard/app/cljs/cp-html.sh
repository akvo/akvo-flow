#!/bin/bash

OUT_ROOT=../../../GAE/war/admin/frames/

mkdir -p ${OUT_ROOT}

if [[ $* == *--production* ]]; then
    cat users.html | sed 's/users\.css/users\.min\.css/' > ${OUT_ROOT}users.html
else
    cp users.html ${OUT_ROOT}users.html
fi
