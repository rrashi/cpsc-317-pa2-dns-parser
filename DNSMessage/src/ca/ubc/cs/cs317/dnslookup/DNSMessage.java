package ca.ubc.cs.cs317.dnslookup;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class DNSMessage {
    public static final int MAX_DNS_MESSAGE_LENGTH = 512;

    // The offset into the message where the header ends and the data begins.
    public final static int DataOffset = 12;

    // Opcode for a standard query
    public final static int QUERY = 12;

    private final ByteBuffer buffer;




    /**
     * Initializes an empty DNSMessage with the given id.
     *
     * @param id The id of the message.
     */
    public DNSMessage(short id) {
        this.buffer = ByteBuffer.allocate(MAX_DNS_MESSAGE_LENGTH);
        // TODO: Complete this method
        setID(id);
    }

    /**
     * Initializes a DNSMessage with the first length bytes of the given byte array.
     *
     * @param recvd The byte array containing the received message
     * @param length The length of the data in the array
     */
    public DNSMessage(byte[] recvd, int length) {
        buffer = ByteBuffer.wrap(recvd, 0, length);
        // TODO: Complete this method
        getID();
        getQR();
        getOpcode();

    }

    /**
     * Getters and setters for the various fixed size and fixed location fields of a DNSMessage
     * TODO:  They are all to be completed
     */
    public int getID() {
        buffer.position(0);
        return (int)buffer.getShort() & 0xFFFF;
    }

    public void setID(int id) {
        buffer.position(0);
        buffer.putShort((short) id);
    }

    public boolean getQR() {
        int qr = buffer.getChar(2)>>15;
        System.out.println("qr in get " + qr);
        if(qr == 1){
            return true;
        }
        return false;
    }

    public void setQR(boolean qr) {
        System.out.println("set qr "+ qr);
        char temp = buffer.getChar(2);
        int QR =  qr ? (0x8000|temp) : (0xFFFE&temp);
        System.out.println("temp | char "+ (temp|QR));
        buffer.putChar(2, (char)QR);
    }

    public boolean getAA() {
        int aa = (buffer.getChar(2)>>10) & 0x1; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        System.out.println("aa in get " + aa);
        if(aa == 1){
            return true;
        }
        return false;
    }

    public void setAA(boolean aa) {
        System.out.println("set aa "+ aa);
        char temp = buffer.getChar(2);
        int AA =  aa ? (0x4000|temp) : (0x3ff&temp);
        buffer.putChar(2, (char)AA);
    }


    public int getOpcode() {
        int aa = (buffer.getChar(2)>>11) & 0xF; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        return aa;
    }

    public void setOpcode(int opcode) {
        char temp = buffer.getChar(2);
        int OP = opcode<<15|temp;
        buffer.putChar(2, (char)OP);
    }

    public boolean getTC() {
        int tc = (buffer.getChar(2)>>9) & 0x1; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        System.out.println("aa in get " + tc);
        if(tc == 1){
            return true;
        }
        return false;
    }

    public void setTC(boolean tc) {
        System.out.println("set tc "+ tc);
        char temp = buffer.getChar(2);
        int TC =  tc ? (0x200|temp) : (0x1ff&temp);
        buffer.putChar(2, (char)TC);
    }

    public boolean getRD() {
        int rd = (buffer.getChar(2)>>8) & 0x1; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        System.out.println("rd in get " + rd);
        if(rd == 1){
            return true;
        }
        return false;
    }

    public void setRD(boolean rd) {
        System.out.println("set rd "+ rd);
        char temp = buffer.getChar(2);
        int RD =  rd ? (0x100|temp) : (0x1fe&temp);
        buffer.putChar(2, (char)RD);
    }

    public boolean getRA() {
        int ra = (buffer.getChar(2)>>7) & 0x1; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        System.out.println("ra in get " + ra);
        if(ra == 1){
            return true;
        }
        return false;
    }

    public void setRA(boolean ra) {
        System.out.println("set ra "+ ra);
        char temp = buffer.getChar(2);
        int RA =  ra ? (0x80|temp) : (0xfe&temp);
        buffer.putChar(2, (char)RA);
    }

    public int getRcode() {
        int rcode = buffer.getChar(2) & 0xF; //shift by 9 buts to the right, only keep the first bit (get's rid of opcode and QR)
        return rcode;
    }

    public void setRcode(int rcode) {
        char temp = buffer.getChar(2);
        int OP = rcode<<4|temp;
        buffer.putChar(2, (char)OP);

    }

    public int getQDCount() {
        buffer.position(4);
        return buffer.getShort() & 0xFFFF;
    }

    public void setQDCount(int count) {
        buffer.position(4);
        buffer.putShort((short) count);
    }

    public int getANCount() {
        buffer.position(6);
        return buffer.getShort() & 0xFFFF;
    }

    public int getNSCount() {
        buffer.position(8);
        return buffer.getShort() & 0xFFFF;
    }

    public int getARCount() {
        buffer.position(10);
        return buffer.getShort() & 0xFFFF;
    }

    public void setARCount(int count) {
        buffer.position(10);
        buffer.putShort((short) count);
    }

    /**
     * Return the name at the current position() of the buffer.
     *
     * The encoding of names in DNS messages is a bit tricky.
     * You should read section 4.1.4 of RFC 1035 very, very carefully.  Then you should draw a picture of
     * how some domain names might be encoded.  Once you have the data structure firmly in your mind, then
     * design the code to read names.
     *
     * @return The decoded name
     */
    public String getName() {
        // TODO: Complete this method
        return "";
    }

    /**
     * The standard toString method that displays everything in a message.
     * @return The string representation of the message
     */
    public String toString() {
        // Remember the current position of the buffer so we can put it back
        // Since toString() can be called by the debugger, we want to be careful to not change
        // the position in the buffer.  We remember what it was and put it back when we are done.
        int end = buffer.position();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(getID()).append(' ');
            sb.append("QR: ").append(getQR() ? "Response" : "Query").append(' ');
            sb.append("OP: ").append(getOpcode()).append(' ');
            sb.append("AA: ").append(getAA()).append('\n');
            sb.append("TC: ").append(getTC()).append(' ');
            sb.append("RD: ").append(getRD()).append(' ');
            sb.append("RA: ").append(getRA()).append(' ');
            sb.append("RCODE: ").append(getRcode()).append(' ')
                    .append(dnsErrorMessage(getRcode())).append('\n');
            sb.append("QDCount: ").append(getQDCount()).append(' ');
            sb.append("ANCount: ").append(getANCount()).append(' ');
            sb.append("NSCount: ").append(getNSCount()).append(' ');
            sb.append("ARCount: ").append(getARCount()).append('\n');
            buffer.position(DataOffset);
            showQuestions(getQDCount(), sb);
            showRRs("Authoritative", getANCount(), sb);
            showRRs("Name servers", getNSCount(), sb);
            showRRs("Additional", getARCount(), sb);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "toString failed on DNSMessage";
        }
        finally {
            buffer.position(end);
        }
    }

    /**
     * Add the text representation of all the questions (there are nq of them) to the StringBuilder sb.
     *
     * @param nq Number of questions
     * @param sb Collects the string representations
     */
    private void showQuestions(int nq, StringBuilder sb) {
        sb.append("Question [").append(nq).append("]\n");
        for (int i = 0; i < nq; i++) {
            DNSQuestion question = getQuestion();
            sb.append('[').append(i).append(']').append(' ').append(question).append('\n');
        }
    }

    /**
     * Add the text representation of all the resource records (there are nrrs of them) to the StringBuilder sb.
     *
     * @param kind Label used to kind of resource record (which section are we looking at)
     * @param nrrs Number of resource records
     * @param sb Collects the string representations
     */
    private void showRRs(String kind, int nrrs, StringBuilder sb) {
        sb.append(kind).append(" [").append(nrrs).append("]\n");
        for (int i = 0; i < nrrs; i++) {
            ResourceRecord rr = getRR();
            sb.append('[').append(i).append(']').append(' ').append(rr).append('\n');
        }
    }

    /**
     * Decode and return the question that appears next in the message.  The current position in the
     * buffer indicates where the question starts.
     *
     * @return The decoded question
     */
    public DNSQuestion getQuestion() {
        // TODO: Complete this method
        return null;
    }

    /**
     * Decode and return the resource record that appears next in the message.  The current
     * position in the buffer indicates where the resource record starts.
     *
     * @return The decoded resource record
     */
    public ResourceRecord getRR() {
        // TODO: Complete this method
        return null;
    }

    /**
     * Helper function that returns a hex string representation of a byte array. May be used to represent the result of
     * records that are returned by a server but are not supported by the application (e.g., SOA records).
     *
     * @param data a byte array containing the record data.
     * @return A string containing the hex value of every byte in the data.
     */
    public static String byteArrayToHexString(byte[] data) {
        return IntStream.range(0, data.length).mapToObj(i -> String.format("%02x", data[i])).reduce("", String::concat);
    }
    /**
     * Helper function that returns a byte array from a hex string representation. May be used to represent the result of
     * records that are returned by a server but are not supported by the application (e.g., SOA records).
     *
     * @param hexString a string containing the hex value of every byte in the data.
     * @return data a byte array containing the record data.
     */
    public static byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String s = hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte)Integer.parseInt(s, 16);
        }
        return bytes;
    }

    /**
     * Add an encoded name to the message. It is added at the current position and uses compression
     * as much as possible.  Make sure you understand the compressed data format of DNS names.
     *
     * @param name The name to be added
     */
    public void addName(String name) {
        // TODO: Complete this method
    }

    /**
     * Add an encoded question to the message at the current position.
     * @param question The question to be added
     */
    public void addQuestion(DNSQuestion question) {
        // TODO: Complete this method
    }

    /**
     * Add an encoded resource record to the message at the current position.
     * The record is added to the additional records section.
     * @param rr The resource record to be added
     */
    public void addResourceRecord(ResourceRecord rr) {
        addResourceRecord(rr, "additional");
    }

    /**
     * Add an encoded resource record to the message at the current position.
     *
     * @param rr The resource record to be added
     * @param section Indicates the section to which the resource record is added.
     *                It is one of "answer", "nameserver", or "additional".
     */
    public void addResourceRecord(ResourceRecord rr, String section) {
        // TODO: Complete this method
    }

    /**
     * Add an encoded type to the message at the current position.
     * @param recordType The type to be added
     */
    private void addQType(RecordType recordType) {
        // TODO: Complete this method
    }

    /**
     * Add an encoded class to the message at the current position.
     * @param recordClass The class to be added
     */
    private void addQClass(RecordClass recordClass) {
        // TODO: Complete this method
    }

    /**
     * Return a byte array that contains all the data comprising this message.  The length of the
     * array will be exactly the same as the current position in the buffer.
     * @return A byte array containing this message's data
     */
    public byte[] getUsed() {
        // TODO: Complete this method
        return new byte[0];
    }

    /**
     * Returns a string representation of a DNS error code.
     *
     * @param error The error code received from the server.
     * @return A string representation of the error code.
     */
    public static String dnsErrorMessage(int error) {
        final String[] errors = new String[]{
                "No error", // 0
                "Format error", // 1
                "Server failure", // 2
                "Name error (name does not exist)", // 3
                "Not implemented (parameters not supported)", // 4
                "Refused" // 5
        };
        if (error >= 0 && error < errors.length)
            return errors[error];
        return "Invalid error message";
    }
}
