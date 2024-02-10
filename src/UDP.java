import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class UDP {
    public static DatagramPacket createPacket(int type, int bufferSize, String fileName) {
        byte[] packetArray = new byte[bufferSize];
        int arrayPointer = 0;

        //This part is common to all the types
        packetArray[arrayPointer] = 0;
        arrayPointer++;

        switch (type) { //Read Request
            case 1:
                packetArray[arrayPointer] = 1;
                arrayPointer++;

            case 2: //Write Request
                packetArray[arrayPointer] = 2;
                arrayPointer++;

            case 3: //Server response to read request
                packetArray[arrayPointer] = 3;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
                packetArray[arrayPointer] = 1;
                arrayPointer++;
            case 4: //Server response to write request
                packetArray[arrayPointer] = 4;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
        }

        if (type == 1 || type == 2) { //This part is common for both client types
            byte[] messageBytes = fileName.getBytes(StandardCharsets.US_ASCII);
            for (byte b : messageBytes) {
                packetArray[arrayPointer] = b;
                arrayPointer++;
            }

            packetArray[arrayPointer] = 0;
            arrayPointer++;

            byte[] modeBytes = "netascii".getBytes(StandardCharsets.US_ASCII);
            for (byte b : modeBytes) {
                packetArray[arrayPointer] = b;
                arrayPointer++;
            }

            packetArray[arrayPointer] = 0;
            arrayPointer++;
        }

        return new DatagramPacket(packetArray, arrayPointer);
    }
}
