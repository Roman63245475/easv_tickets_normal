package easv.easv_tickets_bar.be;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public Event(int id, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String venue, String locationGuidance, String notes, int coordinators, int capacity) {
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
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
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
}
