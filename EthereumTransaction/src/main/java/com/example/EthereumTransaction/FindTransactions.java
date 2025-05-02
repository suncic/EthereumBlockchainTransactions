package com.example.EthereumTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import com.example.EthereumTransaction.service.TransactionService;

public class FindTransactions {
	private TransactionService trs;
	
	private Web3j web3;
	private long firstBlockNumber;
	private String adress;
	private long lastBlockNumber;
	private Scanner sc;

	
	public FindTransactions(long firstBlockNumber, String adress, Web3j web3, Scanner sc, TransactionService trs) throws IOException, InterruptedException {
		this.firstBlockNumber = firstBlockNumber;
		this.adress = adress;
		this.web3 = web3;
		this.sc = sc;
		this.trs = trs;
		
		lastBlockNumber = web3.ethBlockNumber().send().getBlockNumber().longValue();
		
		startSearch();
	}
	
	private void startSearch() throws IOException, InterruptedException {
		int i = 0;
		long blockNumber = firstBlockNumber + i;
		
		while(blockNumber <= lastBlockNumber) {
			if (System.in.available() > 0) {
                String input = sc.nextLine();
                if ("out".equalsIgnoreCase(input.trim())) {
                    System.out.println("Search cancelled by user.");
                    trs.deleteAllTransactions();
                    return;
                }
            }
			
			//true --> for detailed information of transaction inside block
			EthBlock block = web3.ethGetBlockByNumber(
					new DefaultBlockParameterNumber(blockNumber),
					true).send();
			
			processBlock(block, blockNumber);
			
			blockNumber += i;
			i++;
			Thread.sleep(300);
		}
		
	}
	
	
	// moze se desiti vise transakcija u jednom bloku sa ili ka istoj adresi
	private void processBlock(EthBlock b, long numberOfBlock) {
		EthBlock.Block block = b.getBlock();
		List<EthBlock.TransactionResult> transactions = block.getTransactions();

		
		for(EthBlock.TransactionResult transaction : transactions) {
			Transaction tr = (Transaction) transaction.get();
			boolean sameAdress = false;
			
			
			if(tr.getTo() == null) {
				if(tr.getFrom().equalsIgnoreCase(adress)) {
					sameAdress = true;
				}
			}else {
				if(tr.getTo().equalsIgnoreCase(adress) || 
						tr.getFrom().equals(adress)) {
					sameAdress = true;
				}
			}
			
			if(sameAdress) {
				BigInteger balanceInWei = tr.getValue();
				BigDecimal balanceInEth = Convert.fromWei(balanceInWei.toString(), Convert.Unit.ETHER);
				System.out.println(balanceInWei);
				
				com.example.EthereumTransaction.model.Transaction t = new com.example.EthereumTransaction.model.Transaction(tr.getFrom(), tr.getTo(), numberOfBlock, balanceInEth);
				trs.addTransaction(t);
				System.out.println(t);
			}
		}
	}
}
