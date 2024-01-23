package com.DataSysCoinv1;

import java.nio.ByteBuffer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bitcoinj.core.Base58;

public class MetronomeImpl extends UnicastRemoteObject implements MetronomeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ConsoleLogger log = new ConsoleLogger();
	Blake32 hasher1 = new Blake32();
	private double reward = 2048;
	private short diff = 30;
	public AtomicInteger ready = new AtomicInteger(0);
	private byte[] hash = new byte[32];
	private List<Register> register = new CopyOnWriteArrayList<>();
	private List<HashInput> proofs = new CopyOnWriteArrayList<>();
	private int blockId = 1;
	private String createdBy;
	private Block block = new Block();

	protected MetronomeImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public short getDiff() throws RemoteException {
		// TODO Auto-generated method stub
		return diff;
	}

	@Override
	public int register(String ipaddr, int port, byte[] publicKey) throws RemoteException {
		// TODO Auto-generated method stub
		Register reg = new Register();
		reg.setIpAddress(ipaddr);
		reg.setPort(port);
		reg.setPublicKeyb(publicKey);
		register.add(reg);
		sendRegisterMonitor(reg);
		log.logWithTimestamp("Validator " + Base58.encode(publicKey) + " added to register with ip address " + ipaddr
				+ " and port " + port);
		return 1;
	}

	private void sendRegisterMonitor(Register reg) {
		// TODO Auto-generated method stub
		try {
			// log.logWithTimestamp("hashInput is " + hashInput.getIpAddress() + " and " +
			// hashInput.getPort());
			Registry registry2 = LocateRegistry.getRegistry(MetronomeRead.getMonitorIp(),
					MetronomeRead.getMonitorPort());
			MonitorInterface monitorImpl = (MonitorInterface) registry2.lookup("Monitor");

			monitorImpl.receiveRegisterInfo(reg);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void blockCreated(Block block, byte[] hash, String publicKeyb) throws RemoteException {
		// TODO Auto-generated method stub
		log.logWithTimestamp(
				"block " + block.getBlockId() + " created with hash " + Base58.encode(hash) + " by " + publicKeyb);
		this.createdBy = publicKeyb;
		this.blockId = block.getBlockId() + 1;
		this.block = block;
		this.hash = hash;
		if (reward > 1 && block.getBlockId() % 10 == 0) {
			this.reward = (double) this.reward / 2;
			log.logWithTimestamp("reward at block " + block.getBlockId() + " is " + reward);
		}
		ready.set(1);
	}

	@Override
	public int sendProof(HashInput input) throws RemoteException {
		// TODO Auto-generated method stub
		byte[] hashBytes = calculateHash(input);
		log.logWithTimestamp("Validator " + Base58.encode(input.getPublic_keyb()) + " claims to win block with diff "
				+ input.getDiff() + " hash " + hasher1.diffZeroAndOne(hashBytes, input.getDiff()) + " NONCE "
				+ input.getNonce());
		boolean found = false;
		//log.logWithTimestamp("prev hash " + Base58.encode(input.getPrevHash()) + " fingerprint "
			//+ Base58.encode(input.getFingerprintb()) + " publicKey " + Base58.encode(input.getPublic_keyb()));
		found = compareFirstBits(hashBytes, input.getPrevHash(), input.getDiff());
		if (found) {
			proofs.add(input);
			//int i = 0;
			/*for(HashInput proof: proofs) {
				log.logWithTimestamp("validator proof list " + i + " fingerprint " + Base58.encode(proof.getFingerprintb()) + " port " + proof.getPort());
			}*/
			log.logWithTimestamp("Validator " + Base58.encode(input.getPublic_keyb()) + " request approved");
			return 0;
		}
		log.logWithTimestamp("Validator " + Base58.encode(input.getPublic_keyb()) + " request not approved");
		return -1;
	}

	private boolean compareFirstBits(byte[] byteArray1, byte[] byteArray2, int bitsToRead) {
		// TODO Auto-generated method stub
		int bytesToRead = bitsToRead / 8;
		int remainingBits = bitsToRead % 8;

		// Compare whole bytes first
		for (int i = 0; i < bytesToRead; i++) {
			if (byteArray1[i] != byteArray2[i]) {
				return false;
			}
		}

		// Compare remaining bits if any
		if (remainingBits > 0) {
			int index1 = bytesToRead;
			int index2 = bytesToRead;

			for (int i = 0; i < remainingBits; i++) {
				int bit1 = (byteArray1[index1] >> (7 - i)) & 1;
				int bit2 = (byteArray2[index2] >> (7 - i)) & 1;

				if (bit1 != bit2) {
					return false;
				}
			}
		}
		return true;
	}

	private byte[] calculateHash(HashInput input) {
		// TODO Auto-generated method stub
		int totalLength = input.getFingerprintb().length + input.getPublic_keyb().length + Integer.BYTES + Long.BYTES;
		ByteBuffer buffer = ByteBuffer.allocate(totalLength);
		buffer.put(input.getFingerprintb());
		buffer.put(input.getPublic_keyb());
		buffer.putInt(input.getThreadId());
		buffer.putLong(input.getNonce());
		byte[] inputBytes = buffer.array();
		byte[] hashBytes = hasher1.blakeInBytes(inputBytes);
		return hashBytes;
	}

	@Override
	public void sendSignal() {
		if (!register.isEmpty()) {
			for (Register reg : register) {
				try {
					Registry registry1 = LocateRegistry.getRegistry(reg.getIpAddress(), reg.getPort());
					ValidatorInterface validatorImpl = (ValidatorInterface) registry1.lookup("Validator");

					validatorImpl.sendSignal(this.diff);
					// log.logWithTimestamp("signal to start sent to " + reg.getIpAddress() + " port
					// is " + reg.getPort() + " publickey is " +
					// Base58.encode(reg.getPublicKeyb()));

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void sendCreateSignals() {
		// Check if the proofs list is not empty
		if (!proofs.isEmpty()) {
			// Get the first element of the list
			HashInput firstElement = proofs.get(0);

			// Sending a different signal to the first element
			sendDifferentSignal(firstElement);

			// Sending a different signal to the rest of the elements
			for (int i = 1; i < proofs.size(); i++) {
				HashInput element = proofs.get(i);
				sendAnotherSignal(element);
			}
		}
	}

	private void sendDifferentSignal(HashInput hashInput) {
		try {
			// log.logWithTimestamp("hashInput is " + hashInput.getIpAddress() + " and " +
			// hashInput.getPort());
			Registry registry1 = LocateRegistry.getRegistry(hashInput.getIpAddress(), hashInput.getPort());
			ValidatorInterface validatorImpl = (ValidatorInterface) registry1.lookup("Validator");

			validatorImpl.createBlockSignal(1, proofs, reward);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendAnotherSignal(HashInput hashInput) {
		try {
			Registry registry1 = LocateRegistry.getRegistry(hashInput.getIpAddress(), hashInput.getPort());
			ValidatorInterface validatorImpl = (ValidatorInterface) registry1.lookup("Validator");

			validatorImpl.createBlockSignal(0, proofs, reward);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setDiff() {
		if (diff > 1) {
			if (!proofs.isEmpty()) {
				if (proofs.size() >= 4) {
					diff++;
				}
			} else {
				diff--;
			}
		}
	}

	@Override
	public int proofSize() {
		return proofs.size();
	}

	@Override
	public void clearProofs() {
		if (!proofs.isEmpty()) {
			proofs.clear();
		}
	}

	@Override
	public int getReady() {
		return ready.get();
	}

	@Override
	public void setReady(int value) {
		ready.set(value);
	}

	@Override
	public int getBlockId() {
		return this.blockId;
	}

	@Override
	public byte[] getHash() {
		return this.hash;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public double getReward() {
		return reward;
	}

	@Override
	public void setReward(double reward) {
		this.reward = reward;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public void setBlock(Block block) {
		this.block = block;
	}

	@Override
	public List<HashInput> getProofs() {
		return proofs;
	}

}
