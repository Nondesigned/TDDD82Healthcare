package tddd82.healthcare;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Clynch on 2017-03-23.
 */

public class CacheManager {

    public static String get(String dir, Context context){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(context.getCacheDir() + dir);
            String s = convertStreamToString(inputStream);
            inputStream.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR in RetrieveCacheTask";
    }

    public static void put(String value, String dir, Context context){
        try {
            OutputStream outputStream = new FileOutputStream(context.getCacheDir().getPath()+dir);
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getJSON(String dir, Context context){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(context.getCacheDir() + dir);
            String s = convertStreamToString(inputStream);
            JSONArray array = new JSONArray(s);
            inputStream.close();
            return array;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void putJSON(String value, String dir, Context context){
        try {
            OutputStream outputStream = new FileOutputStream(context.getCacheDir().getPath()+dir);
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
