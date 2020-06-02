package service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import entity.OssLocalBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.util.Date;

public class OssService {

    @Autowired
    private OssLocalBean ossLocalBean;

    /**
     * 文件形式上传
     *
     * @param key
     * @param file
     * @return
     */
    public String upload(String key, File file) {
        if (ossLocalBean.getEnable()) {
            OSS ossClient = null;
            try {
                ossClient = getClient();
                ossClient.putObject(ossLocalBean.getBucketName(), key, file);
                Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
                URL url = ossClient.generatePresignedUrl(ossLocalBean.getBucketName(), key, expiration);
                return ossLocalBean.getProtocol() + url.getHost() + url.getPath();
            } finally {
                ossClient.shutdown();
            }
        }
        return null;
    }

    /**
     * 流式上传
     *
     * @param key
     * @param inputStream
     * @return
     */
    public String upload(String key, InputStream inputStream) {
        if (ossLocalBean.getEnable()) {
            OSS ossClient = null;
            try {
                ossClient = getClient();
                ossClient.putObject(ossLocalBean.getBucketName(), key, inputStream);
                Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
                URL url = ossClient.generatePresignedUrl(ossLocalBean.getBucketName(), key, expiration);
                return ossLocalBean.getProtocol() + url.getHost() + url.getPath();
            } finally {
                ossClient.shutdown();
            }
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param key
     * @return
     * @throws IOException
     */
    public BufferedReader download(String key) throws IOException {
        if (ossLocalBean.getEnable()) {
            OSS ossClient = null;
            BufferedReader reader;
            try {
                ossClient = getClient();
                OSSObject ossObject = ossClient.getObject(ossLocalBean.getBucketName(), key);
                reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                }
                reader.close();
                return reader;
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        return null;
    }

    /**
     * 文件是否存在
     *
     * @param key
     */
    public boolean exist(String key) {
        if (ossLocalBean.getEnable()) {
            OSS ossClient = null;
            try {
                ossClient = getClient();
                return ossClient.doesObjectExist(ossLocalBean.getBucketName(), key);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param key 文件key
     */
    public void delete(String key) {
        if (ossLocalBean.getEnable()) {
            OSS ossClient = null;
            try {
                ossClient = getClient();
                ossClient.deleteObject(ossLocalBean.getBucketName(), key);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
    }

    /**
     * 获取 oss Client
     *
     * @return OSS
     */
    private OSS getClient() {
        return new OSSClientBuilder()
                .build(ossLocalBean.getEndpoint(), ossLocalBean.getAccessKeyId(), ossLocalBean.getAccessKeySecret());
    }
}
