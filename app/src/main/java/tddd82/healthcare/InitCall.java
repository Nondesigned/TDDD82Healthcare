package tddd82.healthcare;

import android.os.AsyncTask;
import android.util.Log;

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
    private Event callEvent;
    // Defines header information and sends to server
    // tyeofFlag = 0 , initialize
    //  = 1 endCall
    // = 2 accept
    //= 3 decline
    // TODO check if it works
    // Remove key part until sprint 3

    public void init(int sourceNr,int destNr, Event callEvent){
        // TODO generate key
       /* String key = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secret = keyGen.generateKey();
            byte[] binary = secret.getEncoded();
            key = String.format("%032X", new BigInteger(+1,binary));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    */

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
        //TODO token shall come from sharedpref
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtb2JpbGUiLCJleHAiOjE1MTk5ODAxODQsImlhdCI6MTQ4ODQ0NDE4NCwiaXNzIjoiU2p1a3bDpXJkc2dydXBwZW4iLCJzdWIiOiJUREREODJMb2dpbiJ9.otRhLd-pm_cuF973fUapuLkTUOhzUwD7JRJTp45emsysGKopkexVWh9GcI3aWAx_9eRedDo9k_V-rfrl-CDP2nZPv5dtPtT0rGzcXqu-Hi4PVCgwGyJNE4XlpmjdCJRzT8kmUPW6tdtiTIM8R1SD4dYABrbBxltuU9_P5FKufBACb2lXPHlz1uTinE8ME5hzMJyKxRE6rY4Zcw9MCw6Nu0ecz_MqYUixiPQ9efU5ryh4B6iIOsSvsO4wOtpuFmmD4MfvB79AeVy-bhoyNvhcksd3hVIvdpR--QhvroKzfn72-6KQiX3zlTiGwcChUFoMTivZvgc4b-xjsnOqLkNySM_eE6lUTZkmZxcdCUOwB6Wvcn-TrG8Z85PFQqeH3ePfCD77M5FaWocw4CZskxzGAih76pGzrVKCO-g7eQilWcRVuqDaq-gHQEspbCKhTt9UwlT9oePY9e9VpSq5plJOX545N93n-5e1ckMcXu07zRELNUV1_vKfPEEb9qtMY-DRrCON01aR3gTQtSKQzIV0crcknY1gRqJ24_3LAPAIWjEGmUQI64KXKtvEQ6E-X0OKj_TlEKUfOxcq3WLcgJhjn7R41JBQwGaMeq_rbkdRvycszhafHRD6QYQAU50Wkvj53LDYN6tkITDs-djpjM1vWz9yZxgbA3cSz-cqdjCPs_s";
        ctrl.setPayload(token.getBytes());
    }



    public void send(int typeOfFlag){
        ControlFlags flags = new ControlFlags();
        switch (typeOfFlag) {
            case 0:
                flags.setFlag(INITCALL, true);
                break;
            case 1:
                flags.setFlag(ENDCALL, true);
                break;
            case 2:
                flags.setFlag(ACCEPTCALL,true);
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
            Log.d("bob","Lyckas inte skicka");
        }

    }
    @Override
    public void run() {
        boolean connected = true;
        while(connected){
            try {
                ControlPacket recievedPacket = new ControlPacket(readData(tcpSocket.getInputStream()));
                boolean flag0 = recievedPacket.getFlag(0);
                boolean flag1 = recievedPacket.getFlag(1);
                boolean flag2 = recievedPacket.getFlag(2);
                boolean flag3 = recievedPacket.getFlag(3);

                if(flag1 == true|| flag3 == true){
                    tcpSocket.close();
                    connected = false;
                    callEvent.onCallEnded();

                }
                if(flag2 == true){
                    callEvent.onCallStarted();
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
            input.close();
        }
        return data;
    }
}
