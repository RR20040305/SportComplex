package ru.mirea.sport.ui;

import ru.mirea.sport.service.ClientService;
import ru.mirea.sport.service.FacilityService;

public class SearchMenu {
    private final ClientService   clientService;
    private final FacilityService facilityService;

    public SearchMenu(ClientService c, FacilityService f) {
        this.clientService = c;
        this.facilityService = f;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Поиск ---");
            ConsoleHelper.println("1. Клиенты по части ФИО");
            ConsoleHelper.println("2. Площадки по части названия");
            ConsoleHelper.println("3. Площадки по виду спорта");
            ConsoleHelper.println("0. Назад");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> ConsoleHelper.tryRun(() -> {
                    String q = ConsoleHelper.readLine("Часть ФИО: ");
                    clientService.searchByName(q).forEach(ConsoleHelper::println);
                });
                case 2 -> ConsoleHelper.tryRun(() -> {
                    String q = ConsoleHelper.readLine("Часть названия: ");
                    facilityService.searchByName(q).forEach(ConsoleHelper::println);
                });
                case 3 -> ConsoleHelper.tryRun(() -> {
                    String q = ConsoleHelper.readLine("Вид спорта: ");
                    facilityService.findBySportType(q).forEach(ConsoleHelper::println);
                });
                case 0 -> back = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }
}