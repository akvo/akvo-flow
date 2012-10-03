#!/bin/bash

THIS_SCRIPT=$0

DASHBOARD_HOME="$(cd `dirname "$THIS_SCRIPT"` && pwd)"
echo "Updating dashboard at: $DASHBOARD_HOME"

cd $DASHBOARD_HOME
git status
git pull origin feature/frontend
git remote update
sudo rm -r assets/*
sudo chmod g+w assets
bundle
rake build
sudo apache2ctl restart

