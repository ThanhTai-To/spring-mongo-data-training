FROM openjdk:11
VOLUME /tmp
ADD target/superblog-0.0.1-SNAPSHOT.jar superblog.jar
ENTRYPOINT exec java -jar superblog.jar