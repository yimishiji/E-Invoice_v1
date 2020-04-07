package com.rongzer.efapiao.util;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

/**
 * url生成共通 Created by he.bing on 2017/1/18.
 */
public class UrlGenerateUtil {

	public static String generateUrlByGzip(String webUrl, String json, String appId, String appSecret) {
		String url = "";
		try {
			// 使用gzip方法对json体进行压缩
			byte[] gZipJson = GZipUtil.compress(json.getBytes("UTF-8"));
			String base64Str = Base64.encodeBase64URLSafeString(gZipJson);
			System.out.println("Base64_gzipJson:" + base64Str);
			// 生成sign参数
			String sign = HMAC.encryptHMAC(base64Str, appSecret);
			url = webUrl + "?t=" + base64Str + "&a=" + appId + "&s="+ java.net.URLEncoder.encode(sign, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public static String generateUrlByDeflater(String webUrl, String json, String appId, String appSecret) {
		String url = "";
		try {
			// 使用deflater方法对json体进行压缩
			byte[] compressBytes = DeflaterZipUtil.compress(json.getBytes("UTF-8"));
			String base64Str = Base64.encodeBase64URLSafeString(compressBytes);
			System.out.println("Base64_deflaterJson:" + base64Str);
			// 生成sign参数
			String sign = HMAC.encryptHMAC(base64Str, appSecret);
			url = webUrl + "?t=" + base64Str + "&a=" + appId + "&s=" + java.net.URLEncoder.encode(sign, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	// 测试方法
	public static void main(String[] args) throws IOException {
		String webUrl = "http://localhost:8080/efapiao/pc/cms/index/index.htm";
		String json = "{\"tn\":\"123456\",\"tt\":\"2017-02-05 12:12:12\",\"sn\":2,\"pn\":\"2\",\"ta\":18,\"eid\":3,\"en\":\"he.bing\",\"st\":1,\"ti\":[{\"ic\":5,\"iq\":1,\"ia\":20,\"iad\":18,\"di\":[{\"ic\":3,\"iq\":1,\"ia\":-2}]}],\"pi\":[{\"ic\":1,\"iq\":1,\"ia\":10},{\"ic\":2,\"iq\":1,\"ia\":8}]}";
		String appId = "001";
		String appSecret = "imjCkUsm+VVZNatR/fC/kldbhSkfT+fp5OYGqFnY5O1KMPoZwwxo6EJwSG04Y3ZLbbghLE0mWqfp";
		// 加密之后的URL
		String urlGZip = generateUrlByGzip(webUrl, json, appId, appSecret);
		System.out.println("GZIP:" + urlGZip);
		System.out.println("GZIP_len:" + urlGZip.length());
		String urlDeflater = generateUrlByDeflater(webUrl, json, appId, appSecret);
		System.out.println("Deflater:" + urlDeflater);
		System.out.println("Deflater_len:" + urlDeflater.length());
		
	}
}
