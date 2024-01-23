package com.DataSysCoinv1;

import java.nio.ByteBuffer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bitcoinj.core.Base58;

public class Wallet {
	static ConsoleLogger log = new ConsoleLogger();
	static float balance = 0;

	public static void createWallet() {
		log.logWithTimestamp("DSC v1.0");
		WalletRead.create();

	}

	public static void getKeys() {
		log.logWithTimestamp("DSC v1.0");
		log.logWithTimestamp("Reading dsc-config.yaml and dsc-key.yaml...");
		WalletRead.read();
		log.logWithTimestamp("DSC Public Address: " + WalletRead.getPublic_key());
		log.logWithTimestamp("DSC Private Address: " + WalletRead.getPrivate_key());
	}

	public static float getBalance() {
		log.logWithTimestamp("DSC v1.0");
		WalletRead.read();

		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(WalletRead.getBlockchainIp(), WalletRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");
			Balance balance = blockImpl.getBalance(WalletRead.getPublic_keyb());
			log.logWithTimestamp(
					"DSC Wallet balance: " + (float) balance.getBalance() + " coins at block" + balance.getBlockId());
			return (float) balance.getBalance();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (float) 0;

	}

	public static void send(double amount, String receipientAddress) {
		WalletRead.read();
		balance = Wallet.getBalance();
		Transaction transaction = new Transaction();
		String status = null;
		transaction = createTransaction(amount, receipientAddress);
		log.logWithTimestamp("Created transaction " + Base58.encode(transaction.getTransactionID()) + ", Sending "
				+ amount + " coins to " + receipientAddress);
		if(amount > balance) {
			log.logWithTimestamp("Invalid transaction. Value is more than balance. Balance " + balance);
			System.exit(1);
		}
		try {
			Registry registry = LocateRegistry.getRegistry(WalletRead.getPoolIp(), WalletRead.getPoolPort());
			PoolKeyInterface poolKeyImpl = (PoolKeyInterface) registry.lookup("Pool");
			poolKeyImpl.sendTransaction(transaction);
			log.logWithTimestamp("Transaction " + Base58.encode(transaction.getTransactionID()) + " submitted to pool");
			status = poolKeyImpl.getStatus(transaction.getTransactionID());
			log.logWithTimestamp(
					"Transaction " + Base58.encode(transaction.getTransactionID()) + " status [" + status + "]");
			while (status.compareTo("confirmed") != 0 && status.compareTo("invalid") != 0) {
				if (status.compareTo("unknown") == 0) {
					ArrayList<Transaction> transactions = new ArrayList<>();
					try {
						Registry registry1 = LocateRegistry.getRegistry(WalletRead.getBlockchainIp(),
								WalletRead.getBlockchainPort());
						BlockInterface blockImpl = (BlockInterface) registry1.lookup("Block");
						transactions = blockImpl.transactionList(WalletRead.getPublic_keyb());
						int i = 1;
						if (transactions != null) {
							if (!transactions.isEmpty()) {
								for (Transaction transaction1 : transactions) {
									if (Base58.encode(transaction1.getTransactionID())
											.compareTo(Base58.encode(transaction.getTransactionID())) == 0) {
										status = "confirmed";
									}
									i++;
								}
							} else {
								log.logWithTimestamp("No transactions found in blockcain");
							}
						} else {
							log.logWithTimestamp("No transactions found in blockcain");
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					status = poolKeyImpl.getStatus(transaction.getTransactionID());
					log.logWithTimestamp("Transaction " + Base58.encode(transaction.getTransactionID()) + " status ["
							+ status + "]");
				}
			}
			if (status.compareTo("confirmed") == 0) {
				log.logWithTimestamp("Transaction completed");
			} else if (status.compareTo("invalid") == 0) {
				log.logWithTimestamp("Transaction error");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Transaction createTransaction(double amount, String receipientAddress) {
		Transaction transaction = new Transaction();
		transaction.setTransactionID(createTransactionId());
		transaction.setRecipientPublicAddress(Base58.decode(receipientAddress));
		transaction.setSenderPublicAddress(WalletRead.getPublic_keyb());
		transaction.setValue(amount);
		transaction.setTimestamp(generateTimestamp());
		transaction.setSignature(generateSignature());
		return transaction;

	}

	private static byte[] generateSignature() {
		byte[] signature = new byte[32];
		return signature;
	}

	private static long generateTimestamp() {
		// TODO Auto-generated method stub
		return System.currentTimeMillis();
	}

	private static byte[] createTransactionId() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		bb.position(0);
		byte[] byteArray = new byte[bb.remaining()];
		bb.get(byteArray);
		return byteArray;
	}

	public static void transactions() {
		log.logWithTimestamp("DSC v1.0");
		WalletRead.read();
		ArrayList<Transaction> transactions = new ArrayList<>();
		try {
			Registry registry = LocateRegistry.getRegistry(WalletRead.getBlockchainIp(),
					WalletRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");
			transactions = blockImpl.transactionList(WalletRead.getPublic_keyb());
			int i = 1;
			if (transactions != null) {
				if (!transactions.isEmpty()) {
					for (Transaction transaction : transactions) {
						log.logWithTimestamp("Transaction #" + i + ": id="
								+ Base58.encode(transaction.getTransactionID()) + ", status=confirmed, timestamp="
								+ formatDate(transaction.getTimestamp()) + ", coin=" + transaction.getValue()
								+ ", source=" + Base58.encode(transaction.getSenderPublicAddress()) + ", destination="
								+ Base58.encode(transaction.getRecipientPublicAddress()));
						i++;
					}
				} else {
					log.logWithTimestamp("No transactions found in blockcain");
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void transaction(String transactionId) {
		// TODO Auto-generated method stub
		try {
			Registry registry1 = LocateRegistry.getRegistry(WalletRead.getPoolIp(), WalletRead.getPoolPort());
			PoolKeyInterface poolKeyImpl = (PoolKeyInterface) registry1.lookup("Pool");
			String status = poolKeyImpl.getStatus(Base58.decode(transactionId));
			if (status.compareTo("unknown") == 0) {
				log.logWithTimestamp("Transaction " + transactionId + " status [" + status + "]");
			} else {
				getFromBlock(transactionId);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void getFromBlock(String transactionId) {
		// TODO Auto-generated method stub
		ArrayList<Transaction> transactions = new ArrayList<>();
		try {
			Registry registry = LocateRegistry.getRegistry(WalletRead.getBlockchainIp(),
					WalletRead.getBlockchainPort());
			BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");
			transactions = blockImpl.transactionList(WalletRead.getPublic_keyb());
			int i = 0;
			if (transactions != null) {
				if (!transactions.isEmpty()) {
					for (Transaction transaction : transactions) {
						String transactionIdString = Base58.encode(transaction.getTransactionID());
						if (transactionId.compareTo(transactionIdString) == 0) {
							i = 1;
							log.logWithTimestamp("Transaction " + transactionId + " status [confirmed]");
						}
					}
				}
			}
			if (i == 0) {
				log.logWithTimestamp("transaction " + transactionId + " not found");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String formatDate(long currentTimeMillis) {
		Date date = new Date(currentTimeMillis);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static void latencyExperiment(int numOfTransactions, double amount, String receipientAddress) {

		log.logWithTimestamp("Latency Experiment, wallet " + WalletRead.getPublic_key());
		int i = 0;
		long start = System.currentTimeMillis();
		long end = 0;
		while (i < numOfTransactions) {
			send(amount, receipientAddress);
			i++;
		}
		end = System.currentTimeMillis();
		long totalTime = end - start;
		log.logWithTimestamp("Total time taken for wallet " + WalletRead.getPublic_key() + " to complete " + numOfTransactions + " is "
				+ (double) (totalTime / (double) 1000));


	}

	public static void throughputExperiment(int numOfTransactions, double amount, String receipientAddress) {

		log.logWithTimestamp("Throughput Experiment, wallet " + WalletRead.getPublic_key());
		WalletRead.read();
		int i = 0;
		long start = System.currentTimeMillis();
		long end = 0;
		//while (i < numOfTransactions) {
			// balance = Wallet.getBalance();
			// Transaction transaction = new Transaction();
			// transaction = createTransaction(amount, receipientAddress);
			// log.logWithTimestamp("Created transaction " +
			// Base58.encode(transaction.getTransactionID()) + ", Sending " + amount
			// + " coins to " + receipientAddress);

			try {
				Registry registry = LocateRegistry.getRegistry(WalletRead.getPoolIp(), WalletRead.getPoolPort());
				PoolKeyInterface poolKeyImpl = (PoolKeyInterface) registry.lookup("Pool");
				while (i < numOfTransactions) {
					Transaction transaction = new Transaction();
					transaction = createTransaction(amount, receipientAddress);
					poolKeyImpl.sendTransaction(transaction);
					//log.logWithTimestamp(
							//"Transaction " + Base58.encode(transaction.getTransactionID()) + " submitted to pool");
					if(i % 1000 == 0) {
						log.logWithTimestamp(i + "transactins sent to pool");
					}
					i++;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//i++;
		//}

		int size = 0;
		byte[] publicKeyb = WalletRead.getPublic_keyb();
		while (size != numOfTransactions) {
			ArrayList<Transaction> transactions = new ArrayList<>();
			try {
				Registry registry = LocateRegistry.getRegistry(WalletRead.getBlockchainIp(),
						WalletRead.getBlockchainPort());
				BlockInterface blockImpl = (BlockInterface) registry.lookup("Block");
				transactions = blockImpl.transactionList(publicKeyb);
				if (transactions != null) {
					size = transactions.size();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (size == numOfTransactions) {
			log.logWithTimestamp("Transactions with size " + size + " sent to blockchain");
		}

		end = System.currentTimeMillis();
		long totalTime = end - start;
		log.logWithTimestamp("Total time taken for wallet " + WalletRead.getPublic_key() + " is "
				+ (double) (totalTime / (double) 1000));

	}

}
