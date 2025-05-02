package com.example.EthereumTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;

public class FindTransaction {
	private Web3j web3;
	private LocalDateTime date;
	private String address;
	
	public FindTransaction(Web3j web3, LocalDateTime date, String address) throws IOException {
		this.web3 = web3;
		this.date = date;
		this.address = address;
		
		startSearch();
	}
	
	private void startSearch() throws IOException {
		long timeStamp = date.toEpochSecond(ZoneOffset.UTC);
		
		EthBlock block = findBlock(timeStamp);
		
		EthGetBalance balance = web3.ethGetBalance(address, 
				new DefaultBlockParameterNumber(block.getBlock().getNumber())).send();
		BigInteger balanceInWei = balance.getBalance();
		BigDecimal balanceInEth = new BigDecimal(balanceInWei).divide(BigDecimal.TEN.pow(18));
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		// address from USDT token
		String usdtContract = "0xdAC17F958D2ee523a2206206994597C13D831ec7";
		int decimals = 6;
		
		BigDecimal tokenBalance = tokenFromBlock(web3, usdtContract, 
				block.getBlock().getNumber(), decimals);
		
		System.out.println("Balance of address " + address + 
				" on \n" + date.format(formatter) + " at 00:00 UTC: " + balanceInEth 
				+ " ETH, token: " + tokenBalance + " USDT");
		
	}
	
	private EthBlock findBlock(long timeStamp) throws IOException {
		long startBlock = 0;
		long lastBlock = web3.ethBlockNumber().send().getBlockNumber().longValue();
		EthBlock block = null;
		
		while(block == null) {
			long middleIndex = (startBlock + lastBlock) / 2;
			EthBlock middleBlock = web3.ethGetBlockByNumber(
					new DefaultBlockParameterNumber(middleIndex),
					true).send();
			long timeStampMiddle = middleBlock.getBlock().getTimestamp().longValue();
			
			if(timeStampMiddle == timeStamp) {
				return middleBlock;
			}else if(timeStampMiddle < timeStamp) {
				block = middleBlock;
				startBlock = middleIndex + 1;
			}else {
				lastBlock = middleIndex - 1;
			}
		}
		
		return block;
	}
	
	private BigDecimal tokenFromBlock(Web3j web3, String addressOfToken, BigInteger blockNumber, int decimals) throws IOException {
	    Function function = new Function(
	        "balanceOf",
	        Collections.singletonList(new Address(address)),
	        Collections.singletonList(new TypeReference<Uint256>() {})
	    );

	    // transform my function in hexa address do blockchain can understand
	    String encodedFunction = FunctionEncoder.encode(function);

	    // just read transactons in block
	    EthCall response = web3.ethCall(
	        Transaction.createEthCallTransaction(address, addressOfToken, encodedFunction),
	        DefaultBlockParameter.valueOf(blockNumber)
	    ).send();

		String value = response.getValue();
		if (value != null && !value.equals("0x")) {
			List<Type> decoded = FunctionReturnDecoder.decode(value, function.getOutputParameters());
			if (decoded.isEmpty()) {
				System.out.println("Error: The response is empty – something is probably wrong with the call.");
				return BigDecimal.ZERO;
			}

			BigInteger balance = (BigInteger) (BigInteger) FunctionReturnDecoder.decode(
					value, function.getOutputParameters()).get(0).getValue();
			return new BigDecimal(balance).divide(BigDecimal.TEN.pow(decimals));
		} else {
			System.out.println("Failed to retrieve balance – the RPC may not support the block or the address might be incorrect.");
			return BigDecimal.ZERO;
		}
	}

	
}
