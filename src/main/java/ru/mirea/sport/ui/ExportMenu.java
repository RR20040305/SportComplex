package ru.mirea.sport.ui;

import java.nio.file.Files;
import java.nio.file.Path;

import ru.mirea.sport.service.BookingService;
import ru.mirea.sport.service.ClientService;
import ru.mirea.sport.service.FacilityService;
import ru.mirea.sport.util.ExcelExporter;

public class ExportMenu {
    private final ClientService clientService;
    private final FacilityService facilityService;
    private final BookingService bookingService;

    public ExportMenu(ClientService c, FacilityService f, BookingService b) {
        this.clientService = c;
        this.facilityService = f;
        this.bookingService = b;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Экспорт данных ---");
            ConsoleHelper.println("1. Экспорт броней в Excel");
            ConsoleHelper.println("2. Экспорт клиентов в Excel");
            ConsoleHelper.println("3. Экспорт площадок в Excel");
            ConsoleHelper.println("0. Назад");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> ConsoleHelper.tryRun(() -> exportBookings());
                case 2 -> ConsoleHelper.tryRun(() -> exportClients());
                case 3 -> ConsoleHelper.tryRun(() -> exportFacilities());
                case 0 -> back = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }

    private void exportBookings() throws Exception {
        String path = askPath("bookings.xlsx");
        ExcelExporter.exportBookings(bookingService.getAll(), path);
        ConsoleHelper.println("Экспортировано: " + path);
    }

    private void exportClients() throws Exception {
        String path = askPath("clients.xlsx");
        ExcelExporter.exportClients(clientService.getAll(), path);
        ConsoleHelper.println("Экспортировано: " + path);
    }

    private void exportFacilities() throws Exception {
        String path = askPath("facilities.xlsx");
        ExcelExporter.exportFacilities(facilityService.getAll(), path);
        ConsoleHelper.println("Экспортировано: " + path);
    }

    private String askPath(String defaultName) throws Exception {
        Path defaultPath = Path.of("export", defaultName);
        Files.createDirectories(defaultPath.getParent());

        String input = ConsoleHelper.readLine("Путь (Enter — " + defaultPath + "): ");
        return input.isBlank() ? defaultPath.toString() : input;
    }
}