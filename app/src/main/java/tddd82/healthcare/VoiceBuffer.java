package tddd82.healthcare;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoiceBuffer {

    private Queue<byte[]> sendQueue;

    public VoiceBuffer(){
        sendQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean empty(){
        return sendQueue.isEmpty();
    }

    public byte[] poll(){
        return sendQueue.poll();
    }

    public void push(byte[] data){
        sendQueue.add(data);
    }
}
