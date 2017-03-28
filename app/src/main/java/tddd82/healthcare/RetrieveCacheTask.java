package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Clynch on 2017-03-21.
 */

class RetrieveCacheTask extends AsyncTask<String, Void, String>{
    private final Context context;
    public AsyncResponse delegate;
    public RetrieveCacheTask(Context context, AsyncResponse delegate){
        this.context = context;
        this.delegate = delegate;
    }

    public interface AsyncResponse{
        void processFinish(String output);
    }

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(context.getCacheDir() + params[0]);
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
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
