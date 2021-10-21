package cn.sinso.DICOMNetwork.model;

import lombok.Data;

@Data
public class MailClient {

    private String smtpHost;
    private String smtpPort;
    private String smtpAuth;
    private String usercode;
    private String password;
    private String username;
    private String popHost;
    private String popPort;
    private String interfaceUrl;

    public MailClient() {
    }

    public MailClient(String smtpHost,
                          String smtpPort,
                          String smtpAuth,
                          String usercode,
                          String password,
                          String username,
                          String popHost,
                          String popPort,
                          String interfaceUrl
                      ) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpAuth = smtpAuth;
        this.usercode = usercode;
        this.password = password;
        this.username = username;
        this.popHost = popHost;
        this.popPort = popPort;
        this.interfaceUrl = interfaceUrl;
    }
}
