package easv.easv_tickets_bar.gui;

public enum Role {
    ADMIN("Admin"),
    EVENT_COORDINATOR("Event Coordinator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
