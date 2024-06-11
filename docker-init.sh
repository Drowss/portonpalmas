#!/bin/bash
for d in $(find . -type f -name 'pom.xml' -printf '%h\n'); do
    echo "Running mvn install -DskipTests in $d"
    cd $d
    mvn install -DskipTests
    cd -
done

#echo "Running docker-compose up -d --build --force-recreate"
#docker-compose up -d --build --force-recreate