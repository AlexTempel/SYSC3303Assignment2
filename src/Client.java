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

        int numPacketsUnaccounted =0;

        for (int i = 0; i < 1; i++) {
            //Send read request
            sendPacket = UDP.createPacket(1, bufferSize, fileName);
            UDP.printPacketInfo(sendPacket, 1);
            try {
                clientSocket.send(sendPacket);
            } catch (Exception e) {
                System.out.println("Could not send Packet from client to host");
            }
            numPacketsUnaccounted++;


            //Send write request
            sendPacket = UDP.createPacket(2, bufferSize, fileName);
            UDP.printPacketInfo(sendPacket, 1);
            try {
                clientSocket.send(sendPacket);
            } catch (Exception e) {
                System.out.println("Could not send Packet from client to host");
            }
            numPacketsUnaccounted++;

        }
        /*
        //Send Invalid
        sendPacket = UDP.createPacket(0, bufferSize, fileName);
        UDP.printPacketInfo(sendPacket, 1);
        try {
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            System.out.println("Could not send Packet from client to host");
        }
        numPacketsUnaccounted++;

             */

        clientSocket.disconnect();
        while (true) {
            receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try {
                clientSocket.receive(receivePacket);
                System.out.println("Received Packet");
                UDP.printPacketInfo(receivePacket, 1);
                numPacketsUnaccounted--;
                System.out.println("\nNumber of Client packets not received back: " + numPacketsUnaccounted);
                if (numPacketsUnaccounted == 0) {
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("\nIssue receiving packet in client");
                e.printStackTrace();
            }
        }

    }
}
