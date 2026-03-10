FROM openjdk:11.0.6

WORKDIR /app

COPY ./target/webservice-1.0-SNAPSHOT.jar /app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]