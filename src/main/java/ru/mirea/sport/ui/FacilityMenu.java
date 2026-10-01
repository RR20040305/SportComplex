package ru.mirea.sport.ui;

import java.math.BigDecimal;
import java.util.List;

import ru.mirea.sport.model.Facility;
import ru.mirea.sport.service.FacilityService;

public class FacilityMenu {
    private final FacilityService service;

    public FacilityMenu(FacilityService service) {
        this.service = service;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Площадки ---");
            ConsoleHelper.println("1. Список площадок");
            ConsoleHelper.println("2. Добавить площадку");
            ConsoleHelper.println("3. Редактировать площадку");
            ConsoleHelper.println("4. Удалить площадку");
            ConsoleHelper.println("5. Найти по ID");
            ConsoleHelper.println("0. Назад");

            int choice = ConsoleHelper.readInt("Выбор: ");
            switch (choice) {
                case 1 -> ConsoleHelper.tryRun(this::listAll);
                case 2 -> ConsoleHelper.tryRun(this::add);
                case 3 -> ConsoleHelper.tryRun(this::edit);
                case 4 -> ConsoleHelper.tryRun(this::delete);
                case 5 -> ConsoleHelper.tryRun(this::findById);
                case 0 -> back = true;
                default -> ConsoleHelper.println("Нет такого пункта");
            }
        }
    }

    private void listAll() throws Exception {
        List<Facility> list = service.getAll();
        if (list.isEmpty()) {
            ConsoleHelper.println("Площадок нет");
        } else {
            list.forEach(ConsoleHelper::println);
        }
    }

    private void add() throws Exception {
        String name = ConsoleHelper.readLine("Название: ");
        String sport = ConsoleHelper.readLine("Вид спорта: ");
        BigDecimal pr = ConsoleHelper.readBigDecimal("Цена за час: ");
        Facility f = service.create(name, sport, pr);
        ConsoleHelper.println("Создана площадка: " + f);
    }

    private void edit() throws Exception {
        int id = ConsoleHelper.readInt("ID площадки: ");
        Facility f = service.getById(id);
        ConsoleHelper.println("Текущее: " + f);

        String name = ConsoleHelper.readLine("Новое название [" + f.getName() + "]: ");
        if (name.isBlank()) name = f.getName();

        String sport = ConsoleHelper.readLine("Новый вид спорта [" + f.getSportType() + "]: ");
        if (sport.isBlank()) sport = f.getSportType();

        BigDecimal pr = ConsoleHelper.readBigDecimal("Новая цена [" + f.getPricePerHour() + "]: ");

        service.update(id, name, sport, pr);
        ConsoleHelper.println("Обновлено");
    }

    private void delete() throws Exception {
        int id = ConsoleHelper.readInt("ID площадки: ");
        if (service.delete(id)) {
            ConsoleHelper.println("Удалено");
        } else {
            ConsoleHelper.println("Площадка не найдена");
        }
    }

    private void findById() throws Exception {
        int id = ConsoleHelper.readInt("ID площадки: ");
        ConsoleHelper.println(service.getById(id).toString());
    }
}