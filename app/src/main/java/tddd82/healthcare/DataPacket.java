package tddd82.healthcare;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket {

    private final static int ENCRYPTION_START = 12;
    public final static int HEADER_SIZE = 57;
    public final static int MAX_SIZE = 65507 - HEADER_SIZE;

    public final static int FLAG_IS_VIDEO = 1;
    public final static int FLAG_INCREASE_QUALITY = 2;
    public final static int FLAG_DECREASE_QUALITY = 3;

    private byte[] buffer;
    private long creationTime;

    public DataPacket(int dataSize){
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
        return getLength() - HEADER_SIZE;
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

    public byte[] getChecksum(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 25, 57);
        return tmp;
    }

    public int getSequenceNumber(){

        byte[] tmp = Arrays.copyOfRange(this.buffer, 21, 25);
        return ByteBuffer.wrap(tmp).getInt();
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

    public void setChecksum(byte[] checksum){
        setRange(checksum, this.buffer, 25);
    }

    public void setSequenceNumber(int sequenceNumber){
        byte[] tmp = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        setRange(tmp, this.buffer, 21);
    }

    public void setPayload(byte[] payload){
        System.arraycopy(payload, 0, this.buffer, HEADER_SIZE, payload.length);
    }


    public boolean validChecksum(){
        byte[] sha = ServerUtils.getSHA256(buffer, 0, 25);
        for (int i = 25; i < 57; i++){
            if (sha[i - 25] != buffer[i])
                return false;
        }

        return true;
    }

    public void addChecksum(){
        setChecksum(ServerUtils.getSHA256(buffer, 0, 25));
    }

    public static byte[] setRange(byte[] source, byte[] destination, int start){
        for(int i=start; i < start+source.length; i++)
            destination[i]=source[i-start];

        return destination;
    }

    public boolean encrypt(CallCrypto cc){
        int src = getSource();
        int dst = getDestination();
        int len = getLength();
        byte[] encrypted = cc.encrypt(this.buffer, ENCRYPTION_START, len - ENCRYPTION_START);
        if (encrypted == null){
            return false;
        }
        this.buffer = new byte[encrypted.length + ENCRYPTION_START];
        setSource(src);
        setDestination(dst);
        setLength(encrypted.length + ENCRYPTION_START);
        System.arraycopy(encrypted, 0, this.buffer, ENCRYPTION_START, encrypted.length);

        return true;
    }

    public boolean decrypt(CallCrypto cc){
        int totalLength = getLength();
        int src = getSource();
        int dst = getDestination();
        byte[] decrypted = cc.decrypt(this.buffer, ENCRYPTION_START, totalLength - ENCRYPTION_START);
        if (decrypted == null) {
            return false;
        }
        this.buffer = new byte[decrypted.length + ENCRYPTION_START];
        System.arraycopy(decrypted, 0, this.buffer, ENCRYPTION_START, decrypted.length);

        setSource(src);
        setDestination(dst);
        setLength(decrypted.length + ENCRYPTION_START);

        return true;
    }

    public float getFrameRate(){
        byte[] tmp = Arrays.copyOfRange(this.buffer, 16, 20);
        return ByteBuffer.wrap(tmp).getFloat();
    }

    public void setFrameRate(float frameRate){
        byte[] tmp = ByteBuffer.allocate(4).putFloat(frameRate).array();
        setRange(tmp, buffer, 16);
    }
}
