#!/bin/bash

# Usage e.g. DASHBOARD_TAB=users lein cljsbuild once adv

OUT_ROOT=../../../GAE/war/admin/frames/

if [[ $* == Successfully* ]]; then
    echo "Copying files $DASHBOARD_TAB to GAE/war/admin"
    cp ${DASHBOARD_TAB}.js ${OUT_ROOT}${DASHBOARD_TAB}.js
    cp html/${DASHBOARD_TAB}-adv.html ${OUT_ROOT}${DASHBOARD_TAB}.html
    echo "Done."
fi
