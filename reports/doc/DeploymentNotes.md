# How do I deploy this?

## Introduction

Although The `reports` application is written in [Clojure](http://clojure.org/), it is intended to be deployed as a standard JVM WAR file.

This document describes how to generate and deploy such a WAR file.

## Generating the WAR file

To generate a deployable WAR file for the `reports` application, run the following command:

    lein ring uberwar reports.war

This will generate a file called `reports.war` in the `target` directory of the project.

A new WAR file must be generated every time the application code changes.

## Installing and configuring Jetty

The `reports` app should be deployed using [Jetty](http://jetty.codehaus.org/jetty/).

Jetty can be installed on Debian systems as follows:

    sudo apt-get install jetty

Configuring Jetty is beyond the scope of this document, but you should probably set the following environment variables in `/etc/defaults/jetty` at a minimum:

    NO_START=0              # Set Jetty to start automatically
    JETTY_PORT=8000         # The port Jetty should run on
    JAVA_HOME=/path/to/jdk  # The path to your installed JDK

## Deploying the WAR file

By default, Jetty looks for web apps in `/var/lib/jetty/webapps` on a Debian or Ubuntu system. The default `root` folder should be moved aside and the WAR file you generated earlier should be copied or symlinked into this directory as `root.war`. The filename is important!

In the following example we copy the generated WAR file to the `webapps` directory:

    sudo cp target/reports.war /var/lib/jetty/webapps/root.war
    sudo chown jetty:adm /var/lib/jetty/webapps/root.war

Jetty can now be started as a regular Debian system service:

    sudo /etc/init.d/jetty start    # restart and stop are also available
