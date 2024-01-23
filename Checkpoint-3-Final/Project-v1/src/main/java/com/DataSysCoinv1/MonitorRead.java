package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class MonitorRead {

	private static String poolIp;
	private static int poolPort;
	private static String monitorIp;
	private static int monitorPort;

	public static void read() {
		// TODO Auto-generated method stub
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

			try (FileWriter output = new FileWriter("dsc-config.yaml")) {
				yaml.dump(yamlData, output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readMonitor(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> monitor) {
		// TODO Auto-generated method stub
		Object value1 = monitor.get("server");
		Object value2 = monitor.get("port");
		monitorIp = (String) value1;
		monitorPort = (int) value2;
	}

	private static void readPool(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> pool) {
		// TODO Auto-generated method stub
		Object value1 = pool.get("server");
		Object value2 = pool.get("port");
		poolIp = (String) value1;
		poolPort = (int) value2;
	}
	
	// Getter for poolIp
    public static String getPoolIp() {
        return poolIp;
    }

    // Getter for poolPort
    public static int getPoolPort() {
        return poolPort;
    }
    
    public static String getMonitorIp() {
        return monitorIp;
    }

    public static int getMonitorPort() {
        return monitorPort;
    }

}
