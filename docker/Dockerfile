FROM openjdk:17.0.2

COPY imustsz-business.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]