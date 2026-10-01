package ru.mirea.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Facility;
import ru.mirea.sport.util.DatabaseManager;

public class FacilityRepository implements Repository<Facility, Integer> {
    private final DatabaseManager db;

    public FacilityRepository(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Facility findById(Integer id) throws SQLException, EntityNotFoundException {
        String sql = """
            SELECT id, name, sport_type, price_per_hour
            FROM facilities
            WHERE id = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntityNotFoundException("Площадка", id);
                return mapRow(rs);
            }
        }
    }

    @Override
    public List<Facility> findAll() throws SQLException {
        String sql = """
            SELECT id, name, sport_type, price_per_hour
            FROM facilities
            ORDER BY id
            """;
        List<Facility> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public List<Facility> findBySportType(String sportType) throws SQLException {
        String sql = """
            SELECT id, name, sport_type, price_per_hour
            FROM facilities
            WHERE sport_type = ?
            ORDER BY name
            """;
        List<Facility> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sportType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Facility> searchByName(String part) throws SQLException {
        String sql = """
            SELECT id, name, sport_type, price_per_hour
            FROM facilities
            WHERE name ILIKE ?
            ORDER BY name
            """;
        List<Facility> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + part + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    @Override
    public Facility save(Facility f) throws SQLException {
        String sql = """
            INSERT INTO facilities (name, sport_type, price_per_hour)
            VALUES (?, ?, ?)
            RETURNING id
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getSportType());
            ps.setBigDecimal(3, f.getPricePerHour());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Facility(rs.getInt("id"), f.getName(), f.getSportType(), f.getPricePerHour());
            }
        }
    }

    @Override
    public void update(Facility f) throws SQLException, EntityNotFoundException {
        String sql = """
            UPDATE facilities
            SET name = ?, sport_type = ?, price_per_hour = ?
            WHERE id = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getSportType());
            ps.setBigDecimal(3, f.getPricePerHour());
            ps.setInt(4, f.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("Площадка", f.getId());
            }
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM facilities WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsById(Integer id) throws SQLException {
        String sql = "SELECT 1 FROM facilities WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Facility mapRow(ResultSet rs) throws SQLException {
        return new Facility(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("sport_type"),
            rs.getBigDecimal("price_per_hour")
        );
    }
}