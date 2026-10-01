package ru.mirea.sport.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import ru.mirea.sport.exception.BusinessException;
import ru.mirea.sport.exception.EntityNotFoundException;

public final class ConsoleHelper {
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static Terminal terminal;
    private static LineReader reader;
    private static PrintWriter out;

    private ConsoleHelper() {}

    public static void init() throws IOException {
        terminal = TerminalBuilder.builder()
            .system(true)
            .encoding(StandardCharsets.UTF_8)
            .build();
        out = terminal.writer();
        reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    public static void close() {
        if (terminal != null) {
            try { terminal.close(); } catch (Exception ignored) {}
        }
    }

    public static void println(Object s) {
        out.println(s);
        out.flush();
    }

    public static String readLine(String prompt) {
        return reader.readLine(prompt).trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                println("Ошибка: введите целое число");
            }
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                return new BigDecimal(readLine(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                println("Ошибка: введите число");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readLine(prompt), DATE_FMT);
            } catch (DateTimeParseException e) {
                println("Ошибка: формат даты дд.мм.гггг");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(readLine(prompt), TIME_FMT);
            } catch (DateTimeParseException e) {
                println("Ошибка: формат времени чч:мм");
            }
        }
    }

    @FunctionalInterface
    public interface Action {
        void run() throws Exception;
    }

    public static void tryRun(Action action) {
        try {
            action.run();
        } catch (BusinessException e) {
            println("Бизнес-правило нарушено: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            println("Запись не найдена: " + e.getMessage());
        } catch (SQLException e) {
            println("Ошибка БД: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            println("Непредвиденная ошибка: " + e.getMessage());
        }
    }
}