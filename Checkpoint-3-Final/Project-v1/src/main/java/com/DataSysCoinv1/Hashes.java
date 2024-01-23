package com.DataSysCoinv1;

import java.io.Serializable;
import java.util.ArrayList;

public class Hashes implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] hash = new byte[32];
	private long nonce = -1;
	private int threadId = -1;
	
	public Hashes() {
	}
	
	
	// Getter for hash
    public byte[] getHash() {
        return this.hash;
    }

    // Setter for hash
    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    // Getter for nonce
    public long getNonce() {
        return nonce;
    }

    // Setter for nonce
    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
    
 // Setter for threadId
    public void setThreadId(int id) {
        this.threadId = id;
    }

    // Getter for threadId
    public int getThreadId() {
        return threadId;
    }
	
}
