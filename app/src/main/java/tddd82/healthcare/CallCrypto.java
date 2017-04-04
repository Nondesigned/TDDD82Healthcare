package tddd82.healthcare;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

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
    private IvParameterSpec params;

    public CallCrypto(){
        generateKey();
    }

    public CallCrypto(byte[] IV, byte[] keyRaw){
        this.IV = IV;
        this.key = new SecretKeySpec(keyRaw, "AES");
        this.params = new IvParameterSpec(IV);

    }

    private void generateKey(){
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecureRandom prng = new SecureRandom();

        Random rng = new Random((((System.currentTimeMillis() << 4) | 57) >> 5 & 0x8e));

        prng.setSeed(rng.nextLong());

        keyGen.init(256, prng);
        this.key = keyGen.generateKey();
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(c.ENCRYPT_MODE, key);
            this.IV = c.getIV();
            this.params = new IvParameterSpec(IV);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] bytes, int offset, int length){
        byte[] ret = null;
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key, params);
            ret = c.doFinal(bytes, offset, length);
            return ret;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public byte[] decrypt(byte[] bytes, int offset, int length){
        byte[] ret = null;
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, key, params);
            ret = c.doFinal(bytes, offset, length);
            return ret;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
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
