package com.rongzer.efapiao.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.ServiceAbbreviations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

//import sajt.shdzfp.sl.service.Client;

/**
 * Created by Administrator on 2018/3/7.
 */
public class AmazonUploadTest {

    final static String bucketName = "invoice-hg/invoice";
    final static String keyId = "AKIAOWSMHYYTKK6UTB5Q";
    final static String keyValue = "VdSERUaORJsca81zPMOT+wXfIe4MODtKZH11a9CS";

    final static String filePath = "D:/abd.pdf";
    final static String fildId="006b5f2b26b747aba76c76b280a12ace";
    final static String filePathDownload = "D:/lx/abd.pdf";

    public static void main(String[] args) {
       // listFile(fildId, filePath);
      downloadFile(fildId, filePathDownload);
    }

    /**
     *
     * @param bizId
     * @param
     */
    public static void uploadFile(String bizId, String filePath) {
        if (bizId != "" && filePath != "") {
            // 更新数据状态，并保存文件和数据关系
            Map<String, Object> fileInfo = new HashMap<String, Object>();
            AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(
                    keyId, keyValue));
            try {
                Region region = Region.getRegion(Regions.CN_NORTH_1);
                s3Client.setRegion(region);
                final String serviceEndpoint = region
                        .getServiceEndpoint(ServiceAbbreviations.S3);
                s3Client.setEndpoint(serviceEndpoint);
                File fileUpload = new File(filePath);
//				byte[] getData = getDecodeBase64(fileUpload);
                //InputStream inWithCode = new ByteArrayInputStream(fileUpload);
                //ObjectMetadata metaData = new ObjectMetadata();
                //metaData.setContentType("pdf");
                s3Client.putObject(new PutObjectRequest(bucketName, bizId, fileUpload));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void listFile(String bizId, String filePath) {
        if (bizId != "" && filePath != "") {
            // 更新数据状态，并保存文件和数据关系
            Map<String, Object> fileInfo = new HashMap<String, Object>();
            AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(
                    keyId, keyValue));
            try {
                Region region = Region.getRegion(Regions.CN_NORTH_1);
                s3Client.setRegion(region);
                final String serviceEndpoint = region
                        .getServiceEndpoint(ServiceAbbreviations.S3);
                s3Client.setEndpoint(serviceEndpoint);
               System.out.println(s3Client.listBuckets());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void downloadFile(String bizId, String filePathDownload){
        if (bizId != "" && filePathDownload != "") {
            // 更新数据状态，并保存文件和数据关系
            Map<String, Object> fileInfo = new HashMap<String, Object>();
            AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(
                    keyId, keyValue));
            try {
                Region region = Region.getRegion(Regions.CN_NORTH_1);
                s3Client.setRegion(region);
                final String serviceEndpoint = region
                        .getServiceEndpoint(ServiceAbbreviations.S3);
                s3Client.setEndpoint(serviceEndpoint);

                S3Object object = s3Client.getObject(new GetObjectRequest(bucketName,bizId));
                S3ObjectInputStream objectContent = object.getObjectContent();
                File fileNew = new File(filePathDownload);
                if(!fileNew.exists()){
                    fileNew.createNewFile();
                }
                byte[] buffer = new byte[1024];
                int byteread = 0;
                // byteread表示一次读取到buffers中的数量。
                FileOutputStream fop = new FileOutputStream(fileNew);
                while ((byteread = objectContent.read(buffer)) != -1) {
                    fop.write(buffer,0,byteread);
                }
                fop.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
