package com.rongzer.efapiao.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2018/3/14.
 */
public class AesEncodeUtil {
    //初始向量
    public static final String VIPARA = "aabbccddeeffgghh";   //AES 为16bytes. DES 为8bytes

    //编码方式
    public static final String bm = "UTF-8";

    //私钥
    private static final String ASE_KEY="aabbccddeeffgghh";   //AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。


    /**
     * 加密
     *
     * @param cleartext
     * @return
     */
    public static String encrypt(String cleartext) {
        //加密方式： AES128(CBC/PKCS5Padding) + Base64, 私钥：aabbccddeeffgghh
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec key = new SecretKeySpec(ASE_KEY.getBytes(), "AES");
            //实例化加密类，参数为加密方式，要写全
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            //初始化，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            //加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));

            return new BASE64Encoder().encode(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     *
     * @param encrypted
     * @return
     */
    public static String decrypt(String encrypted) {
        try {
            byte[] byteMi = new BASE64Decoder().decodeBuffer(encrypted);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(
                    ASE_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //与加密时不同MODE:Cipher.DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, bm);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 测试
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        String content = "{\"transaction\":\n" +
                "    {\"transaction_num\":\"A0011712200881\",\"transaction_time\":\"2018-03-06 12:00:00\",\"store_no\":\"1\",\"total_amount\":\"1000\",\"fapiao_info\":{\"purchaser_name\":\"个人\",\"taxpayer_no\":\"\",\"address\":\"上海嘉定区0221\",\"email\":\"1204728512@qq.com\",\"tel\":\"021-5836757\",\"account\":\"62267559854412638\",\"bank\":\"招商银行\",\"mobile\":\"15026705343\",\"detail_type\":\"1\"},\"details\":[{\"item_code\":\"2001040016\",\"item_disamount\":\"800\",\"item_amount\":\"1000\",\"item_quantity\":\"10\",\"item_orderWeight\":\"0\"}],\"payments\":[{\"payment_quantity\":\"121\",\"payment_amount\":\"800\",\"payment_code\":\"HGZF2\"}]}\n" +
                "}\n";

        // 加密
        System.out.println("加密前：" + content);
        String encryptResult = encrypt(content);

        System.out.println("加密后：" + new String(encryptResult));
        // 解密
        String decryptResult = decrypt(encryptResult);
        System.out.println("解密后：" + new String(decryptResult));


    }
}
