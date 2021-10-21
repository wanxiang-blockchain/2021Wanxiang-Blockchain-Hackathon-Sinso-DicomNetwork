package cn.sinso.DICOMNetwork.config;

import cn.sinso.DICOMNetwork.ByteBaseClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 冰塔数据配置
 *
 * @author sum
 * @version 1.0.0
 * @date 2021/3/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytebase")
@Slf4j
public class ByteBaseClientConfig {
    private String accessKeyId;
    private String secretAccessKey;

    /**
     * 区域
     */
    private String location;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 冰塔Api域名
     */
    private String host;

    private String bucket;

    @Bean
    public ByteBaseClient byteBaseClient() {
        if (accessKeyId.equals("xxx")) {
            return new ByteBaseClient();
        }
        return new ByteBaseClient(accessKeyId, secretAccessKey, location, serviceType, host,bucket);
    }
}
