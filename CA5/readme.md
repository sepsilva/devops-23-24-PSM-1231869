# Report for Class assignment 5 - Luís Silva 1231869
## Report Structure
- [Description of assigment](#description-of-assignment)
- [Container management - Docker compose](#containerization---docker)
  - [Task preparation](#task-preparation)
    - [Task 1 - Running a simple Jenkins pipeline script](#task-1---running-a-simple-jenkins-pipeline-script)
    - [Task 2 - Using our CAs to build a pipeline - CA2/Part1](#task-2---using-our-cas-to-build-a-pipeline---ca2part1)
    - [Task 3 - Using Jenkins and Docker together for CA2/Part2](#task-3---using-jenkins-and-docker-together-for-ca2part2)
- [Conclusion](#conclusion)
  - [Class assigment wrap-up](#class-assigment-wrap-up)
## Description of assignment
This assigment focuses on using Jenkins to create a pipeline for both [CA2/Part1](../CA2/Part1/master) and [CA2/Part2](../CA2/Part2/react-and-spring-data-rest-basic).

## Pipeline creation with Jenkins
[Jenkins](https://www.jenkins.io/) is a tool that allows the automation of tasks like building, testing and deploying software. It is mostly used as a continuous integration and continuous delivery tool.  
Jenkins has the capability to create a series of steps (pipineline) that can be executed in sequence, namely checking out a repository, running tests, compiling code, generating documentation and automatically deploying the application.
The scripts can be easily written and scheduled to run at specific times or when a specific event occurs, which allows an application to be continuously updated  without needing manual intervention.
### Task preparation
We will deploy our Jenkins server using docker. The tasks for this assignment are as follows:
- Task 1: Create a Jenkins server and a pipeline with the given script;
- Task 2: Create a pipeline for the [CA2/Part1](../CA2/Part1/master) project. The task should include the following stages:
  - Checkout the repository;
  - Assemble the project;
  - Test the project;
  - Archive the generated binary file from the assemble stage.
- Task 3: Create a pipeline for the [CA2/Part2](../CA2/Part2/react-and-spring-data-rest-basic) project. The task should include the following stages:
  - Checkout the repository;
  - Build the project;
  - Test the project;
  - Archive the generated binary file from the build stage;
  - Build a docker image;
  - Push the docker image to Docker Hub.

#### Task 1 - Running a simple Jenkins pipeline script
- We create a container based on the command: `docker run -d -p 8080:8080 -p 50000:50000 -v jenkins-data:/var/jenkins_home --name=jenkins jenkins/jenkins:lts-jdk17`
- By going to `localhost:8080` we can access the Jenkins container and see it is starting;
- We install the suggested plugins and create an admin user and restart Jenkins;
- We can then log in and start a new job to execute the pipeline named `pipeline_job` and selecting the pipeline option;
- We paste the following code (provided in CA tutorial) into the pipeline script section:
```groovy
pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        echo 'Checking out...'
        git 'https://bitbucket.org/pssmatos/gradle_basic_demo'
      }
    }
    stage('Build') {
      steps {
        echo 'Building...'
        sh './gradlew clean build'
      }
    }
    stage('Archiving') {
      steps {
        echo 'Archiving...'
        archiveArtifacts 'build/distributions/*'
      }
    }
  }
}
```
- We see the pipeline script is composed of 3 stages: checkout, build and archiving. Each has 2 steps: echo and the command to be executed;
- We save the job and select build now and then we the build is successful;
- In this task we left the pipeline script in Jenkins, but in the next tasks we will use a Jenkinsfile in the repository.

#### Task 2 - Using our CAs to build a pipeline - CA2/Part1
- We will use the same Jenkins container as in the previous [task](#task-1---running-a-simple-jenkins-pipeline-script);
- Aditionality we will install the HTML Publisher plugin to allow us to publish the test results;
- We create a new [Jenkinsfile](JenkinsfileCA2P1) in our repository. Our Jenkins container will be instructed to use this file to execute the pipeline;
- We will follow a similar structure as before, using the echos to more easily understand the state of the pipeline:
```groovy
pipeline {
    agent any
    stages {
    //What repository to checkout
        stage('Checkout') {
            steps {
                echo 'Checking out repo...'
                git branch: 'main', url: 'https://github.com/sepsilva/devops-23-24-PSM-1231869.git'
            }
        }

        //Assemble the project. Different from build, assemble is used to create the binary file
        stage('Assemble') {
            steps {
                echo 'Assembling project...'
                dir('CA2/Part1/master') {
                //Change gradlew permissions
                    sh 'chmod +x gradlew'
                    sh './gradlew assemble'
                }
            }
        }

        //Test stage to validate project
        stage('Test') {
            steps {
                echo 'Testing project...'
                dir('CA2/Part1/master') {
                    sh './gradlew test'
                }
            }
        }

        //Archive the generated binary file from the assemble stage
        stage('Archive') {
            steps {
                dir('CA2/Part1/master') {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                }
            }
        }
    }
}
```
- All stages have the `dir('CA2/Part1/master')` command to specify the directory where the commands should be executed. The executed commands are `git clone` to clone the repository, `./gradlew assemble` and `./gradlew test` to assemble and test the project;
- We also add a stage, `Archive`, that allows Jenkins to archive the generated binary file from the assemble stage;
- We create a new pipeline job named `pipeline_ca2_part1`;
- Since our repository is public, we do not need to add credentials. However if it was private, we would need to add them using Jenkins' credentials manager;
- We need to tell Jenkins where to find the script path (CA5/JenkinsfileCA2P2) to run within the repository when building our pipeline;
- We run the pipeline and it is successful. We can also see the generated binary file in the artifacts section of the job;

#### Task 3 - Using Jenkins and Docker together for CA2/Part2
- For this task we need to use Jenkins to build and publish a functional image that could be used to run our application;
- We create a new [Dockerfile](Dockerfile) in the repository to build the image for the Jenkins container;
- We will discard our previous Jenkins container and use the following commands to launch the needed containers:
  - `docker network create jenkins` : create a Jenkins network;
  - `docker run --name jenkins-docker --rm --detach --privileged --network jenkins --network-alias docker --env DOCKER_TLS_CERTDIR=/certs --volume jenkins-docker-certs:/certs/client --volume jenkins-data:/var/jenkins_home --publish 2376:2376 docker:dind` : run docker in docker container;
  - `docker build -t myjenkins-blueocean:2.452.2-1 .` : build the docker image, based on the Jenkins Dockerfile;
  - `docker run --name jenkins-blueocean --restart=on-failure --detach --network jenkins --env DOCKER_HOST=tcp://docker:2376 --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 --volume jenkins-data:/var/jenkins_home --volume jenkins-docker-certs:/certs/client:ro --publish 8080:8080 --publish 50000:50000 myjenkins-blueocean:2.452.2-1` : run the jenkins blue ocean image;
- We can then access Jenkins through `localhost:8080` and install the suggested plugins plus the HTML Publisher and Docker Pipeline;
- We need to add our Docker Hub credentials to Jenkins. To do so we go to Manage Jenkins -> Manage Credentials -> Jenkins -> Global credentials -> Add credentials;
- Now we can start working on our [Jenkinsfile](JenkinsfileCA2P2). As before we have the checkout, build and test stages. A report of our tests is also generated with the help of the HTML Publisher plugin. At the start of the script we also specify an environment variable for our CA project path:
```groovy
pipeline {
    agent any

    environment {
        CA_DIR = 'CA2/Part2/react-and-spring-data-rest-basic'
    }

    //CHECKOUT STAGE
    stages {
        //What repository to checkout
        stage('Checkout') {
            steps {
                echo 'Checking out repo...'
                git branch: 'main', url: 'https://github.com/sepsilva/devops-23-24-PSM-1231869.git'
            }
        }

        //ASSEMBLE STAGE
        stage('Assemble') {
            steps {
                echo 'Assembling project...'
                dir(env.CA_DIR) {
                    //Change gradlew permissions
                    sh 'chmod +x gradlew'
                    sh './gradlew assemble'
                }
            }
        }

        //TEST STAGE
        stage('Test') {
            steps {
                echo 'Testing project...'
                dir(env.CA_DIR) {
                    sh './gradlew test'
                }
            }
        }

        //GENERATE JAVADOCS AND PUBLISH IT
        stage('Javadocs') {
            steps {
                echo 'Generating Javadocs...'
                dir(env.CA_DIR) {
                    sh './gradlew javadoc'
                }
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'CA2/Part2/react-and-spring-data-rest-basic/build/docs/javadoc',
                    reportFiles: 'index.html',
                    reportName: 'Javadoc'
                ])
            }
        }

        //ARCHIVE PROJECT
        stage('Archive') {
            steps {
                echo 'Archiving project...'
                dir(env.CA_DIR) {
                    archiveArtifacts artifacts: 'build/libs/*.war', fingerprint: true
                }
            }
        }
```
- Now we must tell Jenkins to build the image and publish it to dockerhub. To build the docker image, we tell Jenkins to execute a script, similar to [CA4/Part2](../CA4/Part2). We tell Jenkins to name our file `DockerfileCA2Part2`:
```groovy
        //CREATE DOCKERFILE
        //Dockerfile gets placed in the CA5 directory
        stage('Create Dockerfile') {
            steps {
                echo 'Creating Dockerfile...'
                dir(env.CA_DIR) {
                    script {
                        def dockerfileContent = """
                        FROM tomcat:10-jdk17-openjdk-slim
                        COPY build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war
                        EXPOSE 8080
                        CMD ["catalina.sh", "run"]
                        """
                        writeFile file: 'DockerfileCA2Part2', text: dockerfileContent
                    }
                }
            }
        }
```
- Then we tell Jenkins to publish our image. The images can be found [here](https://hub.docker.com/repository/docker/silvasilva/devops/general):
```groovy

        //PUBLISH DOCKER IMAGE
        stage('Publish Docker Image') {
            steps {
                echo 'Publishing Docker image...'
                dir(env.CA_DIR) {
                    script {
                        def dockerImage = docker.build("silvasilva/devops:${env.BUILD_ID}", "-f DockerfileCA2Part2 .")
                        docker.withRegistry('https://index.docker.io/v1/', 'docker-id') {
                            dockerImage.push()
                        }
                    }
                }
            }
        }
    }
}
```
- On the Jenkins interface we create a new pipeline job named `pipeline_ca2_part2` and specify the script [JenkinsfileCA2P2](JenkinsfileCA2P2);
- We run the pipeline and it is successful and can confirm the image is published to dockerhub.
- Instead of deploying our containers manually, we can also use a Docker compose file to orchestrate the deployment;
- To do so, we start a new [compose file](compose.yaml). For the docker container, as in the command above, we use a base image specifically for `docker:dind`. We specify the volumes, network and alias for the container mostly, so the Jenkins container knows who to communicate with:
```yaml
services:
  docker-dind:
    image: docker:dind
    container_name: jenkins-docker
    privileged: true
    environment:
      - DOCKER_TLS_CERTDIR=/certs
    volumes:
      - jenkins-docker-certs:/certs/client
      - jenkins-data:/var/jenkins_home
    networks:
      jenkins:
        aliases:
          - docker
    ports:
      - 2376:2376
```
- Then we tell the compose file to also create a Jenkins container based on the [Dockerfile](Dockerfile) we created, set its connection with the docker-dind container and set up a network and volumes for both containers:
```yaml
  jenkins-blueocean:
    build: .
    container_name: jenkins-blueocean
    restart: on-failure
    environment:
      - DOCKER_HOST=tcp://docker:2376
      - DOCKER_CERT_PATH=/certs/client
      - DOCKER_TLS_VERIFY=1
    volumes:
      - jenkins-data:/var/jenkins_home
      - jenkins-docker-certs:/certs/client:ro
    networks:
      - jenkins
    ports:
      - 8080:8080
      - 50000:50000

volumes:
  jenkins-docker-certs:
  jenkins-data:

networks:
  jenkins:
```
- We can now more easily launch our containers using the command `docker-compose up` as we can repeat the steps we did before to set up a pipeline. Both the manual commands and compose file, set up volumes so our data is persisted even if containers are removed.
## Conclusion
In this final assigment we can catch a glimpse of how Jenkins can simplify an application's deployment and the life of developers.  
As stated in their promotional video, Jenkins can easily integrate with other tools being used and has a vast ecosystem of plugins and a large community to help with any issues that may arise.
It would've been interesting to apply this to a larger project and have a more complex, perhaps scheduled pipeline that would release test, verify and publish our application automatically.

### Issues

Running Docker in Docker (DinD), was a tough issue. On one instance, during [task 3](#task-3---using-jenkins-and-docker-together-for-ca2part2) one machine was not able to run the docker containers using the compose file, as the Jenkins container would state it did not have permission to access/run docker commands. One fix attempt was to use docker sockets however this persisted. When switching to a different machine, Jenkins would launch without issues. The cause of the problem is still unknown.
### Class assigment wrap-up
These class assignments were a great way to explore useful development tools that aren't directly used to produce code.
Tools like Git help us keep track of our code and collaborate with others.  
Maven and Gradle help us manage all the dependencies and execute tasks much easier.  
Vagrant was very interesting as a tool to create standard and predictable environments to test our applications.
Docker was similar in principle to Vagrant, however the addition of Docker compose made it possible to orchestrate more complex applications that require something like a separate database and frontend.
Finally, Jenkins ties a lot of these concept together and showed us how we can automate the delivery of our applications.  

All these tools simplify our work and help us focus on what really matters: the code. No longer do we need to spend hours, managing dependencies our thinking how we'll share this application.
It was a shame that this couldn't be explored more in depth during class but I personally think it is a great jumpoff point a great set of tools for any future developer.