package com.DataSysCoinv1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bitcoinj.core.Base58;

public class MonitorImpl extends UnicastRemoteObject implements MonitorInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ConsoleLogger log = new ConsoleLogger();
	ArrayList<Block> blocks = new ArrayList<>();
	AtomicInteger newBlock = new AtomicInteger(0);
	ArrayList<Register> registers = new ArrayList<>();

	protected MonitorImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receiveBlock(Block block) throws RemoteException {
		// TODO Auto-generated method stub
		Block block1 = new Block();
		block1 = block;
		blocks.add(block1);
		this.newBlock.set(block1.getBlockId());
		log.logWithTimestamp("New Block added, blockId " + block1.getBlockId() + " No. of Transactions " + block1.getCounter() + " timestamp " + formatDate(block1.getTimestamp()));
	}

	@Override
	public void receiveRegisterInfo(Register register) throws RemoteException {
		// TODO Auto-generated method stub
		Register register1 = new Register();
		register1 = register;
		registers.add(register1);
		log.logWithTimestamp("Validator " + Base58.encode(register1.getPublicKeyb()) + " registered with Metronome");
	}

	@Override
	public void receiveSize(int unprocessedSize, int unconfirmedSize) throws RemoteException {
		// TODO Auto-generated method stub
		log.logWithTimestamp("unprocessed list size " + unprocessedSize + " unconfirmed list size" + unconfirmedSize);
	}
	
	@Override
	public int getBlockId() {
		return this.newBlock.get();
	}
	
	@Override
	public Block getBlockAtIndex(int index) {
        if (index >= 0 && index < blocks.size()) {
            return blocks.get(index);
        } else {
            
            return null;
        }
    }
	
	public static String formatDate(long currentTimeMillis) {
        Date date = new Date(currentTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}
