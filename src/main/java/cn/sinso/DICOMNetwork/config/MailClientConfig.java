package cn.sinso.DICOMNetwork.config;

import cn.sinso.DICOMNetwork.ByteBaseClient;
import cn.sinso.DICOMNetwork.model.MailClient;
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
@ConfigurationProperties(prefix = "mail")
@Slf4j
public class MailClientConfig {
    private String smtpHost;
    private String smtpPort;
    private String smtpAuth;
    private String usercode;
    private String password;
    private String username;
    private String popHost;
    private String popPort;
    private String interfaceUrl;
    @Bean
    public MailClient mailClient() {
        if (smtpHost.equals("xxx")) {
            return new MailClient();
        }
        return new MailClient(smtpHost, smtpPort, smtpAuth, usercode, password,username,popHost,popPort,interfaceUrl);
    }
}
