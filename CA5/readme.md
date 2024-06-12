Part 1 - Tutorial in class PP
- We create a container based on this command `docker run -d -p 8080:8080 -p 50000:50000 -v jenkins-data:/var/jenkins_home --name=jenkins jenkins/jenkins:lts-jdk17`
- By going to localhost:8080 we can access the Jenkins container and see it is starting;
- We go into our container to retrieve the password by cd var/jenkins_home/secrets and then cat initialAdminPassword;
- We are prompted to install plugins and we choose the suggested plugins;
- We skip creating first admin user;
- We set the URL to localhost:8080;
- We restart Jenkins;
- We can then log in using the user admin and our initial password (c7f3f21051fc4831855ef364f724c85a);
- We start a new job to execute the pipeline named `pipeline_job` and selecting the pipeline option;
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
- We save the job and select build now and then we the build is successful;

Part 2 - Using our CAs to build a pipeline - CA2/Part1
- We create a new pipeline job named `pipeline_ca2_part1`;
- In our CA5 folder we create a new [jenkins file named JenkinsfileCA2P1](JenkinsfileCA2P1);
- In our pipeline job we specify our repository link, branch and the Jenkinsfile path that it should execute;
- We do not need to add credentials as our repository is public;
- In our [jenkinsfile](JenkinsfileCA2P1) we add:
```groovy
pipeline {
    agent any

    stages {
        //What repository to checkout
        stage('Checkout') {
            steps {
                echo 'Checking out repo...'
                git branch: 'main', url: 'https://github.com/sepsilva/devops-23-24-PSM-1231869.git'
                dir('CA2/Part1/master') {
                }
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

Part 3 - Using our CAs to build a pipeline - CA2/Part2
- We need to run docker in docker as in this [guide](https://www.jenkins.io/doc/book/installing/docker/);
- We create the [Dockerfile](Dockerfile) and run the following commands:
  - `docker network create jenkins` : create a Jenkins network;
  - `docker run --name jenkins-docker --rm --detach --privileged --network jenkins --network-alias docker --env DOCKER_TLS_CERTDIR=/certs --volume jenkins-docker-certs:/certs/client --volume jenkins-data:/var/jenkins_home --publish 2376:2376 docker:dind` : run docker in docker container;
  - `docker build -t myjenkins-blueocean:2.452.2-1 .` : build the docker image, based on [Dockerfile](Dockerfile);
  - `docker run --name jenkins-blueocean --restart=on-failure --detach --network jenkins --env DOCKER_HOST=tcp://docker:2376 --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 --volume jenkins-data:/var/jenkins_home --volume jenkins-docker-certs:/certs/client:ro --publish 8080:8080 --publish 50000:50000 myjenkins-blueocean:2.452.2-1` : run the jenkins blue ocean image;

- We access jenkins throuhg `localhost:8080` and install the suggested plugins plus, HTML publisher and Docker pipeline;
- We need to add our docker hub credentials to Jenkins. To do so we go to Manage Jenkins -> Manage Credentials -> Jenkins -> Global credentials -> Add credentials;
- We had to create a new docker hub account, since the previous one was not working as we used github login. The published images can be found [here](https://hub.docker.com/repository/docker/silvasilva/devops/general);
- We create a new pipeline job named `pipeline_ca2_part2` and choose from script and add the script from [JenkinsfileCA2P2](JenkinsfileCA2P2);
- We run the pipeline and it is successful;