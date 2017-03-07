package tddd82.healthcare;

import android.provider.ContactsContract;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoiceBuffer {

    private Queue<DataPacket> sendQueue;
    private final int MAX_SIZE = 1000;

    public VoiceBuffer(){
        sendQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean empty(){
        return sendQueue.isEmpty();
    }

    public DataPacket poll(){
        return sendQueue.poll();
    }

    public int size(){
        return sendQueue.size();
    }
    public void push(DataPacket data){
        if (sendQueue.size() >= MAX_SIZE)
            sendQueue.remove();
        sendQueue.add(data);
    }
}