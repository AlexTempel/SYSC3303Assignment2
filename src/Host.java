import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Host implements Runnable {

    private final int bufferSize;
    private final InetAddress serverIP;
    private InetAddress clientIP;
    private int clientPort;
    private final int serverPort;
    private final int hostClientPort;
    private final int hostPort;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket clientSocket;
    private DatagramSocket sendSocket;

    Host(int serverPort, int hostClientPort, int hostPort, int bufferSize, InetAddress serverIP) {
        this.serverPort = serverPort;
        this.hostClientPort = hostClientPort;
        this.hostPort = hostPort;
        this.bufferSize = bufferSize;
        this.serverIP = serverIP;

        //Create new socket to receive from client
        try {
            clientSocket = new DatagramSocket(hostClientPort);
        } catch (Exception e) {
            System.out.println("Cannot create Host Socket");
            System.exit(1);
        }
        //Create new socket to send/receive from server and client
        try {
            sendSocket = new DatagramSocket(hostPort);
        } catch (Exception e) {
            System.out.println("Cannot create Host Socket");
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("Starting Host");

        while (true) {
            //Receive Packet from client
            receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try {
                clientSocket.receive(receivePacket);
            } catch (Exception e) {
                System.out.println("Error receiving packet on Host");
            }
            UDP.printPacketInfo(receivePacket, 2); //Print info
            clientIP = receivePacket.getAddress(); //Record its IP
            clientPort = receivePacket.getPort(); //Record its port

            //Create and send packet to Server
            sendPacket = UDP.createPacket(UDP.packetDecode(receivePacket), bufferSize, UDP.extractMessage(receivePacket));
            UDP.printPacketInfo(sendPacket, 2);
            try {
                sendSocket.connect(serverIP, serverPort);
                sendSocket.send(sendPacket);
                sendSocket.disconnect();
            } catch (Exception e) {
                System.out.println("Could not send Packet to Server");
                System.exit(1);
            }

            //Receive Response from server
            receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try {
                sendSocket.receive(receivePacket);
            } catch (Exception e) {
                System.out.println("Error receiving packet on Host");
            }
            UDP.printPacketInfo(receivePacket, 2);

            //Send response from server to client
            sendPacket = UDP.createPacket(UDP.packetDecode(receivePacket), bufferSize, UDP.extractMessage(receivePacket));
            UDP.printPacketInfo(sendPacket, 2);
            try {
                sendSocket.connect(clientIP, clientPort);
                sendSocket.send(sendPacket);
                sendSocket.disconnect();
            } catch (Exception e) {
                System.out.println("Could not send packet to host");
                System.exit(1);
            }
        }
    }
}
