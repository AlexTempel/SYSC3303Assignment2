import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        int clientPort = 5099;
        int hostPort = 5098;
        int serverPort = 5097;

        int bufferSize = 1024;

        InetAddress clientIP = InetAddress.getLoopbackAddress();
        InetAddress hostIP = InetAddress.getLoopbackAddress();
        InetAddress serverIP = InetAddress.getLoopbackAddress();


    }
}