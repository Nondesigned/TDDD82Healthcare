package tddd82.healthcare;

public interface CallEvent {

    void onTimeout(int currentSequenceNumber, int destinationNumber);

}