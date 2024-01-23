package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Map;

import org.bitcoinj.core.Base58;
import org.yaml.snakeyaml.Yaml;

public class WalletRead {
	
	static ConsoleLogger log = new ConsoleLogger();
	
	static String public_key;
	static byte[] public_keyb = new byte[32];
	static String private_key;
	static byte[] private_keyb = new byte[32];
	static String poolIp;
	static int poolPort;
	static String blockchainIp;
	static int blockchainPort;

	public static void read() {
		// TODO Auto-generated method stub
		try (FileInputStream input = new FileInputStream("dsc-config.yaml")) {
			Yaml yaml = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData = yaml.load(input);
			@SuppressWarnings("unchecked")
			Map<String, Object> wallet = (Map<String, Object>) yamlData.get("wallet");
			@SuppressWarnings("unchecked")
			Map<String, Object> pool = (Map<String, Object>) yamlData.get("pool");
			@SuppressWarnings("unchecked")
			Map<String, Object> blockchain = (Map<String, Object>) yamlData.get("blockchain");

			readPublicKey(yaml, yamlData, wallet);
			readPool(yaml, yamlData, pool);
			readBlockchain(yaml, yamlData, blockchain);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try (FileInputStream input1 = new FileInputStream("dsc-key.yaml")) {
			Yaml yaml1 = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData1 = yaml1.load(input1);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> wallet1 = (Map<String, Object>) yamlData1.get("wallet");
			
			readPrivateKey(yaml1, yamlData1, wallet1);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void readPrivateKey(Yaml yaml1, Map<String, Object> yamlData1, Map<String, Object> wallet1) {
		if (wallet1.containsKey("private_key")) {
			// Key is present in the YAML file
			Object value1 = wallet1.get("private_key");

			if (value1 != null) {
				// Key has a non-null value
				private_key = (String) value1;
				private_keyb = Base58.decode(private_key);
				//System.out.println("private_key is: " + private_key);
			} else {
				// Key is present but has a null value
				log.logWithTimestamp("Error in finding key information, ensure that dsc-config.yaml and dsckey.yaml exist and that they contain the correct information. You may need to run \"./dsc wallet create\"");
				System.exit(1);
			}
		} else {
			// Key is not present in the YAML file
			log.logWithTimestamp("Error in finding key information, ensure that dsc-config.yaml and dsckey.yaml exist and that they contain the correct information. You may need to run \"./dsc wallet create\"");
			System.exit(1);

		}
		
	}

	private static void readBlockchain(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> blockchain) {
		Object value1 = blockchain.get("server");
		Object value2 = blockchain.get("port");
		blockchainIp = (String) value1;
		blockchainPort = (int) value2;
		//System.out.println("blockIp is: " + blockchainIp + " and port is: " + blockchainPort);
		
	}

	private static void readPool(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> pool) {
		Object value1 = pool.get("server");
		Object value2 = pool.get("port");
		poolIp = (String) value1;
		poolPort = (int) value2;
		//System.out.println("poolIp is: " + poolIp + " and port is: " + poolPort);
	}

	private static void readPublicKey(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> wallet) {
		if (wallet.containsKey("public_key")) {
			// Key is present in the YAML file
			Object value = wallet.get("public_key");

			if (value != null) {
				// Key has a non-null value
				public_key = (String) value;
				public_keyb = Base58.decode(public_key);
				//System.out.println("public_key is: " + public_key);
			} else {
				// Key is present but has a null value
				log.logWithTimestamp("Error in finding key information, ensure that dsc-config.yaml and dsckey.yaml exist and that they contain the correct information. You may need to run \"./dsc wallet create\"");
				System.exit(1);
			}
		} else {
			// Key is not present in the YAML file
			log.logWithTimestamp("Error in finding key information, ensure that dsc-config.yaml and dsckey.yaml exist and that they contain the correct information. You may need to run \"./dsc wallet create\"");
			System.exit(1);

		}
		
	}

	public static void create() {
		try (FileInputStream input = new FileInputStream("dsc-config.yaml")) {
			Yaml yaml = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData = yaml.load(input);
			@SuppressWarnings("unchecked")
			Map<String, Object> wallet = (Map<String, Object>) yamlData.get("wallet");
		
			try (FileInputStream input1 = new FileInputStream("dsc-key.yaml")) {
				Yaml yaml1 = new Yaml();
				// Parse the YAML file into a Map
				Map<String, Object> yamlData1 = yaml1.load(input1);
				
				@SuppressWarnings("unchecked")
				Map<String, Object> wallet1 = (Map<String, Object>) yamlData1.get("wallet");
				
				createKeys(yaml, yaml1, yamlData, yamlData1, wallet, wallet1);
				
				try (FileWriter output1 = new FileWriter("dsc-key.yaml")) {
					yaml1.dump(yamlData1, output1);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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

	private static void createKeys(Yaml yaml, Yaml yaml1, Map<String, Object> yamlData, Map<String, Object> yamlData1,
			Map<String, Object> wallet, Map<String, Object> wallet1) {
		if (wallet.containsKey("public_key") && wallet1.containsKey("private_key")) {
			Object value = wallet.get("public_key");
			Object value1 = wallet1.get("private_key");
			
			if (value != null && value1 != null) {
				log.logWithTimestamp(" Wallet already exists at dsc-key.yaml, wallet create aborted");
				System.exit(1);
			}else {
				try {
					generateKyes();
					log.logWithTimestamp("DSC Public Address: " + public_key);
					log.logWithTimestamp("DSC Private Address: " + private_key);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wallet.put("public_key", public_key);
				wallet1.put("private_key", private_key);
				log.logWithTimestamp("Saved public key to dsc-config.yaml and private key to dsc-key.yaml in local folder");
			}
			
		}
		
		
		
	}

	private static void generateKyes() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");

        // Initialize the key pair generator with a specific elliptic curve and key size
        // Example curve: secp256r1 (also known as P-256)
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        try {
			keyPairGen.initialize(ecSpec);
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Generate the key pair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Retrieve the public and private keys
        byte[] publicKey1 = keyPair.getPublic().getEncoded();
        byte[] privateKey1 = keyPair.getPrivate().getEncoded();
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        byte[] publicKey2 = digest.digest(publicKey1);
        byte[] privateKey2 = digest.digest(privateKey1);
        
        public_keyb = publicKey2;
        private_keyb = privateKey2;
        
        public_key = Base58.encode(public_keyb);
        private_key = Base58.encode(private_keyb);
		
	}
	
	public static String getPublic_key() {
        return public_key;
    }

    public static byte[] getPublic_keyb() {
        return public_keyb;
    }

    public static String getPrivate_key() {
        return private_key;
    }

    public static byte[] getPrivate_keyb() {
        return private_keyb;
    }

    public static String getPoolIp() {
        return poolIp;
    }

    public static int getPoolPort() {
        return poolPort;
    }

    public static String getBlockchainIp() {
        return blockchainIp;
    }

    public static int getBlockchainPort() {
        return blockchainPort;
    }


}
