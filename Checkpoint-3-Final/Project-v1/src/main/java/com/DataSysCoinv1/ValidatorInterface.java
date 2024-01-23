package com.DataSysCoinv1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ValidatorInterface extends Remote{
	
	public void sendSignal(short diff) throws RemoteException;
	
	public void createBlockSignal(int createBlock, List<HashInput> proofs, double reward) throws RemoteException;

	int getSignal() throws RemoteException;

	void setSignal(int signal) throws RemoteException;

	int getCreateBlock() throws RemoteException;

	void setCreateBlock(int createBlock) throws RemoteException;

	short getDiff() throws RemoteException;

	void setDiff(short diff) throws RemoteException;

}
