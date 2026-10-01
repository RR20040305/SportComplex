package ru.mirea.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Booking;
import ru.mirea.sport.model.BookingStatus;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.model.Facility;
import ru.mirea.sport.util.DatabaseManager;

public class BookingRepository implements Repository<Booking, Integer> {
    private final DatabaseManager db;

    private static final String BASE_SELECT = """
        SELECT b.id AS b_id,
               b.start_time AS b_start,
               b.end_time AS b_end,
               b.status AS b_status,
               b.created_at AS b_created,
               c.id AS c_id,
               c.full_name AS c_name,
               c.phone AS c_phone,
               c.email AS c_email,
               c.created_at AS c_created,
               f.id AS f_id,
               f.name AS f_name,
               f.sport_type AS f_sport,
               f.price_per_hour AS f_price
        FROM bookings b
        JOIN clients c ON c.id = b.client_id
        JOIN facilities f ON f.id = b.facility_id
        """;

    public BookingRepository(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Booking findById(Integer id) throws SQLException, EntityNotFoundException {
        String sql = BASE_SELECT + " WHERE b.id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntityNotFoundException("Бронь", id);
                return mapRow(rs);
            }
        }
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY b.start_time";
        List<Booking> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    @Override
    public Booking save(Booking b) throws SQLException {
        String sql = """
            INSERT INTO bookings (client_id, facility_id, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getClient().getId());
            ps.setInt(2, b.getFacility().getId());
            ps.setTimestamp(3, Timestamp.valueOf(b.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(b.getEndTime()));
            ps.setString(5, b.getStatus().name());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int id = rs.getInt("id");
                LocalDateTime created = rs.getTimestamp("created_at").toLocalDateTime();
                return new Booking(id, b.getClient(), b.getFacility(), b.getStartTime(), b.getEndTime(), b.getStatus(), created);
            }
        }
    }

    @Override
    public void update(Booking b) throws SQLException, EntityNotFoundException {
        String sql = """
            UPDATE bookings
            SET client_id = ?, facility_id = ?, start_time = ?, end_time = ?, status = ?
            WHERE id = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getClient().getId());
            ps.setInt(2, b.getFacility().getId());
            ps.setTimestamp(3, Timestamp.valueOf(b.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(b.getEndTime()));
            ps.setString(5, b.getStatus().name());
            ps.setInt(6, b.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("Бронь", b.getId());
            }
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsById(Integer id) throws SQLException {
        String sql = "SELECT 1 FROM bookings WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean hasOverlap(int facilityId, LocalDateTime start, LocalDateTime end, Integer excludeBookingId) throws SQLException {
        String sql = """
            SELECT 1 FROM bookings
            WHERE facility_id = ?
              AND start_time < ?
              AND end_time > ?
              AND status <> 'CANCELLED'
              AND (? IS NULL OR id <> ?)
            LIMIT 1
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            if (excludeBookingId == null) {
                ps.setNull(4, Types.INTEGER);
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(4, excludeBookingId);
                ps.setInt(5, excludeBookingId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Booking> findByFacilityAndDate(int facilityId, LocalDate date) throws SQLException {
        String sql = BASE_SELECT + """
            WHERE b.facility_id = ?
              AND b.start_time >= ?
              AND b.start_time <  ?
            ORDER BY b.start_time
            """;
        List<Booking> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Booking> findByClient(int clientId) throws SQLException {
        String sql = BASE_SELECT + " WHERE b.client_id = ? ORDER BY b.start_time DESC";
        List<Booking> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Booking> findByStatus(BookingStatus status) throws SQLException {
        String sql = BASE_SELECT + " WHERE b.status = ? ORDER BY b.start_time";
        List<Booking> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Client client = new Client(
            rs.getInt("c_id"),
            rs.getString("c_name"),
            rs.getString("c_phone"),
            rs.getString("c_email"),
            rs.getTimestamp("c_created").toLocalDateTime()
        );
        Facility facility = new Facility(
            rs.getInt("f_id"),
            rs.getString("f_name"),
            rs.getString("f_sport"),
            rs.getBigDecimal("f_price")
        );
        return new Booking(
            rs.getInt("b_id"),
            client,
            facility,
            rs.getTimestamp("b_start").toLocalDateTime(),
            rs.getTimestamp("b_end").toLocalDateTime(),
            BookingStatus.valueOf(rs.getString("b_status")),
            rs.getTimestamp("b_created").toLocalDateTime()
        );
    }
}