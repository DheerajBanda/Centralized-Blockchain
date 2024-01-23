package com.DataSysCoinv1;

public class ProofOfWork implements Runnable{
	HashInput input;
	int powThreads;
	ConsoleLogger log = new ConsoleLogger();
	
	public ProofOfWork(int powThreads, HashInput input) {
		this.powThreads = powThreads;
		this.input = input;
	}

	@Override
    public void run() {
        for (int i = 0; i < this.powThreads; i++) {
            Thread thread = new Thread(new PowWorkerThread(input, i)); // Pass i as threadId
            thread.start();
        }
    }

}
