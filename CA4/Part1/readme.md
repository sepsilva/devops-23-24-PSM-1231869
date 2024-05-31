# Report for Class assignment 4 - Part 1 - Luís Silva 1231869
## Report Structure
- [Description of assigment](#description-of-assignment)
- [Containerization - Docker](#containerization---docker)
    - [Task preparation - Docker](#task-preparation---docker)
        - [Task 1](#task-1)
        - [Task 2](#task-2)
        - [Task 3](#task-3)
          - [Part 1 - Building the application](#part-1---building-the-application)
          - [Part 2 - Running the application](#part-2---running-the-application)
            - [Part 2.1 - With build container locally](#part-21---with-build-container-locally)
            - [Part 2.2 - Using a hosted image as our base image to run the application](#part-22---using-a-hosted-image-as-our-base-image-to-run-the-application)
- [Conclusion](#conclusion) 



## Description of assignment
In this assignment we explore the Containerization of applications in order to quickly and efficiently launch these applications within a self-contained item, that guarantees all needed dependencies.  
We will explore the use of Docker to create images that can be shared, deployment of containers and management of multiple dependant containers using Docker compose.

## Containerization - Docker
Containerization is the process of packaging an application and its dependencies in a container, which is a standardized unit (same as real life containers) that can be passed around and run on any system that has a container engine installed.  
The containerization process is different from virtualization, since it doesn't require a full OS to run, it only requires the container engine and the container itself and uses the host OS to run the application, making it much lighter when compared to full virtualization.  

For this we will be using [Docker](https://docs.docker.com/get-started/overview/). Docker offers the capability of deploying an application in a container, with the container containing all needed dependencies, however it also offers the ability to publish these images and to create a [Docker compose](https://docs.docker.com/compose/) which allows us to automate the deployment of multiple containers and manage their network/file sharing settings.  
In [CA3/Part2](../../CA3/Part2) we already explored the use of Docker for our CA2/Part2 application, since the principle of Vagrant is similar to Docker/Docker compose, which is the creation of a standard environment to run a set application.  
In that assignment we used pre-built images (for our database) and in this we will be building them from scratch and uploading them to Docker hub. We will also make use of the Docker compose to automate the deployment of our application/database set.

### Task preparation - Docker
We must first install Docker. In our case we install [Docker desktop](https://www.docker.com/products/docker-desktop/) for Windows and register an account to publish our Docker images.  
All Docker images created for all these tasks are available [here](https://hub.docker.com/repository/docker/sepsilva/chat-app/tags).  
The application that is used for this assigment is available [here](https://bitbucket.org/pssmatos/gradle_basic_demo/src/master/).  

The tasks for this assignment are as follows:
- Task 1 - Create a container that clones, builds and runs the chat application;
- Task 2 - Compile the binary locally and copy it to the container;
- Task 3 - Compile the binary in one container and copy it to the container;

#### Task 1
- We start by creeating a new [Dockerfile](DockerCloneBuildRun/Dockerfile) using the command `docker init`.
- This generates a Dockerfile, .dockerignore, README.Docker.md and compose.yaml. We will ignore all except the Dockerfile.
- To run this application we must guarantee that Java is installed, so we start by adding a base image that has Java installed. We can also specify the image author:
```Dockerfile
    FROM openjdk:17-jdk-slim
    
    LABEL authors="Luis"
```
- We can then run commands using the keywords `RUN` and `MKDIR` to execute Linux commands since Docker uses a Linux kernel. We add commands to update, install git, clone and build the application:
```Dockerfile
    FROM openjdk:17-jdk-slim
    
    LABEL authors="Luis"
    
    RUN mkdir -p /cloneHome
    WORKDIR /cloneHome
    
    RUN apt-get update && apt-get install -y git
    
    RUN git clone https://bitbucket.org/pssmatos/gradle_basic_demo/src/master .
    
    RUN ./gradlew build
```

- Now we use the `ENTRYPOINT` keyword to specify the command that will be executed when the container starts. In the case of this application, we must specify the port number (it should be run on port 59001):
```Dockerfile
    FROM openjdk:17-jdk-slim
    
    LABEL authors="Luis"
    
    RUN mkdir -p /cloneHome
    WORKDIR /cloneHome
    
    RUN apt-get update && apt-get install -y git
    
    RUN git clone https://bitbucket.org/pssmatos/gradle_basic_demo/src/master .
    
    RUN ./gradlew build
    
    ENTRYPOINT ["java", "-cp", "/cloneHome/build/libs/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]
```
- Now we build our image, to do so we run the command `docker build -t mychat:v1 -f CA4/Part1/CopyingOurOwn/Dockerfile .`. This command tells Docker to build an image with the name `mychat` and tag `v1` using the Dockerfile in the specified path.
- To run our container we must then use the command `docker run --name testcontainer mychat:v1`. This command deploys a container with the name `testcontainer` using the image `mychat:v1`.
- We can validate that our container works by running the client application in our host machine and by using the command `docker logs testcontainer` to see the output of the container or by using the Docker Desktop application. We see the message `The chat server is running...`.
- We can tag our image using the command `docker tag mychat:v1 sepsilva/chat-app:clone-build-run` and push it to Docker hub using the command `docker push sepsilva/chat-app:clone-build-run`.

#### Task 2
- For this task we will need to first build our project locally and then tell our Dockerfile to copy the jar file to the container.
- After we run the command `./gradlew build` we can find the jar file in the `libs` folder. We copy this over manually to the [folder](DockerWithBinary) that will contain our Dockerfile and the binary.
- We initiate our Docker with the command `docker init` and create a new Dockerfile. We use the same base image as before and author label. We then use the keyword `COPY` to tell Docker to copy the binary file into a given directory inside the container:
```Dockerfile
    FROM openjdk:21-jdk-slim
    
    LABEL authors="Luis"
    
    COPY basic_demo-0.1.0.jar /app/basic_demo-0.1.0.jar
```
- Then we must tell the container to execute the server and pass the port number, similar to [task 1](#task-1):
```Dockerfile
    FROM openjdk:21-jdk-slim
    
    LABEL authors="Luis"
    
    COPY basic_demo-0.1.0.jar /app/basic_demo-0.1.0.jar
    
    ENTRYPOINT ["java", "-cp", "/app/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]
```
- We then build our Docker image and run the container as we did in [task 1](#task-1).
- We also tag and push this [image to Docker hub](https://hub.docker.com/layers/sepsilva/chat-app/binary/images/sha256-710cb92c88cca3c56ef6acceed27296e187b63ee04ae1a034f48d8055670cecb?context=repo) as we did in previously 
#### Task 3
- In this task we will have 2 containers, one that is responsible for building our application and sharing the binary with the container which will run it. To do this we must have a Docker compose file that will manage both containers.
##### Part 1 - Building the application
- We start by creating our base container, the one responsible for building. As in the previous tasks, we specify a base image but we must identify this container as a builder container. We also specify a volume that will be shared with the other container:
```Dockerfile
    FROM openjdk:17-jdk-slim as dockerdoublecontainer-builder
    
    LABEL authors="Luis"
```
- We then run the commands to clone and build the application, and place our container's current directory to the binary location:
```Dockerfile
    FROM openjdk:17-jdk-slim as dockerdoublecontainer-builder
    
    LABEL authors="Luis"
    
    RUN apt-get update && apt-get install -y git

    RUN mkdir -p /cloneHome
    WORKDIR /cloneHome
    
    RUN git clone https://bitbucket.org/pssmatos/gradle_basic_demo/src/master .
    
    RUN ./gradlew build
    
    RUN mkdir /outputBinary && cp build/libs/basic_demo-0.1.0.jar /outputBinary/
    
    WORKDIR /outputBinary
```
- We then build our image, tag it and push it to [Docker hub](https://hub.docker.com/layers/sepsilva/chat-app/build/images/sha256-ac170ca0f7235844a629ce586c64ea8efe23099dd95e7d1e71f457bbcc168f5e?context=repo).
- Pushing our image to Docker hub is required in order to execute [Part 2.2](#part-22---using-a-hosted-image-as-our-base-image-to-run-the-application) of this task.

##### Part 2 - Running the application
- We can run our binary in 2 ways:
  - Locally: we copy the binary from the builder container to the runner container and run it with both containers being run on the same host;
    - This has the advantage of not needing to push the binary to a repository, but it has the disadvantage of needing to run both containers on the same host, since one is dependant on the other.
  - Remotely: we push the [build image](DockerDoubleContainer/DockerfileBuild) to Docker hub and then use that as our base image for our run container.
    - This removes the strict dependency of both containers being run on the same host, but it requires the binary to be pushed to a repository. 

###### Part 2.1 - With build container locally
- We start a new [Dockerfile](DockerDoubleContainer/DockerfileRun) that uses the same base image as always:
```Dockerfile
    FROM openjdk:17-jdk-slim

    LABEL authors="Luis"
```
- The key is the copy of the binary file where we specify the name of the container that contains the binary and where we copy it from and to:
```Dockerfile
    FROM openjdk:17-jdk-slim

    LABEL authors="Luis"
    
    COPY --from=dockerdoublecontainer-builder /outputBinary/basic_demo-0.1.0.jar /app/basic_demo-0.1.0.jar
```
- We place ourselves in the directory containing the binary and then use the keyword `CMD` to specify the command to be executed by the container:
```Dockerfile
    FROM openjdk:17-jdk-slim

    LABEL authors="Luis"
    
    COPY --from=dockerdoublecontainer-builder /outputBinary/basic_demo-0.1.0.jar /app/basic_demo-0.1.0.jar
    
    WORKDIR /app


    CMD ["java", "-cp", "/app/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]
```
- To run we the application we must first set up the generated [Docker compose file](DockerDoubleContainer/compose.yaml) to manage the building of both images and execution of the containers.
- In our Docker compose file we must specify 2 services, named builder and runner:
```yaml
version: '3.8'

    services:
      builder:
        
      runner:
        build:
```
- We specify the build context for each service (where the Dockerfiles are located) and which Dockerfile to use:
```yaml
version: '3.8'

services:
  builder:
    build:
      context: .
      dockerfile: DockerfileBuild

  runner:
    build:
      context: .
      dockerfile: DockerfileRun
```
- To the runner service we must specify that it depends on the builder so that the builder is run first:
```yaml
version: '3.8'

services:
  builder:
    build:
      context: .
      dockerfile: DockerfileBuild

  runner:
    build:
      context: .
      dockerfile: DockerfileRun
    depends_on:
      - "builder"
```
- To run these we must use the command `docker-compose up` in the directory containing the Docker compose file. This will build the images and run the containers as specified in the compose file.
- We can't push Docker compose files to Docker hub and pushing the runner image is not possible since it's depedant on the builder. So we'll explore a new solution, which is to use the builder as a base image.

###### Part 2.2 - Using a hosted image as our base image to run the application
- We create a new [Dockerfile](DockerDoubleContainer/DockerfileRunRemote). Instead of specifying a base image that has Java installed, we specify the [image that we pushed to Docker hub](https://hub.docker.com/layers/sepsilva/chat-app/build/images/sha256-ac170ca0f7235844a629ce586c64ea8efe23099dd95e7d1e71f457bbcc168f5e?context=repo):
```Dockerfile
    FROM sepsilva/chat-app:build

    LABEL authors="Luis"
```
- Then we must place ourselves in the directory containing the binary and specify the command to be executed by the container:
```Dockerfile
    FROM sepsilva/chat-app:build

    LABEL authors="Luis"
    
    WORKDIR /app

    CMD ["java", "-cp", "/app/basic_demo-0.1.0.jar", "basic_demo.ChatServerApp", "59001"]
```
- In this case we can build the image similar to the previous tasks, and run the container by itself since the base image has already been uploaded.
- The decoupling of container dependancies allows us to push the container to Docker hub, so we build, tag and push our [image to Docker hub](https://hub.docker.com/layers/sepsilva/chat-app/run-remote/images/sha256-3df88b85b0bb63533689cc3cab4e6ebea137a76453f733803b56cffbb9424768?context=repo).

## Conclusion
This assigment was quite useful to understand how the process of containerization works, how we can have containers interact with each other and how we can share these standard environments.  
We saw that the containerization process is quite lighter than the virtualization process, making it a great way to share an application along with all the items required to run it.
We also saw how the Docker compose  can be used to automate the deployment of multiple and even dependant containers, similar to Vagrant.