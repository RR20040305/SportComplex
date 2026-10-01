package ru.mirea.sport.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.BookingStatus;
import ru.mirea.sport.service.BookingService;
import ru.mirea.sport.service.ClientService;
import ru.mirea.sport.service.FacilityService;

public class BookingMenu {
    private final BookingService bookingService;
    private final ClientService clientService;
    private final FacilityService facilityService;

    public BookingMenu(BookingService b, ClientService c, FacilityService f) {
        this.bookingService = b;
        this.clientService = c;
        this.facilityService = f;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Брони ---");
            ConsoleHelper.println("1. Список всех броней");
            ConsoleHelper.println("2. Создать бронь");
            ConsoleHelper.println("3. Изменить статус");
            ConsoleHelper.println("4. Перенести бронь");
            ConsoleHelper.println("5. Отменить бронь");
            ConsoleHelper.println("6. Брони клиента");
            ConsoleHelper.println("7. Брони площадки на дату");
            ConsoleHelper.println("0. Назад");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> ConsoleHelper.tryRun(this::listAll);
                case 2 -> ConsoleHelper.tryRun(this::create);
                case 3 -> ConsoleHelper.tryRun(this::changeStatus);
                case 4 -> ConsoleHelper.tryRun(this::reschedule);
                case 5 -> ConsoleHelper.tryRun(this::cancel);
                case 6 -> ConsoleHelper.tryRun(this::byClient);
                case 7 -> ConsoleHelper.tryRun(this::byFacility);
                case 0 -> back = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }

    private void listAll() throws Exception {
        List<Booking> list = bookingService.getAll();
        if (list.isEmpty()) ConsoleHelper.println("Броней нет");
        else list.forEach(ConsoleHelper::println);
    }

    private void create() throws Exception {
        ConsoleHelper.println("Клиенты:");
        clientService.getAll().forEach(ConsoleHelper::println);
        int clientId = ConsoleHelper.readInt("ID клиента: ");

        ConsoleHelper.println("Площадки:");
        facilityService.getAll().forEach(ConsoleHelper::println);
        int facilityId = ConsoleHelper.readInt("ID площадки: ");

        LocalDate date = ConsoleHelper.readDate("Дата (дд.мм.гггг): ");
        LocalDateTime start = LocalDateTime.of(date, ConsoleHelper.readTime("Время начала (чч:мм): "));
        LocalDateTime end = LocalDateTime.of(date, ConsoleHelper.readTime("Время окончания (чч:мм): "));

        Booking b = bookingService.create(clientId, facilityId, start, end);
        ConsoleHelper.println("Создана бронь #" + b.getId());
    }

    private void changeStatus() throws Exception {
        int id = ConsoleHelper.readInt("ID брони: ");
        ConsoleHelper.println("Статусы: CREATED, CONFIRMED, COMPLETED, CANCELLED");
        String s = ConsoleHelper.readLine("Новый статус: ").toUpperCase();
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            ConsoleHelper.println("Неизвестный статус: " + s);
            return;
        }
        bookingService.changeStatus(id, status);
        ConsoleHelper.println("Статус изменён");
    }

    private void reschedule() throws Exception {
        int id = ConsoleHelper.readInt("ID брони: ");
        LocalDate date = ConsoleHelper.readDate("Новая дата (дд.мм.гггг): ");
        LocalDateTime start = LocalDateTime.of(date, ConsoleHelper.readTime("Новое начало (чч:мм): "));
        LocalDateTime end = LocalDateTime.of(date, ConsoleHelper.readTime("Новое окончание (чч:мм): "));
        bookingService.reschedule(id, start, end);
        ConsoleHelper.println("Бронь перенесена");
    }

    private void cancel() throws Exception {
        int id = ConsoleHelper.readInt("ID брони: ");
        bookingService.cancel(id);
        ConsoleHelper.println("Бронь отменена");
    }

    private void byClient() throws Exception {
        int id = ConsoleHelper.readInt("ID клиента: ");
        List<Booking> list = bookingService.getByClient(id);
        if (list.isEmpty()) ConsoleHelper.println("Нет броней");
        else list.forEach(ConsoleHelper::println);
    }

    private void byFacility() throws Exception {
        int id = ConsoleHelper.readInt("ID площадки: ");
        LocalDate date = ConsoleHelper.readDate("Дата (дд.мм.гггг): ");
        List<Booking> list = bookingService.getByFacilityAndDate(id, date);
        if (list.isEmpty()) ConsoleHelper.println("Нет броней");
        else list.forEach(ConsoleHelper::println);
    }
}