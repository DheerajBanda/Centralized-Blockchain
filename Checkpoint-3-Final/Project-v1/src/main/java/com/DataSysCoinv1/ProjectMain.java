package com.DataSysCoinv1;
public class ProjectMain {

	public static void main(String[] args) {
		String input = args[0];
		if (input.equalsIgnoreCase("help")) {
			System.out.println("DataSys Coin Blockchain v1.0 Help menu, supported commands:\n"
					+ "help\nwallet\nblockchain\npool key\nmetronome\nvalidator\nmonitor");
		} else if (input.equals("wallet")) {
			String walletInput = args[1];
			if (walletInput.equalsIgnoreCase("help")) {
				System.out.println("DataSys Coin Blockchain v1.0 Help menu for wallet, supported commands:\n"
						+ "wallet help\nwallet create\nwallet key\nwallet balance\nwallet send <amount> <address>\nwallet transaction <ID>");
			} else if (walletInput.equalsIgnoreCase("create")) {
				Wallet.createWallet();
			} else if (walletInput.equalsIgnoreCase("key")) {
				Wallet.getKeys();
			} else if (walletInput.equalsIgnoreCase("balance")) {
				Wallet.getBalance();
			} else if (walletInput.equalsIgnoreCase("send")) {
				double amount = Double.valueOf(args[2]);
				String receipientAddress = args[3];
				Wallet.send(amount, receipientAddress);
			} else if (walletInput.equalsIgnoreCase("transactions")) {
				Wallet.transactions();
			} else if (walletInput.equalsIgnoreCase("transaction")) {
				String transactionId = args[2];
				Wallet.transaction(transactionId);
			} else if (walletInput.equalsIgnoreCase("latency")) {
				int numOfTransactions = Integer.valueOf(args[2]);
				double amount = Double.valueOf(args[3]);
				String receipientAddress = args[4];
				Wallet.latencyExperiment(numOfTransactions, amount, receipientAddress);
			}else if (walletInput.equalsIgnoreCase("throughput")) {
				int numOfTransactions = Integer.valueOf(args[2]);
				double amount = Double.valueOf(args[3]);
				String receipientAddress = args[4];
				Wallet.throughputExperiment(numOfTransactions, amount, receipientAddress);
			}
		} else if (input.equals("blockchain")) {
			BlockChain.start();

		} else if (input.equals("validator")) {
			Validator.start();

		} else if (input.equals("pool")) {
			PoolKey.start();

		} else if (input.equals("metronome")) {
			Metronome.start();

		} else if(input.equals("monitor")) {
			Monitor.start();
		}

	}

}
