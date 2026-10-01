package ru.mirea.sport.ui;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.BookingStatus;
import ru.mirea.sport.service.BookingService;

public class FilterMenu {
    private final BookingService bookingService;

    public FilterMenu(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Фильтрация и сортировка ---");
            ConsoleHelper.println("1. Брони по статусу");
            ConsoleHelper.println("2. Брони по клиенту");
            ConsoleHelper.println("3. Брони по площадке и дате");
            ConsoleHelper.println("4. Все брони — сортировка по дате начала");
            ConsoleHelper.println("5. Все брони — сортировка по цене площадки");
            ConsoleHelper.println("0. Назад");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> ConsoleHelper.tryRun(this::byStatus);
                case 2 -> ConsoleHelper.tryRun(this::byClient);
                case 3 -> ConsoleHelper.tryRun(this::byFacilityAndDate);
                case 4 -> ConsoleHelper.tryRun(this::sortedByStart);
                case 5 -> ConsoleHelper.tryRun(this::sortedByPrice);
                case 0 -> back = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }

    private void byStatus() throws Exception {
        ConsoleHelper.println("Статусы: CREATED, CONFIRMED, COMPLETED, CANCELLED");
        String s = ConsoleHelper.readLine("Статус: ").toUpperCase();
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            ConsoleHelper.println("Неизвестный статус: " + s);
            return;
        }
        show(bookingService.getByStatus(status));
    }

    private void byClient() throws Exception {
        int id = ConsoleHelper.readInt("ID клиента: ");
        show(bookingService.getByClient(id));
    }

    private void byFacilityAndDate() throws Exception {
        int id = ConsoleHelper.readInt("ID площадки: ");
        LocalDate date = ConsoleHelper.readDate("Дата (дд.мм.гггг): ");
        show(bookingService.getByFacilityAndDate(id, date));
    }

    private void sortedByStart() throws Exception {
        List<Booking> sorted = bookingService.getAll().stream()
            .sorted(Comparator.comparing(Booking::getStartTime))
            .toList();
        show(sorted);
    }

    private void sortedByPrice() throws Exception {
        List<Booking> sorted = bookingService.getAll().stream()
            .sorted(Comparator.comparing((Booking b) -> b.getFacility().getPricePerHour()).reversed())
            .toList();
        show(sorted);
    }

    private void show(List<Booking> list) {
        if (list.isEmpty()) ConsoleHelper.println("Записей нет");
        else list.forEach(ConsoleHelper::println);
    }
}