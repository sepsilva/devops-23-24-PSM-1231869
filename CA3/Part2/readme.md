Part 1 - Following tutorial  

Get Repo and install Vagrant;
Create folder called VagrantVA and put the Vagrantfile inside;
Open a terminal in the VagrantVA folder and run the command `vagrant up`;
Vagrant creates 2 VMS  that we can see in virtual box;
We can ssh into these machines by opening a terminal in the VagrantVA folder and running the command `vagrant ssh <machine name>`;
We can use the browser to access it using the IP and ports inside the vagrant file;
Using http://192.168.56.11:8082 we have access to the H2 console and using http://192.168.56.10:8080/ we get the It works! landing page of Apache Tomcat  
------------------------------------------------------------------------------------------------------------------------


Part 2 - Setup CA3Part2 Issue #20

Make our repo public so Vagrant can clone it;
Copy Vagrant file into folder;

Use the manually done requirements in CA3/Part1:
- For both VMs change java version to 17;
- Starting with the Web VM:
  - Add set -e command to the top of the script to avoid continuing if an error occurs;
  - Install Git;
  - Install Gradle 8.6 which is what we manually did in CA3/Part1 so our application could run;

  - Remove war file deployment as we are not using it;
  - Add section to clone our repo;
  - Add section to navigate into our cloned repo and then into CA2/Part2;
  - Change gradlew permissions like what we had to do in CA3/Part1;
  - Add command to build our application;
  - Add command to run our application (has this funky command that pipes the possible errors into a file);

- Run vagrant up and check if the vms boot up fine - We open a terminal where our Vagrant file is and run the command `vagrant up` and check if the VMs boot up fine.
We see the Gradle notification saying build successful and the application is running;
- SSH into the web vm and check if content is present (it is) using the command `vagrant ssh web` and then `ls` to see the contents of the folder. We see:;
vagrant@web:~$ ls
devops-23-24-PSM-1231869  gradle-8.6-bin.zip  spring-boot-app.log
We can then exit our ssh session using the command `exit`;
- Check if app is running by going into the VM's ip specified in the VagrantFile 192.168.56.10 and port 8080 since it's where Tomcat is running in.
We see the same table we had in CA3/Part1 which is the same as in CA2/Part2 (with the new line and column that were added in CA1);
- We stop our VMs using the command `vagrant halt` and commit our changes to our repo to issue #20;
------------------------------------------------------------------------------------------------------------------------


Part 3 - Set up H2 Database and try to connect Issue #21

CA2/Part2 has the H2 database dependency in the build.gradle file;
We connect to the IP of the db VM and port 8082 to access the H2 console using our browser. There we can log into our database create/run our queries;
We boot up our VMs using the command `vagrant up` and ssh into the db vm using the command `vagrant ssh db` and we see a jar inside obtained after vagrant executes the wget command specified in the DB VM section;
This however doesn't mean that our Spring application is communicating with the H2 database (Web VM is not communicating with the DB VM);
We must set this up by adding/altering the application.properties file in the resources folder of our Spring application (CA2/Part2):
- We add the following lines to the application.properties file based on the application.properties in this repo (https://bitbucket.org/pssmatos/tut-basic-gradle/src/master/):
  ```properties
  server.servlet.context-path=/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT
  spring.data.rest.base-path=/api
  
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
  On the end of spring.datasource.url, this is added to avoid automatic closure of our database when the last connection is closed;
- Since our CA2 Repo has changed we must clone it again and build it again, so we use vagrant to destroy the current VMs and then start them up with the correct data;
- We destroy our VMs using the command `vagrant destroy` and then commit our done for CA2/Part2 to issue #21 so vagrant has access to the changes in applications.properties;
- First we can ssh into our web VM and ping our db VM to check if they are communicating using the command `ping 192.168.56.11`;
- Then we can try to access the H2 console using the IP of the db VM and port 9092 in our browser;
  - At the login screen we can use the credentials we specified in the application.properties file (sa and BLANK and the link to see the H2 database: jdbc:h2:tcp://192.168.56.11:9092/./jpadb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE);
  - We see the same data that was rendered in the web VM when we accessed the application in the browser after doing the query SELECT * FROM EMPLOYEE;
  - 
- If we try to connect to our web VM using the browser we see it doesn't render and by using `vagrant ssh web` and reading the log we see there's an exception encountered with spring after connecting the DB;

GOOD ISH UNTIL HERE commit  7169c98 - BASICALLY IT BUILDS CORRECTLY BUT BOOTRUN FAILS SEE SPRING LOG IN VM.
#Install required items like specified in CA3/Part1
#Gradle was removed because it was redudant, Gradlewrapper worked fine

- Upon investigation we see there's an issue between jakarta and Hibernate (https://www.baeldung.com/hibernate-no-persistence-provider);
- We specify the Hibernate version to be used (6.4.2.Final) in our build.gradle file - did not fix;
- turns out the issue is with hibernate and h2 https://groups.google.com/g/h2-database/c/vq3Q7X3muJQ;
- We found this blog (https://velog.io/@soluinoon/H2-Column-startvalue-not-found-%EC%98%A4%EB%A5%98) swapped the application.properties line:
  spring.jpa.hibernate.hbm2ddl-auto=update;
- Still nothing. 

AFTER A LOT OF TRIAL AND ERROR:
Tried in side project normally with basic version of application built in CA1/CA2. Built it from scratch:
https://github.com/sepsilva/testDevops


 ------------------------------------------------------------------------------------------------------------------------