# Report for Class assignment 4 - Part 2 - Luís Silva 1231869
## Report Structure
- [Description of assigment](#description-of-assignment)
- [Container management - Docker compose](#containerization---docker)
  - [Task preparation](#task-preparation)
    - [Task 1 - Creating database container](#task-1---creating-database-container)
    - [Task 2 - Creating application container](#task-2---creating-application-container)
    - [Task 3 - Creating a docker compose file with volume](#task-3---creating-a-docker-compose-file-with-volume)
- [Conclusion](#conclusion) 
  - [Kubernetes - Docker compose +](#kubernetes---docker-compose-) 

## Description of assignment
This assigment focuses on using the Docker compose tool to manage the deployment of multiple containers using as a base the application from [CA2/Part2](../../CA2/Part2).

## Container management - Docker compose
Docker compose is a tool that allows us to create a yaml file that can configure multiple containers needed for one or multiple applications.  
These configurations include, services, networks, volumes, etc. and allow us to manage the containers in a more organized way. To put it simply:
- Services are the containers that we want to run;
- Networks manage the network configurations for our container. All containers that run on the same network can communicate with each other, however we can create subnets to isolate containers;
- Volumes are used to persist data from the containers. This allows us to store data even if the container is deleted or share the same set of data between multiple containers.

### Task preparation
We have already installed all needed Docker tools. For our database container we must first locate a database type that we wish to use. In this case we will use [H2 Version 1.4.200](https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar), the same that we used in CA3/Part2.

The tasks for this assignment are as follows:
- Task 1: Create a Dockerfile for the database container;
- Task 2: Create a Dockerfile for the application container;
- Task 3: Create a docker-compose file that deploys both containers and creates a volume to share the database file

#### Task 1 - Creating database container
- Similar to [CA4/Part1](../Part1), we start a new [Dockerfile](DockerfileDatabase) named DockerfileDatabase. This image will be responsible for running an instance of the H2 jar.
- We start by specifying the base image, and since H2 is a Java application we use the openjdk image:
```Dockerfile
FROM openjdk:17-jdk-slim

LABEL authors="Luis"
```
- We install wget to download the H2 jar and then download the H2 jar file:
```Dockerfile
FROM openjdk:17-jdk-slim

LABEL authors="Luis"

RUN wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
```
- Since H2 needs 2 ports to run (Console and Data) we will expose the default ports for H2 (8082 and 9092):
```Dockerfile
FROM openjdk:17-jdk-slim

LABEL authors="Luis"

RUN wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar

EXPOSE 8082 9092
```
- Finally we add the command to run the H2 jar:
```Dockerfile
FROM openjdk:17-jdk-slim

LABEL authors="Luis"

RUN wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar

EXPOSE 8082 9092

CMD ["java", "-cp", "h2-1.4.200.jar", "org.h2.tools.Server", "-web", "-webAllowOthers", "-tcp", "-tcpAllowOthers", "-ifNotExists"]
```
- We can build the image with the command `docker build -t h2-server -f DockerfileDatabase .` and run it with `docker run -d -p 8082:8082 -p 9092:9092 h2-server`. We can check if it is running by going to http://localhost:8082 and seeing the H2 console.  

#### Task 2 - Creating application container
- Again, we start a new Dockerfile named [DockerfileApp](DockerfileApp) that will be responsible for running the application. This container will run a Tomcat server that will execute our war file.
- On our Dockerfile we specify the base image that has tomcat and java, install git, clone our repository and place ourselves in the CA2/Part2 folder:
```Dockerfile
FROM tomcat:10-jdk17-openjdk-slim

LABEL authors="Luis"

RUN mkdir -p /cloneHome

WORKDIR /cloneHome

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/sepsilva/devops-23-24-PSM-1231869

WORKDIR /cloneHome/devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic
```
- Similar to CA3/Part2, we change our gradle wrapper permissions, build the project and copy the war file to the tomcat webapps folder:
```Dockerfile
FROM tomcat:10-jdk17-openjdk-slim

LABEL authors="Luis"

RUN mkdir -p /cloneHome

WORKDIR /cloneHome

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/sepsilva/devops-23-24-PSM-1231869

WORKDIR /cloneHome/devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic

RUN chmod +x gradlew

RUN ./gradlew build

RUN cp ./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/
```

- We then expose our port and run the http server:
```Dockerfile
FROM tomcat:10-jdk17-openjdk-slim

LABEL authors="Luis"

RUN mkdir -p /cloneHome

WORKDIR /cloneHome

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/sepsilva/devops-23-24-PSM-1231869

WORKDIR /cloneHome/devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic

RUN chmod +x gradlew

RUN ./gradlew build

RUN cp ./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]
```

#### Task 3 - Creating a docker compose file with volume
- To manage both containers we create a [Docker compose file](compose.yaml) that will allow us to set up:
  - 2 services:
    - Database container: Based on [DockerfileDatabase](DockerfileDatabase), named `db` with the IP 192.168.56.11 and ports 8082 and 9092 and using 1 volume;
    - App container: Based on [DockerfileApp](DockerfileApp), named `web` with the IP 192.168.56.10 and port 8080. Dependant on the `db` service; 
  - 1 network: Named `app-network` with the subnet mask `192.168.56.0/24`;
  - 1 volume: Named `database_data` that will store the database file in our local machine.

- We create our services in the same way as in [CA4/Part1](../Part1) by specifying the context and Dockerfiles of the images that need to be built:
```yaml
services:

  db:
    build:
      context: .
      dockerfile: DockerfileDatabase

  web:
    build:
      context: .
      dockerfile: DockerfileApp
    depends_on:
      - "db"
```
- We then specify the network for our container group (pod). We specify the driver (default for containers on the same host is bridge) and subnet mask with the name of this subnet being `app_network`:
```yaml
services:

  db:
    build:
      context: .
      dockerfile: DockerfileDatabase

  web:
    build:
      context: .
      dockerfile: DockerfileApp
    depends_on:
      - "db"      

networks:
  app_network:
    driver: bridge
    ipam:
      config:
        - subnet: 192.168.56.0/24
```
- With our pod network defined we can now specify the IP addresses for our containers and ports. We specify the network for each container:
```yaml
services:

  db:
    build:
      context: .
      dockerfile: DockerfileDatabase
    ports:
      - "8082:8082"
      - "9092:9092"
    networks:
      app_network:
        ipv4_address: 192.168.56.11      

  web:
    build:
      context: .
      dockerfile: DockerfileApp
    ports:
      - "8080:8080"
    networks:
      app_network:
        ipv4_address: 192.168.56.10   
    depends_on:
      - "db"        

networks:
  app_network:
    driver: bridge
    ipam:
      config:
        - subnet: 192.168.56.0/24
```
- In order to share files between containers we can create a volume. These volumes can serve as both a provider of files or storage for files and we can specify the location of the volume. In our case we want the file to be local:
```yaml
services:

  db:
    build:
      context: .
      dockerfile: DockerfileDatabase
    ports:
      - "8082:8082"
      - "9092:9092"
    networks:
      app_network:
        ipv4_address: 192.168.56.11      

  web:
    build:
      context: .
      dockerfile: DockerfileApp
    ports:
      - "8080:8080"
    networks:
      app_network:
        ipv4_address: 192.168.56.10   
    depends_on:
      - "db"        

volumes:
  database_data:
    driver: local

networks:
  app_network:
    driver: bridge
    ipam:
      config:
        - subnet: 192.168.56.0/24
```
- We want to provide the file that H2 uses to store the data. We want this data to be copied to our volume location so it could be used. To do this we must specify the location of the file in the container using the `target` keyword and the destination volume `source`:
```yaml
services:

  db:
    build:
      context: .
      dockerfile: DockerfileDatabase
    ports:
      - "8082:8082"
      - "9092:9092"
    networks:
      app_network:
        ipv4_address: 192.168.56.11      
    volumes:
      - type: volume
        source: database_data
        target: /root
  web:
    build:
      context: .
      dockerfile: DockerfileApp
    ports:
      - "8080:8080"
    networks:
      app_network:
        ipv4_address: 192.168.56.10   
    depends_on:
      - "db"        

volumes:
  database_data:
    driver: local

networks:
  app_network:
    driver: bridge
    ipam:
      config:
        - subnet: 192.168.56.0/24
```
- Since we are running Docker Desktop on Windows, the volume is technically not on our host machine but on a Linux virtual machine. Regardless this still serves the purpose of data persistence.
- We can now run `docker-compose up` and start up our containers. 
- If we go to our browser and type http://localhost:8080/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT/ we can see our application running. We can also go to http://localhost:8082 and see the H2 console.

## Conclusion
This assigment was great to both apply the knowledge gained during CA4/Part1 and to expand it. We saw how we can setup isolated containers within the same network without worrying about conflicts and we saw how containers can persist relevant data and share it amongst applications.
Docker is now and even more powerful set of tools than before, crucial for any application deployment, especially when it comes to harnessing the power of Docker compose.

### Kubernetes - Docker compose +
Kubernetes is Docker compose's big brother. While Docker compose is aimed at small applications, Kubernetes is aimed at large scale applications.  
It allows us to do much the same as compose but with some added features:
- Load balancing: Kubernetes can automatically balance the load between containers;
- Scaling: Kubernetes can automatically scale the number of containers/pods based on the load;
- Self-healing: If a container fails, Kubernetes can automatically restart it;
- Storage orchestration: Kubernetes can automatically manage storage;

In a sense Kubernetes is a more powerful version of Docker compose but with that comes more complexity, however this is required for large applications as the load/traffic can be unpredictable and managing this manually would be impossible.