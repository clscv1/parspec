package repository;

import db.DbConfig;
import pojos.Order;
import pojos.OrderStatus;

import java.sql.*;
import java.util.Arrays;

public class OrderRepository {

    private DbConfig dbConfig;
    public OrderRepository(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void create(Order order) {
        String sql = "INSERT INTO orders (orderId,userId, itemIds, totalAmount, status) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, order.getOrderId());
            stmt.setObject(2, order.getUserId());
            stmt.setString(3, String.join(",", order.getItemIds()));
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getStatus().name());
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public void update(Order order) {
        String sql = "UPDATE orders SET userId = ?, itemIds = ?, totalAmount = ?, status = ? WHERE orderId = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, order.getUserId());
            stmt.setString(2, String.join(",", order.getItemIds()));
            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().name());
            stmt.setObject(5, order.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public Order getById(String orderId) {
        String sql = "SELECT orderId, userId, itemIds, totalAmount, status FROM orders WHERE orderId = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("length: "+rs.getString("status").length());
                    return new Order(
                            rs.getString("orderId"),
                            rs.getString("userId"),
                            Arrays.asList(rs.getString("itemIds").split(",")),
                            rs.getDouble("totalAmount"),
                            OrderStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
        return null;
    }
}
