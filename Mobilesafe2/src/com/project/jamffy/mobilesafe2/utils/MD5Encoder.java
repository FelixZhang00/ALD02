package com.project.jamffy.mobilesafe2.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {

	public static String encode(String pwd)  {
		// 获取以某种算法加密的加密器
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(pwd.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < bs.length; i++) {
				String s = Integer.toHexString(0xff & bs[i]); //将字节信息转换为16进制的整数
				if (s.length() == 1) {
					buffer.append("0" + s);
				} else {
					buffer.append(s);
				}
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("MD5的加密算法很好找，这个错不太会发生");
		}

	}
}
