package com.DataSysCoinv1;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

import org.bitcoinj.core.Base58;

public class PomWorkerThread implements Runnable {

	int threadId;
	HashesList hashes = new HashesList();
	ConsoleLogger log = new ConsoleLogger();
	private HashStoring hashStoring;
	
	public PomWorkerThread(HashesList hashes, int i, HashStoring hashStoring) {
		// TODO Auto-generated constructor stub
		this.hashes = hashes;
		this.threadId = i;
		this.hashStoring = hashStoring;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int totalLength = this.hashStoring.getFingerprintb().length + this.hashStoring.getPublic_keyb().length + Integer.BYTES
				+ Long.BYTES;
		Blake32 hasher = new Blake32();
		long nonce = 0;
		
		long memoryHashes = hashStoring.memoryHashes.get();
		log.logWithTimestamp("generating hashes [Thread #" + this.threadId + "]");
		while(hashStoring.totalHashes.get() <= memoryHashes) {
			Hashes hash = new Hashes();
			ByteBuffer buffer = ByteBuffer.allocate(totalLength);
			buffer.put(this.hashStoring.getFingerprintb());
			buffer.put(this.hashStoring.getPublic_keyb());
			buffer.putInt(this.threadId);
			buffer.putLong(nonce);
			byte[] inputBytes = buffer.array();
			byte[] hashBytes = hasher.blakeInBytes(inputBytes);
			//log.logWithTimestamp("hashbytes: " + Base58.encode(hashBytes));
			hash.setHash(hashBytes);
			hash.setNonce(nonce);
			hash.setThreadId(this.threadId);
			hashes.addToHashes(hash);
			nonce++;
			hashStoring.totalHashes.incrementAndGet();
			//log.logWithTimestamp("hash " + Base58.encode(hashes.getHash()) + " nonce " + hashes.getNonce());
		}
		log.logWithTimestamp("finished generating hashes [Thread #" + this.threadId + "]");
		hashStoring.done.incrementAndGet();
	}

}
