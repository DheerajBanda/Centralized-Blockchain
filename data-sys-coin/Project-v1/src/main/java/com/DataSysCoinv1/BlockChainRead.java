package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class BlockChainRead {
	
	static int threads;
	static int blockchainPort;
	static String metronomeIp;
	static int metronomePort;
	static String monitorIp;
	static int monitorPort;
	static String poolIp;
	static int poolPort;
	static String blockchainIp;
	

	public static void read() {
		
		try (FileInputStream input = new FileInputStream("dsc-config.yaml")) {
			Yaml yaml = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData = yaml.load(input);
			@SuppressWarnings("unchecked")
			Map<String, Object> blockchain = (Map<String, Object>) yamlData.get("blockchain");
			@SuppressWarnings("unchecked")
			Map<String, Object> metronome = (Map<String, Object>) yamlData.get("metronome");
			@SuppressWarnings("unchecked")
			Map<String, Object> monitor = (Map<String, Object>) yamlData.get("monitor");
			@SuppressWarnings("unchecked")
			Map<String, Object> pool = (Map<String, Object>) yamlData.get("pool");
			
			readBlockchain(yaml, yamlData, blockchain);
			readMetronome(yaml, yamlData, metronome);
			readMonitor(yaml, yamlData, monitor);
			readPool(yaml, yamlData, pool);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void readPool(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> pool) {
		// TODO Auto-generated method stub
		Object value = pool.get("server");
		Object value1 = pool.get("port");
		
		poolIp = (String)value;
		poolPort = (int) value1;
	}

	private static void readMonitor(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> monitor) {
		// TODO Auto-generated method stub
		Object value = monitor.get("server");
		Object value1 = monitor.get("port");
		
		monitorIp = (String)value;
		monitorPort = (int) value1;
	}

	private static void readMetronome(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> metronome) {
		// TODO Auto-generated method stub
		Object value = metronome.get("server");
		Object value1 = metronome.get("port");
		
		metronomeIp = (String)value;
		metronomePort = (int) value1;
	}

	private static void readBlockchain(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> blockchain) {
		Object value = blockchain.get("threads");
		Object value1 = blockchain.get("server");
		Object value2 = blockchain.get("port");
		
		threads = (int)value;
		blockchainIp = (String) value1;
		blockchainPort = (int) value2;
		
	}
	
	public static int getThreads() {
        return threads;
    }
	
	public static int getPort() {
		return blockchainPort;
	}
	
	// Getter for metronomeIp
    public static String getMetronomeIp() {
        return metronomeIp;
    }


    // Getter for metronomePort
    public static int getMetronomePort() {
        return metronomePort;
    }


    // Getter for monitorIp
    public static String getMonitorIp() {
        return monitorIp;
    }


    // Getter for monitorPort
    public static int getMonitorPort() {
        return monitorPort;
    }
    
 // Getter for poolIp
    public static String getPoolIp() {
        return poolIp;
    }

    // Getter for poolPort
    public static int getPoolPort() {
        return poolPort;
    }
    
 // Getter method
    public static String getBlockchainIp() {
        return blockchainIp;
    }


}
