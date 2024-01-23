package com.DataSysCoinv1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PoolKeyInterface extends Remote {
	
	public String sendTransaction(Transaction transaction) throws RemoteException;
	
	public String getStatus(byte[] transactionId) throws RemoteException;
	
	public ArrayList<Transaction> sendTransactionsValidator(byte[] publicKeyb) throws RemoteException;
	
	public void receiveBlock(Block block)throws RemoteException;

	public void sendTransactionsMetronome(ArrayList<Transaction> transactions) throws RemoteException;

	int monitorSignalGet() throws RemoteException;

	int unprocessedSize() throws RemoteException;

	int unconfirmedSize() throws RemoteException;

	void monitorSignalSet(int value) throws RemoteException;
	
	

}
