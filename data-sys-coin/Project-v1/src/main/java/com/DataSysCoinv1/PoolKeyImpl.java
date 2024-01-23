package com.DataSysCoinv1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import org.bitcoinj.core.Base58;

public class PoolKeyImpl extends UnicastRemoteObject implements PoolKeyInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ConsoleLogger log = new ConsoleLogger();
	Queue<Transaction> unprocessed = new ConcurrentLinkedQueue<>();
	Map<String, Transaction> unconfirmed = new ConcurrentHashMap<>();
	Map<String, Transaction> invalid = new ConcurrentHashMap<>();
	AtomicInteger monitorSignal = new AtomicInteger(0);

	protected PoolKeyImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String sendTransaction(Transaction transaction) throws RemoteException {
		log.logWithTimestamp("Transaction id " + Base58.encode(transaction.getTransactionID()) + " received from "
				+ Base58.encode(transaction.getSenderPublicAddress()) + ", ACK");
		unprocessed.add(transaction);
		return "unprocessed";
	}

	@Override
	public String getStatus(byte[] transactionId) throws RemoteException {
		// TODO Auto-generated method stub
		String transactionIdString = Base58.encode(transactionId);
		if (!unprocessed.isEmpty()) {
			for (Transaction transaction : unprocessed) {
				if (Base58.encode(transaction.getTransactionID()).equals(transactionIdString)) {
					return "unprocessed";
				}
			}
		}
		if (!unconfirmed.isEmpty()) {
			if (unconfirmed.containsKey(transactionIdString)) {
				return "unconfirmed";
			}
		}
		if (!invalid.isEmpty()) {
			if (invalid.containsKey(transactionIdString)) {
				return "invalid";
			}
		}

		return "unknown";
	}

	@Override
	public ArrayList<Transaction> sendTransactionsValidator(byte[] publicKeyb) throws RemoteException {
		// TODO Auto-generated method stub
		ArrayList<Transaction> transactions = new ArrayList<>();
		int counter1 = 0;
		while (!unprocessed.isEmpty() && (counter1 < 8191)) {
			Transaction transaction = new Transaction();
			transaction = unprocessed.remove();
			transactions.add(transaction);
			unconfirmed.put(Base58.encode(transaction.getTransactionID()), transaction);
			/*for (Map.Entry<String, Transaction> entry : unconfirmed.entrySet()) {
				System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
			}*/
			//System.out.println("unprocessed at: " + counter1 + " transactionid " + Base58.encode(transaction.getTransactionID()));
			counter1++;
		}
		log.logWithTimestamp("Transactions request from validator " + Base58.encode(publicKeyb) + ", " + counter1
				+ " transactions sent");
		return transactions;
	}

	@Override
	public void receiveBlock(Block block) throws RemoteException {
		// TODO Auto-generated method stub
		ArrayList<Transaction> transactions = new ArrayList<>();
		transactions = block.getTransactions();
		if (!transactions.isEmpty()) {
			for (Transaction transaction : transactions) {
				if (unconfirmed.containsKey(Base58.encode(transaction.getTransactionID()))) {
					unconfirmed.remove(Base58.encode(transaction.getTransactionID()));
				} else {
					invalid.put(Base58.encode(transaction.getTransactionID()), transaction);
					unconfirmed.remove(Base58.encode(transaction.getTransactionID()));
				}
			}
		}
		this.monitorSignal.set(1);
	}

	@Override
	public void sendTransactionsMetronome(ArrayList<Transaction> transactions) throws RemoteException {
		// TODO Auto-generated method stub
		for (Transaction transaction : transactions) {
			unprocessed.add(transaction);
		}
		log.logWithTimestamp("Reward Transactions Recieved from Metronome - " + transactions.size());
	}
	
	@Override
	public int monitorSignalGet() {
		return this.monitorSignal.get();
	}
	
	@Override
	public void monitorSignalSet(int value) {
		this.monitorSignal.set(value);
	}
	
	@Override
	public int unprocessedSize() {
		return unprocessed.size();
	}
	
	@Override
	public int unconfirmedSize() {
		return unconfirmed.size();
	}

}
