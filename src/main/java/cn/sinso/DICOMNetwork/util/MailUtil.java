//package cn.sinso.DICOMNetwork.util;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.mail.EmailAttachment;
//import org.apache.commons.mail.EmailException;
//import org.apache.commons.mail.HtmlEmail;
//import org.apache.commons.mail.SimpleEmail;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Maps;
//import MailConfig;
//
///**
// *
// * <b>Description:</b>发送邮件工具类<br>
// */
//public class MailUtil {
//    private static Logger log = LoggerFactory.getLogger(MailUtil.class);
//
//    private static String userName = MailConfig.getUserName();
//    private static String passWord = MailConfig.getPassWord();
//    private static String smtpHost = MailConfig.getSmtpHost();
//    private static String fromEmail = MailConfig.getFromEmail();
//    private static String charset = MailConfig.getCharset();
//    private static String senderName = MailConfig.getSenderName();
//    private static String encryptionType = MailConfig.getEncryptionType();
//    private static int mailPort = Integer.valueOf(MailConfig.getMailPort());
//
//    /**
//     * <b>Description:</b>发送Html邮件<br>
//     *
//     * @param to 收件人
//     * @param cc 抄送
//     * @param list
//     * @param subject 主题
//     * @param htmlContent 内容
//     */
//    public static void sendHtmlMailWithoutSSL(Map<String, String> to, Map<String, String> cc, String subject, String htmlContent) {
//
//        HtmlEmail email = new HtmlEmail();
//
//        email.setCharset(MailUtil.charset);
//        email.setHostName(smtpHost);
//        email.setAuthentication(userName, passWord);
//        email.setSubject(subject);
//
//        email.setSSL(true);
//        email.setSmtpPort(mailPort);
//        try {
//            email.setFrom(fromEmail);
//
//            if (to != null) {
//                for (Map.Entry<String, String> map : to.entrySet()) {
//                    email.addTo(map.getValue(), map.getKey());
//                }
//            }
//
//            if (cc != null) {
//                for (Map.Entry<String, String> map : cc.entrySet()) {
//                    email.addCc(map.getValue(), map.getKey());
//                }
//            }
//
//            if (list != null) {
//                for (EmailAttachment attachment : list) {
//                    email.attach(attachment);
//                }
//            }
//
//            email.setMsg(htmlContent);
//
//            email.send();
//        } catch (EmailException e) {
//            log.error("$$$ Error sending mail, catch a EmailException !userName:" + userName);
//            e.printStackTrace();
//        }
//        log.debug("$$$ Send email successful!");
//    }
//}
//
