package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.provider.Settings;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static tddd82.healthcare.ControlFlag.ACCEPTCALL;
import static tddd82.healthcare.ControlFlag.DECLINECALL;
import static tddd82.healthcare.ControlFlag.ENDCALL;
import static tddd82.healthcare.ControlFlag.INITCALL;
import static tddd82.healthcare.ControlFlag.INITVID;

/**
 * Created by Oskar on 2017-03-07.
 */

public class InitCall extends Thread implements Runnable{
    SSLSocketFactory socketFactory;
    Socket tcpSocket;
    ControlPacket ctrl;
    //private String key;
    //String ip = "130.236.181.196";
    String ip = GlobalVariables.getCallServerIp();
    private int port = GlobalVariables.getCallServerTCPPort();
    int sourceNr;
    int destNr;
    private Context context;
    private Event callEvent;
    CallVariables callVariables;
    // Defines header information and sends to server
    // tyeofFlag = 0 , initialize
    //  = 1 endCall
    // = 2 accept
    //= 3 decline
    // Remove key part until sprint 3

    public void init(int sourceNr,int destNr, Event callEvent,Context context){


        SSLContext SSLcontext = null;
        try {
           SSLcontext =  SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        try {
            SSLcontext.init(null, trustAllCerts, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        SSLSocketFactory sslFactory = SSLcontext.getSocketFactory();



        this.context = context;
        this.callEvent = callEvent;
        this.sourceNr = sourceNr;
        this.destNr = destNr;
        try {
            InetAddress addr = InetAddress.getByName(ip);
            tcpSocket = sslFactory.createSocket(addr,port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[2044];
        ctrl = new ControlPacket(bytes);
        ctrl.setSource(sourceNr);
        ctrl.setDestination(destNr);
        callVariables.setSourceNr(sourceNr);
        callVariables.setDestNr(destNr);


        SharedPreferences sharedPreferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");

        ctrl.setPayload(token.getBytes());
    }



    public void send(int typeOfFlag, CallCrypto crypto){
        ControlFlags flags = new ControlFlags();

        flags.setFlag(ControlFlag.ENDVID, BatteryMng.doVideo());
        switch (typeOfFlag) {
            case 0:
                flags.setFlag(INITCALL, true);
                break;
            case 1:
                flags.setFlag(ENDCALL, true);
                break;
            case 2:
                flags.setFlag(ACCEPTCALL,true);
                ctrl.setKey(crypto.getKey());
                ctrl.setIV(crypto.getIV());
                break;
            case 3:
                flags.setFlag(DECLINECALL,true);
                break;
            case 8:
                flags.setFlag(INITVID,true);
                break;
        }
        ctrl.setFlags(flags);
        try {
            ServerUtils.sendBytes(ctrl.getPacketBytes(),tcpSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void send(int typeOfFlag1, int typeOfFlag2, CallCrypto crypto){
        ControlFlags flags = new ControlFlags();

        switch (typeOfFlag1) {
            case 0:
                flags.setFlag(INITCALL, true);
                break;
            case 1:
                flags.setFlag(ENDCALL, true);
                break;
            case 2:
                flags.setFlag(ACCEPTCALL,true);
                ctrl.setKey(crypto.getKey());
                ctrl.setIV(crypto.getIV());
                break;
            case 3:
                flags.setFlag(DECLINECALL,true);
                break;
            case 8:
                flags.setFlag(INITVID,true);
                break;
        }
        switch (typeOfFlag2) {
            case 0:
                flags.setFlag(INITCALL, true);
                break;
            case 1:
                flags.setFlag(ENDCALL, true);
                break;
            case 2:
                flags.setFlag(ACCEPTCALL,true);
                ctrl.setKey(crypto.getKey());
                ctrl.setIV(crypto.getIV());
                break;
            case 3:
                flags.setFlag(DECLINECALL,true);
                break;
            case 8:
                flags.setFlag(INITVID,true);
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
                boolean isVideo = receivedPacket.getFlag(8);

                if(flag1 == true|| flag3 == true){
                    tcpSocket.close();
                    connected = false;
                    callEvent.onCallEnded();
                }
                if(flag2 == true){

                    callEvent.onCallStarted(this.ip, GlobalVariables.getCallServerUDPPort(), this.sourceNr, this.destNr, receivedPacket.getIV(), receivedPacket.getKey(),isVideo);
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
