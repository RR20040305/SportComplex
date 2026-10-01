package ru.mirea.sport.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import ru.mirea.sport.exception.BusinessException;
import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Facility;
import ru.mirea.sport.repository.FacilityRepository;

public class FacilityService {
    private final FacilityRepository repository;

    public FacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    public List<Facility> getAll() throws SQLException {
        return repository.findAll();
    }

    public Facility getById(int id) throws SQLException, EntityNotFoundException {
        return repository.findById(id);
    }

    public List<Facility> findBySportType(String sportType) throws SQLException {
        if (sportType == null || sportType.isBlank()) {
            throw new IllegalArgumentException("Вид спорта не может быть пустым");
        }
        return repository.findBySportType(sportType);
    }

    public List<Facility> searchByName(String part) throws SQLException {
        if (part == null || part.isBlank()) {
            throw new IllegalArgumentException("Строка поиска не может быть пустой");
        }
        return repository.searchByName(part);
    }

    public Facility create(String name, String sportType, BigDecimal price) throws SQLException, BusinessException {
        if (name == null || name.trim().length() < 2) {
            throw new BusinessException("Название площадки должно содержать минимум 2 символа");
        }
        
        if (sportType == null || sportType.trim().isEmpty()) {
            throw new BusinessException("Вид спорта обязателен");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Цена за час должна быть больше нуля");
        }

        return repository.save(new Facility(name.trim(), sportType.trim(), price));
    }

    public void update(int id, String name, String sportType, BigDecimal price) throws SQLException, BusinessException, EntityNotFoundException {
        Facility f = repository.findById(id);

        if (name == null || name.trim().length() < 2) {
            throw new BusinessException("Название площадки должно содержать минимум 2 символа");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Цена за час должна быть больше нуля");
        }

        f.setName(name.trim());
        f.setSportType(sportType.trim());
        f.setPricePerHour(price);

        repository.update(f);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}