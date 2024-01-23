package com.DataSysCoinv1;

import java.io.Serializable;

public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] senderPublicAddress = new byte[32];
	private byte[] recipientPublicAddress = new byte[32];
	private double value;
	private long timestamp;
	private byte[] transactionID = new byte[16];
	private byte[] signature = new byte[32];
	
	public byte[] getSenderPublicAddress() {
        return senderPublicAddress;
    }

    public void setSenderPublicAddress(byte[] senderPublicAddress) {
        this.senderPublicAddress = senderPublicAddress;
    }

    // Getters and Setters for recipientPublicAddress
    public byte[] getRecipientPublicAddress() {
        return recipientPublicAddress;
    }

    public void setRecipientPublicAddress(byte[] recipientPublicAddress) {
        this.recipientPublicAddress = recipientPublicAddress;
    }

    // Getters and Setters for value
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    // Getters and Setters for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and Setters for transactionID
    public byte[] getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(byte[] transactionID) {
        this.transactionID = transactionID;
    }

    // Getters and Setters for signature
    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

}
