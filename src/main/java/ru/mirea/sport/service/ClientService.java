package ru.mirea.sport.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ru.mirea.sport.exception.BusinessException;
import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.repository.ClientRepository;

public class ClientService {
    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";
    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> getAll() throws SQLException {
        return repository.findAll();
    }

    public Client getById(int id) throws SQLException, EntityNotFoundException {
        return repository.findById(id);
    }

    public List<Client> searchByName(String part) throws SQLException {
        if (part == null || part.isBlank()) {
            throw new IllegalArgumentException("Строка поиска не может быть пустой");
        }
        return repository.searchByName(part);
    }

    public Client create(String fullName, String phone, String email) throws SQLException, BusinessException {
        if (fullName == null || fullName.trim().length() < 2) {
            throw new BusinessException("ФИО должно содержать минимум 2 символа");
        }
        fullName = fullName.trim();

        if (phone == null || !phone.trim().matches(PHONE_REGEX)) {
            throw new BusinessException("Телефон должен быть в формате +7XXXXXXXXXX (10-15 цифр)");
        }
        phone = phone.trim();

        Optional<Client> existing = repository.findByPhone(phone);
        if (existing.isPresent()) {
            throw new BusinessException("Клиент с телефоном " + phone + " уже существует (id=" + existing.get().getId() + ")");
        }

        if (email != null && !email.isBlank()) {
            email = email.trim();
            if (!email.contains("@") || !email.contains(".")) {
                throw new BusinessException("Email указан в некорректном формате");
            }
        } else {
            email = null;
        }

        return repository.save(new Client(fullName, phone, email));
    }

    public void update(int id, String fullName, String phone, String email) throws SQLException, BusinessException, EntityNotFoundException {
        Client client = repository.findById(id);

        if (fullName == null || fullName.trim().length() < 2) {
            throw new BusinessException("ФИО должно содержать минимум 2 символа");
        }
        if (phone == null || !phone.trim().matches(PHONE_REGEX)) {
            throw new BusinessException("Телефон указан в некорректном формате");
        }
        phone = phone.trim();

        Optional<Client> byPhone = repository.findByPhone(phone);
        if (byPhone.isPresent() && byPhone.get().getId() != id) {
            throw new BusinessException("Телефон " + phone + " уже занят клиентом id=" + byPhone.get().getId());
        }

        client.setFullName(fullName.trim());
        client.setPhone(phone);
        client.setEmail((email == null || email.isBlank()) ? null : email.trim());

        repository.update(client);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}