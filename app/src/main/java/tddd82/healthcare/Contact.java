package tddd82.healthcare;

/**
 * Created by Clynch on 2017-03-06.
 */

public class Contact {
    private String name;
    private int number;

    Contact(String name, int number){
        this.name = name;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
