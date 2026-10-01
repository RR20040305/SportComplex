package ru.mirea.sport.ui;

import java.util.List;

import ru.mirea.sport.model.Client;
import ru.mirea.sport.service.ClientService;

public class ClientMenu {
    private final ClientService service;

    public ClientMenu(ClientService service) {
        this.service = service;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.println("\n--- Клиенты ---");
            ConsoleHelper.println("1. Список клиентов");
            ConsoleHelper.println("2. Добавить клиента");
            ConsoleHelper.println("3. Редактировать клиента");
            ConsoleHelper.println("4. Удалить клиента");
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
        List<Client> list = service.getAll();
        if (list.isEmpty()) {
            ConsoleHelper.println("Клиентов нет");
        } else {
            list.forEach(ConsoleHelper::println);
        }
    }

    private void add() throws Exception {
        String name = ConsoleHelper.readLine("ФИО: ");
        String phone = ConsoleHelper.readLine("Телефон: ");
        String email = ConsoleHelper.readLine("Email (можно пусто): ");
        Client c = service.create(name, phone, email);
        ConsoleHelper.println("Создан клиент: " + c);
    }

    private void edit() throws Exception {
        int id = ConsoleHelper.readInt("ID клиента: ");
        Client c = service.getById(id);
        ConsoleHelper.println("Текущее: " + c);

        String name = ConsoleHelper.readLine("Новое ФИО [" + c.getFullName() + "]: ");
        if (name.isBlank()) name = c.getFullName();

        String phone = ConsoleHelper.readLine("Новый телефон [" + c.getPhone() + "]: ");
        if (phone.isBlank()) phone = c.getPhone();

        String emailDefault = (c.getEmail() == null ? "нет" : c.getEmail());
        String email = ConsoleHelper.readLine("Новый email [" + emailDefault + "]: ");
        if (email.isBlank()) email = c.getEmail();

        service.update(id, name, phone, email);
        ConsoleHelper.println("Обновлено");
    }

    private void delete() throws Exception {
        int id = ConsoleHelper.readInt("ID клиента: ");
        if (service.delete(id)) {
            ConsoleHelper.println("Удалено");
        } else {
            ConsoleHelper.println("Клиент не найден");
        }
    }

    private void findById() throws Exception {
        int id = ConsoleHelper.readInt("ID клиента: ");
        ConsoleHelper.println(service.getById(id).toString());
    }
}