# Deployment Notes

## Introduction

Although the `reports` application is written in [Clojure](http://clojure.org/), it is intended to be deployed as a standard JVM WAR file.

This document describes how to generate and deploy such a WAR file.

It is assumed that you have [Leiningen](http://leiningen.org/) version 2.x installed.

## Generating the WAR file

To generate a deployable WAR file for the `reports` application, run the following `lein` command:

    lein ring uberwar root.war

This will generate a file called `root.war` in the `target` directory of the project.

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

In the following example we symlink the WAR file we generated to the `webapps` directory:

    sudo ln -s /path/to/akvo-flow/reports/target/root.war /var/lib/jetty/webapps/

### Managing the Jetty application container

Jetty can now be managed as a regular Unix System V system service:

    sudo /etc/init.d/jetty start        # Starts the service
    sudo /etc/init.d/jetty stop         # Stops the service
    sudo /etc/init.d/jetty restart      # Restarts the service

The following commands are useful for checking on the status of a running Jetty service:

    sudo /etc/init.d/jetty status       # Reports the status of a running service
    sudo /etc/init.d/jetty check        # Checks the arguments the running service was run with

## Configuring Nginx

[Nginx](http://wiki.nginx.org/) should be configured to proxy through to the Jetty application server running on port 8000. Configuring Nginx is beyond the scope of this document, but the following serves as an example:

    location / {
        proxy_pass http://127.0.0.1:8000/;
        ...
    }

The Akvo FLOW Dashboard app explicitl depends on being able to fetch generated reports from the hardcoded URL `/report` and the reports app generates these reports at the hardcoded path `/var/tmp/akvo/flow/reports`. Consequently, Nginx **must** be configured accordingly:

    location /report/ {
        alias /var/tmp/akvo/flow/reports/;
        autoindex off;
        allow all;
    }
