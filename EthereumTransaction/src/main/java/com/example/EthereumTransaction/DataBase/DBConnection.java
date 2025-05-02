package com.example.EthereumTransaction.DataBase;

import java.sql.*;

public class DBConnection {
	private static Connection conn = null;
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection
					(
							"jdbc:mysql://127.0.0.1:3307/ethereumblockchain",
							"root",
							"!suncicaPmf1"
					);
		}catch (Exception e) {
			System.out.println("Database down");
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return conn;
	}
	
	public static void closeConnection() {
		try {
			if(conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("Connection closure failed");
			e.printStackTrace();
		}
	}
}

