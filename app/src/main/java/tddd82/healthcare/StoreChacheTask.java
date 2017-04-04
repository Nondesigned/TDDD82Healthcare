package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Clynch on 2017-03-21.
 */

public class StoreChacheTask extends AsyncTask<String,Void,Void>{
    Context context;

    public StoreChacheTask(Context context){
        this.context = context;
    }
/*
params[1] is the file you want to store it in format /filename
params[0] is the string value to be stored
 */
    @Override
    protected Void doInBackground(String... params) {

        try {
            OutputStream outputStream = new FileOutputStream(context.getCacheDir().getPath()+params[1]);
            outputStream.write(params[0] .getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
