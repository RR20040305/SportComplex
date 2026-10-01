package ru.mirea.sport.model;

import java.math.BigDecimal;

public class Facility {
    private int id;
    private String name;
    private String sportType;
    private BigDecimal pricePerHour;

    public Facility(String name, String sportType, BigDecimal pricePerHour) {
        this.name = name;
        this.sportType = sportType;
        this.pricePerHour = pricePerHour;
    }

    public Facility(int id, String name, String sportType, BigDecimal pricePerHour) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.pricePerHour = pricePerHour;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSportType() { return sportType; }
    public BigDecimal getPricePerHour() { return pricePerHour; }

    public void setName(String name) { this.name = name; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public void setPricePerHour(BigDecimal p) { this.pricePerHour = p; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) — %s руб./час", id, name, sportType, pricePerHour);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility)) return false;
        Facility other = (Facility) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}