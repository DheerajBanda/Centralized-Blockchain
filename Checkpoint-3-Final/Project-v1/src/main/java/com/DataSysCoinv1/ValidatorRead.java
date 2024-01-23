package com.DataSysCoinv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.util.Map;
import java.util.UUID;

import org.yaml.snakeyaml.Yaml;
import org.bitcoinj.core.Base58;

public class ValidatorRead {
	
	static boolean pow = false;
	static int powthreads = 0;
	static boolean pom = false;
	static int pomthreads = 0;
	static String valueWithSuffix;
	static int memory;
	static boolean pos = false;
	static int posthreads = 0;
	static String fingerprint;
	static String public_key;
	static byte[] fingerprintb;
	static byte[] public_keyb;
	static String metronomeIp;
	static String blockchainIp;
	static String poolIp;
	static int metronomePort;
	static int blockchainPort;
	static int poolPort;
	static String validatorIp;
	static int validatorPort;
	

	public static void read() {
		
		
		try (FileInputStream input = new FileInputStream("dsc-config.yaml")) {
			Yaml yaml = new Yaml();
			// Parse the YAML file into a Map
			Map<String, Object> yamlData = yaml.load(input);
			@SuppressWarnings("unchecked")
			Map<String, Object> validator = (Map<String, Object>) yamlData.get("validator");
			@SuppressWarnings("unchecked")
			Map<String, Object> blockchain = (Map<String, Object>) yamlData.get("blockchain");
			@SuppressWarnings("unchecked")
			Map<String, Object> metronome = (Map<String, Object>) yamlData.get("metronome");
			@SuppressWarnings("unchecked")
			Map<String, Object> pool = (Map<String, Object>) yamlData.get("pool");

			readFingerprint(yaml, yamlData, validator);
			readProofs(yaml, yamlData, validator);
			readPublickey(yaml, yamlData, validator);
			readValidatorPort(yaml, yamlData, validator);
			getBytes(fingerprint, public_key);
			readBlockchain(yaml, yamlData, blockchain);
			readMetronome(yaml, yamlData, metronome);
			readPool(yaml, yamlData, pool);

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
	
	private static void readValidatorPort(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> validator) {
		// TODO Auto-generated method stub
		Object value1 = validator.get("port");
		Object value2 = validator.get("server");
		validatorPort = (int) value1;
		validatorIp = (String) value2;
	}

	private static void readPool(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> pool) {
		// TODO Auto-generated method stub
		Object value1 = pool.get("server");
		Object value2 = pool.get("port");
		poolIp = (String) value1;
		poolPort = (int) value2;
		
	}

	private static void readMetronome(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> metronome) {
		Object value1 = metronome.get("server");
		Object value2 = metronome.get("port");
		metronomeIp = (String) value1;
		metronomePort = (int) value2;
		
	}

	private static void readBlockchain(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> blockchain) {
		Object value1 = blockchain.get("server");
		Object value2 = blockchain.get("port");
		blockchainIp = (String) value1;
		blockchainPort = (int) value2;
		
	}

	private static void getBytes(String fingerprint2, String public_key2) {
		// TODO Auto-generated method stub
		
	}

	private static void readPublickey(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> validator) throws NoSuchAlgorithmException {
		if (validator.containsKey("public_key")) {
			// Key is present in the YAML file
			Object value = validator.get("public_key");

			if (value != null) {
				// Key has a non-null value
				public_key = (String) value;
				public_keyb = Base58.decode(public_key);
			} else {
				// Key is present but has a null value
				public_key = createPublic_key();
				public_keyb = Base58.decode(public_key);
				validator.put("public_key", public_key);
			}
		} else {
			// Key is not present in the YAML file
			public_key = createPublic_key();
			public_keyb = Base58.decode(public_key);
			validator.put("public_key", public_key);

		}

	}

	private static String createPublic_key() throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
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
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        byte[] publicKey2 = digest.digest(publicKey1);
        public_keyb = publicKey2;
        public_key = Base58.encode(public_keyb);
		return public_key;
	}

	@SuppressWarnings("unchecked")
	private static void readProofs(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> validator) {
		Map<String, Object> proof_pow = (Map<String, Object>) validator.get("proof_pow");
		pow = (boolean) proof_pow.get("enable");
		powthreads = (int) proof_pow.get("threads_hash");
		Map<String, Object> proof_pom = (Map<String, Object>) validator.get("proof_pom");
		pom = (boolean) proof_pom.get("enable");
		pomthreads = (int) proof_pom.get("threads_hash");
		valueWithSuffix = (String) proof_pom.get("memory");
		memory = extractInteger(valueWithSuffix);
		Map<String, Object> proof_pos = (Map<String, Object>) validator.get("proof_pos");
		pos = (boolean) proof_pos.get("enable");
		posthreads = (int) proof_pos.get("threads_hash");

	}

	private static void readFingerprint(Yaml yaml, Map<String, Object> yamlData, Map<String, Object> validator) {
		if (validator.containsKey("fingerprint")) {
			// Key is present in the YAML file
			Object value = validator.get("fingerprint");

			if (value != null) {
				// Key has a non-null value
				fingerprint = (String) value;
				fingerprintb = Base58.decode(fingerprint);
			} else {
				// Key is present but has a null value
				fingerprint = createUUID();
				fingerprintb = Base58.decode(fingerprint);
				validator.put("fingerprint", fingerprint);
			}
		} else {
			// Key is not present in the YAML file
			fingerprint = createUUID();
			fingerprintb = Base58.decode(fingerprint);
			validator.put("fingerprint", fingerprint);

		}

	}

	private static String createUUID() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.position(0);
        byte[] byteArray = new byte[bb.remaining()];
        bb.get(byteArray);
		String fingerprint = Base58.encode(byteArray);
		return fingerprint;

	}

	private static int extractInteger(String valueWithSuffix) {
		String numericPart = valueWithSuffix.replaceAll("[^0-9]", "");
		return Integer.parseInt(numericPart);
	}
	
	public static int getProof() {
		if(pow) {
			return 1;
		}else if(pom) {
			return 2;
		}else if(pos) {
			return 3;
		}
		
		return 0;
	}

	public static boolean isPow() {
        return pow;
    }

    public static int getPowthreads() {
        return powthreads;
    }

    public static boolean isPom() {
        return pom;
    }

    public static int getPomthreads() {
        return pomthreads;
    }

    public static String getValueWithSuffix() {
        return valueWithSuffix;
    }

    public static int getMemory() {
        return memory;
    }

    public static boolean isPos() {
        return pos;
    }

    public static int getPosthreads() {
        return posthreads;
    }

    public static String getFingerprint() {
        return fingerprint;
    }

    public static String getPublickey() {
        return public_key;
    }
    
    public static byte[] getFingerprintb() {
        return fingerprintb;
    }

    public static byte[] getPublic_keyb() {
        return public_keyb;
    }
    
    public static String getMetronomeIp() {
        return metronomeIp;
    }

    public static String getBlockchainIp() {
        return blockchainIp;
    }

    public static int getMetronomePort() {
        return metronomePort;
    }

    public static int getBlockchainPort() {
        return blockchainPort;
    }
    
    // Getter for poolIp
    public static String getPoolIp() {
        return poolIp;
    }

    // Getter for poolPort
    public static int getPoolPort() {
        return poolPort;
    }
    
    public static int getValidatorPort() {
    	return validatorPort;
    }
    
    // Getter method
    public static String getValidatorIp() {
        return validatorIp;
    }


}
