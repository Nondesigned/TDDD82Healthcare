package tddd82.healthcare;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket {


    private final static int HEADER_SIZE = 25;
    public final static int MAX_SIZE = 65507 - HEADER_SIZE;

    public final static int FLAG_IS_VIDEO = 1;
    public final static int FLAG_ENCODING = 2;

    private byte[] buffer;
    private int dataSize;
    private int length;
    private long creationTime;

    public DataPacket(int dataSize){
        this.dataSize = dataSize;
        this.length = dataSize + HEADER_SIZE;
        this.buffer = new byte[HEADER_SIZE + dataSize];
        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime(){
        return creationTime;
    }

    public byte[] getBuffer(){
        return this.buffer;
    }

    public int getLength(){
        return length;
    }

    public int getPayloadIndex(){
        return HEADER_SIZE;
    }

    public int getPayloadLength(){
        return dataSize;
    }

    public int getSource(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 0, 4);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getDestination(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 4, 8);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getSampleRate(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 8, 12);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getBufferSize(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 12, 16);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public byte getFlags(){
        return buffer[16];
    }

    public boolean hasFlag(int flag){
        return ServerUtils.getBit(getFlags(), flag) > 0;
    }

    public int getChecksum(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 17, 21);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getSequenceNumber(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 21, 25);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
    }

    public void setSource(int src){
        byte[] tmp = ByteBuffer.allocate(4).putInt(src).array();
        setRange(tmp, this.buffer, 0);
    }

    public void setDestination(int dst){
        byte[] tmp = ByteBuffer.allocate(4).putInt(dst).array();
        setRange(tmp, this.buffer, 4);
    }

    public void setSampleRate(int sampleRate){
        byte[] tmp = ByteBuffer.allocate(4).putInt(sampleRate).array();
        setRange(tmp, this.buffer, 8);
    }

    public void setBufferSize(int bufferSize){
        byte[] tmp = ByteBuffer.allocate(4).putInt(bufferSize).array();
        setRange(tmp, this.buffer, 12);
    }

    public void setFlags(byte flags){
        this.buffer[16] = flags;
    }

    public void setFlag(int flag, boolean value){
        byte mod = ServerUtils.setBit(getFlags(), flag, value);

        setFlags(mod);
    }

    public void setChecksum(int checksum){
        byte[] tmp = ByteBuffer.allocate(4).putInt(checksum).array();
        setRange(tmp, this.buffer, 17);
    }

    public void setSequenceNumber(int sequenceNumber){
        byte[] tmp = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        setRange(tmp, this.buffer, 21);
    }

    public static byte[] setRange(byte[] source, byte[] destination, int start){
        for(int i=start; i < start+source.length; i++)
            destination[i]=source[i-start];

        return destination;
    }

    public static byte[] setRange(byte[] source, byte[] destination, int start, int end ){
        for(int i=start; i <= end; i++)
            destination[i]=source[i-start];
        return destination;
    }
    public static void encryptPacket(String key){

    }
    public static void decryptPacket(String key){

    }
}
