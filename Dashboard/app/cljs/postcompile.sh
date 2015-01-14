#!/bin/bash

# Usage e.g. DASHBOARD_TAB=users lein cljsbuild auto dev

OUT_ROOT=../../../GAE/war/admin/frames/

if [[ $* == Successfully* ]]; then
    echo "Copying $DASHBOARD_TAB files to ../GAE/war/admin"
    mkdir -p ${OUT_ROOT}
    cp ${DASHBOARD_TAB}.js ${OUT_ROOT}${DASHBOARD_TAB}.js
    cp html/${DASHBOARD_TAB}.html ${OUT_ROOT}${DASHBOARD_TAB}.html
    rsync -a out $OUT_ROOT
    echo "Done."
fi
