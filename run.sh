#!/usr/bin/env bash
NAME=${NAME:-pttg-fs-api}

JAR=$(find . -name ${NAME}*.jar|head -1)
echo "Environment java opts:"
echo ${JAVA_OPTS}
java -Xms512m -Xmx512m -Dcom.sun.management.jmxremote.local.only=false -Djava.security.egd=file:/dev/./urandom -jar "${JAR}"
