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

Part 3 - Creating a docker compose file with volume
Similar to CA4/Part1, we will create a docker-compose file that creates the two containers and connects them.
We want to create a volume for our database. This allows us to persist the data even if the container is deleted.
It basically copies/duplicates the file that stores the data persisted in the database to a folder in our computer.
Other containers can use this volume to access the data.

To do this we must first find where h2 stores its data inside the container. We start up a container and exec into it.
After checking we see a file called test.mv.db. We can use the h2 console to create a new table and populate it and while
the container is running we can also use the 'touch test2.mv.db' to create a new database file.

When logging in through h2 we can specify jdbc:h2:file:/root/test2 as the url to connect to the new database and we see that
the table we create is not in there but if we log in with jdbc:h2:file:/root/test we see the table we created.

Since we're using our CA2/Part2 Spring application and it has in it's application.properties the url to connect to an external database (jdbc:h2:tcp://192.168.56.11:9092/./jpadb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE)
we must create a "mask" around our container(s) that allow them to communicate with eachother, the outside world without having the need to change the application.properties file.

To do so, we specificy a network in the docker-compose file and we add the containers to that network, named "app-network". We can check our docker network with 'docker network ls'.
We specify the network mask subnet: 192.168.56.0/24 and name it app-network, we also specify the driver as bridge since we're using a bridge network.
For each container we specify the network and the ip address we want to assign to it.

We can now run docker compose up and start up our containers.
Our container ports (8080 and 8082) are exposed to the outside and since they are on our machine we can use localhost.
Even though we defined a docker network, that subnet is relative to docker so we can run each cluster of docker containers on the same subnet, without having to worry about ip conflicts.