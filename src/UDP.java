import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class UDP {
    public static String typeLookup(int type) {
        switch (type) {
            case 1:
                return "Client Read Request";
            case 2:
                return "Client Write Request";
            case 3:
                return "Server Read Response";
            case 4:
                return "Server Write Response";
            default:
                return "Invalid";
        }
    }
    public static void printPacketInfo(DatagramPacket packet) {
        System.out.println("Packet Info: ");
        byte[] packetData = packet.getData();

        System.out.println("Packet type: " + typeLookup(packetDecode(packet)));
        if (packetDecode(packet) == 1 || packetDecode(packet) == 2) {
            System.out.println("Message: " + extractMessage(packet));
            System.out.println("Bytes: " + byteArrayToBinary(packetData));
        }

    }
    public static String byteArrayToBinary(byte[] array) {
        String returnString = "";
        for (byte b : array) {

            returnString = returnString.concat(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            returnString = returnString.concat(", ");
        }
        return returnString;
    }
    public static String extractMessage(DatagramPacket packet) {
        if (packetDecode(packet) != 1 && packetDecode(packet) != 2) {
            return "This packet has no message";
        }

        byte[] packetData = packet.getData();
        int packetLength = packet.getLength();
        int arrayPointer = 2;
        ArrayList<Byte> messageBytes = new ArrayList<>();

        while (arrayPointer < packetLength) {
            if (packetData[arrayPointer] == 0) {
                break;
            }
            messageBytes.add(packetData[arrayPointer]);
            arrayPointer++;
        }

        byte[] messagePrimitive = new byte[messageBytes.toArray().length];
        int primitiveArrayPointer = 0;
        for (Byte b : messageBytes) {
            messagePrimitive[primitiveArrayPointer] = b;
            primitiveArrayPointer++;
        }


        return new String(messagePrimitive);
    }

    public static int packetDecode(DatagramPacket packet) {
        byte[] packetData = packet.getData();
        switch (packetData[1]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default: //Invalid Packet
                return -1;
        }
    }
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
                break;
            case 2: //Write Request
                packetArray[arrayPointer] = 2;
                arrayPointer++;
                break;

            case 3: //Server response to read request
                packetArray[arrayPointer] = 3;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
                packetArray[arrayPointer] = 1;
                arrayPointer++;
                break;
            case 4: //Server response to write request
                packetArray[arrayPointer] = 4;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
                packetArray[arrayPointer] = 0;
                arrayPointer++;
                break;
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
