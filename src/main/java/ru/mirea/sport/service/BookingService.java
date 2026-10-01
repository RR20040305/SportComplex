package ru.mirea.sport.service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ru.mirea.sport.exception.BusinessException;
import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.BookingStatus;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.model.Facility;
import ru.mirea.sport.repository.BookingRepository;
import ru.mirea.sport.repository.ClientRepository;
import ru.mirea.sport.repository.FacilityRepository;

public class BookingService {
    private static final Duration MIN_DURATION = Duration.ofMinutes(30);
    private static final Duration MAX_DURATION = Duration.ofHours(4);

    private final BookingRepository bookingRepo;
    private final ClientRepository clientRepo;
    private final FacilityRepository facilityRepo;

    public BookingService(BookingRepository bookingRepo, ClientRepository clientRepo, FacilityRepository facilityRepo) {
        this.bookingRepo = bookingRepo;
        this.clientRepo = clientRepo;
        this.facilityRepo = facilityRepo;
    }

    public List<Booking> getAll() throws SQLException {
        return bookingRepo.findAll();
    }

    public Booking getById(int id) throws SQLException, EntityNotFoundException {
        return bookingRepo.findById(id);
    }

    public List<Booking> getByFacilityAndDate(int facilityId, LocalDate date) throws SQLException {
        return bookingRepo.findByFacilityAndDate(facilityId, date);
    }

    public List<Booking> getByClient(int clientId) throws SQLException {
        return bookingRepo.findByClient(clientId);
    }

    public List<Booking> getByStatus(BookingStatus status) throws SQLException {
        return bookingRepo.findByStatus(status);
    }

    public Booking create(int clientId, int facilityId, LocalDateTime start, LocalDateTime end) throws SQLException, BusinessException, EntityNotFoundException {
        Client client = clientRepo.findById(clientId);
        Facility facility = facilityRepo.findById(facilityId);

        if (!end.isAfter(start)) {
            throw new BusinessException("Время окончания должно быть позже времени начала");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Нельзя создать бронь в прошлом");
        }

        Duration d = Duration.between(start, end);
        if (d.compareTo(MIN_DURATION) < 0) {
            throw new BusinessException("Минимальная длительность брони — 30 минут");
        }
        if (d.compareTo(MAX_DURATION) > 0) {
            throw new BusinessException("Максимальная длительность брони — 4 часа");
        }

        if (bookingRepo.hasOverlap(facilityId, start, end, null)) {
            throw new BusinessException("Площадка занята в это время. Выберите другой интервал");
        }

        Booking booking = new Booking(client, facility, start, end, BookingStatus.CREATED);
        return bookingRepo.save(booking);
    }

    public void changeStatus(int bookingId, BookingStatus newStatus) throws SQLException, BusinessException, EntityNotFoundException {
        Booking b = bookingRepo.findById(bookingId);

        if (b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Нельзя изменить статус брони со статусом " + b.getStatus().getDisplayName());
        }

        BookingStatus current = b.getStatus();
        boolean allowed = switch (current) {
            case CREATED -> newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED;
            case CONFIRMED -> newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.CANCELLED;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException(String.format("Недопустимый переход: %s → %s", current.getDisplayName(), newStatus.getDisplayName()));
        }

        b.setStatus(newStatus);
        bookingRepo.update(b);
    }

    public void reschedule(int bookingId, LocalDateTime newStart, LocalDateTime newEnd) throws SQLException, BusinessException, EntityNotFoundException {
        Booking b = bookingRepo.findById(bookingId);

        if (b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Нельзя перенести завершённую или отменённую бронь");
        }

        if (!newEnd.isAfter(newStart)) {
            throw new BusinessException("Время окончания должно быть позже времени начала");
        }
        if (newStart.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Нельзя перенести бронь в прошлое");
        }

        Duration d = Duration.between(newStart, newEnd);
        if (d.compareTo(MIN_DURATION) < 0 || d.compareTo(MAX_DURATION) > 0) {
            throw new BusinessException("Длительность должна быть от 30 минут до 4 часов");
        }

        if (bookingRepo.hasOverlap(b.getFacility().getId(), newStart, newEnd, b.getId())) {
            throw new BusinessException("Площадка занята в новое время");
        }

        b.setStartTime(newStart);
        b.setEndTime(newEnd);
        bookingRepo.update(b);
    }

    public void cancel(int bookingId) throws SQLException, BusinessException, EntityNotFoundException {
        Booking b = bookingRepo.findById(bookingId);

        if (b.getStatus() == BookingStatus.COMPLETED) {
            throw new BusinessException("Нельзя отменить завершённую бронь");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Бронь уже отменена");
        }

        b.setStatus(BookingStatus.CANCELLED);
        bookingRepo.update(b);
    }
}