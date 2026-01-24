FROM openjdk:11.0.6

WORKDIR /app

COPY ./imustsz-business/target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]