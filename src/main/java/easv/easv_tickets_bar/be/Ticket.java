package easv.easv_tickets_bar.be;

public class Ticket {
    private int id;
    private String name;
    private double price;
    private String description;

    public Ticket(int id, String name, double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }


    @Override
    public String toString() {
        String firstLetter = name.substring(0, 1).toUpperCase();
        String otherLetters = name.substring(1);
        return firstLetter + otherLetters;
    }
}
