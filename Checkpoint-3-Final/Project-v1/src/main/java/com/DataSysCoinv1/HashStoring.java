package com.DataSysCoinv1;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HashStoring implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int threadId;
	private short diff;
	private long nonce;
	private byte[] fingerprintb;
	private byte[] public_keyb;
	private byte[] prevHash;
	private int blockId;
	private String ipAddress;
	private int port;
	public AtomicInteger ready = new AtomicInteger(0);
	public AtomicLong totalHashes = new AtomicLong(0);
	public AtomicInteger done = new AtomicInteger(0);
	public AtomicBoolean found = new AtomicBoolean(false);
	public byte[] hash;
	public AtomicLong memoryHashes = new AtomicLong(0);
	
	public HashStoring(int threadId, short diff, long nonce, byte[] fingerprintb, byte[] public_keyb, byte[] prevHash) {
		this.threadId = threadId;
		this.diff = diff;
        this.nonce = nonce;
        this.fingerprintb = fingerprintb;
        this.public_keyb = public_keyb;
        this.prevHash = prevHash;
	}
	
	public HashStoring() {
		
	}
	
	public int getThreadId() {
        return threadId;
    }

    public synchronized void setThreadId(int threadId) {
        this.threadId = threadId;
    }
    
    public short getDiff() {
    	return diff;
    }
    
    public short setDiff(short diff) {
    	return this.diff = diff;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

	public byte[] getFingerprintb() {
		return fingerprintb;
	}

	public void setFingerprintb(byte[] fingerprintb) {
		this.fingerprintb = fingerprintb;
	}

	public byte[] getPublic_keyb() {
		return public_keyb;
	}

	public void setPublic_keyb(byte[] public_keyb) {
		this.public_keyb = public_keyb;
	}

	public byte[] getPrevHash() {
		return prevHash;
	}

	public void setPrevHash(byte[] prevHash) {
		this.prevHash = prevHash;
	}
	
	public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
    
    // Getter method for ipAddress
    public String getIpAddress() {
        return ipAddress;
    }

    // Setter method for ipAddress
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Getter method for port
    public int getPort() {
        return port;
    }

    // Setter method for port
    public void setPort(int port) {
        this.port = port;
    }

	
}
