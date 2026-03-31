package easv.easv_tickets_bar.be;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private String venue;
    private String locationGuidance;
    private String notes;
    private int coordinators;
    private int capacity;
    private int soldAmount;
    private int availableTickets;

    public Event(int id, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String venue, String locationGuidance, String notes, int coordinators, int capacity, int soldAmount) {
        this.id = id;
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.venue = venue;
        this.locationGuidance = locationGuidance;
        this.notes = notes;
        this.coordinators = coordinators;
        this.capacity = capacity;
        this.soldAmount = soldAmount;
        this.availableTickets = this.capacity - this.soldAmount;
    }

    public Event(int id, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, String location) {
        this.id = id;
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDateTime() {
        if (startDateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm");
        return startDateTime.format(formatter);
    }

    public String getEndDateTime() {
        if (endDateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm");
        return endDateTime.format(formatter);
    }

    public String getLocation() {
        return location;
    }

    public String getVenue() {
        return venue;
    }

    public String getLocationGuidance() {
        return locationGuidance;
    }

    public String getNotes() {
        return notes;
    }

    public int getCoordinators() {
        return coordinators;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return this.name;
    }


    public String getStatus(){
        return "Active";
    }

    public int getSoldAmount(){
        return this.soldAmount;
    }

    public int getAvailableTickets(){
        return this.availableTickets;
    }
}
