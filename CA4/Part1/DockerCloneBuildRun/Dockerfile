#Create a basic container with java 17
FROM openjdk:17-jdk-slim

LABEL authors="Luis"

# Create a directory for the project and clone the repository there
RUN mkdir -p /cloneHome
WORKDIR /cloneHome

# Update package list and install Git
RUN apt-get update && apt-get install -y git

#Clone given repository
RUN git clone https://bitbucket.org/pssmatos/gradle_basic_demo/src/master .

#Run the gradle build command
RUN ./gradlew build

#Tell docker to run the jar file when the container starts since we don't have the jar file in the same directory
#we could also run the individual class
ENTRYPOINT ["java", "-cp", "/cloneHome/build/libs/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]