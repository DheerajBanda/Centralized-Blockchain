package com.DataSysCoinv1;

import java.nio.ByteBuffer;

public class PowWorkerThread implements Runnable {
	int threadId;
	HashInput input;
	ConsoleLogger log = new ConsoleLogger();

	public PowWorkerThread(HashInput input, int threadId) {
		this.input = input;
		this.threadId = threadId;
	}

	@Override
	public void run() {
		int ready = 0;
		int totalLength = this.input.getFingerprintb().length + this.input.getPublic_keyb().length + Integer.BYTES
				+ Long.BYTES;
		/*ByteBuffer buffer = ByteBuffer.allocate(totalLength);
		buffer.put(this.input.getFingerprintb());
		buffer.put(this.input.getPublic_keyb());
		buffer.putInt(this.threadId);*/
		Blake32 hasher = new Blake32();
		while (true) {
			while (ready == this.input.ready.get()) {
				continue;
			}
			ready = this.input.ready.get();
			boolean found = false;
			long nonce = 0;
			long startTime = System.currentTimeMillis();
			long duration = 6000;
			while (System.currentTimeMillis() - startTime < duration && this.input.found.get() == false) {
				ByteBuffer buffer = ByteBuffer.allocate(totalLength);
				buffer.put(this.input.getFingerprintb());
				buffer.put(this.input.getPublic_keyb());
				buffer.putInt(this.threadId);
				buffer.putLong(nonce);
				byte[] inputBytes = buffer.array();
				byte[] hashBytes = hasher.blakeInBytes(inputBytes);
				//System.out.println(hasher.diffZeroAndOne(hashBytes, 2));
				found = compareFirstBits(hashBytes, this.input.getPrevHash(), input.getDiff());
				if(found) {
					this.input.found.set(found);
					this.input.setNonce(nonce);
					this.input.setThreadId(this.threadId);
				}else {
					nonce++;
				}

			}
			//log.logWithTimestamp("Thread ID: " + this.threadId + ", nonce: " + nonce);
			this.input.totalHashes.set(this.input.totalHashes.get()+nonce);
			this.input.done.incrementAndGet();
		}
	}

	private static boolean compareFirstBits(byte[] byteArray1, byte[] byteArray2, int bitsToRead) {
		int bytesToRead = bitsToRead / 8;
		int remainingBits = bitsToRead % 8;

		// Compare whole bytes first
		for (int i = 0; i < bytesToRead; i++) {
			if (byteArray1[i] != byteArray2[i]) {
				return false;
			}
		}

		// Compare remaining bits if any
		if (remainingBits > 0) {
			int index1 = bytesToRead;
			int index2 = bytesToRead;

			for (int i = 0; i < remainingBits; i++) {
				int bit1 = (byteArray1[index1] >> (7 - i)) & 1;
				int bit2 = (byteArray2[index2] >> (7 - i)) & 1;

				if (bit1 != bit2) {
					return false;
				}
			}
		}
		return true;
	}

}
