package tddd82.healthcare;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ServerUtils
 */
public class ServerUtils{
    public static byte[] inputStreamToByte(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = stream.read(data, 0, data.length)) != -1)
            buffer.write(data, 0, nRead);
        buffer.flush();

        return buffer.toByteArray();
    }

    /**
     * Return the i:th bit from byte
     */
    public static Integer getBit(byte input, int position){
        return ((input >> position) & 1);
    }

    /**
     * Sets the i:th bit in a byte to specified value
     */
    public static byte setBit(byte input, int position, boolean val){
        return val? (byte) (input | (1 << position)):(byte) (input & ~(1 << position));
    }

    /**
     * Sets range of array
     */
    public static byte[] setRange(byte[] source, byte[] destination, int start, int end ){
        for(int i=start; i <= end; i++)
            destination[i]=source[i-start];
        return destination;
    }

    /**
     * Sets range of array
     */
    public static byte[] setRange(byte[] source, byte[] destination, int start){
        for(int i=start; i < start+source.length; i++)
            destination[i]=source[i-start];

        return destination;
    }

    public static void sendBytes(byte[] bytesToSend, Socket clientSocket) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        if (bytesToSend.length > 0)
            dos.write(bytesToSend, 0, bytesToSend.length);
        Log.d("bob","hejhejhej");
    }
}