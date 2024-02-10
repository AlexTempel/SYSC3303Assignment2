SYSC 3303 Assignment 2
Alex Tempel 101225006

Files:
Main.java - File to be run
Client.java - Process for the client
Host.java - Process for the host
Server.java - Process for the server
UDP.java - Collection of static methods useful for doing UDP things

To Run:
Run Main.java
If you want to run each process on a different computer
    Set the hostIP, clientIP, and serverIP to the computers you want to use
    And then comment out the Thread.start() commands in main for which one you want to run where

Things you can change:
You can set the port variables in main to whatever you want as long as they aren't the same, and they're value port numbers
You can set the fileName to whatever you want as long as it does not cause the packets to be longer than the bufferSize
You can set the bufferSize to whatever you want as long as it can fit the packets