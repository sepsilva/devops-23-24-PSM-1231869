Part 1 - Creating database container
We could use a already existing H2 database container like we did in CA3/Part2, but we will create a new one from scratch.
Create new [Dockerfile](DockerfileDatabase) that executes https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
We install wget and download the h2 jar and use the cmd to run it.

To build the container we: docker build -t h2-server -f DockerfileDatabase .
To run the container we: docker run -d -p 8082:8082 -p 9092:9092 h2-server

We can check that it runs by going to http://localhost:8082 and seeing the console

Part 2 - Creating application container
Create a new [Dockerfile](DockerfileApp) that copies the jar file from the target folder and runs it.
Use as base what was done in CA3/Part2

We build with
docker build -t app -f DockerfileApp .
docker run -d -p 8080:8080 app

Obviously we get a connection error, because the application is trying to connect to a database that doesn't exist in or in its network.

