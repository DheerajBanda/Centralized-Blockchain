package com.DataSysCoinv1;
import java.io.Serializable;

public class Balance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double balance;
	private int blockId;

	public double getBalance() {
		return balance;
	}

	public void setBalance(double value) {
		this.balance = value;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

}
