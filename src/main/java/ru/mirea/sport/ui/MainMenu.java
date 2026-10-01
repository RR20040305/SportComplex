package ru.mirea.sport.ui;

import ru.mirea.sport.service.BookingService;
import ru.mirea.sport.service.ClientService;
import ru.mirea.sport.service.FacilityService;
import ru.mirea.sport.service.StatisticsService;

public class MainMenu {
    private final ClientService clientService;
    private final FacilityService facilityService;
    private final BookingService bookingService;
    private final StatisticsService statisticsService;

    public MainMenu(ClientService c, FacilityService f, BookingService b, StatisticsService s) {
        this.clientService = c;
        this.facilityService = f;
        this.bookingService = b;
        this.statisticsService = s;
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            ConsoleHelper.println("\n===== СПОРТИВНЫЙ КОМПЛЕКС =====");
            ConsoleHelper.println("1. Клиенты");
            ConsoleHelper.println("2. Площадки");
            ConsoleHelper.println("3. Брони");
            ConsoleHelper.println("4. Поиск");
            ConsoleHelper.println("5. Фильтрация и сортировка");
            ConsoleHelper.println("6. Статистика");
            ConsoleHelper.println("7. Экспорт данных");
            ConsoleHelper.println("0. Выход");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> new ClientMenu(clientService).run();
                case 2 -> new FacilityMenu(facilityService).run();
                case 3 -> new BookingMenu(bookingService, clientService, facilityService).run();
                case 4 -> new SearchMenu(clientService, facilityService).run();
                case 5 -> new FilterMenu(bookingService).run();
                case 6 -> new StatisticsMenu(statisticsService).run();
                case 7 -> new ExportMenu(clientService, facilityService, bookingService).run();
                case 0 -> exit = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }
}