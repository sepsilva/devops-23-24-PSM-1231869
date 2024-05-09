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