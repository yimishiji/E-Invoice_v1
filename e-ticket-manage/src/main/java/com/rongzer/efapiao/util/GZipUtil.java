package com.rongzer.efapiao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * 将一个字符串按照zip方式压缩和解压缩 Created by he.bing on 2017/2/5.
 */
public class GZipUtil {

	// 压缩
	public static byte[] compress(byte[] inputByte) throws IOException {
		if (inputByte == null || inputByte.length == 0) {
			return inputByte;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(inputByte);
		gzip.close();
		return out.toByteArray();
	}

	// 解压缩
	public static byte[] uncompress(byte[] inputByte) throws IOException {
		if (inputByte == null || inputByte.length == 0) {
			return inputByte;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(inputByte);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
		return out.toByteArray();
	}

	// 测试方法
	public static void main(String[] args) throws IOException {

		// 测试字符串
		String str = "{\"aid\":\"rongzer_test\",\"s\":\"1234567890\",\"d\":{\"tn\":\"123456\",\"tt\":\"2017-02-05 12:12:12\",\"sn\":2,\"pn\":\"2\",\"ta\":18,\"eid\":3,\"en\":\"he.bing\",\"st\":1,\"i\":[{\"ic\":5,\"iq\":1,\"ia\":20,\"iad\":18,\"di\":[{\"ic\":3,\"iq\":1,\"ia\":-2}]}],\"pi\":[{\"ic\":1,\"iq\":1,\"ia\":10},{\"ic\":2,\"iq\":1,\"ia\":8}]}}";
		// 压缩
		byte[] compressStr = GZipUtil.compress(str.getBytes("UTF-8"));
		byte[] base64CompressStr = Base64.encodeBase64URLSafe(compressStr);
		String base64Str = new String(base64CompressStr, "UTF-8");
		// 解压缩
		byte[] decode64CommpressStr = Base64.decodeBase64(base64Str);
		byte[] uncompressStr = GZipUtil.uncompress(decode64CommpressStr);
		String decode64Str = new String(uncompressStr, "UTF-8");
		System.out.println("原字符：" + str);
		System.out.println("原长度：" + str.length());
		System.out.println("压缩字符：" + base64Str);
		System.out.println("压缩长度：" + base64Str.length());
		System.out.println("解压缩：" + decode64Str);
	}
}
