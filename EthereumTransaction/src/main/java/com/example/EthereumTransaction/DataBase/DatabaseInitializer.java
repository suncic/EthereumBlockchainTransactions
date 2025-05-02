package com.example.EthereumTransaction.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	private static String host;
    private static String port;
    private static String user;
    private static String password;
    private static final String DATABASE = "ethereumblockchain";
    
    public DatabaseInitializer(String host, String port, String user, String password) {
    	this.host = host;
    	this.port = port;
    	this.user = user;
    	this.password = password;
    }
	
	 public void createDatabaseIfNotExists() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE);
        }
	 }
	 
	 public void createTablesIfNotExists() throws SQLException {
        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + DATABASE + "?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement()) {
        	String sql = """
        	        CREATE TABLE IF NOT EXISTS `Transaction` (
        	            id INT AUTO_INCREMENT PRIMARY KEY,
        	            `from` VARCHAR(100),
        	            `to` VARCHAR(100),
        	            block BIGINT,
        	            eth DECIMAL(38, 18)
        	        )
        	    """;
            stmt.executeUpdate(sql);
        }
	 }
}
