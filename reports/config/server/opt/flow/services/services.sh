#!/bin/sh

JAR="/opt/akvo/flow/services/current.jar"
CONFIG_REPO="/opt/akvo/flow/repo/akvo-flow-server-config"
PORT_NUMBER=3000
JVM_OPTS="-Xmx1024m -d64 -server"

/usr/bin/java $JVM_OPTS -jar $JAR $CONFIG_REPO $PORT
