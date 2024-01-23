package com.DataSysCoinv1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import org.bitcoinj.core.Base58;

public class BlockImpl extends UnicastRemoteObject implements BlockInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ConsoleLogger log = new ConsoleLogger();
	Blake32 hasher = new Blake32();
	Block block = new Block(0, (short)0, null, 0, 0, (short) 0, 0, 0, null, null);
	private AtomicInteger newBlockId = new AtomicInteger(-1);
	private byte[] newHash;
	private int blockId = 0;
	private ArrayList<Block> blocks = new ArrayList<>();
	Map<String, Double> balanceCache = new ConcurrentHashMap<>();
	Map<String, ArrayList<Transaction>> transactionMap = new ConcurrentHashMap<>();
	private AtomicInteger updatedBlockId = new AtomicInteger(0);

	protected BlockImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void sendBlock(Block block) throws RemoteException {
		// TODO Auto-generated method stub
		this.block = block;
		this.blockId = block.getBlockId();
		this.newHash = calculateHash(this.block);
		blocks.add(block);
		sendBlockMonitor(block);
		log.logWithTimestamp("New block received from Metronome, Block " + this.block.getBlockId() + " hash " + Base58.encode(this.newHash));
		newBlockId.set(blockId);
		sendSignal(this.block, this.newHash, "Metronome");
	}

	private void sendBlockMonitor(Block block2) {
		// TODO Auto-generated method stub
		try {
			Registry registry2 = LocateRegistry.getRegistry(BlockChainRead.getMonitorIp(), BlockChainRead.getMonitorPort());
			MonitorInterface monitorImpl = (MonitorInterface) registry2.lookup("Monitor");
			
			monitorImpl.receiveBlock(block2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendSignal(Block block, byte[] hash, String publicKeyb) {
		// TODO Auto-generated method stub
		
		try {
			Registry registry1 = LocateRegistry.getRegistry(BlockChainRead.getMetronomeIp(), BlockChainRead.getMetronomePort());
			MetronomeInterface metronomeImpl = (MetronomeInterface) registry1.lookup("Metronome");
			System.out.println("metronome ip: " + BlockChainRead.getMetronomeIp() + " and port: " + BlockChainRead.getMetronomePort());
			metronomeImpl.blockCreated(block, hash, publicKeyb);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendBlockValidator(Block block, byte[] publicKeyb) {
		this.block = block;
		this.blockId = block.getBlockId();
		this.newHash = calculateHash(this.block);
		blocks.add(block);
		log.logWithTimestamp("New block received from Validator, " + Base58.encode(publicKeyb) + " Block " + this.block.getBlockId() + " hash " + Base58.encode(this.newHash));
		newBlockId.set(blockId);
		sendBlockMonitor(block);
		sendSignal(this.block, this.newHash, Base58.encode(publicKeyb));
	}

	private byte[] calculateHash(Block block1) {
		// TODO Auto-generated method stub
		byte[] input = block1.toString().getBytes();
		byte[] hash = hasher.blakeInBytes(input);
		return hash;
	}

	@Override
	public Block getBlock() throws RemoteException {
		return this.block;
	}
	
	@Override
	public Block getBlockIdAndHash(byte[] public_key){
		log.logWithTimestamp("Block request from validator "+ Base58.encode(public_key) + ", Block " + block.getBlockId() + "hash " + Base58.encode(this.newHash));
		return this.block;
	}
	
	@Override
	public int getNewBlock() {
		return newBlockId.get();
	}

	@Override
	public Balance getBalance(byte[] public_key) throws RemoteException {
		// TODO Auto-generated method stub
		Balance balance = new Balance();
		double value = 0.0;
		if(balanceCache.containsKey(Base58.encode(public_key))) {
			value = balanceCache.get(Base58.encode(public_key));
		}else {
			balanceCache.put(Base58.encode(public_key), 100.0);
			value = balanceCache.get(Base58.encode(public_key));
		}
		balance.setBalance((double) value);
		balance.setBlockId(this.updatedBlockId.get());
		log.logWithTimestamp("Balance request for " + Base58.encode(public_key) + ", " + (float)value + " coins");
		return balance;
	}
	
	@Override
	public void createGenesisBlock() {
		Block block = new Block(0, (short)0, null, 0, 0, (short) 0, 0, 0, null, null);
		block.setTimestamp(System.currentTimeMillis());
		this.block = block;
		this.blockId = block.getBlockId();
		this.newHash = calculateHash(block);
		newBlockId.set(blockId);
		blocks.add(block);
		log.logWithTimestamp("Genesis Block Created with hash " + Base58.encode(newHash));
		sendSignal(this.block, newHash, "Metronome");
	}
	
	@Override
	public Block getBlockAtIndex(int index) {
        if (index >= 0 && index < blocks.size()) {
            return blocks.get(index);
        } else {
            
            return null;
        }
    }
	
	@Override
	public double getBalanceLocal(String publicKey) {
		double balance = 0.0;
		if(balanceCache.containsKey(publicKey)) {
			balance = balanceCache.get(publicKey);
		}else {
			balanceCache.put(publicKey, 100.0);
			balance = balanceCache.get(publicKey);
		}
		return balance;
	}
	
	@Override
	public void updateBalance(String publicKey, double balance) {
		if(balanceCache.containsKey(publicKey)) {
			balanceCache.put(publicKey, balance);
		}
	}
	
	@Override
	public ArrayList<Transaction> getTransactionsForKey(String key) {
		if(transactionMap.containsKey(key)) {
			return transactionMap.get(key);
		}else {
			ArrayList<Transaction> transactions = new ArrayList<>();
			transactionMap.put(key, transactions);
			return transactionMap.get(key);
		}
	}
	
	@Override
	public void addTransactions(String key, ArrayList<Transaction> transactions) {
		transactionMap.put(key, transactions);
	}
	
	@Override
	public ArrayList<Transaction> transactionList(byte[] publicKeyb){
		String publicKey = Base58.encode(publicKeyb);
		
		if(transactionMap.containsKey(publicKey)) {
			//ArrayList<Transaction> list = new ArrayList<>();
			//list = transactionMap.get(publicKey);
			log.logWithTimestamp("Transactions request for " + publicKey + ", " + transactionMap.get(publicKey).size() + " transactions sent");
			return transactionMap.get(publicKey);
		}else {
			log.logWithTimestamp("Transactions request for " + publicKey + ", none found");
			return null;
		}
		
	}
	
	@Override
    public int getUpdatedBlockId() {
        return updatedBlockId.get();
    }

    @Override
    public void setUpdatedBlockId(int value) {
        updatedBlockId.set(value);
    }


}
