package com.DataSysCoinv1;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bitcoinj.core.Base58;

public class BlockChain {

	static Blake32 hasher = new Blake32();
	static ConsoleLogger log = new ConsoleLogger();

	public static void start() {

		log.logWithTimestamp("DSC v1.0");

		BlockChainRead.read();

		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.rmi.server.hostname", BlockChainRead.getBlockchainIp());
			BlockInterface blockImpl = new BlockImpl();
			Registry registry = LocateRegistry.createRegistry(BlockChainRead.getPort());
			registry.rebind("Block", blockImpl);
			AtomicInteger newBlock = new AtomicInteger(0);
			
			blockImpl.createGenesisBlock();

			while (true) {
				//System.out.println("In true loop");
				while (newBlock.get() <= blockImpl.getNewBlock()) {
					//System.out.println("newblock " + newBlock.get() + " and blockImpl.getNewBlock " + blockImpl.getNewBlock());
					//System.out.println("In true loop");
					Block block = new Block();
					block = blockImpl.getBlockAtIndex(newBlock.get());
					if (block != null) {
						ArrayList<Transaction> transactions = new ArrayList<>();
						transactions = block.getTransactions();
						if (transactions != null) {
							for (Transaction transaction : transactions) {
								//System.out.println("transaction id " + Base58.encode(transaction.getTransactionID()) + " blockId " + block.getBlockId());
								byte[] recipientAddressb = transaction.getRecipientPublicAddress();
								byte[] senderAddressb = transaction.getSenderPublicAddress();
								double amount = transaction.getValue();
								String recipeintAddress = Base58.encode(recipientAddressb);
								double currentRecipientValue = blockImpl.getBalanceLocal(recipeintAddress);
								blockImpl.updateBalance(recipeintAddress, currentRecipientValue + amount);
								if (senderAddressb != null) {
									String senderAddress = Base58.encode(senderAddressb);
									double currentSenderValue = blockImpl.getBalanceLocal(senderAddress);
									//double updatedAmount = currentSenderValue - amount;
									//System.out.println("transaction sender " + senderAddress + " updatedAmount " + updatedAmount);
									blockImpl.updateBalance(senderAddress, currentSenderValue - amount);
									ArrayList<Transaction> transactions1 = new ArrayList<>();
									//System.out.println("transaction id " + Base58.encode(transaction.getTransactionID()));
									transactions1 = blockImpl.getTransactionsForKey(senderAddress);
									transactions1.add(transaction);
									blockImpl.addTransactions(senderAddress, transactions1);
								}

							}
						}
					}
					blockImpl.setUpdatedBlockId(newBlock.get());
					newBlock.incrementAndGet();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
