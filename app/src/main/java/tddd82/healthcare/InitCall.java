package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static tddd82.healthcare.ControlFlag.ACCEPTCALL;
import static tddd82.healthcare.ControlFlag.DECLINECALL;
import static tddd82.healthcare.ControlFlag.ENDCALL;
import static tddd82.healthcare.ControlFlag.INITCALL;

/**
 * Created by Oskar on 2017-03-07.
 */

public class InitCall extends Thread implements Runnable{
    Socket tcpSocket;
    ControlPacket ctrl;
    //private String key;
    String ip = "130.236.181.196";
    private int port = 1337;
    int sourceNr;
    int destNr;
    private Context context;
    private Event callEvent;
    // Defines header information and sends to server
    // tyeofFlag = 0 , initialize
    //  = 1 endCall
    // = 2 accept
    //= 3 decline
    // Remove key part until sprint 3

    public void init(int sourceNr,int destNr, Event callEvent,Context context){
        this.context = context;
        this.callEvent = callEvent;
        this.sourceNr = sourceNr;
        this.destNr = destNr;
        try {
            tcpSocket = new Socket(ip,port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[2044];
        ctrl = new ControlPacket(bytes);
        ctrl.setSource(sourceNr);
        ctrl.setDestination(destNr);

        SharedPreferences sharedPreferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");

        ctrl.setPayload(token.getBytes());
    }



    public void send(int typeOfFlag){
        ControlFlags flags = new ControlFlags();

        if(BatteryMng.doVideo()) {
            flags.setFlag(ControlFlag.ENDVID, true);
        }
        switch (typeOfFlag) {
            case 0:
                flags.setFlag(INITCALL, true);
                break;
            case 1:
                flags.setFlag(ENDCALL, true);
                break;
            case 2:
                flags.setFlag(ACCEPTCALL,true);
                String key = getKey();
                ctrl.setKey(key);
                break;
            case 3:
                flags.setFlag(DECLINECALL,true);
                break;
        }
        ctrl.setFlags(flags);
        try {
            ServerUtils.sendBytes(ctrl.getPacketBytes(),tcpSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        boolean connected = true;
        while(connected){
            try {
                ControlPacket receivedPacket = new ControlPacket(readData(tcpSocket.getInputStream()));
                boolean flag0 = receivedPacket.getFlag(0);
                boolean flag1 = receivedPacket.getFlag(1);
                boolean flag2 = receivedPacket.getFlag(2);
                boolean flag3 = receivedPacket.getFlag(3);

                if(flag1 == true|| flag3 == true){
                    tcpSocket.close();
                    connected = false;
                    callEvent.onCallEnded();
                }
                if(flag2 == true){
                    callEvent.onCallStarted(this.ip, this.port, this.sourceNr, this.destNr,receivedPacket.getKey());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static byte[] readData(InputStream input) throws Exception{
        DataInputStream dataInputStream = new DataInputStream(input);
        byte[] data = new byte[2044];
        try {
            dataInputStream.readFully(data);
        } catch (Exception e) {

        }
        return data;
    }
    //Gets symmetric 32-bytes key generated using AES
    public String getKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(256);
        SecretKey secret = keyGen.generateKey();
        byte[] binary = secret.getEncoded();
        return String.format("%032X", new BigInteger(+1,binary));
    }
}
