CS4470
Project #2, Distance Vector Routing Protocols

[ CONTRIBUTIONS ]

Four team members: Ryan, Carlos, Pavit, Ana

1st version (submitted 12/12/2020):
	Ryan Dunning
	- Wrote majority of code

	Carlos Alberto Mendoza Jr 
	- Contributed to code by adding features

	Pavit Chawla
	- We used his original scripts from Project #1

	Ana Guardado
	- Wrote majority of code
	- Video demo

2nd version (submitted 12/16/2020):
	Ryan Dunning
	- Wrote majority of the code w/ Ana
	- Tested and ensured project met requirements

	Ana Guardado
	- Wrote majority of code w/ Ryan
	- Video demo

[ INSTALLATION ]

- Works best on Ubuntu 16.04
- makefile, Java files, and txt file must all be in the same directory
- Confirm that /etc/hosts file contains the correct private IP address
	- If Java code returns 127.0.1.1 for private, apply the following steps
		- When using VirtualBox, VMs returned loopback IP when asking for private
		- Use command "sudo nano /etc/hosts"
		- Type private IP address next to hostname
			- Example: "192.168.56.104 ubuntu-1604-1"
- Ensure that the machines involved in this program can communicate with eachother
	- Use the ping command against the IPs/hostnames
		- If there is a response, the machines can be used in this program
	- Machines will need to be on the same network
- Install Java on Ubuntu using command "sudo apt-get install openjdk-8-jdk"
- Compile Java files
	- Use the command "make". The make command will run the makefile attached to this project
- After compiled, run Java chat file using command "java distance_vector_routing server -t <file> -i <interval>"
	- Will immediately start to update neighbors and print success/error messages
	- Replace <file> with topology file name
	- Replace <interval> with routing-update-interval

[ RESOURCES ]

The following links were used in the development of this project:

- Installing Java and compiling code
	- https://askubuntu.com/questions/145748/how-to-compile-a-java-file-on-ubuntu
- Socket programming
	- http://beej.us/guide/bgnet/html/#client-server-background
	- https://stackoverflow.com/questions/27200158/inetaddress-getlocalhost-gethostaddress-returning-unwanted-address-java
	- https://stackoverflow.com/questions/8051863/how-can-i-close-the-socket-in-a-proper-way
	- https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
	- https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
	- https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html
- Java Threads
	- https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
- Makefile guide
	- https://www.cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html

