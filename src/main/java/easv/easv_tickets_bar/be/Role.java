package easv.easv_tickets_bar.be;

public enum Role {
    ADMIN("Admin", 1),
    EVENT_COORDINATOR("Event Coordinator", 2);

    private final String displayName;
    private final int id;

    Role(String displayName, int id){
        this.displayName = displayName;
        this.id = id;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getId() {
        return id;
    }
}
