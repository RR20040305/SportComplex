package ru.mirea.sport.ui;

import java.util.Map;

import ru.mirea.sport.service.StatisticsService;

public class StatisticsMenu {
    private final StatisticsService service;

    public StatisticsMenu(StatisticsService service) {
        this.service = service;
    }

    public void run() {
        ConsoleHelper.println("\n===== СТАТИСТИКА =====");
        ConsoleHelper.tryRun(() -> {
            Map<String, Object> stats = service.collect();
            if (stats.isEmpty()) 
            {
                ConsoleHelper.println("Нет данных");
                return;
            }
            stats.forEach((k, v) -> ConsoleHelper.println(String.format("%-35s %s", k + ":", v)));
        });
        ConsoleHelper.readLine("\nНажмите Enter, чтобы вернуться...");
    }
}