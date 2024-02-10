import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        String fileName = "test.txt";

        int clientPort = 5099;
        int hostPort = 5098;
        int hostClientPort = 5097;
        int serverPort = 5096;

        int bufferSize = 1024;

        InetAddress clientIP = InetAddress.getLoopbackAddress();
        InetAddress hostIP = InetAddress.getLoopbackAddress();
        InetAddress serverIP = InetAddress.getLoopbackAddress();

        Client client = new Client(hostClientPort, clientPort, bufferSize, fileName, hostIP);
        Thread clientThread = new Thread(client);
        clientThread.start();

        Host host = new Host(serverPort, hostClientPort, hostPort, bufferSize, serverIP);
        Thread hostThread = new Thread(host);
        hostThread.start();

        Server server = new Server(serverPort, bufferSize);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}