FROM  frolvlad/alpine-oraclejdk8-temporary:slim
VOLUME /tmp
ARG application
ADD target/${application}.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
