package com.DataSysCoinv1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;

public class Block implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int blockSize;
	private short version;
	private byte[] prevHash = new byte[32];
	private int blockId;
	private long timestamp;
	private short diff;
	private long nonce;
	private int counter;
	private byte[] reserved = new byte[64];
	private ArrayList<Transaction> transactions = new ArrayList<>();

	public Block(int blockSize, short version, byte[] prevHash, int blockId, long timestamp, short diff, int nounce,
			int counter, ArrayList<Transaction> transactions, byte[] reserved) {
		this.blockSize = blockSize;
		this.version = version;
		this.prevHash = prevHash;
		this.blockId = blockId;
		this.timestamp = timestamp;
		this.diff = diff;
		this.nonce = nounce;
		this.counter = counter;
		this.transactions = transactions;
		this.reserved = reserved;
	}

	public Block() {

	}

	// Getters and Setters for blockSize
	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	// Getters and Setters for version
	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	// Getters and Setters for prevHash
	public byte[] getPrevHash() {
		return prevHash;
	}

	public void setPrevHash(byte[] prevHash) {
		this.prevHash = prevHash;
	}

	// Getters and Setters for blockId
	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	// Getters and Setters for timestamp
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// Getters and Setters for diff
	public short getDiff() {
		return diff;
	}

	public void setDiff(short diff) {
		this.diff = diff;
	}

	// Getters and Setters for nonce
	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	// Getters and Setters for counter
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	// Getters and Setters for reserved
	public byte[] getReserved() {
		return reserved;
	}

	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	public String toString() {
		return blockSize + version + Arrays.toString(prevHash) + blockId + timestamp + diff + nonce + counter
				+ Arrays.toString(reserved);
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}

	public ArrayList<Transaction> getTransactions() {
		if (transactions != null) {
			return transactions;
		}else {
			return null;
		}
	}

	// Method to add a transaction to the list
	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}

	// Method to remove a transaction from the list
	public void removeTransaction(Transaction transaction) {
		transactions.remove(transaction);
	}

	// Method to get the size of the list
	public int getListSize() {
		return transactions.size();
	}

}
