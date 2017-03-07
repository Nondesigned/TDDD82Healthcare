package tddd82.healthcare;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket {

    private final int HEADER_SIZE = 8;
    private byte[] buffer;
    private int length;

    public DataPacket(int dataSize){
        this.length = dataSize;
        this.buffer = new byte[HEADER_SIZE + dataSize];
    }

    public byte[] getBuffer(){
        return this.buffer;
    }

    public int getLength(){
        return this.length;
    }

    public int getDataIndex(){
        return HEADER_SIZE;
    }

    public void setSource(int src){
        byte[] source = ByteBuffer.allocate(4).putInt(src).array();
        setRange(source, this.buffer, 0);
    }

    public void setDestination(int dst){
        byte[] destination = ByteBuffer.allocate(4).putInt(dst).array();
        setRange(destination, this.buffer, 4);
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

}
