package ru.mirea.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ru.mirea.sport.exception.EntityNotFoundException;
import ru.mirea.sport.model.Client;
import ru.mirea.sport.util.DatabaseManager;

public class ClientRepository implements Repository<Client, Integer> {
    private final DatabaseManager db;

    public ClientRepository(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Client findById(Integer id) throws SQLException, EntityNotFoundException {
        String sql = """
            SELECT id, full_name, phone, email, created_at
            FROM clients
            WHERE id = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new EntityNotFoundException("Клиент", id);
                }
                return mapRow(rs);
            }
        }
    }

    @Override
    public List<Client> findAll() throws SQLException {
        String sql = """
            SELECT id, full_name, phone, email, created_at
            FROM clients
            ORDER BY id
            """;
        List<Client> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public Optional<Client> findByPhone(String phone) throws SQLException {
        String sql = """
            SELECT id, full_name, phone, email, created_at
            FROM clients
            WHERE phone = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Client> searchByName(String part) throws SQLException {
        String sql = """
            SELECT id, full_name, phone, email, created_at
            FROM clients
            WHERE full_name ILIKE ?
            ORDER BY full_name
            """;
        List<Client> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + part + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    @Override
    public Client save(Client c) throws SQLException {
        String sql = """
            INSERT INTO clients (full_name, phone, email)
            VALUES (?, ?, ?)
            RETURNING id, created_at
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            if (c.getEmail() == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, c.getEmail());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int id = rs.getInt("id");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                return new Client(id, c.getFullName(), c.getPhone(), c.getEmail(), createdAt);
            }
        }
    }

    @Override
    public void update(Client c) throws SQLException, EntityNotFoundException {
        String sql = """
            UPDATE clients
            SET full_name = ?, phone = ?, email = ?
            WHERE id = ?
            """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            if (c.getEmail() == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, c.getEmail());
            ps.setInt(4, c.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new EntityNotFoundException("Клиент", c.getId());
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsById(Integer id) throws SQLException {
        String sql = "SELECT 1 FROM clients WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        return new Client(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}