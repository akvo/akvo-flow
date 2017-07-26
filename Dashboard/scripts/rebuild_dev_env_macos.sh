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
DASHBOARD_HOME="$(cd $(dirname "$THIS_SCRIPT")/.. && pwd)"

function exit_if_homebrew_not_installed
{
    if [[ -z $(command -v brew) ]]; then
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

    INSTALLED_PACKAGE=$(brew list --versions $_PACKAGE_NAME)

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

function switch_to_ruby_version
{
    _EXPECTED_RUBY_VERSION="$1"

    rbenv shell $_EXPECTED_RUBY_VERSION
    rbenv rehash
}

function ensure_ruby_version_installed_is
{
    _EXPECTED_RUBY_VERSION="$1"

    printf "\n>> Initialising rbenv...\n"
    eval "$(rbenv init -)"

    if [[ -z $(rbenv versions | grep $_EXPECTED_RUBY_VERSION) ]]; then
        printf ">> Installing Ruby version: $_EXPECTED_RUBY_VERSION\n"
        rbenv install $_EXPECTED_RUBY_VERSION
    fi

    printf ">> Expected Ruby version [$_EXPECTED_RUBY_VERSION] is installed:\n"
    rbenv versions
    switch_to_ruby_version $_EXPECTED_RUBY_VERSION

    printf ">> Expected Ruby version installed at:\n"
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

    else
        printf ">> Local Ruby version for Flow builds is: "
        rbenv local
    fi

    printf ">> Current Ruby version is: "
    switch_to_ruby_version $_EXPECTED_RUBY_VERSION
    ruby --version
}

function ensure_gem_is_installed
{
    _GEM_NAME="$1"
    _GEM_VERSION="$2"

    EXPECTED_GEM_IS_INSTALLED=$(gem list -i $_GEM_NAME -v $_GEM_VERSION)

    if [[ "$EXPECTED_GEM_IS_INSTALLED" = "false" ]]; then
        printf ">> Gem $_GEM_NAME $_GEM_VERSION not installed\n"
        gem install $_GEM_NAME -v $_GEM_VERSION
    fi

    if [[ "$EXPECTED_GEM_IS_INSTALLED" = "false" ]]; then
        printf "## Error: Unable to install gem $_GEM_NAME version $_GEM_VERSION\n"
        exit 1
    else
        printf ">> Expected gem is installed: $_GEM_NAME $_GEM_VERSION\n"
    fi
}

function ensure_build_dependencies_are_installed
{
    printf "\n>> Current gem system version: $(gem --version)\n"
    echo '>> Updating gem system...'
    gem update --system
    printf ">> Current gem system version: $(gem --version)\n"
    printf "\n>> Installing gems for building the Dashboard\n"
    ensure_gem_is_installed bundler 1.15.3
    ensure_gem_is_installed json 2.1.0
    printf "\n>> Checking for outdated gems...\n"
    gem outdated
    echo '>> Upgrading gems...'
    gem update
    echo '>> Cleaning up'
    gem cleanup
    printf "\n>> Installed gems"
    gem list
}

exit_if_homebrew_not_installed
ensure_build_tools_are_installed
use_local_ruby_version "2.4.1"
ensure_build_dependencies_are_installed
