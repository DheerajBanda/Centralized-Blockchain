package com.DataSysCoinv1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.bitcoinj.core.Base58;

public class HashesList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Hashes> hashes = new ArrayList<>();
	ConsoleLogger log = new ConsoleLogger();
	private ReentrantLock lock = new ReentrantLock();
	
	
	
	// Synchronized method to add a Hashes object to the list
    public synchronized void addToHashes(Hashes newHash) {
    	 hashes.add(newHash);
    }

    // Synchronized method to search for a Hashes object in the list
    public synchronized boolean searchInHashes(Hashes searchHash) {
        return hashes.contains(searchHash);
    }
    
    public ArrayList<Hashes> getList(){
    	return this.hashes;
    }
    
    public void printList() {
    	for(Hashes hash: hashes) {
    		log.logWithTimestamp("printing " + hash.getHash() + " nonce " + hash.getNonce() + " id " + hash.getThreadId());
    	}
    }

}
