package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class PoolKeyRead {
	
	static String poolIp;
	static int poolPort;
	static String monitorIp;
	static int monitorPort;
	static int threads;
	
	public static void read() {
		
		try (FileInputStream input = new FileInputStream("dsc-config.yaml")) {
			Yaml yaml = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData = yaml.load(input);
			@SuppressWarnings("unchecked")
			Map<String, Object> pool = (Map<String, Object>) yamlData.get("pool");
			@SuppressWarnings("unchecked")
			Map<String, Object> monitor = (Map<String, Object>) yamlData.get("monitor");
			
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
		Object value = pool.get("server");
		Object value1 = pool.get("port");
		Object value2 = pool.get("threads");
		
		poolIp = (String)value;
		poolPort = (int) value1;
		threads = (int) value2;
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
    
    public static int getThreads() {
        return threads;
    }

}
