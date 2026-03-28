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

    public TicketEvent(int id, String ticketType, double price, int totalQuantity) {
        this.id = id;
        this.ticketType = ticketType;
        this.price = price;
        this.totalQuantity = totalQuantity;
    }

    public int getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTicketType() {
        return ticketType;
    }

    public String getPrice() {
        return String.valueOf(price) + " kr.";
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public int getAvailable(){
        return totalQuantity - soldQuantity;
    }

    public String getStatus(){
        if (getAvailable() == 0){
            return "Sold out";
        }
        return "Available";
    }

}
