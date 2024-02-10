import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server implements Runnable {
    private DatagramSocket serverSocket;
    private final int serverPort;
    private final int bufferSize;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;

    Server(int serverPort, int bufferSize) {
        this.serverPort = serverPort;
        this.bufferSize = bufferSize;

        try {
            serverSocket = new DatagramSocket(serverPort);
        } catch (Exception e) {
            System.out.println("Error creating server socket");
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("Server Starting");

        while (true) {
            //Receive Packet from host
            receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try {
                serverSocket.receive(receivePacket);
            } catch (Exception e) {
                System.out.println("Error receiving on Server");
            }
            UDP.printPacketInfo(receivePacket, 3);
            if (UDP.packetDecode(receivePacket) == -1) {
                System.out.println("Invalid Packet");
                System.exit(1);
            }

            //Send Response to host
            sendPacket = UDP.createPacket(UDP.packetDecode(receivePacket) + 2, bufferSize, "");
            UDP.printPacketInfo(sendPacket, 3);
            try {
                serverSocket.connect(receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket);
                serverSocket.disconnect();
            } catch (Exception e) {
                System.out.println("Could not send response from server");
                System.exit(1);
            }
        }
    }
}
