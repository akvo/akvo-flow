# Installation Notes

This document assumes that you have a working JDK installation.

## Installing Leiningen

[Leiningen](http://leiningen.org/) is a build tool for Clojure projects and must be installed on your system `$PATH`.

The following commands fetch the `lein` script and install it to `/usr/local/bin`:

    $ cd /usr/local/bin
    $ sudo wget https://raw.github.com/technomancy/leiningen/stable/bin/lein
    $ sudo chmod +x lein

Verify that the `lein` command is now available:

    $ which lein
    /usr/local/bin/lein

    $ lein version
    Leiningen 2.1.2 on Java 1.6.0_27 OpenJDK 64-Bit Server VM

`lein` will take care of installing its dependent JAR files on first run.

## Installing manual dependencies

First make sure that [Maven](https://maven.apache.org/) is installed on your system.

The `project.clj` file defines a dependency on `[exporterapplet "1.0.0"]` which must be added to your local Maven cache manually as follows:

    $ mvn install:install-file -Dfile=../GAE/war/exporterapplet.jar \
          -DartifactId=exporterapplet \
          -Dversion=1.0.0 \
          -DgroupId=exporterapplet \
          -Dpackaging=jar
