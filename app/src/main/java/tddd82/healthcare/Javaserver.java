package tddd82.healthcare;

import android.util.Log;
import android.webkit.JsPromptResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Created by Oskar on 2017-03-03.
 */

public class Javaserver {
    private String token = "f3uwoUDpEvY:APA91bEZiTWjAtMsLUpaXkN5mAzGU_jI20qUDvjI0MK4E8nVzDIqSXtoVcT-OPPVoQqrycjvxYf75r4PHl24m-b6Nu8O0-SpuKuuSyN4GdzzCD_QirIZv3uXuz3mgEvsYXMLFyOHPD99";
    public Javaserver(){
        setToken(145664748);
        getToken(145664748);

    }

    public void startCall (int callerID, int userID){
        token = "f3uwoUDpEvY:APA91bEZiTWjAtMsLUpaXkN5mAzGU_jI20qUDvjI0MK4E8nVzDIqSXtoVcT-OPPVoQqrycjvxYf75r4PHl24m-b6Nu8O0-SpuKuuSyN4GdzzCD_QirIZv3uXuz3mgEvsYXMLFyOHPD99";
        token = getToken(userID);
    }

    private void setToken(int userId) {
        String user = "itkand_2017_3_1";
        String host = "db-und.ida.liu.se";
        String pass = "itkand_2017_3_1_7f41";
        try {
            Connection conn = DriverManager.getConnection(host,user,pass);
            String query = "{CALL set_user_token(" + userId +","+token+")}";
            CallableStatement stmt = conn.prepareCall(query);
            stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getToken(int userId) {
        String user = "itkand_2017_3_1";
        String host = "db-und.ida.liu.se";
        String pass = "itkand_2017_3_1_7f41";
        try {
            Connection conn = DriverManager.getConnection(host, user, pass);
            String query = "{CALL get_user_token(" + userId + ")}";
            CallableStatement stmt = conn.prepareCall(query);
            token = stmt.executeQuery().toString();
            Log.d("bob",token);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public void sendPost() throws IOException {
        String authKey = "AIzaSyAfOZbxa1N5C5G8Y7xnYLdwZ8l7HCEENiE";
        String site = "https://fcm.googleapis.com/fcm/send";

        URL url = new URL(site);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setRequestProperty("Authorization" ,"key="+" "+authKey);


        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        dos.writeBytes(createJson().toString());
        dos.flush();
        dos.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
            Log.d("bob",inputLine);
        }
        in.close();



    }

    public JSONObject createJson(){
        JSONObject JsonPost = new JSONObject();
        JSONObject payload = new JSONObject();
        String callerid = "911";
        try {
            payload.put("TYPE","call");
            payload.put("CALLER",callerid);
            JsonPost.put("data", payload);
            JsonPost.put("to", token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JsonPost;
    }

}
