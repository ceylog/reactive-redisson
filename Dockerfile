FROM adoptopenjdk:11-jre-openj9

COPY ./target/redisson-0.0.1-SNAPSHOT.jar /root/startup/redisson.jar
WORKDIR /root/startup/
EXPOSE 8080

CMD ["java","-jar","redisson.jar","--spring.profiles.active=docker"]
