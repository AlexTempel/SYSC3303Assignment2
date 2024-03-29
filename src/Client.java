import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable{
    private String fileName;
    private final int hostPort;
    private final int clientPort;
    private final int bufferSize;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket clientSocket;
    private final InetAddress hostIP;
    Client(int hostPort, int clientPort, int bufferSize, String fileName, InetAddress hostIP) {
        this.hostPort = hostPort;
        this.clientPort = clientPort;
        this.bufferSize = bufferSize;
        this.fileName = fileName;
        this.hostIP = hostIP;

        //Create a socket to send/receive from the host
        try {
            clientSocket = new DatagramSocket(clientPort);
            clientSocket.connect(hostIP, hostPort);
        } catch (Exception e) {
            System.out.println("Cannot create Client Socket");
            System.exit(1);
        }
    }

    public void run(){
        System.out.println("Client Starting");

        //Send 5 read packets and 5 write packets
        for (int i = 0; i < 5; i++) {
            //Send read request
            sendPacket = UDP.createPacket(1, bufferSize, fileName);
            UDP.printPacketInfo(sendPacket, 1);
            try {
                clientSocket.send(sendPacket);
            } catch (Exception e) {
                System.out.println("Could not send Packet from client to host");
            }

            //Send write request
            sendPacket = UDP.createPacket(2, bufferSize, fileName);
            UDP.printPacketInfo(sendPacket, 1);
            try {
                clientSocket.send(sendPacket);
            } catch (Exception e) {
                System.out.println("Could not send Packet from client to host");
            }
        }
        //Send Invalid
        sendPacket = UDP.createPacket(0, bufferSize, fileName);
        UDP.printPacketInfo(sendPacket, 1);
        try {
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            System.out.println("Could not send Packet from client to host");
        }
        clientSocket.disconnect(); //Disconnect socket to let it receive

        while (true) {
            receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try { //Receive and print packets in response to host/server
                clientSocket.receive(receivePacket);
                System.out.println("Received Packet");
                UDP.printPacketInfo(receivePacket, 1);
            } catch (Exception e) {
                System.out.println("\nIssue receiving packet in client");
                e.printStackTrace();
            }
        }

    }
}
