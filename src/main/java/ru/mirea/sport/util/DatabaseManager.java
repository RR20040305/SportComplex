package ru.mirea.sport.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private final Properties props = new Properties();

    public DatabaseManager() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("db.properties не найден в resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить настройки БД", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}