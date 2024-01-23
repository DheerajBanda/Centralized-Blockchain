package com.DataSysCoinv1;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;

import org.bitcoinj.core.Base58;

public class Validator {

	static ConsoleLogger log = new ConsoleLogger();
	static Blake32 hasher1 = new Blake32();

	public static void start() {
		log.logWithTimestamp("DSC v1.0");
		ValidatorRead.read(); // read config file and get the parameters.

		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.rmi.server.hostname", currentIp());
			ValidatorInterface validatorImpl = new ValidatorImpl();
			Registry registry = LocateRegistry.createRegistry(ValidatorRead.getValidatorPort());
			registry.rebind("Validator", validatorImpl);

			// which proof you want to implement.(get from ValidatorRead)
			int proof = ValidatorRead.getProof();
			if (proof == 1) {
				int result = register();
				if (result == 1) {
					log.logWithTimestamp("Validator Registered");
				}else {
					log.logWithTimestamp("error while registering");
				}
				int powThreads = ValidatorRead.getPowthreads();
				log.logWithTimestamp("Proof of Work (" + powThreads + "-threads)");
				log.logWithTimestamp("Fingerprint: " + ValidatorRead.getFingerprint());
				short diff = 0;
				int blockId = 0;
				byte[] prevHash = new byte[32];
				HashInput input = new HashInput(0, diff, 0, ValidatorRead.getFingerprintb(), ValidatorRead.public_keyb,
						prevHash);
				String ipAddress = null;
				try {
					ipAddress = currentIp();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int port = ValidatorRead.getValidatorPort();
				input.setIpAddress(ipAddress);
				input.setPort(port);
				Thread thread = new Thread(new ProofOfWork(powThreads, input));
				thread.start();
				input.done.set(powThreads);
				AtomicInteger signal = new AtomicInteger(1);

				while (validatorImpl.getSignal() != signal.get()) {

				}
				validatorImpl.setSignal(0);
				Block block = new Block();
				block = getBlock();
				blockId = block.getBlockId();
				prevHash = calculateHash(block);
				input.setBlockId(++blockId);
				// log.logWithTimestamp("hash from blockchain is " + Base58.encode(prevHash));
				diff = validatorImpl.getDiff();
				// log.logWithTimestamp("diff from metronome is " + diff);
				input.setPrevHash(prevHash);
				// System.out.println("diff: " + (short) diff);
				input.setDiff(diff);
				input.setNonce(-1);
				input.totalHashes.set(0);
				input.done.set(0);
				log.logWithTimestamp("block " + input.getBlockId() + ", diff " + diff + ", hash "
						+ hasher1.diffZeroAndOne(prevHash, diff));
				input.ready.set(input.ready.incrementAndGet());
				while (true) {
					if (input.done.get() == powThreads) {

						log.logWithTimestamp("block " + input.getBlockId() + ", NONCE " + input.getNonce() + "("
								+ ((float) input.totalHashes.get() / 6000000) + " MH/s)");

						if (input.found.get() == true) {
							int approved = sendProof(input);
							if (approved == 0) {
								//System.out.println("create block before " + validatorImpl.getCreateBlock());
								while (validatorImpl.getCreateBlock() == -1) {

								}
								if (validatorImpl.getCreateBlock() == 1) {
									log.logWithTimestamp("You get to create block");
									createBlock(input);
								} else if (validatorImpl.getCreateBlock() == 0) {
									log.logWithTimestamp("You do not get to create block");
								}
								validatorImpl.setCreateBlock(-1);
							}
						}
						while (validatorImpl.getSignal() != signal.get()) {

						}
						validatorImpl.setSignal(0);
						Block block1 = new Block();
						block1 = getBlock();
						blockId = block1.getBlockId();
						prevHash = calculateHash(block1);
						input.setBlockId(++blockId);
						// log.logWithTimestamp("hash from blockchain is " + Base58.encode(prevHash));
						diff = validatorImpl.getDiff();
						// log.logWithTimestamp("diff from metronome is " + diff);
						input.setPrevHash(prevHash);
						diff = validatorImpl.getDiff();
						input.setDiff(diff);
						input.found.set(false);
						input.setNonce(-1);
						input.totalHashes.set(0);
						input.done.set(0);
						log.logWithTimestamp("block " + input.getBlockId() + ", diff " + diff + ", hash "
								+ hasher1.diffZeroAndOne(prevHash, diff));
						input.ready.set(input.ready.incrementAndGet());
					}

				}

			} else if (proof == 2) {
				int pomThreads = ValidatorRead.getPomthreads();
				int memory = ValidatorRead.getMemory();
				// log.logWithTimestamp("Need to be implemented" + pomThreads + memory);
				log.logWithTimestamp("Proof of Memory (" + pomThreads + "-threads, " + memory + "GB RAM)");
				log.logWithTimestamp("Fingerprint: " + ValidatorRead.getFingerprint());

				long start = System.currentTimeMillis();
				HashStoring hashStoring = new HashStoring();
				hashStoring.setFingerprintb(ValidatorRead.getFingerprintb());
				hashStoring.setPublic_keyb(ValidatorRead.getPublic_keyb());
				try {
					hashStoring.setIpAddress(currentIp());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				hashStoring.setPort(ValidatorRead.getValidatorPort());
				hashStoring.memoryHashes.set(calculateMemoryHashes(memory));

				HashesList hashes = new HashesList();

				Thread thread = new Thread(new ProofOfMemory(pomThreads, hashes, hashStoring));
				thread.start();
				log.logWithTimestamp("gen/org " + memory + "GB hashes using " + pomThreads + " passes");

				while (hashStoring.done.get() < pomThreads) {

				}

				ArrayList<Hashes> hashes1 = new ArrayList<>();
				hashes1 = hashes.getList();

				log.logWithTimestamp("sorting hashes");
				sortHashesByHash(hashes1);
				log.logWithTimestamp("finished sorting hashes");

				long end = System.currentTimeMillis();
				long total = end - start;
				log.logWithTimestamp("gen/org 1GB hashes (" + (double) (total/1000) + " sec)");
				HashInput input = new HashInput();
				String ipAddress = null;
				try {
					ipAddress = currentIp();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int port = ValidatorRead.getValidatorPort();
				input.setIpAddress(ipAddress);
				input.setPort(port);
				input.setFingerprintb(ValidatorRead.getFingerprintb());
				input.setPublic_keyb(ValidatorRead.getPublic_keyb());
				short diff = 0;
				int blockId = 0;
				byte[] prevHash = new byte[32];
				int result = register();
				if (result == 1) {
					log.logWithTimestamp("Validator Registered");
				}else {
					log.logWithTimestamp("error while registering");
				}

				AtomicInteger signal = new AtomicInteger(1);

				while (true) {
					while (validatorImpl.getSignal() != signal.get()) {

					}
					validatorImpl.setSignal(0);
					Block block = new Block();
					block = getBlock();
					blockId = block.getBlockId();
					prevHash = calculateHash(block);
					input.setBlockId(++blockId);
					// log.logWithTimestamp("hash from blockchain is " + Base58.encode(prevHash));
					diff = validatorImpl.getDiff();
					// log.logWithTimestamp("diff from metronome is " + diff);
					input.setPrevHash(prevHash);
					// System.out.println("diff: " + (short) diff);
					input.setDiff(diff);
					input.setNonce(-1);
					input.setPrevHash(prevHash);
					log.logWithTimestamp("block " + input.getBlockId() + ", diff " + diff + ", hash "
							+ hasher1.diffZeroAndOne(prevHash, diff));
					long startTime = System.currentTimeMillis();
					int searchIndex = binarySearchFirstNBits(hashes1, input.getPrevHash(), diff, startTime + 6000); 

					if (searchIndex != -1) {
						Hashes hashFound = new Hashes();
						System.out.println("Key found at index: " + searchIndex);
						hashFound = hashes1.get(searchIndex);
						log.logWithTimestamp("hashFound " + Base58.encode(hashFound.getHash()) + " nonce " + hashFound.getNonce() + " threadId " + hashFound.getThreadId());
						//input.setPrevHash(hashesFound.getHash());
						long nonce = hashFound.getNonce();
						input.setNonce(nonce);
						int threadId = hashFound.getThreadId();
						input.setThreadId(threadId);
						//System.out.println("Hash " + Base58.encode(hashFound.getHash()) + " Nonce is " + hashFound.getNonce() + " in string " + hasher1.diffZeroAndOne(hashFound.getHash(), diff));
						int approved = sendProof(input);
						log.logWithTimestamp("input " + input.getNonce() + " nonce " +nonce + " threadId " + input.getThreadId());
						log.logWithTimestamp("prev hash " + Base58.encode(input.getPrevHash()) + " fingerprint " + Base58.encode(input.getFingerprintb()) + " publicKey " + Base58.encode(input.getPublic_keyb()));
						if (approved == 0) {
							while (validatorImpl.getCreateBlock() == -1) {

							}
							if (validatorImpl.getCreateBlock() == 1) {
								log.logWithTimestamp("You get to create block");
								createBlock(input);
							} else if (validatorImpl.getCreateBlock() == 0) {
								log.logWithTimestamp("You do not get to create block");
							}
							validatorImpl.setCreateBlock(-1);
						}
					} else {
						System.out.println("Key not found within the time limit");
					}
				}

			} else if (proof == 3) {
				// to be implemented in future if there is time.
			} else {
				log.logWithTimestamp("Invalid Config File - Aborted");
				System.exit(1);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static int binarySearchFirstNBits(ArrayList<Hashes> hashes1, byte[] prevHash, short diff, long endTime) {
		// TODO Auto-generated method stub
		int left = 0;
		int right = hashes1.size() - 1;
		while (left <= right && System.currentTimeMillis() <= endTime) {
			int mid = left + (right - left) / 2;
			int cmp = compareFirstNBits(hashes1.get(mid).getHash(), prevHash, diff);

			if (cmp == 0) {
				return mid; // Key found
			} else if (cmp < 0) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}
		return -1;
	}

	private static int compareFirstNBits(byte[] bs, byte[] prevHash, short diff) {
		// TODO Auto-generated method stub
		int mask = (1 << diff) - 1; // Create a mask with 'numBits' set to 1

		// Extract first 'numBits' of byte array 'a'
		int firstNBitsA = extractFirstNBits(bs, diff, mask);

		// Extract first 'numBits' of byte array 'b'
		int firstNBitsB = extractFirstNBits(prevHash, diff, mask);

		// Compare the extracted bits
		return Integer.compare(firstNBitsA, firstNBitsB);
	}

	private static int extractFirstNBits(byte[] bs, short diff, int mask) {
		// TODO Auto-generated method stub
		int result = 0;
		for (int i = 0; i < diff; i++) {
			int byteIndex = i / 8;
			int bitIndex = 7 - (i % 8);

			int bitValue = (bs[byteIndex] >> bitIndex) & 1;
			result |= bitValue << (diff - 1 - i);
		}
		return result & mask;
	}

	public static void sortHashesByHash(ArrayList<Hashes> hashesList) {
		Collections.sort(hashesList, new HashesComparator());
	}

	private static class HashesComparator implements Comparator<Hashes> {
		@Override
		public int compare(Hashes h1, Hashes h2) {
			byte[] hash1 = h1.getHash();
			byte[] hash2 = h2.getHash();

			// Custom comparison logic for byte[] arrays
			for (int i = 0; i < hash1.length && i < hash2.length; i++) {
				int cmp = Byte.compare(hash1[i], hash2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}

			return Integer.compare(hash1.length, hash2.length);
		}
	}

	private static long calculateMemoryHashes(int memory) {
		// TODO Auto-generated method stub
		long hashes = (long) ((memory * 1024 * 1024 * 1024) / 56);
		return hashes;
	}

	private static void createBlock(HashInput input) {
		// TODO Auto-generated method stub
		ArrayList<Transaction> transactions = new ArrayList<>();
		transactions = getTransactions();
		int blockSize = 0;
		int counter = 0;
		if (transactions != null) {
			//transactions = checkTransactions(transactions);
			counter = transactions.size();
			blockSize = calculateBlockSize(counter);
		} else {
			blockSize = 128;
		}
		log.logWithTimestamp(" Retrieved " + counter + " transactions from pool");
		Block block = new Block();
		block.setBlockId(input.getBlockId());
		block.setVersion((short) 1);
		block.setCounter(counter);
		block.setBlockSize(blockSize);
		block.setDiff(input.getDiff());
		block.setNonce(input.getNonce());
		block.setPrevHash(input.getPrevHash());
		block.setTransactions(transactions);
		block.setTimestamp(System.currentTimeMillis());
		sendBlock(block);
	}

	private static void sendBlock(Block block) {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getBlockchainIp(),
					ValidatorRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");

			byte[] hash = new byte[32];
			hash = calculateHash(block);
			log.logWithTimestamp("New block #" + block.getBlockId() + " created with " + block.getCounter()
					+ " transactions, hash " + Base58.encode(hash));

			blockImpl.sendBlockValidator(block, ValidatorRead.getPublic_keyb());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int calculateBlockSize(int counter) {
		// TODO Auto-generated method stub
		int size = 128 + (128 * counter);
		return size;
	}

	/*private static ArrayList<Transaction> checkTransactions(ArrayList<Transaction> transactions) {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getBlockchainIp(),
					ValidatorRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");

			for (Transaction transaction : transactions) {
				// log.logWithTimestamp("transactionId and transaction recipeient is " +
				// Base58.encode(transaction.getTransactionID()) + " " +
				// Base58.encode(transaction.getRecipientPublicAddress()));
				if (transaction.getSenderPublicAddress() != null) {
					Balance balance = blockImpl.getBalance(transaction.getSenderPublicAddress());
					double value = transaction.getValue();
					if (value > balance.getBalance()) {
						transactions.remove(transaction);
						//System.out.println("transaction id " + Base58.encode(transaction.getTransactionID()) + " removed");
					}
				}
			}

			return transactions;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}*/

	private static ArrayList<Transaction> getTransactions() {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getPoolIp(), ValidatorRead.getPoolPort());
			PoolKeyInterface poolImpl = (PoolKeyInterface) registry.lookup("Pool");

			ArrayList<Transaction> transactions = new ArrayList<>();
			transactions = poolImpl.sendTransactionsValidator(ValidatorRead.getPublic_keyb());

			return transactions;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] calculateHash(Block block) {
		// TODO Auto-generated method stub
		byte[] inputPrevHash = block.toString().getBytes();
		byte[] hash = hasher1.blakeInBytes(inputPrevHash);
		return hash;
	}

	private static int sendProof(HashInput input) {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getMetronomeIp(),
					ValidatorRead.getMetronomePort());
			MetronomeInterface metronomeImpl = (MetronomeInterface) registry.lookup("Metronome");

			int approved = metronomeImpl.sendProof(input);
			return approved;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static int register() {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getMetronomeIp(),
					ValidatorRead.getMetronomePort());
			MetronomeInterface metronomeImpl = (MetronomeInterface) registry.lookup("Metronome");

			int result;
			result = metronomeImpl.register(currentIp(), ValidatorRead.getValidatorPort(),
					ValidatorRead.getPublic_keyb());

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static Block getBlock() {
		try {
			Registry registry = LocateRegistry.getRegistry(ValidatorRead.getBlockchainIp(),
					ValidatorRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");

			Block block = new Block();
			block = blockImpl.getBlockIdAndHash(ValidatorRead.public_keyb);
			return block;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String currentIp() throws UnknownHostException {
		String currentIP = InetAddress.getLocalHost().getHostAddress(); // Always server starts on the current host
		if (currentIP.startsWith("127."))
			try {
				currentIP = getLocalIpv4Address();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Unable to get IP Address of Validator");
			}
		return currentIP;
	}

	public static String getLocalIpv4Address() throws Exception {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface iface = interfaces.nextElement();
			if (iface.isLoopback() || !iface.isUp())
				continue;

			Enumeration<InetAddress> addresses = iface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();

				if (addr.isLoopbackAddress() || !(addr.getHostAddress().contains(".")))
					continue; // This filters out IPv6 addresses

				return addr.getHostAddress();
			}
		}
		return null;
	}

}
