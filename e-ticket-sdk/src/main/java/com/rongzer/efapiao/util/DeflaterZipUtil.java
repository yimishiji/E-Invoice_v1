package com.rongzer.efapiao.util;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DeflaterZipUtil {

	public static byte[] compress(byte[] inputByte) throws IOException {
		int len = 0;
		// 设置Deflater压缩级别，1为速度最快，9为压缩率最高
		Deflater defl = new Deflater(9, true);
		defl.setInput(inputByte);
		defl.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] outputByte = new byte[1024];
		try {
			while (!defl.finished()) {
				len = defl.deflate(outputByte);
				bos.write(outputByte, 0, len);
			}
			defl.end();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	public static byte[] uncompress(byte[] inputByte) throws IOException {
		int len = 0;
		Inflater infl = new Inflater(true);
		infl.setInput(inputByte);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] outByte = new byte[1024];
		try {
			while (!infl.finished()) {
				len = infl.inflate(outByte);
				if (len == 0) {
					break;
				}
				bos.write(outByte, 0, len);
			}
			infl.end();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	// 测试方法
	public static void main(String[] args) throws IOException {
		// 测试字符串
		String str = "{\"aid\":\"rongzer_test\",\"s\":\"1234567890\",\"d\":{\"tn\":\"123456\",\"tt\":\"2017-02-05 12:12:12\",\"sn\":2,\"pn\":\"2\",\"ta\":18,\"eid\":3,\"en\":\"he.bing\",\"st\":1,\"i\":[{\"ic\":5,\"iq\":1,\"ia\":20,\"iad\":18,\"di\":[{\"ic\":3,\"iq\":1,\"ia\":-2}]}],\"pi\":[{\"ic\":1,\"iq\":1,\"ia\":10},{\"ic\":2,\"iq\":1,\"ia\":8}]}}";
		// 压缩
		byte[] compressStr = DeflaterZipUtil.compress(str.getBytes("UTF-8"));
		byte[] base64CompressStr = Base64.encodeBase64URLSafe(compressStr);
		String base64Str = new String(base64CompressStr, "UTF-8");
		// 解压缩
		byte[] decode64CommpressStr = Base64.decodeBase64(base64Str);
		byte[] uncompressStr = DeflaterZipUtil.uncompress(decode64CommpressStr);
		String decode64Str = new String(uncompressStr, "UTF-8");
		System.out.println("原字符：" + str);
		System.out.println("原长度：" + str.length());
		System.out.println("压缩字符：" + base64Str);
		System.out.println("压缩长度：" + base64Str.length());
		System.out.println("解压缩：" + decode64Str);
	}

}
