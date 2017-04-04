package tddd82.healthcare;

import android.provider.Settings;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CallCrypto {

    private byte[] IV;
    private SecretKey key;
    private Cipher cipher;
    private IvParameterSpec params;

    public CallCrypto(){
        generateKey();
    }

    public CallCrypto(byte[] IV, byte[] keyRaw){
        this.IV = IV;
        this.key = new SecretKeySpec(keyRaw, "AES");
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.params = new IvParameterSpec(IV);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private void generateKey(){
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(256);
        this.key = keyGen.generateKey();
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.IV = this.cipher.getIV();
            this.params = new IvParameterSpec(IV);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] bytes, int offset, int length){
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, key, params);
            return this.cipher.doFinal(bytes, offset, length);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public byte[] decrypt(byte[] bytes, int offset, int length){
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, key, params);
            return this.cipher.doFinal(bytes, offset, length);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public byte[] getKey(){
        return key.getEncoded();
    }

    public byte[] getIV(){
        return IV;
    }
}
