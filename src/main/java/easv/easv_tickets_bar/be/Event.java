package easv.easv_tickets_bar.be;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String location;
    private String venue;
    private String locationGuidance;
    private String notes;
    private int coordinators;
    private int capacity;
    private int soldAmount;
    private int availableTickets;

    public Event(int id, String name, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String location, String venue, String locationGuidance, String notes, int coordinators, int capacity, int soldAmount) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.venue = venue;
        this.locationGuidance = locationGuidance;
        this.notes = notes;
        this.coordinators = coordinators;
        this.capacity = capacity;
        this.soldAmount = soldAmount;
        this.availableTickets = this.capacity - this.soldAmount;
    }

    public Event(int id, String name, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String location) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        if (startTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        if (endTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return endTime.format(formatter);
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

    public int getSoldAmount(){
        return this.soldAmount;
    }

    public int getAvailableTickets(){
        return this.availableTickets;
    }
}
