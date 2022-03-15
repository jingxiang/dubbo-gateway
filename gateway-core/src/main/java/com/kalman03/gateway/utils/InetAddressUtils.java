package com.kalman03.gateway.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public class InetAddressUtils {
	
	public static String getLocalIP() {
		try {

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress address;
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					address = addresses.nextElement();
					if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
						return address.getHostAddress();
					}
				}
			}
			return "";
		} catch (Throwable t) {
			return "";
		}

	}
}
