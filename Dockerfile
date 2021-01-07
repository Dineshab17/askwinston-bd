FROM openjdk:8u111-jdk-alpine
VOLUME /tmp
ADD /target/askwinston.jar askwinston.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/askwinston.jar"]
