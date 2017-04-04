package tddd82.healthcare;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket {


    public final static int HEADER_SIZE = 29;
    public final static int MAX_SIZE = 65507 - HEADER_SIZE;

    public final static int FLAG_IS_VIDEO = 1;
    public final static int FLAG_ENCODING = 2;

    private byte[] buffer;
    private int dataSize;
    private long creationTime;

    public DataPacket(int dataSize){
        this.dataSize = dataSize;
        this.buffer = new byte[HEADER_SIZE + dataSize];
        setLength(dataSize + HEADER_SIZE);
        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime(){
        return creationTime;
    }

    public byte[] getBuffer(){
        return this.buffer;
    }

    public int getLength(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 8, 12);
        return ByteBuffer.wrap(tmp).getInt();
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
        byte[] tmp = Arrays.copyOfRange(this.buffer, 16, 20);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getBufferSize(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 12, 16);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public byte getFlags(){
        return buffer[20];
    }

    public boolean hasFlag(int flag){
        return ServerUtils.getBit(getFlags(), flag) > 0;
    }

    public int getChecksum(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 25, 29);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public int getSequenceNumber(){

        byte[] tmp = Arrays.copyOfRange(this.buffer, 21, 25);
        return ByteBuffer.wrap(tmp).getInt();
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
    }

    public void setPayload(byte[] buffer){
        setRange(buffer, this.buffer, HEADER_SIZE);
    }

    public void setSource(int src){
        byte[] tmp = ByteBuffer.allocate(4).putInt(src).array();
        setRange(tmp, this.buffer, 0);
    }

    public void setDestination(int dst){
        byte[] tmp = ByteBuffer.allocate(4).putInt(dst).array();
        setRange(tmp, this.buffer, 4);
    }

    public void setLength(int length){
        byte[] tmp = ByteBuffer.allocate(4).putInt(length).array();
        setRange(tmp, this.buffer, 8);
    }

    public void setSampleRate(int sampleRate){
        byte[] tmp = ByteBuffer.allocate(4).putInt(sampleRate).array();
        setRange(tmp, this.buffer, 16);
    }

    public void setBufferSize(int bufferSize){
        byte[] tmp = ByteBuffer.allocate(4).putInt(bufferSize).array();
        setRange(tmp, this.buffer, 12);
    }

    public void setFlags(byte flags){
        this.buffer[20] = flags;
    }

    public void setFlag(int flag, boolean value){
        byte mod = ServerUtils.setBit(getFlags(), flag, value);

        setFlags(mod);
    }

    public void setChecksum(int checksum){
        byte[] tmp = ByteBuffer.allocate(4).putInt(checksum).array();
        setRange(tmp, this.buffer, 25);
    }

    public void setSequenceNumber(int sequenceNumber){
        byte[] tmp = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        setRange(tmp, this.buffer, 21);
    }


    public boolean validChecksum(){
        return getChecksum() == ServerUtils.getCRC32(buffer, 0, 25);
    }

    public void addChecksum(){
        setChecksum(ServerUtils.getCRC32(buffer, 0, 25));
    }

    public static byte[] setRange(byte[] source, byte[] destination, int start){
        for(int i=start; i < start+source.length; i++)
            destination[i]=source[i-start];

        return destination;
    }

    public void encrypt(CallCrypto cc){
        int src = getSource();
        int dst = getDestination();
        int len = getLength();
        byte[] encrypted = cc.encrypt(this.buffer, 12, len - 12);
        if (encrypted != null) {
            this.buffer = new byte[encrypted.length + 12];
            setSource(src);
            setDestination(dst);
            setLength(encrypted.length + 12);

            setRange(encrypted, buffer, 12);
        } else{
            System.out.println(src);
        }
    }

    public void decrypt(CallCrypto cc){
        int totalLength = getLength();
        int src = getSource();
        int dst = getDestination();
        byte[] decrypted = cc.decrypt(this.buffer, 12, totalLength - 12);
        this.buffer = new byte[decrypted.length + 12];
        setRange(decrypted, this.buffer, 12);
        setSource(src);
        setDestination(dst);
        setLength(decrypted.length + 12);
    }

    public void printChecksums(){
        System.out.println(((Integer)getChecksum()).toString() + " == " + ((Integer)ServerUtils.getCRC32(this.buffer, 0, 25)).toString());
    }
}
