package com.aashi.aicodereviewer.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.security.MessageDigest;

public class PaymentService {

    // Bad Practice 1: Hardcoded credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/payment_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "SuperSecretPassword123!";

    public boolean processPayment(String userId, String amount, String creditCardNumber) {
        try {
            // Bad Practice 2: Using insecure MD5 to hash credit card number
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(creditCardNumber.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String maskedCard = sb.toString();

            // Bad Practice 3: Plain JDBC connection without connection pooling or Spring injection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Bad Practice 4: SQL Injection vulnerability (String concatenation in SQL query)
            Statement stmt = conn.createStatement();
            String query = "SELECT balance FROM users WHERE user_id = '" + userId + "' AND status = 'ACTIVE'";
            
            // Bad Practice 5: Resource Leak (statement, connection, and resultset are not closed in a finally block or try-with-resources)
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                double paymentAmount = Double.parseDouble(amount);

                if (balance >= paymentAmount) {
                    double newBalance = balance - paymentAmount;
                    // SQL Injection vulnerability on update
                    String updateQuery = "UPDATE users SET balance = " + newBalance + " WHERE user_id = '" + userId + "'";
                    stmt.executeUpdate(updateQuery);
                    System.out.println("Payment processed successfully for user: " + userId);
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            // Bad Practice 6: Generic exception catching and empty/poor exception handling
            e.printStackTrace();
            return false;
        }
    }
}
