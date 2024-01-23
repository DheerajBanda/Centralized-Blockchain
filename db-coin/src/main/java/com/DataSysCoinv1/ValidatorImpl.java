package com.DataSysCoinv1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ValidatorImpl extends UnicastRemoteObject implements ValidatorInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ConsoleLogger log = new ConsoleLogger();
	AtomicInteger signal = new AtomicInteger(0);
	AtomicInteger createBlock = new AtomicInteger(-1);
	short diff = 0;
	
	protected ValidatorImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sendSignal(short diff) throws RemoteException {
		// TODO Auto-generated method stub
		signal.set(1);
		this.diff = diff;
	}
	@Override
	public void createBlockSignal(int createBlock, List<HashInput> proofs, double reward) throws RemoteException {
		// TODO Auto-generated method stub
		this.createBlock.set(createBlock);
	}
	
	@Override
    public int getSignal() {
        return signal.get();
    }

	@Override
    public void setSignal(int signal) {
        this.signal.set(signal);
    }
    
	@Override
    public int getCreateBlock() {
        return createBlock.get();
    }

	@Override
    public void setCreateBlock(int createBlock) {
        this.createBlock.set(createBlock);
    }
    
	@Override
    public short getDiff() {
        return diff;
    }

	@Override
    public void setDiff(short diff) {
        this.diff = diff;
    }
	

}
