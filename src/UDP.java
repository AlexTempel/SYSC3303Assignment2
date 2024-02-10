import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class UDP {
    /**
     * From the type of a packet return a String representation
     * @param type type of the packet. Can be found with packetDecode()
     * @return String representation of type
     */
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

    /**
     * Print relevant information about a packet in the format:
     * Packet Info:
     * Packet in ___
     * Packet type: _
     * Message: _____
     * Bytes: [____, ____]
     * @param packet the UDP packet to be printed
     * @param source the process that is calling the print
     */
    public synchronized static void printPacketInfo(DatagramPacket packet, int source) {
        System.out.println("\nPacket Info: ");
        if (source == 1) {
            System.out.println("Packet in Client");
        } else if (source == 2) {
            System.out.println("Packet in Host");
        } else {
            System.out.println("Packet in Server");
        }

        System.out.println("Packet type: " + typeLookup(packetDecode(packet)));
        if (packetDecode(packet) == 1 || packetDecode(packet) == 2) { //Only client requests have messages
            System.out.println("Message: " + extractMessage(packet));
            System.out.println("Bytes: " + byteArrayToBinary(packet.getData()));
        }

    }

    /**
     * Transforms a byte array to a String of the binary representation of the bytes
     * @param array bytes to be converted to binary
     * @return String representation of bytes in binary
     */
    public static String byteArrayToBinary(byte[] array) {
        String returnString = "";
        for (byte b : array) {
            returnString = returnString.concat(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            returnString = returnString.concat(", ");
        }
        return returnString;
    }

    /**
     * Reconstructs the message from a packet
     * @param packet packet which the message is to be found from. Only accepts client request packets
     * @return String of the message in the packet
     */
    public static String extractMessage(DatagramPacket packet) {
        if (packetDecode(packet) != 1 && packetDecode(packet) != 2) { //If the packet type doesn't have a message
            return "This packet has no message";
        }

        byte[] packetData = packet.getData();
        int packetLength = packet.getLength();
        int arrayPointer = 2; //Message starts on the 3rd byte
        ArrayList<Byte> messageBytes = new ArrayList<>(); //Store relevant bytes in arraylist

        while (arrayPointer < packetLength) {
            if (packetData[arrayPointer] == 0) { //If it reaches the 0 byte, which represents the end of the message
                break;
            }
            messageBytes.add(packetData[arrayPointer]);
            arrayPointer++;
        }

        //Convert arraylist to array of bytes
        //Can't use .toArray since the arraylist is of Byte objects, but needs byte primitives
        byte[] messagePrimitive = new byte[messageBytes.toArray().length];
        int primitiveArrayPointer = 0;
        for (Byte b : messageBytes) {
            messagePrimitive[primitiveArrayPointer] = b;
            primitiveArrayPointer++;
        }

        return new String(messagePrimitive);
    }

    /**
     * Get what type of packet this is
     * @param packet packet to be decoded
     * @return integer of the type of the packet. To get String representation put this value into typeLookup()
     */
    public static int packetDecode(DatagramPacket packet) {
        byte[] packetData = packet.getData();
        switch (packetData[1]) { //Second byte determines the type
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

    /**
     * Creates a UDP packet following the template
     * @param type which type of packet to be created. 1 is client read request. 2 is client write request. 3 is server read response. 4 is server write response
     * @param bufferSize Max length of the packet
     * @param fileName Message to be put into packet
     * @return UDP packet following template
     */
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
