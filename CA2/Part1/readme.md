# Report for Class assignment 2 - Part 1 - Luís Silva 1231869

## Report Structure
- [Description of assignment](#description-of-assignment)
  - [Build tools](#build-tools) 
- [Using Build tools]()
  - [Task 1](#task-1)
  - [Task 2](#task-2)
  - [Task 3](#task-3)
  - [Task 4](#task-4) 
- [Conclusions and issues](#conclusions-and-issues)

## Description of assignment
The following assignment explores build tools, specifically Gradle, and how these are used to automate tasks necessary for software projects.  
As a base, the [following](https://bitbucket.org/pssmatos/gradle_basic_demo/src/master/) project was used by using the `git clone` command. The project was then complemented with the following tasks:
- [Task 1](#task-1): Create a task to automatically execute the server
- [Task 2](#task-2): Add a unit test to the project and add the needed JUnit dependencies
- [Task 3](#task-3): Create a Copy type task that makes a backup of the project's source files
- [Task 4](#task-4): Create a Zip type task that archives the project's source files

### Build tools
Build tools are used in software projects and are responsible for automating tasks such as compiling, testing, packaging, and deploying software. They are also responsible for managing a software's dependencies automatically.  
These tools facilitate the development and maintenance of software projects, especially large ones.  

Two well known offers are [Maven](https://maven.apache.org/) and Gradle. [Gradle](https://gradle.org/maven-vs-gradle/) is a very well known tool, offering better features and performance than Maven and was the tool used in this project.

## Task preparation
We start by cloning the [base project]() using the `git clone` command.  
The project consists in a chat service application, so we'd need to run a server and client.  

The first step is to start the build process of the project using the `./gradlew build` command, making sure all dependencies are taken care of, tasks are executed and the application is compiled.  

Then we must run the server and client:  
 - To run the server we must manually run it with `java -cp build/libs/basic_demo-0.1.0.jar basic_demo.ChatServerApp <server port>` where we must indicate the server port where the application will listen to.  
 - To run the client we must run the `./gradlew runClient` task.  

We see a difference in the commands and notice that to run the Client, we have a Gradle task set up that avoids the need to manually run the client. This is a good example of how Gradle can be used to automate tasks.

Given this, the tasks developed in this project focus on the [build.gradle](master/build.gradle) file, which is where we will define tasks and dependencies.

### Task 1
- To execute the server automatically, we navigate to [build.gradle](master/build.gradle) and we start by declaring a new `task` called runServer;
```groovy
task runServer() {
}
```
- Since we want to execute a Java class the `type` is a `JavaExec` and we also specify that it `dependsOn` the `classes` task to make sure the needed classes are first compiled;
```groovy
task runServer(type:JavaExec, dependsOn: classes) {
}
```
- Inside the task itself we can specify the `group` that this task belongs to and give it a `description`;
```groovy
task runServer(type:JavaExec, dependsOn: classes) {
    group = "DevOps"
    description = "Launches a chat server that runs on localhost:59001"
}
```
- For this type of task we must specify the `classpath` and `mainClass` properties. We then use the `args` to specify what arguments should be passed in the main class. In this case we specify that the server will always listen to port 59001;
```groovy
task runServer(type:JavaExec, dependsOn: classes) {
    group = "DevOps"
    description = "Launches a chat server that runs on localhost:59001"

    classpath = sourceSets.main.runtimeClasspath

    mainClass = 'basic_demo.ChatServerApp'

    args '59001'
}
```
- We execute both `./gradlew runServer` and `./gradlew runClient` to see if the server and client are running correctly and  commit our code using `git commit -m "(Message)"` and close the created [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/7) and push our code to our remote repository using `git push`.
### Task 2
- Build tools also manage the dependencies of a project. In this case we want to add a unit test to the project and add the needed JUnit 4.12 dependencies;
- In the [build.gradle](master/build.gradle), we locate the `dependencies { }` block and add the following:
```groovy
dependencies {
  // Use Apache Log4J for logging
  implementation group: 'org.apache.logging.log4j', name: 'log4j-api', version: '2.11.2'
  implementation group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.11.2'
  
  implementation 'junit:junit:4.12'
}
```
- We can then create the test class, [AppTest](src/main/java/basic_demo/AppTest) and add the provided code. We then commit and close the related [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/8) and push our code.

### Task 3
- Tasks can be of various types. In this case we want a `Copy` type task called backup;
```groovy
task backup(type: Copy) {
}
```
- We can specify the `group` and `description` of similar to [task 1](###Task-1) however more importantly, we must define where the task should copy the file from and then into. To do this, we define two variables `sourceDir` and `backupDir` and use them in the `from` and `into` properties to tell Gradle where to copy from and to;
```groovy
task backup (type:Copy) {

  // Group and description go here
  
  def sourceDir = file('./src')
  def backupDir = file("${buildDir}/backup/src");

  from sourceDir into backupDir
}
```
- We can run the task using `./gradlew backup` and, after navigating to the root of the cloned project, inside the `build` directory we see a new folder `backup` with copies of the project's `src` folder. We then commit and close the related [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/9).
### Task 4
- For this task we follow the same steps as [task 3](###Task-3) but instead of a `Copy` type task we use a `Zip` type task named archive;
```groovy
task archive(type: Zip) {
}
```
- We can also define the group and description, however compared to the `Copy` task, we only need to tell Gradle what files to archive. We do this by  defining a variable named `sourceDir` with the file path and using the keyword `from` point Gradle to that path.  We can also define the name of our generated archive file using the `archiveFileName`;
```groovy
task archive (type:Zip) {
    // Group and description go here

    def sourceDir = file('./src')

    from sourceDir

    archiveFileName = "archivedSRC.zip"
}
```
- We run the task using `./gradlew archive` and inside our `build` we see a folder named `distributions` with the zip file `archiveSRC.zip` inside. We then commit and close the related [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/10).

## Conclusions
In this assigment we saw how powerful build tools can be for projects of any size. They can be used to simplify tasks, especially more mundane ones and we see how, when chained correctly, a series of tasks can be executed in a way that insures that our code is structurally sound and ready to be deployed.  
We also saw how dependencies can be easily managed, eliminating a potential source of issues for developers.  

Overall this assigment was a great introduction to build tools however, it will be even more interesting when a complex set of tasks is built to assure the quality of the code and the project as a whole.