package easv.easv_tickets_bar.be;

public class TicketEvent {
    private int id;
    private String eventName;
    private String ticketType;
    private double price;
    private int totalQuantity;
    private int soldQuantity;

    public TicketEvent(int id, String eventName, String ticketType, double price, int totalQuantity, int soldQuantity) {
        this.id = id;
        this.eventName = eventName;
        this.ticketType = ticketType;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.soldQuantity = soldQuantity;
    }

    public int getAvailable(){
        return totalQuantity - soldQuantity;
    }

}
