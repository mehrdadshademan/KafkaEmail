FROM  openjdk:17
WORKDIR /src/main/java
EXPOSE 8090:8090
COPY target/EmailMessenger-1.0.0.jar .
CMD ["java", "-jar", "EmailMessenger-1.0.0.jar"]
