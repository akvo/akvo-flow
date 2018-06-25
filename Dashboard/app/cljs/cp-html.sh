#!/bin/bash

OUT_ROOT=../../../GAE/target/akvo-flow/admin/frames/

mkdir -p ${OUT_ROOT}

if [[ $* == *--production* ]]; then
    sed 's/users\.css/users\.min\.css/' users.html > ${OUT_ROOT}users.html
else
    cp users.html ${OUT_ROOT}users.html
fi
