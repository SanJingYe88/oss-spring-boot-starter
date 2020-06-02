package entity;

import lombok.Data;

@Data
public class OssLocalBean {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private Boolean enable;

    private final String protocol = "http://";
}
