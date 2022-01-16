#!/bin/sh
BASEDIR=$(dirname "$0")
java -cp "$BASEDIR"/bin:"BASEDIR"/lib/json-java.jar WebServer $@
