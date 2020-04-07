package sajt.shdzfp.sl.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.axis2.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sajt.shdzfp.sl.model.GlobalInfo;
import sajt.shdzfp.sl.model.Interface;
import sajt.shdzfp.sl.model.ZData;
import sun.misc.BASE64Encoder;

public class Client {
	private static Log log=LogFactory.getLog(Client.class);

	//MD5加密
	public static String MD5(String sourceStr){
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return result;
	}
	
	// BASE64加密  
    public static String getBase64(String str) {  
    	String result = null;
        byte[] encodeBase64;
		try {
			encodeBase64 = Base64.encodeBase64(str.getBytes("UTF-8"));
			result = new String(encodeBase64);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return result;
    }  
    
    // BASE64解密  
    public static byte[] getDecodeBase64(String str) {  
		byte[] decodeBase64 = new byte[]{};
		try {
			decodeBase64 = Base64.decodeBase64(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return decodeBase64;
    }  
  
	/**
	 * 
	 * <p>
	 * Description:使用gzip进行解压缩
	 * </p>
	 * 
	 * @param compressedStr
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static byte[] gunzip(byte[] compressed) {
		if (compressed == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] decompressed = null;
		
		try {
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}
	
	 /**
     * ECB解密,不要IV
     * @param key 密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] ees3DecodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("Desede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
	
	//**调用接口service
	public Interface getFPService(GlobalInfo globalInfo, String content){
		Interface result = null;
		try {
			SajtIssueInvoiceServiceStub stud = new SajtIssueInvoiceServiceStub();
			
			Interface inf = new Interface();
			//外层报文封装
			inf.setGlobalInfo(globalInfo);
			
			ZData data = new ZData();
			String keyStr = globalInfo.getPassWord().substring(10);
			byte[] key=keyStr.getBytes();
			byte[] contentByte = content.getBytes("UTF-8");
			byte[] des3EncodeECB = Client.des3EncodeECB(key,contentByte);
			String encodeContent = new BASE64Encoder().encode(des3EncodeECB);
			//encodeContent = Client.getBase64(content);
			data.setContent(encodeContent);
			inf.setzData(data);
			
			//完整报文组合
			EiInterface request = new EiInterface();
			String param = "";
			try {  
	            JAXBContext context = JAXBContext.newInstance(Interface.class);  
	            Marshaller marshaller = context.createMarshaller();  
	            StringWriter sw = new StringWriter();
	            marshaller.marshal(inf, sw);  
	            String temp = sw.toString();
	            param = temp.replace("<zData>", "<Data>").replace("</zData>", "</Data>");
	            log.info("航信接口发送报文:\n"+param);
	        } catch (JAXBException e) {  
	            e.printStackTrace();  
	        } 
			request.setIn0(param);
			
			//请求接口
			EiInterfaceResponse response = stud.eiInterface(request);
			String xmlStr = response.get_return();
			log.info("航信接口返回报文:\n"+xmlStr);
	        try {
	        	result = getInterfaceByStr(xmlStr);
	        } catch (JAXBException e) {  
	            e.printStackTrace();  
	        }  
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}
	
	/**
     * ECB加密,不要IV
     * @param key 密钥
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("Desede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
	
	
	/**
	 * 根据xml转对象
	 * @param xmlStr
	 * @return
	 * @throws JAXBException
	 */
	public Interface getInterfaceByStr(String xmlStr) throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(Interface.class);  
        Unmarshaller unmarshaller = context.createUnmarshaller();  
        return (Interface)unmarshaller.unmarshal(new StringReader(xmlStr.replace("<Data>", "<zData>").replace("</Data>", "</zData>"))); 
	}
}
