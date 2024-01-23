package com.DataSysCoinv1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolKey {
	static ConsoleLogger log = new ConsoleLogger();

	public static void start() {
		
		PoolKeyRead.read();

		log.logWithTimestamp("DSC v1.0");
		log.logWithTimestamp("Pool started with " + PoolKeyRead.getThreads() + " worker threads");
		AtomicInteger monitorSignal = new AtomicInteger(1);

		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.rmi.server.hostname", PoolKeyRead.getPoolIp());
			PoolKeyInterface poolKeyImpl = new PoolKeyImpl();
			Registry registry = LocateRegistry.createRegistry(PoolKeyRead.getPoolPort());
			registry.rebind("Pool", poolKeyImpl);
			
			while(true) {
				if(poolKeyImpl.monitorSignalGet() == monitorSignal.get()) {
					poolKeyImpl.monitorSignalSet(0);
					sendMonitorInfo(poolKeyImpl.unprocessedSize(), poolKeyImpl.unconfirmedSize());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void sendMonitorInfo(int unprocessedSize, int unconfirmedSize) {
		// TODO Auto-generated method stub
		try {
			//log.logWithTimestamp("hashInput is " + hashInput.getIpAddress() + " and " + hashInput.getPort());
			Registry registry = LocateRegistry.getRegistry(PoolKeyRead.getMonitorIp(), PoolKeyRead.getMonitorPort());
			MonitorInterface monitorImpl = (MonitorInterface) registry.lookup("Monitor");

			monitorImpl.receiveSize(unprocessedSize, unconfirmedSize);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
