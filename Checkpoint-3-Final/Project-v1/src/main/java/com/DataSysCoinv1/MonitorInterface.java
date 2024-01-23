package com.DataSysCoinv1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitorInterface extends Remote{

	public void receiveBlock(Block block) throws RemoteException;
	
	public void receiveRegisterInfo(Register register) throws RemoteException;
	
	public void receiveSize(int unprocessedSize, int unconfirmedSize) throws RemoteException;

	int getBlockId() throws RemoteException;

	Block getBlockAtIndex(int index) throws RemoteException;
}
