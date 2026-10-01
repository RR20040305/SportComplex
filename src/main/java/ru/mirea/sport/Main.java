package ru.mirea.sport;

import ru.mirea.sport.repository.BookingRepository;
import ru.mirea.sport.repository.ClientRepository;
import ru.mirea.sport.repository.FacilityRepository;
import ru.mirea.sport.service.BookingService;
import ru.mirea.sport.service.ClientService;
import ru.mirea.sport.service.FacilityService;
import ru.mirea.sport.service.StatisticsService;
import ru.mirea.sport.ui.ConsoleHelper;
import ru.mirea.sport.ui.MainMenu;
import ru.mirea.sport.util.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        try {
            ConsoleHelper.init();
        } catch (Exception e) {
            System.err.println("Не удалось инициализировать консоль: " + e.getMessage());
            return;
        }

        try {
            DatabaseManager db = new DatabaseManager();
            db.getConnection().close();

            ClientRepository clientRepo = new ClientRepository(db);
            FacilityRepository facilityRepo = new FacilityRepository(db);
            BookingRepository bookingRepo = new BookingRepository(db);

            ClientService clientService = new ClientService(clientRepo);
            FacilityService facilityService = new FacilityService(facilityRepo);
            BookingService bookingService = new BookingService(bookingRepo, clientRepo, facilityRepo);
            StatisticsService statisticsService = new StatisticsService(clientService, facilityService, bookingService);

            ConsoleHelper.println("Подключение к БД — ОК");

            new MainMenu(clientService, facilityService, bookingService, statisticsService).run();

        } catch (Exception e) {
            ConsoleHelper.println("Критическая ошибка: " + e.getMessage());
        } finally {
            ConsoleHelper.close();
        }
    }
}