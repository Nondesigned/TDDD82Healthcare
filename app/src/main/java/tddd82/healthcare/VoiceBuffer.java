package tddd82.healthcare;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoiceBuffer {

    private Queue<DataPacket> sendQueue;
    private final int MAX_SIZE = 1000;
    private final int PACKET_LIFETIME = 1500;

    public VoiceBuffer(){
        sendQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean empty(){
        return sendQueue.isEmpty();
    }

    public DataPacket poll(){
        DataPacket ret;
        long current = System.currentTimeMillis();
        do{
            ret = sendQueue.poll();
        } while(!sendQueue.isEmpty() && ret.getAge() + PACKET_LIFETIME < current);
        return ret;
    }

    public int size(){
        return sendQueue.size();
    }
    public void push(DataPacket data){
        if (sendQueue.size() >= MAX_SIZE)
            sendQueue.remove();
        sendQueue.add(data);
    }

    //Milliseconds, dividing by 2 because of 16BIT_PCM
    public int estimateTime(){
        float time = 0;
        for (DataPacket p : sendQueue){
            time += (int)(((float)p.getBufferSize() * 1000f)/(float)p.getSampleRate()) / 2;
        }

        return (int)time;
    }
}