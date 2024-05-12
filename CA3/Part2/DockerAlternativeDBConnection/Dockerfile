#Create a basic container with java 17 and running tomcat 10 similar to what our vagrant file does
FROM tomcat:10-jdk17-openjdk-slim

LABEL authors="Luis"

#Similar to what we did in the vagrant file we must deploy the generated war file that we obtain after ./gradlew build and place it inside our tomcat webapps
COPY ./react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war ./webapps

#State the port that our application will run on
EXPOSE 8080

#start tomcat automatically when container starts
CMD ["catalina.sh", "run"]