# Deployment Notes

## Introduction

Although the `reports` application is written in [Clojure](http://clojure.org/), it is intended to be deployed as a standard [JAR](https://en.wikipedia.org/wiki/JAR_file) file.

This document describes how to generate a JAR file.

It is assumed that you have [Leiningen](http://leiningen.org/) version 2.x installed.

## Generating the JAR file

To generate a JAR file for the `reports` application, run the following `lein` command:

    $ lein uberjar

This will generate a file called `reports-0.1.0-SNAPSHOT-standalone.jar` in the `target` directory of the project.

A new JAR file must be generated every time the application code changes.

## Running the application

You only need to use the `java` binary to run the JAR file:

    java -jar reports-0.1.0-SNAPSHOT-standalone.jar


You might want to define the port number on which the HTTP service will run, e.g.


    java -jar reports-0.1.0-SNAPSHOT-standalone.jar 3000


This will start the HTTP service in the port 3000. The __default port__ is __8080__ if no argument is used.

## Configuring the application to run as a service

The `reports-0.1.0-standalone.jar` file should be deployed to the `/opt/reports/` path on your server.

You can use [Upstart](http://upstart.ubuntu.com/cookbook/) on Ubuntu based systems for configuring a service that will run on startup.

### Defining an Upstart Job

Here you have an example of `/etc/init/report-service.conf` file:

    # report-service - Upstart conf for report-service
    #
    
    description     "Simple conf for starting the report service automatically"
    
    start on runlevel [2345]
    stop on runlevel [!2345]
    
    exec /usr/bin/java -Xmx1024m -jar /opt/reports/reports-0.1.0-SNAPSHOT-standalone.jar


Ater defining the `report-service.conf` the application will start in the server boot process.

### Testing the service

You can query the service by making a HTTP to the app, e.g.

    curl -L http://localhost:8080

It should return `OK`.


## Configuring Nginx

[Nginx](http://wiki.nginx.org/) should be configured to proxy through to the Jetty application server running on port 8080. Configuring Nginx is beyond the scope of this document, but the following serves as an example:

    location / {
        proxy_pass http://127.0.0.1:8080/;
        ...
    }

The Akvo FLOW Dashboard app explicitl depends on being able to fetch generated reports from the hardcoded URL `/report` and the reports app generates these reports at the hardcoded path `/tmp/akvo/flow/reports`. Consequently, Nginx **must** be configured accordingly:

    location /report/ {
        alias /tmp/akvo/flow/reports/;
        autoindex off;
        allow all;
    }
