package com.xcellent.challenge;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Example Implementation of a CIDR.
 * 
 * @see "http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing"
 * 
 * @author stefan.majer@x-cellent.com
 * 
 */
public class CIDR {

	private InetAddress ipAddress;
	private Integer mask;

	private CIDR() {
	}

	private CIDR(InetAddress ipAddress, Integer mask) {
		this.ipAddress = ipAddress;
		this.mask = mask;
	}

	private static int maskToInt(String mask) throws Exception {		
			int number = 0;
			if (mask.indexOf(".") == -1)
				number = Integer.parseInt(mask);
			else {
				StringTokenizer st = new StringTokenizer(mask, ".");
				long temp = 0;
				for (int i = 0; i < 4; i++) {
					int tmp = Integer.parseInt(st.nextToken());
					if (tmp < 0 || tmp > 255)
						throw new Exception();
					temp = (temp << 8) | tmp;
				}
				if (st.hasMoreElements())
					throw new Exception();				
				
				number = 1;
				boolean ck = false;
				while (temp > 0) {
					if (ck) {
						if ((temp & 1) == 0)
							throw new Exception();
						number++;
					} else
						ck = ((temp & 1) == 1);
					temp = temp >> 1;
				}
				if (!ck)
					number--;
			}
			if (number > 32 || number < 0)
				throw new Exception();
			else
				return number;		
	}

	private static long ipToLong(byte[] ip) {
		long result = 0;		
		for (int i = 0; i < 4; i++) {
			long tmp = ip[i] & 0xFF;
			
			result = (result << 8) | tmp;
		}
		return result;
	}
	
	private static byte[] longToIP(long ip) {
		byte[] result = new byte[4];
		for (int i = 3; i >= 0; i--) {
			result[i] = (byte) (ip & 0xFF);
			ip >>= 8;
		}
		return result;
	}
	/**
	 * Create a new Instance of a CIDR from a given String in cidr form.
	 * 
	 * This is either in the form 1.2.3.4/24 or 1.2.3.4/255.255.255.0
	 * 
	 * @param cidrNotation
	 *            the input ipAddress/network in cidr notation.
	 * @return the CIDR instance, throws a appropriate Exception on malformed or
	 *         illegal input.
	 */
	public static CIDR of(String cidrNotation) {
		// throw new IllegalArgumentException("Please implement");
		StringTokenizer st = new StringTokenizer(cidrNotation, "/");
		try {
			InetAddress ip = InetAddress.getByName(st.nextToken());
			int mask = CIDR.maskToInt(st.nextToken());
			return CIDR.of(ip, mask);
		} catch (Exception e) {			
			//e.printStackTrace();
			throw new RuntimeException("invalid CIDR input");
		}
	}

	/**
	 * Create a new Instance from a InetAddress and a Mask.
	 * 
	 * @param inetAddress
	 *            the InetAddress
	 * @param mask
	 *            the mask
	 * @return a CIDR instance.
	 */
	public static CIDR of(InetAddress inetAddress, Integer mask) {
		// throw new IllegalArgumentException("Please implement");
		return new CIDR(inetAddress, mask);
	}

	/**
	 * @return the Address part of this CIDR.
	 */
	public String getAddress() {
		// throw new IllegalArgumentException("Please implement");
		return this.ipAddress.getHostAddress();
	}

	/**
	 * @return the Mask (0 - 32) of this CIDR.
	 */
	public Integer getMask() {
		// throw new IllegalArgumentException("Please implement");
		return this.mask;
	}

	/**
	 * @return the Network address of this CIDR
	 */
	public CIDR getNetwork() {
		//throw new IllegalArgumentException("Please implement");
		long ip = CIDR.ipToLong(this.ipAddress.getAddress());
		ip &= ((0xFFFFFFFFl >> (32 - this.mask)) << (32 - this.mask));
		try {
			return CIDR.of(InetAddress.getByAddress(CIDR.longToIP(ip)), this.mask);
		} catch (UnknownHostException e) {						
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the BroadCast Address of this CIDR
	 */
	public CIDR getBroadCast() {
		//throw new IllegalArgumentException("Please implement");
		long ip = CIDR.ipToLong(this.ipAddress.getAddress());
		ip |= ((0xFFFFFFFFl >> (this.mask)));
		try {
			return CIDR.of(InetAddress.getByAddress(CIDR.longToIP(ip)), this.mask);
		} catch (UnknownHostException e) {						
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the amount of Addresses available in this network.
	 */
	public Long getAddresses() {
		// throw new IllegalArgumentException("Please implement");
		return new Long((long) 1 << (32 - this.mask));
	}

	/**
	 * @return the next IP in this Network throws a Exception if there is no ip
	 *         left.
	 */
	public CIDR getNext() {
		//throw new IllegalArgumentException("Please implement");
		if (this.equals(this.getBroadCast()))
			throw new RuntimeException("there is no ip left");
		long ip = CIDR.ipToLong(this.ipAddress.getAddress());
		ip++;
		try {
			return CIDR.of(InetAddress.getByAddress(CIDR.longToIP(ip)), this.mask);
		} catch (UnknownHostException e) {						
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the previoud IP in this Network throws a Exception if there is no
	 *         ip left.
	 */
	public CIDR getPrevious() {
		//throw new IllegalArgumentException("Please implement");
		if (this.equals(this.getNetwork()))
			throw new RuntimeException("there is no ip left");
		long ip = CIDR.ipToLong(this.ipAddress.getAddress());
		ip--;
		try {
			return CIDR.of(InetAddress.getByAddress(CIDR.longToIP(ip)), this.mask);
		} catch (UnknownHostException e) {						
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Check if a other cidr is inside this cidr.
	 * 
	 * @param cidr
	 *            the cidr wich is checked against this cidr.
	 * @return true if the given cidr is inside this cidr, otherwise false.
	 */
	public boolean contains(CIDR cidr) {
		//throw new IllegalArgumentException("Please implement");
		//return this.getBroadCast().equals(cidr.getBroadCast()) && this.getNetwork().equals(cidr.getNetwork());
		long this_broadCastIp = CIDR.ipToLong(this.ipAddress.getAddress());
		long this_networkIp = this_broadCastIp;
		this_broadCastIp |= ((0xFFFFFFFFl >> (this.mask)));
		this_networkIp &= ((0xFFFFFFFFl >> (32 - this.mask)) << (32 - this.mask));
		
		long broadCastIp = CIDR.ipToLong(cidr.ipAddress.getAddress());
		long networkIp = broadCastIp;
		broadCastIp |= ((0xFFFFFFFFl >> (this.mask)));
		networkIp &= ((0xFFFFFFFFl >> (32 - this.mask)) << (32 - this.mask));
		
		return (broadCastIp >= this_networkIp) && (networkIp <= this_broadCastIp);
	}

	/**
	 * This returns a String representation of this CIDR with a appropriate
	 * formatting. So that a given CIDR for example 1.2.3.4/24 will return
	 * 1.2.3.0/24.
	 * 
	 * /32 CIDRs can ommit the trailing mask.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// throw new IllegalArgumentException("Please implement");
		if (this.mask == 32)
			return this.getAddress();
		return this.getAddress() + "/" + this.mask;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof CIDR) {
			CIDR tmp = (CIDR) obj;
			return this.mask.equals(tmp.mask)
					&& this.getAddress().compareTo(
							tmp.getAddress()) == 0;
		} else
			return false;
	}

}
