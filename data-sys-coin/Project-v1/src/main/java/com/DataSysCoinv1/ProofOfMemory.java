package com.DataSysCoinv1;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProofOfMemory implements Runnable {

	HashesList hashes = new HashesList();
	int pomThreads;
	ConsoleLogger log = new ConsoleLogger();
	private HashStoring hashStoring;
	
	public ProofOfMemory(int pomThreads, HashesList hashes, HashStoring hashStoring) {
		this.pomThreads = pomThreads;
		this.hashes = hashes;
		this.hashStoring = hashStoring;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.pomThreads; i++) {
            Thread thread = new Thread(new PomWorkerThread(hashes, i, hashStoring)); // Pass i as threadId
            thread.start();
        }

	}

}
