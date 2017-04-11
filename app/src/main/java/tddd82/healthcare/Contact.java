package tddd82.healthcare;

class Contact {
    private String name;
    private int number;

    Contact(String name, int number){
        this.name = name;
        this.number = number;
    }

    int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
