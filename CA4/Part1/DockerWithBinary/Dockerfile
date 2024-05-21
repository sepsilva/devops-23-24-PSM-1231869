#Create a basic container with java 17 and running tomcat 10 similar
FROM openjdk:21-jdk-slim

LABEL authors="Luis"

#Deploy the generated jar file that we obtain after ./gradlew build and place it inside our app folder
COPY basic_demo-0.1.0.jar /app/basic_demo-0.1.0.jar

#Tell docker to run the jar file when the container starts
ENTRYPOINT ["java", "-cp", "/app/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]