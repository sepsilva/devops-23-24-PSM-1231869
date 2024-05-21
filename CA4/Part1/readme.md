Part 1 - Cloning repo
We clone https://bitbucket.org/pssmatos/gradle_basic_demo/src/master/;
We use this one instead of CA2/Part2 since it was changed previously to accomadate database integration.

Part 1.1 - Copying the entire project to the container, build and run it;
Create a [new docker file](GivenRepository/DockerCloneBuildRun)
Tell it to clone the given repository, build it and run it;
Upon startup tell it go execute runServer and pass 59001 as the port argument;


Build the image using docker build -t mychat:v1 -f CA4/Part1/CopyingOurOwn/Dockerfile .
Run the image using docker run --name testcontainer mychat:v1

Part 1.2 - Compiling binary locally and copying it to the container
We run ./gradlew build ;
We grab the jar file that is generated from libs and copy it to our main folder CA4/Part1 using cp .\basic_demo-0.1.0.jar ../../../ ;
Using CA3/Part2 Docker alternative as a base we create a new Dockerfile;
We create a dockerfile that installs jdk21 (since we're compiling locally with java 21) and copies the jar file, then starts the chat server specifically  passing 59001 as the port argument;
[DockerFile](GivenRepository/DockerWithBinary/Dockerfile)

Part 1.3 - Compiling binary in one container and copying it to the container
In this approach we will have 2 docker containers. One will clone the given repo and build it and the other will copy the binary to it and run it.

We create a new [docker file](GivenRepository/DockerDoubleContainer/DockerfileBuild) that will be used to clone and build the given repo;
We then create a new [docker file](GivenRepository/DockerDoubleContainer/DockerfileRun) that will be used to copy the binary from the first container and run it;

