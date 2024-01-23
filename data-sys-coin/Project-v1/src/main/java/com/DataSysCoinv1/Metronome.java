package com.DataSysCoinv1;

import java.nio.ByteBuffer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bitcoinj.core.Base58;

public class Metronome {

	// private static final String ALLOWED_CHARACTERS =
	// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	static byte[] reserved = new byte[64];
	static Blake32 hasher = new Blake32();
	static ConsoleLogger log = new ConsoleLogger();
	// static byte[] newHash = new byte[32];
	// static Block block = new Block(0, (short) 0, prevHash, 0, 0, (short) 0, 0, 0,
	// reserved);
	// static int totalLength = Integer.BYTES + Short.BYTES +
	// block.getPrevHash().length + Integer.BYTES + Long.BYTES
	// + Short.BYTES + Long.BYTES + Integer.BYTES + block.getReserved().length;

	public static void start() {

		log.logWithTimestamp("DSC v1.0");

		MetronomeRead.read();

		log.logWithTimestamp("Metronome started with " + MetronomeRead.getThreads() + " worker threads");

		AtomicInteger ready = new AtomicInteger(1);

		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.rmi.server.hostname", MetronomeRead.getMetronomeIp());
			MetronomeInterface metronomeImpl = new MetronomeImpl();
			Registry registry = LocateRegistry.createRegistry(MetronomeRead.getMetronomePort());
			registry.rebind("Metronome", metronomeImpl);

			while (true) {
				if (metronomeImpl.getReady() == ready.get()) {
					metronomeImpl.setReady(0);
					String createdBy = metronomeImpl.getCreatedBy();
					if (createdBy.compareTo("Metronome") != 0) {
						if (metronomeImpl.getReward() > 1) {
							List<HashInput> proofs = metronomeImpl.getProofs();
							double rewardForEach = (double) (metronomeImpl.getReward() / metronomeImpl.proofSize());
							ArrayList<Transaction> transactions = new ArrayList<>();
							for (HashInput proof : proofs) {
								Transaction transaction = new Transaction();
								transaction.setRecipientPublicAddress(proof.getPublic_keyb());
								transaction.setSenderPublicAddress(null);
								transaction.setValue(rewardForEach);
								transaction.setTransactionID(createTransactionId());
								transaction.setSignature(createSignature());
								transaction.setTimestamp(System.currentTimeMillis());
								transactions.add(transaction);
								//log.logWithTimestamp("reward " + rewardForEach + " added to " + Base58.encode(proof.getPublic_keyb()));
							}
							sendTransactionsToPool(transactions);
						} else {
							ArrayList<Transaction> transactions1 = new ArrayList<>();
							Block block = metronomeImpl.getBlock();
							transactions1 = block.getTransactions();
							double totalValue = 0.0;
							double valueToSend = 0.0;
							if (transactions1 != null) {
								for (Transaction transaction : transactions1) {
									totalValue = totalValue + transaction.getValue();
								}
							}
							valueToSend = (double) (totalValue * 0.01);
							ArrayList<Transaction> transactions = new ArrayList<>();
							Transaction transaction = new Transaction();
							transaction.setRecipientPublicAddress(Base58.decode(createdBy));
							transaction.setSenderPublicAddress(null);
							transaction.setValue(valueToSend);
							transaction.setTransactionID(createTransactionId());
							transaction.setSignature(createSignature());
							transaction.setTimestamp(System.currentTimeMillis());
							transactions.add(transaction);
							sendTransactionsToPool(transactions);
						}
					}
					metronomeImpl.setDiff();
					metronomeImpl.clearProofs();
					metronomeImpl.sendSignal();
					Thread.sleep(6000);
					if (metronomeImpl.proofSize() == 0) {
						int blockId = metronomeImpl.getBlockId();
						byte[] hash = metronomeImpl.getHash();
						short diff = metronomeImpl.getDiff();
						createBlock(blockId, hash, diff);
					} else {
						metronomeImpl.sendCreateSignals();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void sendTransactionsToPool(ArrayList<Transaction> transactions) {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(MetronomeRead.getPoolIp(),
					MetronomeRead.getPoolPort());
			PoolKeyInterface poolImpl = (PoolKeyInterface) registry.lookup("Pool");
			
			poolImpl.sendTransactionsMetronome(transactions);

			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static byte[] createSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	private static byte[] createTransactionId() {
		// TODO Auto-generated method stub
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.position(0);
        byte[] byteArray = new byte[bb.remaining()];
        bb.get(byteArray);
		return byteArray;
	}

	private static void createBlock(int blockId, byte[] hash, short diff) {

		Block block1 = new Block();
		block1.setBlockId(blockId);
		block1.setBlockSize(128);
		block1.setVersion((short) 1);
		block1.setPrevHash(hash);
		block1.setDiff(diff);
		block1.setNonce(-1);
		block1.setCounter(0);
		block1.setReserved(reserved);
		block1.setTimestamp(System.currentTimeMillis());

		try {
			Registry registry = LocateRegistry.getRegistry(MetronomeRead.getBlockchainIp(),
					MetronomeRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");

			blockImpl.sendBlock(block1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
