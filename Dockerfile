FROM  adoptopenjdk:17-jre-hotspot
WORKDIR /src/main/java
COPY target/test-1.0.0.jar .
CMD ["java", "-jar", "test-1.0.0.jar"]
