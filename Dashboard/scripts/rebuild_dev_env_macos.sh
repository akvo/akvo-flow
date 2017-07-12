#!/bin/bash

#   Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
#
#   This file is part of Akvo Flow.
#
#   Akvo Flow is free software: you can redistribute it and modify it under the terms of
#   the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#   either version 3 of the License or any later version.
#
#   Akvo Flow is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#   See the GNU Affero General Public License included below for more details.
#
#   The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.


THIS_SCRIPT="$0"
DASHBOARD_HOME="$(cd `dirname "$THIS_SCRIPT"`/.. && pwd)"

function exit_if_homebrew_not_installed
{
    if [[ -z `command -v brew` ]]; then
        echo '## Homebrew not installed -- see http://brew.sh'
        exit 1
    else
        echo ">> Homebrew installed as expected:"
        brew --version
    fi
}

function ensure_homebrew_package_installed
{
    _PACKAGE_NAME="$1"
    _PACKAGE_VERSION="$2"

    INSTALLED_PACKAGE="`brew list --versions $_PACKAGE_NAME`"

    if [[ -z "$INSTALLED_PACKAGE" ]]; then
        printf ">> Installing $_PACKAGE_NAME...\n"
        brew install $_PACKAGE_NAME
    else
        INSTALLED_PACKAGE_VERSION=$(echo $INSTALLED_PACKAGE | cut -d ' ' -f 2)

        if [[ "$INSTALLED_PACKAGE_VERSION" < "$_PACKAGE_VERSION" ]]; then
            printf ">> Upgrading $_PACKAGE_NAME...\n"
            brew upgrade $_PACKAGE_NAME
        else
            printf "Expected package installed: $INSTALLED_PACKAGE\n"
        fi
    fi
}

function ensure_build_tools_are_installed
{
    echo '>> Updating homebrew...'
    brew update
    printf "\n>> Installing build tools...\n"
    ensure_homebrew_package_installed leiningen
    ensure_homebrew_package_installed ruby-build
    ensure_homebrew_package_installed rbenv
    printf "\n>> Checking for outdated packages...\n"
    brew outdated
    echo '>> Cleaning up...'
    brew cleanup
    echo '>> All installed packages:'
    brew list --versions
}

function ensure_ruby_version_installed_is
{
    _EXPECTED_RUBY_VERSION="$1"

    printf "\n>> Initialising rbenv...\n"
    eval "$(rbenv init -)"

    if [[ -z $(rbenv versions | grep $_EXPECTED_RUBY_VERSION) ]]; then
        printf ">> Installing Ruby version: $_EXPECTED_RUBY_VERSION\n"
        rbenv install $_EXPECTED_RUBY_VERSION
        rbenv rehash
    fi

    echo '>> Expected Ruby version installed at:'
    rbenv which ruby
}

function use_local_ruby_version
{
    _EXPECTED_RUBY_VERSION="$1"

    cd "$DASHBOARD_HOME"
    ensure_ruby_version_installed_is $_EXPECTED_RUBY_VERSION

    LOCAL_RUBY_VERSION=$(rbenv local 2>&1) # get version or error text

    if [[ $LOCAL_RUBY_VERSION != $_EXPECTED_RUBY_VERSION ]]; then
        if [[ -z $(echo $LOCAL_RUBY_VERSION | grep 'no local version') ]]; then
            printf ">> Local Ruby version is currently: $LOCAL_RUBY_VERSION\n"
            rbenv local --unset
        fi

        printf ">> Setting local Ruby version for Flow builds to: $_EXPECTED_RUBY_VERSION\n"
        rbenv local $_EXPECTED_RUBY_VERSION
        rbenv rehash

    else
        printf ">> Local Ruby version for Flow builds is: "
        rbenv local
    fi

    printf ">> Global Ruby version is: "
    ruby --version
}

exit_if_homebrew_not_installed
ensure_build_tools_are_installed
use_local_ruby_version "2.4.1"
