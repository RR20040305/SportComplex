package ru.mirea.sport.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.BookingStatus;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.model.Facility;

public class StatisticsService {
    private final ClientService clientService;
    private final FacilityService facilityService;
    private final BookingService bookingService;

    public StatisticsService(ClientService c, FacilityService f, BookingService b) {
        this.clientService = c;
        this.facilityService = f;
        this.bookingService = b;
    }

    public Map<String, Object> collect() throws SQLException {
        List<Client> clients = clientService.getAll();
        List<Facility> facilities = facilityService.getAll();
        List<Booking> bookings = bookingService.getAll();

        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("Всего клиентов", clients.size());
        stats.put("Всего площадок", facilities.size());
        stats.put("Всего броней", bookings.size());

        long active = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CREATED || b.getStatus() == BookingStatus.CONFIRMED)
                .count();
        long completed = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .count();
        long cancelled = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();

        stats.put("Активных броней", active);
        stats.put("Завершённых броней", completed);
        stats.put("Отменённых броней", cancelled);

        double avgMinutes = bookings.stream()
                .mapToLong(b -> Duration.between(b.getStartTime(), b.getEndTime()).toMinutes())
                .average()
                .orElse(0.0);
        stats.put("Средняя длительность (мин)", String.format("%.1f", avgMinutes));

        String popularFacility = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getFacility().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " броней)")
                .orElse("—");
        stats.put("Самая популярная площадка", popularFacility);

        BigDecimal revenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .map(b -> {
                    long minutes = Duration.between(b.getStartTime(), b.getEndTime()).toMinutes();
                    return b.getFacility().getPricePerHour()
                        .multiply(BigDecimal.valueOf(minutes))
                        .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("Выручка (подтв.+заверш.), руб.", revenue);

        return stats;
    }
}