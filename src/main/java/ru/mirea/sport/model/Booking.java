package ru.mirea.sport.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private int id;
    private Client client;
    private Facility facility;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking(Client client, Facility facility, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.client = client;
        this.facility = facility;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Booking(int id, Client client, Facility facility, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.client = client;
        this.facility = facility;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public Facility getFacility() { return facility; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStartTime(LocalDateTime t) { this.startTime = t; }
    public void setEndTime(LocalDateTime t) { this.endTime = t; }
    public void setStatus(BookingStatus s) { this.status = s; }

    @Override
    public String toString() {
        return String.format(
            "Бронь #%d%n" +
            "  Клиент:   %s (%s)%n" +
            "  Площадка: %s%n" +
            "  Время:    %s — %s%n" +
            "  Статус:   %s",
            id,
            client.getFullName(), client.getPhone(),
            facility.getName(),
            startTime.format(FMT), endTime.format(FMT),
            status.getDisplayName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking other = (Booking) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}