#!/bin/bash

docker build -t google-java-format -<<EOF
FROM openjdk:alpine
RUN wget https://github.com/google/google-java-format/releases/download/google-java-format-1.6/google-java-format-1.6-all-deps.jar
CMD cd /src && find . -name '*.java' | xargs -t java -jar /google-java-format-1.6-all-deps.jar -i
EOF
docker run --rm --mount type=bind,src=`pwd`/src/main/java,dst=/src google-java-format
