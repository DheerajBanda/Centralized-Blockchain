package com.DataSysCoinv1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface MetronomeInterface extends Remote {
	short getDiff() throws RemoteException;
	
	int register(String ipaddr, int port, byte[] publicKey) throws RemoteException;
	
	void blockCreated(Block block, byte[] hash, String publicKey) throws RemoteException;
	
	int sendProof(HashInput input) throws RemoteException;

	void sendSignal() throws RemoteException;

	void sendCreateSignals() throws RemoteException;

	void setDiff() throws RemoteException;

	void clearProofs() throws RemoteException;

	int getReady() throws RemoteException;

	void setReady(int value) throws RemoteException;

	int proofSize() throws RemoteException;

	int getBlockId() throws RemoteException;

	byte[] getHash() throws RemoteException;

	String getCreatedBy() throws RemoteException;

	void setCreatedBy(String createdBy) throws RemoteException;

	double getReward() throws RemoteException;

	void setReward(double reward) throws RemoteException;

	Block getBlock() throws RemoteException;

	void setBlock(Block block) throws RemoteException;

	List<HashInput> getProofs() throws RemoteException;

}
