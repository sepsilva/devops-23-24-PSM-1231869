# Report for Class assignment 3 - Part 1 - Luís Silva 1231869

## Report Structure
- [Description of assigment](#description-of-assignment)
    - [Virtual machines and their uses](#virtual-machines-and-their-uses)
- [Task preparation](#task-preparation)
    - [Task 1](#task-1)
    - [Task 2](#task-2)
- [Conclusion](#conclusions)

## Description of assignment
In this assigment we explore virtual machines and how they can be used to different applications.
We also explore how these can be used to run specific applications, like a chat server, and have other machines connect to it.

## Virtual machines and their uses
Virtual machines are the key part of the virtualization concept where a single physical machine, with help from a hypervisor, can run multiple "simulated" and independent machines.  
These machines can have their own operating system, applications and network configurations and are typically known as Guests, while the physical machine is known as the Host.  
The hypervisors manage the resources of the physical machine and allocate them to the Guests can be of two types: Type 1 and type 2.  
Type 1 hypervisors run directly on the hardware and are more efficient, while Type 2 hypervisors run on top of an operating system and are more user-friendly.  
[VirtualBox](https://www.virtualbox.org/wiki/VirtualBox) is a popular Type 2 hypervisor and will be the main tool used in this assignment.

For example. If we have only one physical computer that has enough resources, we could use a type 1 hypervisor that could run multiple virtual machines.
Our company could have a very powerful server that runs multiple virtual machines, one for each department. Each department could then have their own cheap and simple computer (like a thin client) that would communicate with the server and display the virtual machine's content.
## Task preparation

First, we need to install VirtualBox and create a virtual machine.  
For the operating system of the machine, [Ubuntu Server](https://ubuntu.com/download/server) was used.
We also set up the proper network adapters to allow comunication between the guest machine and the host/outside.
Afterwards the following commands are executed on the VM:
- `sudo apt update` - Update the package list;
- `sudo apt install net-tools` - Install the net-tools package;

Then we edit the network configuration file using `sudo nano /etc/netplan/01-netcfg.yaml` and insert:
```yaml
network:
  version: 2
  renderer: networkd
  ethernets:
    enp0s3:
      dhcp4: yes
    enp0s8:
      addresses:
        - 192.168.56.5/24
```
We apply the changes using `sudo netplan apply`. With these we can now connect to our VM using the IP of our previously created network adapter.  
We also install an SSH server using `sudo apt install openssh-server` and set it up and install a FTP server using `sudo apt install vsftpd`.

At this moment we can start our virtual machine and using our host machine's terminal connect to it using SSH. To do this we open a terminal such as, Windows Powershell, and type `ssh luis@192.168.56.5` and we are prompted our user password that was set on our VM.
We see that the aspect of our terminal changes to an aspect similar to the VM's Linux terminal and we've successfully connected.

We install git `sudo apt install git` and Java 8 `sudo apt install openjdk-8-jdk` and finally we can run git commands to clone the following repositories:
- [Spring and React tutorial](https://github.com/spring-guides/tut-react-and-spring-data-rest/tree/master/basic) - A simple Spring+React+Rest tutorial;
- [DevOps](https://github.com/sepsilva/devops-23-24-PSM-1231869) - Our own DevOps repository;


After that, the following tasks were completed:
- [Task 1](#task-1): Running the Spring tutorial and CA1/Part1 using Maven;
- [Task 2](#task-2): Running CA2/Part2 and CA2/Part1 using Gradle;
### Task 1
- To run the Spring tutorial we navigate in our VM, to the `basic` folder inside the main project using the `cd` command and run the command `./mvnw spring-boot:run`.
- We can then go to our Host machine and using the browser type `192.168.56.5:8080` which represents our application socket (IP + Port) and see a table with a single entry.
- To run CA1/Part1, we navigate to the same project folder using the `cd` command however, we must first give execution permissions to the `mvnw` file using `chmod +x mvnw` and then run `./mvnw spring-boot:run`.
- Again, on our Host machine we use a browser to connect to our VM Guest IP and the port where our application is listening we see the same table with more entries, since in CA1/Part1 we added more table columns and more employee entries that were added when we did CA1/Part1.

### Task 2
- To run both Gradle applications we must first prepare our VM:
  - Install Java 17 using `sudo apt install openjdk-17-jdk`
  - When running both parts of the project we must also use the command `chmod u+x` on the `gradlew` file to give it execution permissions.


- We start by trying to run CA2/Part2 which is similar to the applications run on [Task 1](#task-1) but using Gradle. We navigate to the project folder and run the command `./gradlew bootRun` and connect to the VM using our Host machine's browser and see the same table as we saw in the second part of Task 1.


- The application in CA2/Part1 is a chat application composed of a Server program and Client program. The Client program has a Graphical Interface and thus can't be rendered on our Virtual Machine since we are running Ubuntu Server. We can however use our VM to run the Server and use our machine to run a Client that connects to it.  
- To run the Server we navigate to our project folder in our VM and run the command  `./gradlew runServer`. In our build.gradle file in the runServer task we have the application port set as 59001.

- We can then run the Client on our Host machine. To do so we navigate to our project folder and run the command for the runClient task using `./gradlew runClient --args="192.168.56.5:59001"`. This command tells Gradle to execute that task and pass as arguments our VM's IP and the port our application runs in.
- We see the Client GUI pop-up and after typing a username, we see our VM's terminal display the message `A new user has joined`.
- If we force our VM to stop the Server we see our client application gets automatically disconnected.

## Conclusion
The concept of Virtualization, more specifically the use of Virtual Machines, is a very powerful concept that allows us to run several different applications in a specific customized environment that is separate from our own machine. 
We saw how to run different applications that used different build tools and we saw how to communicate between our Host and Guest machines, similar to what happens when we use a browser and go online.  

One issue that was encountered was the fact that the Client application in CA2/Part1 was a GUI application and thus couldn't be run on our Ubuntu VM since it does not have a graphical display. Although this was not an issue since our Server only takes care of managing the clients.




