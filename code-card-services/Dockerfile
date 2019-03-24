FROM  openjdk:8-jdk-alpine
VOLUME /tmp
ARG application
ADD target/${application}.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
