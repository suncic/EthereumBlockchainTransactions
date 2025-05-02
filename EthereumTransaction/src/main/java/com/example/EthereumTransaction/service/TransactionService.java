package com.example.EthereumTransaction.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.EthereumTransaction.DataBase.DBConnection;
import com.example.EthereumTransaction.model.Transaction;

@Service
public class TransactionService {
	
	private List<Transaction> transactions = new LinkedList<Transaction>();

	public List<Transaction> getAllTansactions(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from `Transaction`";

		try {
			ps = DBConnection.getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Transaction t = new Transaction(rs.getString(2), rs.getString(3), rs.getLong(4), rs.getBigDecimal(5));
				transactions.add(t);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(ps != null) {
				try {
					rs.close();
					ps.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return transactions;
	}
	
	public void addTransaction(Transaction t) {
		try (PreparedStatement ps = DBConnection.getConnection()
				.prepareStatement("insert into `Transaction` (`from`, `to`, `block`, `eth`) values (?,?,?,?)")) {
			ps.setString(1, t.getAdressFrom());
			ps.setString(2, t.getAdressTo());
			ps.setLong(3, t.getNumberOfBlock());
			ps.setBigDecimal(4, t.getEth());
			ps.executeUpdate();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAllTransactions() {
		try (PreparedStatement ps = DBConnection.getConnection()
				.prepareStatement("delete from `Transaction`")) {
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
}
