package com.example.EthereumTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.example.EthereumTransaction.DataBase.DatabaseInitializer;
import com.example.EthereumTransaction.service.TransactionService;


@SpringBootApplication
public class EthereumTransactionApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EthereumTransactionApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		// address
		// String walletAddress = "0xaa7a9ca87d3694b5755f213b5d04094b8d0f0a6f";
		// String walletAddress = "0x28C6c06298d514Db089934071355E5743bf21d60";
		
		String api = "https://mainnet.infura.io/v3/4818c5f2b43147b69a4a9ca6ddf181ff";
		Web3j web3 = Web3j.build(new HttpService(api));
		
		TransactionService trs = new TransactionService();
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Do u have database?(y/n)");
		char answer = sc.next().charAt(0);
		
		if(answer == 'n') {
			try {
				System.out.println("Host: ");
				String host = sc.next();
				
				System.out.println("Port: ");
				String port = sc.next();
				
				System.out.println("User: ");
				String user = sc.next();
				
				System.out.println("Password: ");
				String password = sc.next();
			
				DatabaseInitializer dbi = new DatabaseInitializer(host, port, user, password);
	            dbi.createDatabaseIfNotExists();
	            dbi.createTablesIfNotExists();
	            System.out.println("Database and table are ready.");
	        } catch (SQLException e) {
	            System.err.println("Error initializing database: " + e.getMessage());
	        }
		}
		
		while(true) {
			System.out.println("Choose 1, 2 or 3");
			System.out.println("------------------------------");
			System.out.println("1 Get transactions (input: block number and address)");
			System.out.println("2 Get ETH from wallet at 00:00 UTC (input: date in YYYY-MM-DD format and address)");
			System.out.println("3 Exit");
			
			int option;
			try {
		        option = sc.nextInt();
		        sc.nextLine();
		    } catch (InputMismatchException e) {
		        System.out.println("Invalid input. Please enter a number.");
		        sc.nextLine();
		        continue;
		    }
			
			switch(option) {
				case 1 -> {
					try {
						System.out.println("Block number: ");
						long blockNumber = sc.nextLong();
						sc.nextLine();
						
						System.out.println("Address: ");
						String address = sc.next();
						
						System.out.println("If you want to stop search please write `out` in any moment you want.");
						
						FindTransactions findtr = new FindTransactions(blockNumber, address, web3, sc, trs);
					}catch (InputMismatchException e) {
						System.out.println("Invalid input. Block number must be number");
						sc.nextLine();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				case 2 -> {
					try {
						System.out.println("Date (YYYY-MM-DD format): ");
						String input = sc.nextLine();
						LocalDateTime date = LocalDate.parse(input).atStartOfDay();
						
						System.out.println("Address: ");
						String address = sc.next();
						
						FindTransaction findtr = new FindTransaction(web3, date, address);
					}catch (DateTimeParseException e) {
						System.out.println("Invalit date format!");
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				case 3 -> {
					System.out.println("Exiting program...");
					sc.close();
					System.exit(0);
				}
				default -> {System.out.println("Unknown option. Choose 1, 2 or 3");}
			} 
		
		}
	}

}
