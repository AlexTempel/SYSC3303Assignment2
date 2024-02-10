import java.net.DatagramPacket;

public class Client implements Runnable{
    private String fileName;
    private final int hostPort;
    private final int clientPort;
    private final int bufferSize;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    Client(int hostPort, int clientPort, int bufferSize) {
        this.hostPort = hostPort;
        this.clientPort = clientPort;
        this.bufferSize = bufferSize;

        fileName = "test.txt";
    }

    public void run(){
        System.out.println("Client Starting");

        sendPacket = UDP.createPacket(1, bufferSize, fileName);
        UDP.printPacketInfo(sendPacket);



    }
}
