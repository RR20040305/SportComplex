package ru.mirea.sport.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, int id) {
        super(String.format("%s с id=%d не найден", entityName, id));
    }
}