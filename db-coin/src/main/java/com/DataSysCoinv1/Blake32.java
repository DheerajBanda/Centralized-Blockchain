package com.DataSysCoinv1;
public class Blake32 {

	String input;

	public String blakeInZero(String input) {
		Blake32Algorithm hasher = Blake32Algorithm.newInstance();
		hasher.update(input.getBytes());
		byte[] hexhash = hasher.digest();
		return convertToZeroAndOne(hexhash);
	}

	public String blakeInHex(String input) {
		Blake32Algorithm hasher = Blake32Algorithm.newInstance();
		hasher.update(input.getBytes());
		String hexhash = hasher.hexdigest();
		return hexhash;
	}
	
	public byte[] blakeInSBytes(String input) {
		Blake32Algorithm hasher = Blake32Algorithm.newInstance();
		hasher.update(input.getBytes());
		byte[] hash = hasher.digest();
		return hash;
	}

	public byte[] blakeInBytes(byte[] bytes) {
		Blake32Algorithm hasher = Blake32Algorithm.newInstance();
		hasher.update(bytes);
		byte[] hash = hasher.digest();
		return hash;
	}
	
	public String diffZeroAndOne(byte[] bytes, int diff) {
		String fullString = convertToZeroAndOne(bytes);
		String extractedChars = fullString.substring(0, Math.min(diff, fullString.length()));
		return extractedChars;
	}

	public String convertToZeroAndOne(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(byteToBinaryString(b));
		}
		return sb.toString();
	}

	private String byteToBinaryString(byte b) {
		StringBuilder binaryStringBuilder = new StringBuilder(8); // Assuming 8 bits in a byte
		for (int i = 7; i >= 0; i--) {
			binaryStringBuilder.append((b & (1 << i)) != 0 ? '1' : '0');
		}
		return binaryStringBuilder.toString();
	}
}
