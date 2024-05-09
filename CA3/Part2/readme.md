Part 1 - Following tutorial  

Get Repo and install Vagrant;
Create folder called VagrantVA and put the Vagrantfile inside;
Open a terminal in the VagrantVA folder and run the command `vagrant up`;
Vagrant creates 2 VMS  that we can see in virtual box;
We can ssh into these machines by opening a terminal in the VagrantVA folder and running the command `vagrant ssh <machine name>`;
We can use the browser to access it using the IP and ports inside the vagrant file;
Using http://192.168.56.11:8082 we have access to the H2 console and using http://192.168.56.10:8080/ we get the It works! landing page of Apache Tomcat  





Part 2 - Setup CA3Part2 Issue #20

Make our repo public so Vagrant can clone it;
Copy Vagrant file into folder;

Use the manually done requirements in CA3/Part1:
- For both VMs change java version to 17;
- Starting with the Web VM:
  - Add set -e command to the top of the script to avoid continuing if an error occurs;
  - Install Git;
  - Install Gradle 8.6 which is what we manually did in CA3/Part1 so our application could run;
  - Remove tomcat installation as we are not using it;
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
 