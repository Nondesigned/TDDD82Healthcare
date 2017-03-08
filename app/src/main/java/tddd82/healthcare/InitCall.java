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
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtb2JpbGUiLCJleHAiOjE1MjA1MTcwNzEsImlhdCI6MTQ4ODk4MTA3MSwiaXNzIjoiU2p1a3bDpXJkc2dydXBwZW4iLCJzdWIiOiIyMjIifQ.19uGx7zwBSWyFrd-Q478V9uv-5mE_372Jq2uB3KePDzokcz89CyePTHimUWBulqkgllCRhSSUQIpnHjCwGQuI_C870u67HBNHvu-wuwc3hMOn_0P94zI_vFxrc4QR_1XMWuYI6R10gCiNitDFpJKMcReS2W7Jotmy9CT1pMpozDNZ-RMU2_sfjgLaL3EZ7cGyU32eK2K2_t3jqUonPEe2x7DaZ2_opntw8c2bSm3uLO8lpdyMSayoVKhpAPxw95aoBVkW6T144DmNFx33jBBVdy4qNzxkmsZG30PFHxJjWKkDiEZNcygg2KyAxEND7Ejz2JY-vQXcsdIIzA6M49bk1JI6AkAaliDbEjLLi2O1xMH_Y5ExEGOa3M9RP-e-p464EZ95fqLjzCi-jZod_H2HqHjFHIBnWAdDSq_JMtwP3h7YNwZvrT9hbEU1EfAJS8dIjNfjDCl8BqejBNkveJ_rRMHzDdIIY4a8EheZNiJuIRMSoow9MOD29A5IMeNufhM2KtkAeGzU64SXqZRa2UsO8qIl0ArRBkxAcugTRcTjzJQVHIdwQ1_Eo2UfIi1AddjS0LA0EvQHgsO8PktcWy9uSI-PYaStZKpEER2CGdUL5pnpMvfJNzuaJ9o3czvAplf0xkb7_yjgZ-iXUNPSBKfg9hjmTmp_YUAfU3PhxBDopc";
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
                    callEvent.onCallStarted(this.ip, this.port, this.sourceNr, this.destNr);
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
