package tddd82.healthcare;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoBuffer {
    private Queue<DataPacket> sendQueue;
    private final int MAX_SIZE = 300;
    private final int PACKET_LIFETIME = 500;

    private long receiveTimestamp = 0;
    private float receiveRate = 10;
    private float receiveSample = 0;

    public VideoBuffer(){
        sendQueue = new ConcurrentLinkedQueue<>();
    }

    public DataPacket poll(){
        DataPacket ret;
        long current = System.currentTimeMillis();
        do{
            ret = sendQueue.poll();
        } while(!sendQueue.isEmpty() && ret.getCreationTime() + PACKET_LIFETIME < current);
        return ret;
    }

    public int size(){
        return sendQueue.size();
    }

    public boolean empty(){
        return sendQueue.isEmpty();
    }

    public void push(DataPacket data){
        if (sendQueue.size() >= MAX_SIZE)
            sendQueue.remove();
        sendQueue.add(data);

        if (receiveTimestamp + 500 < System.currentTimeMillis()){
            receiveTimestamp = System.currentTimeMillis();
            receiveRate = 2*receiveSample;
            receiveSample = 0;
        }
        receiveSample++;
    }

    public float getReceiveRate(){
        return receiveRate;
    }
}
