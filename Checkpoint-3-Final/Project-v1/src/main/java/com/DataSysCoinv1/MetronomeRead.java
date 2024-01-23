package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class MetronomeRead {
	
	static String blockchainIp;
	static String poolIp;
	static int blockchainPort;
	static int poolPort;
	static int threads;
	static String monitorIp;
	static int monitorPort;
	static String metronomeIp;
	static int metronomePort;
	

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
			Map<String, Object> pool = (Map<String, Object>) yamlData.get("pool");
			@SuppressWarnings("unchecked")
			Map<String, Object> monitor = (Map<String, Object>) yamlData.get("monitor");
			
			readBlockchain(yaml, yamlData, blockchain);
			readMetronome(yaml, yamlData, metronome);
			readPool(yaml, yamlData, pool);
			readMonitor(yaml, yamlData, monitor);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void readMonitor(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> monitor) {
		// TODO Auto-generated method stub
		Object value = monitor.get("server");
		Object value1 = monitor.get("port");
		
		monitorIp = (String)value;
		monitorPort = (int) value1;
	}

	private static void readPool(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> pool) {
		// TODO Auto-generated method stub
		Object value1 = pool.get("server");
		Object value2 = pool.get("port");
		poolIp = (String) value1;
		poolPort = (int) value2;
	}

	private static void readMetronome(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> metronome) {
		Object value1 = metronome.get("threads");
		Object value2 = metronome.get("server");
		Object value3 = metronome.get("port");
		threads = (int) value1;
		metronomeIp = (String) value2;
		metronomePort = (int) value3;
		
	}

	private static void readBlockchain(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> blockchain) {
		Object value1 = blockchain.get("server");
		Object value2 = blockchain.get("port");
		blockchainIp = (String) value1;
		blockchainPort = (int) value2;
		
	}
	
	public static String getBlockchainIp() {
        return blockchainIp;
    }

    public static int getBlockchainPort() {
        return blockchainPort;
    }

    public static int getThreads() {
        return threads;
    }
    
    // Getter for poolIp
    public static String getPoolIp() {
        return poolIp;
    }

    // Getter for poolPort
    public static int getPoolPort() {
        return poolPort;
    }
    
    // Getter for monitorIp
    public static String getMonitorIp() {
        return monitorIp;
    }

    // Getter for monitorPort
    public static int getMonitorPort() {
        return monitorPort;
    }
    
 // Getter methods
    public static String getMetronomeIp() {
        return metronomeIp;
    }

    public static int getMetronomePort() {
        return metronomePort;
    }

}
