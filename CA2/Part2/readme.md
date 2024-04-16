# Report for Class assignment 2 - Part 2 - Luís Silva 1231869

## Report Structure
- [Description of assigment](#description-of-assignment)
  - [Exploring build tool alternatives - Buildr](#exploring-build-tool-alternatives---buildr)
- [Task preparation](#task-preparation)
    - [Task 1](#task-1)
    - [Task 2](#task-2)
- [Conclusion](#conclusions) 

## Description of assignment
In this assigment we continue to use build tools and see how the same project differs between different build tools.
First we explore some other build tools and then apply a build task to an already existing project.


### Exploring build tool alternatives - Buildr and Ant
The choice of using one tool as opposed to another depends on many factors like the project, familiarity with the software and the need to use community resources since most of these offers have a community smaller than Gradle.  

The Apache Foundation offers some build tools that try to compete with Gradle. [Maven](https://maven.apache.org/what-is-maven.html) is one option that was explored in [CA1](../../CA1/tut-react-and-spring-data-rest) however other tools are [Buildr](https://buildr.apache.org/) and [Ant](https://ant.apache.org/).

Below are some examples of how dependencies, tasks and commands used in this class assigment would be run if Buildr was used.
- Building:
  - `./gradlew build` → `buildr build`
- Running tasks:
  - `./gradlew [TASK_NAME]` → `buildr [TASK_NAME]`
- Creating tasks:  

  - Task in Gradle Groovy
  ```groovy 
    task archiveCopy (type:Copy) {

        def jarfile = file('./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.jar')

        def destination = file('./dist')

        from jarfile into destination
    }
    ```  
   
  - Same task in Buildr using Ruby
  ```Ruby
    task :archiveCopy do
    
    jarfile = file('build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.jar')
    destination = file('./dist')
    
    cp (jarfile, destination)
    end
  ```
- Defining project dependencies  
  - Dependencies in Gradle Groovy
  ```Groovy
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    }
  ```
  - Same dependency in Buildr
  ```Ruby
    repositories.remote << 'http://www.ibiblio.org/maven2'
    compile.with 'org.springframework.boot:spring-boot-starter-data-jpa'
  ```
Though [Apache Buildr](https://buildr.apache.org/quick_start.html#first-project) seems to be a good a build tool especially for Java projects with a structure similar to Gradle, the project was [terminated](https://whimsy.apache.org/board/minutes/Buildr.html) by the Apache Foundation 2022 due to inactivity.  

Another alternative that was explored was [Apache Ant](https://ant.apache.org/manual/index.html)  with [Apache Ivy](https://ant.apache.org/ivy/history/2.5.2/tutorial/start.html).  
Below are the same examples of how commands, tasks and dependencies would work when using Ant.
- Building:
  - `./gradlew build` → `ant build`
- Running tasks:
  - `./gradlew [TASK_NAME]` → `ant [TASK_NAME]`
- [Creating tasks](https://ant.apache.org/manual/using.html#buildfile):
  - Copy task in Ant's build.xml file:
  ```xml
  <project name="react-and-spring-data-rest-basic" default=".">
    <property name="jarfile" location="./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.jar"/>
    <property name="destination" location="./dist"/>
    <target name="archiveCopy">
        <copy file="${jarfile}" todir="${destination}"/>
    </target>
  </project>
  ```
- [Dependency management in Ant](https://ant.apache.org/ivy/history/2.5.2/tutorial/dependence.html):  
  - Dependency management with Ant isn't as straightforward as with Gradle, Maven or Buildr. Ant focuses more on tasks and project bulding, however, Ivy can integrate with Ant after configuring Ant's build.xml file.
  Ivy uses its own xml file to manage dependencies and to ivy.xml we can add:
  ```xml
  <ivy-module version="2.0">
    <info organisation="org.springframework.boot" module="spring-boot-starter-data-jpa"/>
    <dependencies>
        <dependency org="org.springframework.boot" name="spring-boot-starter-data-jpa" rev="VERSION"/>
    </dependencies>
  </ivy-module>
  ```
  
## Task preparation
We start by creating a new branch using the `git branch` named `tut-basic-gradle` and switch to it using the `git checkout` command.  

This project uses the spring framework, similar to what was done in [CA1](../../CA1/tut-react-and-spring-data-rest) however, using [spring initializr](https://start.spring.io/) we select a new spring project that uses Gradle Groovy as a build tool and copy the [src](../../CA1/tut-react-and-spring-data-rest/basic/src) files from CA1.  
After having our starting point we can run the command `./gradlew tasks` to see all the tasks available.

At its current state, if we run the project using `./gradlew bootrun` we see that the project is running but when we access `localhost:8080` we see that the page is empty.  
This is due to our project not having a configured frontend. To solve this we add the provided code to our [build.gradle](react-and-spring-data-rest-basic/build.gradle) file and add the necessary scripts to the [package.json](react-and-spring-data-rest-basic/package.json) file:
- Plugin for build.gradle (addition to existing block):
```groovy
plugins {
	id 'org.siouan.frontend-jdk17' version '8.0.0'
}
```
- Frontend block for build.gradle:
```groovy
frontend {
  nodeVersion = "16.20.2"
  assembleScript = "run build"
  cleanScript = "run clean"
  checkScript = "run check"
}
```
- Scripts for package.json (addition to existing block):
```json
"scripts": {
    "webpack": "webpack",
    "build": "npm run webpack",
    "check": "echo Checking frontend",
    "clean": "echo Cleaning frontend",
    "lint": "echo Linting frontend",
    "test": "echo Testing frontend"
  }
```
- Package manager for package.json:
```json
"packageManager": "npm@9.6.7"
```

After adding the needed items to render the frontend of our project we run `./gradlew build` and `./gradlew bootrun` and notice our project is running correctly. We can then consult `localhost:8080` and see a page with the table similar to CA1, meaning that we can now render our frontend.

After that, the following tasks were completed:
- [Task 1](#task-1): Create a Copy task to copy the generated .jar [file](react-and-spring-data-rest-basic/build/libs/), generated after building the project to a folder named dist in the root of the project.
- [Task 2](#task-2): Create a Delete task that deletes the items generated by webpack in the [built](react-and-spring-data-rest-basic/src/main/resources/static/built) folder. Make sure that this task runs when the clean task is executed.

### Task 1
- To create a Copy task, we navigate to [build.gradle](react-and-spring-data-rest-basic/build.gradle) and declare a new `task` called archiveCopy. Since this is a copy task we can include the task type parameter as `type:Copy`:
```groovy
task archiveCopy(type:Copy) {
}
```
- We can add a `group` and `description` to our task and, define where the task should copy from, with the variable `jarfile`, and where the copy should be placed with the variable `destination`.  
  Finally we add the `from` and `into` properties to the task to tell Gradle where to copy from and to:
```groovy
task archiveCopy(type:Copy) {
  // Group and description go here
  
  def jarfile = file('./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.jar')
  def destination = file('${rootProject.projectDir}/dist')
  
  from jarfile into destination
}
```
- We can then run `./gradlew build` and `./gradlew archiveCopy` and see that a folder called `dist` is created with the copied file inside.  
  We can then commit our code and close the created [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/16).
### Task 2
- To create the required delete task, once again, in the build.gradle file we declare a new `task` called deleteWebpackItems with the type parameter as `type:Delete`. We can also add a group and description:
```groovy
task deleteWebpackItems (type: Delete) {
    // Group and description go here
}
```
- Similarly to [task 1](#task-1) we should also define a variable `webpackItems` that points to the folder location to be deleted and we request the deletion using the `delete method`.  
  The more interesting part is the chaining of tasks. We want this task to be run every time the `clean`task is executed so we must add a `dependsOn` outside our current task block:
```groovy
task deleteWebpackItems (type: Delete) {
  // Group and description go here
  
  def webpackItems = file("./src/main/resources/static/built/")
  
  delete webpackItems
}

clean.dependsOn deleteWebpackItems
```
- To test this we can run `./gradlew build` and `./gradlew deleteWebPackItems` and see that the resources are deleted. To make sure that the task dependency is correct we run the task `./gradlew clean` and see that the resources are deleted once again.  
  We can then commit our code and close the created [issue](https://github.com/sepsilva/devops-23-24-PSM-1231869/issues/17).

## Conclusion
We explored some different build tools and tried to understand their differences from Gradle. Buildr seemed like a good alternative to Gradle, however it has been terminated and Ant is more focused on tasks and building projects, needing an additional tool like Ivy to manage dependencies.  
Therefore it was easy to understand the appeal of Gradle to developers and how it became so popular. It offers a robust set of tools allowing us to facilitate and automate a lot of the tasks needed when building a project.  

Overall this project shared some similarities to what was done in [CA1](../../CA1/tut-react-and-spring-data-rest) however, in this one we saw how we could create a spring application from scratch only needing to provide our java source files.
We also saw how to chain Gradle tasks and create dependencies between them, allowing us to execute tasks in a specific order and making sure core/important tasks are always executed in the proper order.