package tddd82.healthcare;

import android.util.Base64;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;

/**
 * JWT
 */
public class JWT {
    String token;
    String certLoc = "cert.pem";
    public JWT (byte[] token) {this.token = new String(token);
    }

    public JWT (byte[] token, String certloc) {
        this.token = new String(token);
        this.certLoc = certLoc;
    }

    /**
     * True if JWT is valid
     */
    public boolean valid(){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = getHeaders()+"."+getPayload();
            byte[] hash = digest.digest(payload.getBytes("UTF-8"));
            Signature signature1 = Signature.getInstance("SHA256withRSA");
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            File file = new File("cert.pem");
            FileInputStream is = new FileInputStream(file);
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            PublicKey key = cer.getPublicKey();
            signature1.initVerify(key);
            signature1.update(payload.getBytes());
            return signature1.verify(getSignature());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public int getNumber(){
        try{
            int flags = Base64.NO_WRAP | Base64.URL_SAFE;

            JSONObject obj = new JSONObject(new String(Base64.decode(getPayload(),flags))); //Maybe UTF-8
            Iterator<String> keys= obj.keys();
            String keyValue = (String)keys.next();
            String number = (String)obj.get("sub");
            return Integer.parseInt(number);
        }catch(Exception e){
            System.out.println("Token has invalid number");
            return 0;
        }
    }
    /**
     * Get header string
     */
    private String getHeaders(){
        return token.split("\\.")[0];
    }

    /**
     * Get payload string
     */
    private String getPayload(){
        return token.split("\\.")[1];
    }

    /**
     * Get the  signature bytes
     */
    private byte[] getSignature()throws Exception{
        int flags = Base64.NO_WRAP | Base64.URL_SAFE;
        return Base64.decode(token.split("\\.")[2].getBytes(), flags);
    }
}