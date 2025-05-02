package com.example.EthereumTransaction.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EthereumTransaction.model.Transaction;
import com.example.EthereumTransaction.service.TransactionService;

@RestController
public class TransactionController {
	
	@Autowired
	TransactionService trs;
	
	@RequestMapping("/transaction")
	public List<Transaction> getTransactions() {
		return trs.getAllTansactions();
	}
	
}
