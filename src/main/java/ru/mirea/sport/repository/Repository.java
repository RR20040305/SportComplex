package ru.mirea.sport.repository;

import java.sql.SQLException;
import java.util.List;

import ru.mirea.sport.exception.EntityNotFoundException;

public interface Repository<T, ID> {
    T findById(ID id) throws SQLException, EntityNotFoundException;

    List<T> findAll() throws SQLException;

    T save(T entity) throws SQLException;

    void update(T entity) throws SQLException, EntityNotFoundException;

    boolean delete(ID id) throws SQLException;

    boolean existsById(ID id) throws SQLException;
}