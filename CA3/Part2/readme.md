# Report for Class assignment 3 - Part 2 - Luís Silva 1231869

## Report Structure
- [Description of assigment](#description-of-assignment)
- [Virtualization automation - Vagrant with Virtual Box](#virtualization-automation---vagrant-with-virtual-box)
  - [Task preparation - Vagrant VB](#task-preparation---vagrant-vb)
    - [Task 1](#task-1)
    - [Task 2](#task-2)
    - [Task 3](#task-3)
      - [Task 3.1](#task-3---removing-dependency-on-repository-state-and-simplifying-vagrant-file) 
- [Alternative - Other virtualization providers - Qemu](#alternative---other-virtualization-providers---qemu)
- [Task preparation - Vagrant Qemu](#task-preparation---vagrant-qemu)
- [Alternative - Containerization with Docker](#alternative---containerization-with-docker)
  - [Task preparation - Docker](#task-preparation---docker) 
    - [Task 4](#task-4)
    - [Task 5](#task-5)
- [Conclusion](#conclusions)

## Description of assignment
In this assigment we explore Virtualization automation tools like Vagrant and see how they can help us with creating standardized specific, virtual machine environments, capable of running our applications.  
We also explore an alternative to virtualization automation which is containerization using Docker, to deploy standard predictable containers, so we can even more easily and quickly deploy our applications.



## Virtualization automation - Vagrant with Virtual Box
Virtualization, as already explored in [CA3 Part1](../Part1/readme.md), is a tool that we can use to build a virtual machine that can run our applications without the need for multiple physical machines.
This management of virtual machines is handled by a hypervisor that can manage physical's machine's resources.  

However we still have to manage these virtual machines manually, such as updating applications, installing new software, etc. 
Vagrant fills this gap by automating the process of creating and managing virtual machines.  
This helps us guarantee that our applications will run in the same environment every time, assuming we use the same Vagrant file.

Vagrant can use different providers to create virtual machines, such as Virtual Box or Qemu.

Virtual Box is a user-friendly hypervisor that can be easily integrated with Vagrant and is used in this assignment.
We've already installed Virtual Box in [CA3 Part1](../Part1/readme.md), so we can now focus on Vagrant.
### Task preparation - Vagrant VB
We must first install Vagrant in our host machine. Since we're on windows we use the installer from the [Vagrant website](https://www.vagrantup.com/downloads).
After installing Vagrant we can open a terminal and check if it was installed correctly by running the command `vagrant --version`.
Vagrant uses Virtual Box as a provider by default, and we have this already installed from the previous assigment

This part of the assigment is divided into 3 tasks:
- [Task 1](#task-1): Use the [repository](https://bitbucket.org/pssmatos/vagrant-multi-spring-tut-demo/src/master/) provided to explore the Vagrant file and understand how it works.
- [Task 2](#task-2): Create our own Vagrant file that deploys 2 virtual machines, one with a web application and another with a database. The web application should use the Spring application created in [CA2 Part2](../../CA2/Part2).
- [Task 3](#task-3): Make is so both virtual machines can communicate with each other. The web application should be able to access the database virtual machine.  
- [Task 3.1](#task-3---removing-dependency-on-repository-state-and-simplifying-vagrant-file): See how to avoid being dependant on repository state and how to simplify Vagrant file.

#### Task 1
- First we clone the [repository](https://bitbucket.org/pssmatos/vagrant-multi-spring-tut-demo/src/master/) into our host machine and explore the given Vagrant file.
- We see that it is set up to create two different Virtual Machines, one named `web` and another named `db` with the IP's defined as `192.168.56.10` and `192.168.56.11` respectively.
- To specify the commands we want to be run inside our virtual machines, we open the following block:
  ```ruby
  config.vm.provision "shell", inline: <<-SHELL
  
  SHELL
  ```
  Inside we can run commands like `sudo apt-get update` or `sudo apt-get install openjdk-11-jdk` to update our machine and install open jdk 11.
- To specify what Linux distribution we want to use we can use the following block:
  ```ruby
  config.vm.box = "ubuntu/bionic64"
  ```
  This specifies that we want to use the ubuntu bionic 64 image.
- Both machines run ubuntu bionic 64 and install open jdk 11.
  - We can configure the IP's of the machines and the ports they expose to the outside. For the db machine we:
    ```ruby
    config.vm.network "private_network", ip: "192.168.56.11"
    db.vm.network "forwarded_port", guest: 8082, host: 8082
    db.vm.network "forwarded_port", guest: 9092, host: 9092
    ```
    
  - The db machine additionally installs and H2 database that runs on port 9092 (Data) and 8082 (Console).
  ```ruby
  db.vm.provision "shell", inline: <<-SHELL
  wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
  SHELL  
  ```
- From analyzing the given Vagrant file we see that it allows us to:  
  - Choose what Linux distribution we want to use;
  - Specify the commands we want to run inside our virtual machines, which:
    - Allows us to install software;
    - Configure setings such as IP's/ports or the amount of memory the machine has;
    - Move into and out of directories in our VM;
    - Copy files that are inside our machine or from the outside (e.g. GitHub repository);
    - Execute application specific commands like `./gradlew build`;  
    
- We navigate to where the Vagrant file is and, we run the command `vagrant up` to start the virtual machines.
- Besides the feedback on the terminal we can also see the machines running in Virtual Box.
- We can use these virtual machines as the ones we created in [CA3 Part1](../Part1/readme.md). We can also SSH into them using the command `vagrant ssh [VM NAME]`.
- We can also access the web application by using the IP and port specified in the Vagrant file.  
  - For example, to access the H2 console we use our browser to go to `192.168.56.11:8082`.
- We can then use the command `vagrant halt` to stop the virtual machines and `vagrant destroy` to delete them.

#### Task 2
- To create a Vagrant file that runs our Spring application and the H2 database, start a new Vagrant file using the comando `vagrant init`.
- We can use the previous Vagrant file as a template and modify it to our needs:
  - For this task we will focus on the web VM, so we will leave the db VM as is. As for general configurations we will be running the same Linux distribution, IP's and ports. We will however install java 17 instead of 11.
- We start by adding our web vm block, that runs ubuntu bionic 64, with the IP and ports specified in the previous Vagrant file. This vm will have 1Gb of RAM:
  ```ruby
    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"
      web.vm.network "private_network", ip: "192.168.56.10"

      #SET RAM AMOUNT FOR VM TO 1GB
      web.vm.provider "virtualbox" do |v|
          v.memory = 1024
      end
  end
  ```
- To avoid wasting time, we tell Vagrant to stop the vm deployment in case it encounters an error by adding the command `set -e`.
- We want our web vm to run the application we created in [CA2 Part2](../../CA2/Part2) so we must add commands to clone our repository, navigate to it and execute gradle tasks, namely build and bootRun.
  - We use nohup so the command executs even if we close our terminal and since we can't directly see the output of the bootRun command we can pipe the output to a log file. If we SSH into our web vm we can see this log file in our home directory:
  ```ruby
    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"
      web.vm.network "private_network", ip: "192.168.56.10"

      #SET RAM AMOUNT FOR VM TO 1GB
      web.vm.provider "virtualbox" do |v|
          v.memory = 1024
      end

      #CLONE REPO AND CHANGE GRADLE WRAPPER PERMISSIONS AND EXECUTE BUILD
      git clone https://github.com/sepsilva/devops-23-24-PSM-1231869
      cd devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic
      chmod u+x gradlew
      ./gradlew build

      #INSTEAD OF USING GRADLE BOOTRUN, REDIRECT ERROR OUTPUT TO A LOG
      nohup ./gradlew bootRun > /home/vagrant/spring-boot-app.log 2>&1 &
  end
  ```
- We can then run the command `vagrant up` to start our virtual machines and check if the web vm boots up correctly.
- First we SSH into our web using `vagrant ssh web` and when inside our vm we can run `nano spring-boot-app.log` and see our log. We can exit our SSH session using the `exit` command;
- We then check everything using our browser by going to `192.168.56.10:8080` and see our employee table. This is the same table we saw in CA2/Part2 when we accessed `localhost:8080`. The main difference is that our appplication is now running in a machine that is "external"/seperate to our host machine network.

#### Task 3
- An application is as useful as the data it can have access to; thus we want our web vm to be able to access some database so it can persist data even if the machine running the application stops.
- We can use various databases, but for this task we will use the same as provided in [Task 1](#task-1), the H2 database. Here we can see the setup of a vm named db that runs H2 on `192.168.56.11` with the data available on port `9092`:
  ```ruby
    #DATABASE VM -------------------------------------------------
      config.vm.define "db" do |db|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      db.vm.box = "ubuntu/bionic64"
      db.vm.hostname = "db"
      db.vm.network "private_network", ip: "192.168.56.11"
      #PORT FORWARD CONNECTION. H2 CONSOLE 8082, DATA 9092
      db.vm.network "forwarded_port", guest: 8082, host: 8082
      db.vm.network "forwarded_port", guest: 9092, host: 9092

      # DOWNLOAD H2 VERSION 1.4.200. VERSION 2.1.214 WAS ALSO TRIED BUT PROBLEMS WERE ENCOUNTERED. CHECK LATER?
      db.vm.provision "shell", inline: <<-SHELL
      wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
      SHELL

      db.vm.provision "shell", :run => 'always', inline: <<-SHELL
      java -cp ./h2*.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -ifNotExists > ~/out.txt &
      SHELL
      end
    #-------------------------------------------------------------
  ```
- The vm setup is simple, only needing to specificy the Linux distribution, IP, ports and the commands to install and start the H2 database.    
- The communication between application-database is not handeled by Vagrant. Vagrant is able to set up a vm that can run a database with a certain specification that fits our application.
  - To do this we go to our [application.properties](../../CA2/Part2/react-and-spring-data-rest-basic/src/main/resources/application.properties) file and add the following lines:
  ```properties
  #To enable the H2 database so our Web VM in CA3/Part2 can communicate with the database in the DB VM in CA3/Part1

  #The command at the end prevents the database from closing when the last connection is closed
  spring.datasource.url=jdbc:h2:tcp://192.168.56.11:9092/./jpadb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  
  spring.jpa.hibernate.ddl-auto=update
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  spring.h2.console.settings.web-allow-others=true
  ```
  - This tells spring to use the jdbc link to connect to the H2 database. The given IP must be the same as the one we defined for our vm, in the Vagrant file, and the port is `9092` as it's what Spring needs to have access/persist data.
  - We also specify the username and password to be used to access the database. In this case we use the default `sa` and a blank password.
  - The database-platform is the dialect that Hibernate will use to communicate with the database. In this case we use the H2 dialect so that Spring can translate its code execution to queries compatible with H2.
- As an added challenge we try to replicate the deployment our application to an HTTP server, more specifically [Tomcat](https://tomcat.apache.org/).
  - First we must tell gradle to generate a war (Web Archive file) instead of a jar. To do this easily we add this to our [build.gradle](../../CA2/Part2/react-and-spring-data-rest-basic/build.gradle) file plugin section:
  ```groovy
  plugins {
	 id 'java'
	 id 'org.springframework.boot' version '3.2.4'
	 id 'io.spring.dependency-management' version '1.1.4'
	 id 'org.siouan.frontend-jdk17' version '8.0.0'
	 id 'war'
  }
  ```
  - Our application's war file is the resource that will be located in the webapps folder of our Tomcat server. Similar to our jar file, this archive file is generated after we execute `./gradlew build`
  - This war file is like an "executable" file that runs our application by a server that supports it. The communication between Spring and Tomcat needs to be specified in the [application.properties](../../CA2/Part2/react-and-spring-data-rest-basic/src/main/resources/application.properties) file with the addition of a line that tells Tomcat the name of this resource:
  ```properties
  server.servlet.context-path=/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT
  ```
  - We also must add a class that helps communication between Tomcat and Spring. This class is the same as the one provided in Task 1's repo, [ServletInitializer](../../CA2/Part2/react-and-spring-data-rest-basic/src/main/java/com/greglturnquist/payroll/ServletInitializer.java).
  - Aditionaly, since our application is a resource, we must tell our frontend where to get the data from, so we add the name of our archive file to the [GET method in app.js](../../CA2/Part2/react-and-spring-data-rest-basic/src/main/js/app.js).
  - We commit and push these changes to our repository, since Vagrant clones our repository as it is there.
- Now we work on our Vagrant file. Vagrant executes the commands from start to bottom, and in our case we want to install Tomcat before cloning our repository.  
First, since our web vm's ubuntu version can't run Tomcat10 by default we must install it, however it isn't available in its package manager. To install it we specify the needed commands in the Vagrant file.  

  Then, since we want to deploy our application using a war file, we must copy the file generated with `./gradlew build` to Tomcat's webapps directory and then start Tomcat.
  ```ruby
    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"
      web.vm.network "private_network", ip: "192.168.56.10"

      #SET RAM AMOUNT FOR VM TO 1GB
      web.vm.provider "virtualbox" do |v|
          v.memory = 1024
      end
      #PORT FORWARD APPLICATIONS TO PORT 8080
      web.vm.network "forwarded_port", guest: 8080, host: 8080
      web.vm.provision "shell", inline: <<-SHELL, privileged: true

      #USE SET -E COMMAND TO EXIT AUTOMATICALLY IF ANY SHELL COMMAND FAILS
      set -e

      #INSTALL TOMCAT10 (MANUALLY INSTALL IS DONE BECAUSE sudo apt install tomcat10 fails)
      wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.18/bin/apache-tomcat-10.0.18.tar.gz
      tar -xvf apache-tomcat-10.0.18.tar.gz
      sudo mv apache-tomcat-10.0.18 /opt/tomcat10
      sudo ln -s /opt/tomcat10 /usr/local/tomcat10
      sudo chown -R vagrant:vagrant /opt/tomcat10

      #CLONE REPO AND CHANGE GRADLE WRAPPER PERMISSIONS AND EXECUTE BUILD
      git clone https://github.com/sepsilva/devops-23-24-PSM-1231869
      cd devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic
      chmod u+x gradlew
      ./gradlew build
  
  
      #COPY WAR FILE GENERATED AFTER GRADLE BUILD INTO TOMCAT WEBAPPS
      sudo cp ./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /opt/tomcat10/webapps

      #STARTUP TOMCAT
      /opt/tomcat10/bin/startup.sh
      SHELL
      #-------------------------------------------------------------
      end    
  ```
- Again, Vagrant runs its instructions sequentially from top to bottom, so the database vm will be deployed first before the web.
- However, if we want to only launch one vm we can specify it by running `vagrant up [VM NAME]`.
- In our case we run `vagrant up` to start both virtual machines. Vagrant returns no errors and we can validate that the machines are running by checking Virtual Box and further by visiting using our browser:
  - The database console at `192.168.56.11:8082` and logging in with the jdbc link and username/password we specified in our Spring application.properties.
  - In another tab we can visit our web application by going to our web vm's IP and the port where Tomcat is running (8080) and specifying the resource that represents our application (/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT).  
  So we visit `192.168.56.10:8080/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT` and see our table of content.
- To validate that our web application is communicating with our database we can add a new entry to our database using the H2 console and see if it is rendered in our web application.
  - We go to our H2 console and execute the following query to add Gandalf to our employee table:
    ```sql
    INSERT INTO EMPLOYEE (ID, DESCRIPTION, EMAIL, FIRST_NAME, JOB_YEARS, JOB_TITLE,LAST_NAME) VALUES (3, 'Never late nor early', 'Gandalf@Magic.com', 'Gadanlf', 100, 'Wizard', 'The Grey');
    ```
  - We refresh our web application's browser page and see the new entry.
  - We can also execute a post command using curl. In a terminal we can execute the following command to add Legolas:
  ```bash
  curl -X POST http://192.168.56.10:8080/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT/api/employees -H 'Content-Type: application/json' -d '{"firstName": "Legolas", "lastName": "Greenleaf", "description": "Prince of Mirkwood", "jobTitle": "Elf", "jobYears": 500, "email": "legolas@mirkwood.com"}'
  ```
  - Again, after refreshing the page we see the new entry.
- The good part about have persistence is that we can stop both machines using the command `vagrant halt` and then start them up again using `vagrant up` and see that the data is still there.
- Before we added this, after we stopped our aplication, either by stopping Spring or the web vm, the data would be lost.
- Here we can see our completed Vagrant file:
  ```ruby
  #BASED ON VAGRANT FILE DEVELOPED IN SIDE PROJECT: https://github.com/sepsilva/testDevops
  #Setting up 2 VM Machines: 1 to run the Spring app and the other an instance of H2 database
  #On a first instance the goal is to have both machines running separate and then have them communicate.
  #To change this the line to bootrun using gradlewrapper must be commented and war file deployed to tomcat server
  #Then server must be executed
  #------------------------------------------------------------------------------------
  
  #VMs will be running ubuntu/bionic64
  Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/bionic64"

    #BOTH VMs ----------------------------------------------------
    #Update all apps and install jdk 17
     config.vm.provision "shell", inline: <<-SHELL
     sudo apt-get update -y
     sudo apt-get install -y iputils-ping avahi-daemon libnss-mdns unzip \ openjdk-17-jdk-headless
     SHELL
    #-------------------------------------------------------------


    #DATABASE VM -------------------------------------------------
      config.vm.define "db" do |db|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      db.vm.box = "ubuntu/bionic64"
      db.vm.hostname = "db"
      db.vm.network "private_network", ip: "192.168.56.11"
      #PORT FORWARD CONNECTION. H2 CONSOLE 8082, DATA 9092
      db.vm.network "forwarded_port", guest: 8082, host: 8082
      db.vm.network "forwarded_port", guest: 9092, host: 9092

      # DOWNLOAD H2 VERSION 1.4.200. VERSION 2.1.214 WAS ALSO TRIED BUT PROBLEMS WERE ENCOUNTERED. CHECK LATER?
      db.vm.provision "shell", inline: <<-SHELL
      wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
      SHELL

      db.vm.provision "shell", :run => 'always', inline: <<-SHELL
      java -cp ./h2*.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -ifNotExists > ~/out.txt &
      SHELL
      end
    #-------------------------------------------------------------

    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"
      web.vm.network "private_network", ip: "192.168.56.10"

      #SET RAM AMOUNT FOR VM TO 1GB
      web.vm.provider "virtualbox" do |v|
          v.memory = 1024
      end
      #PORT FORWARD APPLICATIONS TO PORT 8080
      web.vm.network "forwarded_port", guest: 8080, host: 8080
      web.vm.provision "shell", inline: <<-SHELL, privileged: true

      #USE SET -E COMMAND TO EXIT AUTOMATICALLY IF ANY SHELL COMMAND FAILS
      set -e

      #INSTALL TOMCAT10 (MANUALLY INSTALL IS DONE BECAUSE sudo apt install tomcat10 fails)
      wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.18/bin/apache-tomcat-10.0.18.tar.gz
      tar -xvf apache-tomcat-10.0.18.tar.gz
      sudo mv apache-tomcat-10.0.18 /opt/tomcat10
      sudo ln -s /opt/tomcat10 /usr/local/tomcat10
      sudo chown -R vagrant:vagrant /opt/tomcat10

      #CLONE REPO AND CHANGE GRADLE WRAPPER PERMISSIONS AND EXECUTE BUILD
      git clone https://github.com/sepsilva/devops-23-24-PSM-1231869
      cd devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic
      chmod u+x gradlew
      ./gradlew build

      #INSTEAD OF USING GRADLE BOOTRUN, REDIRECT ERROR OUTPUT TO A LOG
      #Update: since we'll be deploying the generated Web archive file to our
      #tomcat10 we no longer need to start the spring application up manually

      #nohup ./gradlew bootRun > /home/vagrant/spring-boot-app.log 2>&1 &

      #COPY WAR FILE GENERATED AFTER GRADLE BUILD INTO TOMCAT WEBAPPS
      sudo cp ./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /opt/tomcat10/webapps

      #STARTUP TOMCAT
      /opt/tomcat10/bin/startup.sh
      SHELL
      #-------------------------------------------------------------
      end
    end
    ```
#### Task 3 - Removing dependency on repository state and simplifying Vagrant file
- Due to changes done in [CA2/Part2](../../CA2/Part2) to integrate an external database into our Spring application, the Spring application in CA2/Part2 would be unable to run in standalone mode.
- Ideally, we could have supplied a war file (generated by our Gradle build tool) as a [provision](VagrantNoClone/provision) for our Vagrant files and then leave CA2/Part2 as is. We'd however have to tell Vagrant to get that war file and deploy it to an HTTP server like Tomcat by specifying:
  - When we want to run our application without external database integration:
  ```ruby
    #SPECIFY THE WAR FILE TO BE DEPLOYED TO HTTP SERVER
    web.vm.provision "file", source: "provision/NoDBConnection/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war", destination: "/home/vagrant/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war""
  ```
  - When we want to run our application with database integration:
  ```ruby
    #SPECIFY THE WAR FILE TO BE DEPLOYED TO HTTP SERVER
    web.vm.provision "file", source: "provision/DBConnection/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war", destination: "/home/vagrant/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war""
  ```
- We tell Vagrant to copy the file from the provision folder to the `home/vagrant` directory of our web vm.
- Then, we tell Vagrant to move this file from the `home/vagrant` directory to the Tomcat webapps directory in order to avoid rewriting the webapps directory.
- Finally, we tell Vagrant to start Tomcat as we did before.  


- A new [vagrant file](VagrantNoClone/Vagrantfile) was created to test this approach. Currently, it copies uses the war file without database connection.  


- This both avoids the following problems:
  - Depending on the state of the current repository code;
  - Requiring a public repository;
  - Complexity in the Vagrant file, having to specify the execution of commands like: `git clone`, `cd`, `chmod`, `./gradlew build` etc...;

- Below is the now simplified Vagrant file:
```ruby
#------------------------------------------------------------------------------------
#VMs will be running ubuntu/bionic64
Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/bionic64"

    #BOTH VMs ----------------------------------------------------
    #Update all apps and install jdk 17
     config.vm.provision "shell", inline: <<-SHELL
     sudo apt-get update -y
     sudo apt-get install -y iputils-ping avahi-daemon libnss-mdns unzip \ openjdk-17-jdk-headless
     SHELL
    #-------------------------------------------------------------

    #DATABASE VM -------------------------------------------------
      config.vm.define "db" do |db|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      db.vm.box = "ubuntu/bionic64"
      db.vm.hostname = "db"
      db.vm.network "private_network", ip: "192.168.56.11"
      #PORT FORWARD CONNECTION. H2 CONSOLE 8082, DATA 9092
      db.vm.network "forwarded_port", guest: 8082, host: 8082
      db.vm.network "forwarded_port", guest: 9092, host: 9092

      # DOWNLOAD H2 VERSION 1.4.200. VERSION 2.1.214 WAS ALSO TRIED BUT PROBLEMS WERE ENCOUNTERED. CHECK LATER?
      db.vm.provision "shell", inline: <<-SHELL
      wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
      SHELL

      db.vm.provision "shell", :run => 'always', inline: <<-SHELL
      java -cp ./h2*.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -ifNotExists > ~/out.txt &
      SHELL
      end
    #-------------------------------------------------------------

    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"
      web.vm.network "private_network", ip: "192.168.56.10"

      #SET RAM AMOUNT FOR VM TO 1GB
      web.vm.provider "virtualbox" do |v|
          v.memory = 1024
      end
      #PORT FORWARD APPLICATIONS TO PORT 8080
      web.vm.network "forwarded_port", guest: 8080, host: 8080

      web.vm.provision "shell", inline: <<-SHELL, privileged: true

      #USE SET -E COMMAND TO EXIT AUTOMATICALLY IF ANY SHELL COMMAND FAILS
      set -e

      #INSTALL TOMCAT10 (MANUALLY INSTALL IS DONE BECAUSE sudo apt install tomcat10 fails)
      wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.18/bin/apache-tomcat-10.0.18.tar.gz
      tar -xvf apache-tomcat-10.0.18.tar.gz
      sudo mv apache-tomcat-10.0.18 /opt/tomcat10
      sudo ln -s /opt/tomcat10 /usr/local/tomcat10
      sudo chown -R vagrant:vagrant /opt/tomcat10
      SHELL

      #SPECIFY THE WAR FILE TO BE DEPLOYED TO HTTP SERVER
      web.vm.provision "file", source: "provision/NoDBConnection/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war", destination: "/home/vagrant/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war"

      #NEW SHELL TO STARTUP TOMCAT
      web.vm.provision "shell", inline: <<-SHELL, privileged: true

      #MOVE FILE FROM WHERE IT WAS ADDED INTO TO TOMCAT WEBAPPS. THIS AVOIDS REWRITING
      sudo mv /home/vagrant/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /opt/tomcat10/webapps/
      #STARTUP TOMCAT
      /opt/tomcat10/bin/startup.sh

      SHELL
      #-------------------------------------------------------------
      end
end
```

## Alternative - Other virtualization providers - Qemu
As said before, Vagrant uses Virtual Box as its default provider, however Virtual Box has some drawbacks such as:
- Not being as fast as other providers;
- Not being open source;
- Not being supported by some operating systems like macOS;  

[Qemu](https://www.qemu.org/) is an open source hypervisor that can be used as a provider for Vagrant.
Qemu is faster than Virtual Box however its instalation, especially on windows, is more complex.
In the context of this CA, the main difference is in the blocks for creating/setting the specifications of the virtual machines.
The only thing we need to prepare is install Qemu.
To simplify things, we use the binary file to install [Qemu](https://www.qemu.org/download/#windows).  
Then we must accomplish:
- [Task 4](#task-4): Define Qemu as the box provider in the Vagrant file.
#### Task 4
- We create a new [Vagrant file](Qemu/Vagrantfile). The structure is similar to our previous [file](Vagrantfile), however Qemu requires a different approach to specifications:
- We must create the network configuration files for our machines so they can be used as provisions. We create a [web](Qemu/provision/netcfg-web.yaml) and [db](Qemu/provision/netcfg-db.yaml) file.
- Our Vagrant files will now have a section where we specify Qemu as provider and configure our virtual machines. One very important thing is we must tell Qemu, what the specifications of our host machine is, so it can create the virtual machines accordingly.  
  We're running Windows 64 bit and we can tell it to emulate our CPU. We also specify the type of machine (hardware components) we want to emulate.  
  All these specifications are explicitly defined for Qemu in our Vagrant file, however these same specifications were handeled by Virtual Box implicitly.  
  Additionally, we must point Vagrant to our network configuration files.
  - Database vm:
  ```ruby
    #DATABASE VM -------------------------------------------------
      config.vm.define "db" do |db|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      db.vm.box = "ubuntu/bionic64"
      db.vm.hostname = "db"

      db.vm.provider "qemu" do |qe|
          qe.arch = "x86_64"
          qe.machine = "pc-q35"
          qe.cpu = "host"
          qe.net_device = "virtio-net-pci"
          qe.memory = "1024"
          qe.ssh_port = 50122
          qe.extra_qemu_args = %w(-netdev vmnet-host,id=vmnet,start-address=192.168.56.1,end-address=192.168.56.255,subnet-mask=255.255.255.0 -device virtio-net-pci,mac=52:54:00:12:34:50,netdev=vmnet)
    end
    #SPECIFY THE NETWORK CONFIGURATION FILE TO BE USED
    db.vm.provision "file", source: "provision/netcfg-db.yaml", destination: "/home/vagrant/01-netcfg.yaml"    
  ```
  - Web vm:
  ```ruby
      #WEB VM ------------------------------------------------------
      config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"

    web.vm.provider "qemu" do |qe|
      qe.arch = "x86_64"
      qe.machine = "pc-q35"
      qe.cpu = "host"
      qe.net_device = "virtio-net-pci"
      qe.memory = "2048"
      qe.ssh_port = 50222
      qe.extra_qemu_args = %w(-netdev vmnet-host,id=vmnet,start-address=192.168.56.1,end-address=192.168.56.255,subnet-mask=255.255.255.0 -device virtio-net-pci,mac=52:54:00:12:34:51,netdev=vmnet)
    end
      #SPECIFY THE NETWORK CONFIGURATION FILE TO BE USED
      web.vm.provision "file", source: "provision/netcfg-web.yaml", destination: "/home/vagrant/01-netcfg.yaml"  
  ```
  
- Inside the shell command blocks, we must now tell Vagrant to configure our networks based on the provisioned files (this is something we did manually in [CA3 Part 1](../Part1/readme.md)):
  -  Database/Web vm:
  ```ruby
      db.vm.provision "shell", :run => 'always', inline: <<-SHELL

      #APPLY NETWORK CONFIGURATION
      sudo mv /home/vagrant/01-netcfg.yaml /etc/netplan
      chmod 600 /etc/netplan/01-netcfg.yaml
      sudo netplan apply

      SHELL
      end
  ```
- Here is our completed Vagrant file:
  ```ruby
  #BASED ON VAGRANT FILE DEVELOPED IN SIDE PROJECT: https://github.com/sepsilva/testDevops
  #Setting up 2 VM Machines: 1 to run the Spring app and the other an instance of H2 database. THESE MUST USE generic/ubuntu2204
  #On a first instance the goal is to have both machines running separate and then have them communicate.
  #To change this the line to bootrun using gradlewrapper must be commented and war file deployed to tomcat server
  #Then server must be executed
  #------------------------------------------------------------------------------------
  
  #VMs will be running generic/ubuntu2204 with qemu being the provider
  Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/bionic64"

    #BOTH VMs ----------------------------------------------------
    #Update all apps and install jdk 17
     config.vm.provision "shell", inline: <<-SHELL
     sudo apt-get update -y
     sudo apt-get install -y iputils-ping avahi-daemon libnss-mdns unzip \ openjdk-17-jdk-headless
     SHELL
    #-------------------------------------------------------------

    #DATABASE VM -------------------------------------------------
      config.vm.define "db" do |db|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      db.vm.box = "ubuntu/bionic64"
      db.vm.hostname = "db"

      db.vm.provider "qemu" do |qe|
          qe.arch = "x86_64"
          qe.machine = "pc-q35"
          qe.cpu = "host"
          qe.net_device = "virtio-net-pci"
          qe.memory = "1024"
          qe.ssh_port = 50122
          qe.extra_qemu_args = %w(-netdev vmnet-host,id=vmnet,start-address=192.168.56.1,end-address=192.168.56.255,subnet-mask=255.255.255.0 -device virtio-net-pci,mac=52:54:00:12:34:50,netdev=vmnet)
    end
    #SPECIFY THE NETWORK CONFIGURATION FILE TO BE USED
    db.vm.provision "file", source: "provision/netcfg-db.yaml", destination: "/home/vagrant/01-netcfg.yaml"

    db.vm.provision "shell", :run => 'always', inline: <<-SHELL
      # DOWNLOAD H2 VERSION 1.4.200. VERSION 2.1.214 WAS ALSO TRIED BUT PROBLEMS WERE ENCOUNTERED. CHECK LATER?
      wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
      SHELL

      db.vm.provision "shell", :run => 'always', inline: <<-SHELL

      #APPLY NETWORK CONFIGURATION
      sudo mv /home/vagrant/01-netcfg.yaml /etc/netplan
      chmod 600 /etc/netplan/01-netcfg.yaml
      sudo netplan apply

      java -cp ./h2*.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -ifNotExists > ~/out.txt &
      SHELL
      end

    #-------------------------------------------------------------

    #WEB VM ------------------------------------------------------
    config.vm.define "web" do |web|
      #SET MACHINE UBUNTU VERSION, NAME AND IP
      web.vm.box = "ubuntu/bionic64"
      web.vm.hostname = "web"

    web.vm.provider "qemu" do |qe|
      qe.arch = "x86_64"
      qe.machine = "pc-q35"
      qe.cpu = "host"
      qe.net_device = "virtio-net-pci"
      qe.memory = "2048"
      qe.ssh_port = 50222
      qe.extra_qemu_args = %w(-netdev vmnet-host,id=vmnet,start-address=192.168.56.1,end-address=192.168.56.255,subnet-mask=255.255.255.0 -device virtio-net-pci,mac=52:54:00:12:34:51,netdev=vmnet)
    end

      #PORT FORWARD APPLICATIONS TO PORT 8080
      web.vm.network "forwarded_port", guest: 8080, host: 8080
      #SPECIFY THE NETWORK CONFIGURATION FILE TO BE USED
      web.vm.provision "file", source: "provision/netcfg-web.yaml", destination: "/home/vagrant/01-netcfg.yaml"

      web.vm.provision "shell", inline: <<-SHELL, privileged: true

      #USE SET -E COMMAND TO EXIT AUTOMATICALLY IF ANY SHELL COMMAND FAILS
      set -e

      #APPLY NETWORK CONFIGURATION
      sudo mv /home/vagrant/01-netcfg.yaml /etc/netplan
      chmod 600 /etc/netplan/01-netcfg.yaml
      sudo netplan apply

      #INSTALL TOMCAT10 (MANUALLY INSTALL IS DONE BECAUSE sudo apt install tomcat10 fails)
      wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.18/bin/apache-tomcat-10.0.18.tar.gz
      tar -xvf apache-tomcat-10.0.18.tar.gz
      sudo mv apache-tomcat-10.0.18 /opt/tomcat10
      sudo ln -s /opt/tomcat10 /usr/local/tomcat10
      sudo chown -R vagrant:vagrant /opt/tomcat10

      #CLONE REPO AND CHANGE GRADLE WRAPPER PERMISSIONS AND EXECUTE BUILD
      git clone https://github.com/sepsilva/devops-23-24-PSM-1231869
      cd devops-23-24-PSM-1231869/CA2/Part2/react-and-spring-data-rest-basic
      chmod u+x gradlew
      ./gradlew build

      #INSTEAD OF USING GRADLE BOOTRUN, REDIRECT ERROR OUTPUT TO A LOG
      #Update: since we'll be deploying the generated Web archive file to our
      #tomcat10 we no longer need to start the spring application up manually

      #nohup ./gradlew bootRun > /home/vagrant/spring-boot-app.log 2>&1 &

      #COPY WAR FILE GENERATED AFTER GRADLE BUILD INTO TOMCAT WEBAPPS
      sudo cp ./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war /opt/tomcat10/webapps

      #STARTUP TOMCAT
      /opt/tomcat10/bin/startup.sh
      SHELL
      #-------------------------------------------------------------
      end
  end 
  ```
- If we run `vagrant up` we see that Vagrant defaults to Virtual Box. To specify Qemu as the provider we must run `vagrant up --provider=qemu`.
- We can try the same validations as before to check that our virtual machines/applications are running[*](#issues).

## Alternative - Containerization with Docker
Virtualization is a powerful tool, especially when using tools like Vagrant that simplify and standardize the process of creation and setup of a virtual machine.  
This avoids human error when having to configure the same time of machine multiple times. 

However, virtualization has its drawbacks including but not limited to:
  - It is comparatively more resource intensive;
  - It is slower to boot up;
  - It is more complex to set up;

Containerization is an alternative to virtualization. The line of thinking is similar to Vagrant, the standardization of the process of creation and setup of something that can be easily shared and replicated using a simple file.  


Containerization does away with the need for a hypervisor and instead uses a container engine to run containers on the host's operating system.
The speed of a container and "lightness" come from the fact that instead of running a full operating system, containers run on the host's operating system and only contain the necessary libraries and dependencies to run the application they are meant to.

A famous container engine is [Docker](https://www.docker.com/). Each container is an instance of an image, which is a file that contains the necessary libraries and dependencies to run an application.
This image itself is created from a Dockerfile, which is a file that contains the instructions to create the image.
Docker provides a [hub](https://hub.docker.com/) where images can be shared and downloaded, like what we did for our H2 database container, or like, in the case of our Spring application, we can create our own.

For this section we'll acomplish 2 tasks:
- [Task 5](#task-5): Create a Dockerfile for our Spring application (version of app that doesn't use a database) and deploy a container that runs the application similarly to [task 2](#task-2);
- [Task 6](#task-6): Create a Dockerfile for our Spring application (version that uses database) and deploy a container that runs H2 and another that runs our application similarly to [task 3](#task-3);
### Task preparation - Docker
We install the user friendly [Docker Desktop](https://www.docker.com/products/docker-desktop) for Windows.

#### Task 5
- In our CA3/Part2 directory we create a new folder named [DockerAlternativeNoDBConnection](DockerAlternativeNoDBConnection) and run `docker init` to create a new [Dockerfile](DockerAlternativeNoDBConnection%2FDockerfile) (altough it creates a compose,.ignore and readme we'll ignore those).
- We copy into our folder, a [web archive](DockerAlternativeNoDBConnection/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war) file of our Spring application, making sure it's a version that doesn't require a database.
- Docker is quite easy to understand. Since we don't need to set up an entire operating system, we only need to tell it grab java 17 and an HTTP server (Tomcat) and deploy our war file to it and run it:
```dockerfile
#Create a basic container with java 17 and running tomcat 10 similar to what our vagrant file does
FROM tomcat:10-jdk17-openjdk-slim

LABEL authors="Luis"

#Similar to what we did in the vagrant file we must deploy the generated war file that we obtain after ./gradlew build and place it inside our tomcat webapps
COPY ./react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war ./webapps

#State the port that our application will run on
EXPOSE 8080

#start tomcat automatically when container starts
CMD ["catalina.sh", "run"]
```
- We can name our image whatever we want, so we build it using the command `docker build -t cachalote-no-db-spring-app .`. 
- We must then deploy this container and specify its name if we want, port and we must specify the docker image to use using the command `docker run --name no-db-cachalote-container -d -p 8080:8080 cachalote-no-db-spring-app`.
- To validate that our container is running we can check the containers running using `docker ps` or by viewing our Docker Desktop program.
- We can validate our application by accesing it through our browser at `localhost:8080/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT` and we see our employee table.

#### Task 6
- For this task we must create a new folder named [DockerAlternativeWithDBConnection](DockerAlternativeWithDBConnection) and run `docker init` to create a new  (again we ignore the extra files).
- We copy into our folder, a [web archive](DockerAlternativeDBConnection/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.war) file of our Spring application, making sure it's a version that requires a database.
  - Docker containers running on the same host can communicate with each other, so we can run our H2 database in one container and our Spring application in another.
  We must make sure that our application.properties database source url is adapted to point to our container's IP and port:
  ```properties
  spring.datasource.url=jdbc:h2://orca-db-for-spring-app:9092/./jpadb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  ```
- We must pull a suitable H2 database container, such as this one by [buildo](https://hub.docker.com/r/buildo/h2database/#!), by using the command `docker pull buildo/h2database`.
- We can deploy the container using the command `docker run -d --name orca-db-for-spring-app -p 8082:8082 -p 9092:9092 buildo/h2database`.
- Our Dockerfile is the same as in [task 5](#task-5) as the tasks docker has to do are exactly the same.
- We build our image using the command `docker build -t baleia-azul-db-spring-app .`.
- We deploy our application's container using the command `docker run --name baleia-azul-spring-app -d -p 8080:8080 baleia-azul-db-spring-app`.
- We can validate our containers health and we can validate that our application/database are running similarly to [task 3](#task-3).

## Conclusion
We saw how useful virtualization can be with this CA in general. How it helps us to easily simulate a machine with the specifications we want without having to invest in new expensive hardware.
However we noticed that doing this by hand can be quite complex and time consuming, especially when we have to do it multiple times and a lot of times errors could occur.  

In this part of the CA we saw how Vagrant can help us automate this process by means of a simple file that we can share and easily replicate.
Vagrant is very useful, especially for development environments, where we can easily set up a machine with the specifications we want and then share it with our team.

We saw that we can tailor the provider of our virtual machines to our needs, either using Virtual Box, Qemu, VMWare or Hyper-v. These offer different levels of performance, customization and complexity, depending on the user's needs.

However the process of booting up the machines with `vagrant up` could be very time consuming and resource intensive, especially when we have to do it multiple times.
When we want to deploy a full application that we want to be run we could instead use docker containers that reduce the scope of the virtualization, to only the necessary libraries and dependencies to run the application.

Docker is very powerful and quick. Instead of waiting a few minutes for our vms to boot, we can have our containers running in seconds.

Something should be stated, the concept of virtualization and containerization are not mutually exclusive. We can have a virtual machine running a container engine that runs containers.
Also Vagrant is similar to a container orchestrator, like [Kubernetes](https://kubernetes.io/), that can manage multiple containers, also known as pods (a pod is a group of whales!).

With these tools we can enhance our development process, making the deployment of our applications easier and faster.
### Issues
In this CA we had quite a few issues, especially with H2 database [integration](#task-3). A lot of the time was spent figuring out and properly configuring the spring application.
To more easily understand we restarted the CA from scratch and followed the steps more carefully in a new [repository](https://github.com/sepsilva/testDevops).
We also had issues with the alternative solutiong [Qemu](#alternative---other-virtualization-providers---qemu) due to being unable to properly run `vagrant up --provider=qemu`.
An error was returned:
```powershell
  PS C:\Users\Luis\Desktop\Devops\devops-23-24-PSM-1231869\CA3\Part2\Qemu> vagrant up --provider=qemu            
  The provider 'qemu' could not be found, but was requested to
  back the machine 'db'. Please use a provider that exists.
  
  Vagrant knows about the following providers: docker, hyperv, virtualbox
```
Some attempts were made, like installing libvirt plugin for Vagrant, however the error persisted.  

Hyper-v and VMWare were explored as alternatives but:
- Hyper-v is not supported by Windows 11 Home edition (our version);
- VMWare is not free and we didn't have a license.


