package tddd82.healthcare;

/**
 * Created by Oskar on 2017-03-08.
 */

public interface Event {

    void onCallEnded();

    void onCallStarted(String host, int port, int sender, int receiver, byte[] IV, byte[] key);
}
