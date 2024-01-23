package com.DataSysCoinv1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface BlockInterface extends Remote{
	
	AtomicInteger newBlock = new AtomicInteger();
	
	public void sendBlock(Block block) throws RemoteException;
	
	public void sendBlockValidator(Block block, byte[] publicKeyb) throws RemoteException;
	
	public Block getBlock()throws RemoteException;
	
	public Block getBlockIdAndHash(byte[] public_key) throws RemoteException;
	
	public int getNewBlock() throws RemoteException;
	
	public Balance getBalance(byte[] public_key) throws RemoteException;

	void createGenesisBlock() throws RemoteException;

	Block getBlockAtIndex(int index) throws RemoteException;

	double getBalanceLocal(String publicKey) throws RemoteException;

	void updateBalance(String publicKey, double balance) throws RemoteException;

	ArrayList<Transaction> getTransactionsForKey(String key) throws RemoteException;

	void addTransactions(String key, ArrayList<Transaction> transactions) throws RemoteException;

	ArrayList<Transaction> transactionList(byte[] publicKeyb) throws RemoteException;

	int getUpdatedBlockId() throws RemoteException;

	void setUpdatedBlockId(int value) throws RemoteException;

}
