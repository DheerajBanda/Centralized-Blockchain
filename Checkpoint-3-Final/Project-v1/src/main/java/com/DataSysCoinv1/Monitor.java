package com.DataSysCoinv1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor {

	static ConsoleLogger log = new ConsoleLogger();

	public static void start() {

		MonitorRead.read();

		log.logWithTimestamp("DSC v1.0");
		log.logWithTimestamp("Monitor started");
		AtomicInteger blockId = new AtomicInteger(1);

		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.rmi.server.hostname", MonitorRead.getMonitorIp());
			MonitorInterface monitorImpl = new MonitorImpl();
			Registry registry = LocateRegistry.createRegistry(MonitorRead.getMonitorPort());
			registry.rebind("Monitor", monitorImpl);

			while (true) {
				while(blockId.get() <= monitorImpl.getBlockId()) {
					Block block = new Block();
					block = monitorImpl.getBlockAtIndex(blockId.get()-1);
					ArrayList<Transaction> transactions = new ArrayList<>();
					transactions = block.getTransactions();
					if(transactions != null) {
						if(!transactions.isEmpty()) {
							sendBlock(block);
						}
					}
					blockId.incrementAndGet();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void sendBlock(Block block) {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(MonitorRead.getPoolIp(), MonitorRead.getPoolPort());
			PoolKeyInterface poolKeyImpl = (PoolKeyInterface) registry.lookup("Pool");
			
			poolKeyImpl.receiveBlock(block);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
