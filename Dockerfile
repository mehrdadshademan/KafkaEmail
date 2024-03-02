FROM  adoptopenjdk:17-jre-hotspot
WORKDIR /src/main/java
COPY target/EmailMessenger-1.0.0.jar .
CMD ["java", "-jar", "EmailMessenger-1.0.0.jar"]
