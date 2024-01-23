package com.DataSysCoinv1;

import java.io.Serializable;

class Register implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] publicKeyb;
    private String ipAddress;
    private int port;

    public void setIpAddress(String ipAddress) {
    	this.ipAddress = ipAddress;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setPort(int port) {
    	this.port = port;
    }

    public int getPort() {
        return port;
    }
    
    // Getter for publicKeyb
    public byte[] getPublicKeyb() {
        return publicKeyb;
    }

    // Setter for publicKeyb
    public void setPublicKeyb(byte[] publicKeyb) {
        this.publicKeyb = publicKeyb;
    }
}
