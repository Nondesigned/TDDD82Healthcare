package tddd82.healthcare;

/**
 * Created by Clynch on 2017-03-07.
 */

public class GlobalVariables {

    private static final String SHARED_PREFS_TOKEN = "TOKEN";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CARD = "card";
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_DECLINED = "declined";
    private static final String JSON_TOKEN = "token";
    private static final String JSON_MESSAGE = "message";
    private static final String TEST_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6I" +
            "kpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.EkN-DOsnsuRjRO6BxXemmJDm3HbxrbRzXglbN2S4sOkopdU4IsDxTI8jO19W_A4K8ZPJijNLis4EZ" +
            "sHeY559a4DFOd50_OqgHGuERTqYZyuhtF39yxJPAjUESwxk2J5k_4zM3O-vtd1Ghyo4IbqKKSy6J9mTniYJPenn5-HIirE";
    private static final String INTENT_CALL_NUMBER = "DEST";
    private static final String JSON_CONTACT_LIST = "contact list";
    private static final String CALL_SERVER_IP = "itkand-3-1.tddd82-2017.ida.liu.se";
    private static final int CALL_SERVER_UDP_PORT = 1338;
    private static final int CALL_SERVER_TCP_PORT = 1337;


    public static String getJsonContactList() {
        return JSON_CONTACT_LIST;
    }

    public static String getIntentCallNumber(){
        return INTENT_CALL_NUMBER;
    }
    public static String getSharedPrefsTokenTag(){
        return SHARED_PREFS_TOKEN;
    }
    public static String getTestToken(){
        return TEST_TOKEN;
    }
    public static String getJsonPasswordTag(){
        return JSON_PASSWORD;
    }
    public static String getJsonCardTag(){
        return JSON_CARD;
    }
    public static String getJsonAcceptedTag(){
        return JSON_ACCEPTED;
    }
    public static String getJsonStatusTag(){
        return JSON_STATUS;
    }
    public static String getJsonDeclinedTag(){
        return JSON_DECLINED;
    }
    public static String getJsonTokenTag(){
        return JSON_TOKEN;
    }
    public static String getJsonMessageTag() {
        return JSON_MESSAGE;
    }
    public static String getCallServerIp() { return CALL_SERVER_IP; }
    public static int getCallServerUDPPort() { return CALL_SERVER_UDP_PORT; }
    public static int getCallServerTCPPort() { return CALL_SERVER_TCP_PORT; }
}
