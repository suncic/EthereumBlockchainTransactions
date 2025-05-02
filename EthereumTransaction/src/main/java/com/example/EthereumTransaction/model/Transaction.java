package com.example.EthereumTransaction.model;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class Transaction {
	private String adressTo;
	private String adressFrom;
	private long numberOfBlock;
	private BigDecimal eth;
	
	public Transaction() {
		
	}

	public Transaction(String adressTo, String adressFrom, long numberOfBlock, BigDecimal eth) {
		this.adressTo = adressTo;
		this.adressFrom = adressFrom;
		this.numberOfBlock = numberOfBlock;
		this.eth = eth;
	}

	public void setAdressTo(String adressTo) {
		this.adressTo = adressTo;
	}

	public void setAdressFrom(String adressFrom) {
		this.adressFrom = adressFrom;
	}

	public void setNumberOfBlock(long numberOfBlock) {
		this.numberOfBlock = numberOfBlock;
	}

	public void setEth(BigDecimal eth) {
		this.eth = eth;
	}

	public String getAdressTo() {
		return adressTo;
	}

	public long getNumberOfBlock() {
		return numberOfBlock;
	}

	public String getAdressFrom() {
		return adressFrom;
	}

	public BigDecimal getEth() {
		return eth;
	}

	@Override
	public String toString() {
		return "---------------------\nfrom: " + adressFrom + "\nto: " + adressTo + 
				"\nblock: " + numberOfBlock + "\nvalue: " + eth + 
				" ETH\n---------------------";
	}
}
