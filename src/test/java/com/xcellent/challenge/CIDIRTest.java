package com.xcellent.challenge;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This test class need to pass all its tests. Feel free to add/modify it to your need.
 * 
 * @author stefan.majer@x-cellent.com
 * 
 */
@Test
public class CIDIRTest {

	@Test
	public void testCidr() {
		CIDR localhost = CIDR.of("127.0.0.1/32");
		CIDR localhostNetwork = CIDR.of("127.0.0.0/8");
		CIDR privateNetwork = CIDR.of("192.168.0.0/16");
		CIDR privateIP = CIDR.of("192.168.1.1/32");

		Assert.assertEquals(localhost.getAddress(), "127.0.0.1");
		Assert.assertEquals(localhost.getMask().intValue(), 32);
		Assert.assertEquals(privateIP.getAddress(), "192.168.1.1");
		Assert.assertEquals(localhost.getNetwork(), CIDR.of("127.0.0.1/32"));
		
		Assert.assertEquals(localhostNetwork.getNext(), CIDR.of("127.0.0.1/8"));		
		
		CIDR test = CIDR.of("1.2.3.4/255.255.254.0");
		Assert.assertEquals(test.getAddress(), "1.2.3.4");
		Assert.assertEquals(test.getMask().intValue(), 23);
		Assert.assertEquals(localhostNetwork.getAddresses().longValue(), (long)16777216);
		Assert.assertEquals(test.getNext(), CIDR.of("1.2.3.5/23"));
		Assert.assertEquals(test.getPrevious(), CIDR.of("1.2.3.3/23"));		
		
		CIDR test2 = CIDR.of("255.255.255.254/0");
		Assert.assertEquals(test2.getAddress(), "255.255.255.254");
		Assert.assertEquals(test2.getMask().intValue(), 0);
		Assert.assertEquals(test2.getAddresses().longValue(), (long)4294967296L);
		Assert.assertEquals(test2.getBroadCast(), CIDR.of("255.255.255.255/0"));
		Assert.assertEquals(test2.getNext(), CIDR.of("255.255.255.255/0"));
		Assert.assertEquals(test2.getPrevious(), CIDR.of("255.255.255.253/0"));
		
		CIDR test3 = CIDR.of("10.10.1.44/27");
		Assert.assertEquals(test3.getNetwork(), CIDR.of("10.10.1.32/27"));
//		
		CIDR test4 = CIDR.of("255.255.2.4/32");
		Assert.assertEquals(test4.toString(), "255.255.2.4");
		Assert.assertEquals(test4.getBroadCast(), CIDR.of("255.255.2.4/32"));
		Assert.assertEquals(test4.getNetwork(), CIDR.of("255.255.2.4/32"));
		
		CIDR test5 = CIDR.of("12.200.0.255/255.128.0.0");
		Assert.assertEquals(test5.getAddress(), "12.200.0.255");
		Assert.assertEquals(test5.getMask().intValue(), 9);
		Assert.assertEquals(test5.getNetwork(), CIDR.of("12.128.0.0/9"));
		Assert.assertEquals(test5.getBroadCast(), CIDR.of("12.255.255.255/9"));
		Assert.assertEquals(test5.getNext(), CIDR.of("12.200.1.0/255.128.0.0"));
		Assert.assertEquals(test5.getPrevious(), CIDR.of("12.200.0.254/255.128.0.0"));
		
		Assert.assertEquals(test4.contains(test5), false);
		Assert.assertEquals(test5.contains(test4), false);
		
		CIDR test6 = CIDR.of("12.150.0.0/10");
		Assert.assertEquals(test6.contains(test5), false);
		Assert.assertEquals(test5.contains(test6), true);
		Assert.assertEquals(test6.contains(test6), true);
		Assert.assertEquals(test6.getPrevious(), CIDR.of("12.149.255.255/10"));
	}
}
