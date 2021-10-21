package cn.sinso.DICOMNetwork.util;


import cn.sinso.DICOMNetwork.model.MailClient;
import com.sun.mail.pop3.POP3Folder;
import org.hibernate.type.DateType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import javax.mail.Authenticator;

import javax.mail.internet.*;
import java.io.*;

public class EmailUtil {
    public static void main(String[] args) throws Exception {
        String md5File = FileSha256Util.getFileSha256(new File("/Users/zhoucong/go/src/hello/pcYe.zip"));
        System.out.println(md5File);
        String content = buildContent("", "", "", "");
        Multipart multipart = getMultipart(content, "", new ArrayList());
    }

    public static void getEmail(MailClient mailClient,Integer tokenExpireTime) throws Exception {
        String host=mailClient.getPopHost();
        String userCode=mailClient.getUsercode();
        String password=mailClient.getPassword();
        Properties props = new Properties();
        //设置邮件接收协议为pop3
        props.setProperty("mail.store.protocol", "pop3");
        props.setProperty("mail.pop3.host", host);
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");
        //连接要获取数据的邮箱 主机+用户名+密码
        store.connect(host, userCode, password);
        POP3Folder folder = (POP3Folder) store.getFolder("inbox");
        //设置邮件可读可写
        folder.open(Folder.READ_WRITE);
        System.setProperty("mail.mime.charset", "UTF-8");
        Message[] messages = folder.getMessages();

        String sql = "select * from emailstate";
        List<Map<String, Object>> appconfig = SqlUtil.getBySql(sql);
        boolean isNew = true;
        LocalDateTime received_date1 = (LocalDateTime)appconfig.get(0).get("received_date");
        Date dateRecord = Date.from (received_date1.atZone(ZoneId.systemDefault()).toInstant());

        List<String> addressList= new ArrayList<>();
        List<Date> dateList= new ArrayList<>();

        for (int i = messages.length - 1; i >= 0; i--) {
            //解析发件人地址
            String UID = folder.getUID(messages[i]);
            InternetAddress address1 = (InternetAddress) messages[i].getFrom()[0];
            String address = address1.getAddress();
//            String subject = messages[i].getSubject();
            Date receivedDate = messages[i].getSentDate();
            if (dateRecord.before(receivedDate)) {
                addressList.add(address);
                dateList.add(receivedDate);
            } else {
                //解析到已读邮件则停止
                break;
            }
        }
        String interfaceUrl = mailClient.getInterfaceUrl();
        for (int j = addressList.size() - 1; j >= 0; j--) {
            String address = addressList.get(j);
            String token=getToken(address,tokenExpireTime);
            Date diffDate = DateUtil.getDiffDate(tokenExpireTime, Calendar.HOUR);
            String content = buildContent(address, interfaceUrl+"?token=" + token, diffDate.toString(),
                    tokenExpireTime.toString());

            sendEmail(mailClient, new ArrayList<String>(Arrays.asList(address)),"Log in to sinso.dicom-network",content, false);
            String insert = "update emailstate set received_date ='" +  DateUtil.formatYMDHMS(dateList.get(j)) + "'";
            SqlUtil.updateBySql(insert);
        }

    }


    public static void sendEmail(MailClient mailClient,ArrayList toAddresList,String subject,String content, boolean ifRe) throws UnsupportedEncodingException,
            MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailClient.getSmtpHost());
        props.put("mail.smtp.port", mailClient.getSmtpPort());
        props.put("mail.smtp.auth", mailClient.getSmtpAuth());
        Authenticator auth = new SimpleAuthenticator(mailClient.getUsercode(), mailClient.getPassword());
        Session session = Session.getInstance(props, auth);
        session.setDebug(false);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(mailClient.getUsercode(),mailClient.getUsername()));
//        ArrayList arrayList = new ArrayList();
//        arrayList.add("476639390@qq.com");
        msg.setRecipients(Message.RecipientType.TO, getEmailRecipient(toAddresList));
        msg.setRecipients(Message.RecipientType.CC, getEmailRecipient(new ArrayList()));
        msg.setRecipients(Message.RecipientType.BCC, getEmailRecipient(new ArrayList()));
        //是否需要回执
        if (ifRe) {
            msg.setHeader("Disposition-Notification-To", mailClient.getUsercode());
        }
        msg.setSubject(subject);
// 设置邮件内容(包括附件的HTML格式内容)
        msg.setContent(getMultipart(content, "",new ArrayList()));
        msg.saveChanges();
        Transport.send(msg);
    }

    private static InternetAddress[] getEmailRecipient(ArrayList address) throws AddressException {
        int toLen = 0;

        if (address != null) {
            toLen = address.size();

        }

        InternetAddress[] addressTo = new InternetAddress[toLen];

        if (toLen != 0) {
            String m_st_email = "";

            for (int i = 0; i < toLen; i++) {
                m_st_email = (String) address.get(i);

                if (m_st_email != null)

                    addressTo[i] = new InternetAddress(m_st_email.trim());

            }

        }

        return addressTo;

    }

    private static Multipart getMultipart(String text, String attachParentDir, ArrayList attachment) throws MessagingException {
// 混合型邮件内容
        Multipart multi = new MimeMultipart("mixed");// 混合MIME消息
// 加入文本内容
        multi.addBodyPart(createContent(text));
// 加入附件内容
        for (int i = 0; i < attachment.size(); i++) {
            String attachmentI = (String) attachment.get(i);
// 附件的真是存储名称
            String fileRealName = attachmentI.substring(attachmentI.indexOf(""));
// 附件在邮件中的显示名称
            String fileShowName = attachmentI.substring(0, attachmentI.indexOf(">"));
            multi.addBodyPart(createAttachment(fileShowName, new File(attachParentDir + fileRealName)));// 嵌入附件
        }
        return multi;

    }

    /**

     * 邮件文本内容的处理
     * @param text 文本内容
     * @return javax.mail.BodyPart(javaMail的邮件内容)
     * @throws MessagingException
     */

    private static BodyPart createContent(String text) throws MessagingException {
        BodyPart content = new MimeBodyPart() ;
// 邮件正文也是一种组合消息，可以包含多个MimeBodyPart
        Multipart relate = new MimeMultipart("related") ;
        BodyPart html = new MimeBodyPart() ;
        html.setContent(text, "text/html;charset=gbk") ;
        relate.addBodyPart(html) ;
        content.setContent(relate) ;
        return content;

    }

    /**
     * 邮件附件的处理
     * @param fileName 附件显示名称
     * @param file 文件
     * @return javax.mail.BodyPart(javaMail的邮件内容)
     * @throws MessagingException
     */
    private static BodyPart createAttachment(String fileName, File file) throws MessagingException {
        BodyPart attach = new MimeBodyPart();
        DataSource ds = new FileDataSource(file);
        attach.setDataHandler(new DataHandler(ds));
        try {
// 文件名重新编码，解决乱码问题
            attach.setFileName(new String(fileName.getBytes(), "ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return attach;
    }



    private static class SimpleAuthenticator extends Authenticator {
        private String user;
        private String pwd;

        public SimpleAuthenticator(String user, String pwd) {
            this.user = user;
            this.pwd = pwd;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pwd);
        }
    }



    private static String getToken(String email,Integer tokenExpireTime){
        String sqlUser= "select * from user_list where user_email ='"+email+"'";
        List<Map<String, Object>> users = SqlUtil.getBySql(sqlUser);
        if(users==null || users.size()==0){
            String sqlInsert= "insert into user_list(user_email,name,remark) values('"+email+"','"+""+"','"+""+"')";
            SqlUtil.updateBySql(sqlInsert);
            users = SqlUtil.getBySql(sqlUser);
        }
        Map<String, Object> userMap = users.get(0);
        Long userId = (Long)userMap.get("id");


        // 生成uuid-token
        String token = UUID.randomUUID().toString().replace("-", "");
        Date diffDate = DateUtil.getDiffDate(tokenExpireTime, Calendar.DATE);
        String dateStr = DateUtil.formatYMDHMS(diffDate);

//        TokenUser tokenUser= new TokenUser(token,email,"",user_id;
//        String tokenStr = JSON.toJSONString(tokenUser);

        String sqlDelet= "delete from token_list where user_id ="+userId;
        SqlUtil.updateBySql(sqlDelet);
        String sql= "insert into token_list(token,expire_time,user_id) values('"+token+"','"+dateStr+"','"+userId+"')";
        SqlUtil.updateBySql(sql);

        return token;
    }

    public static String buildContent(String emailAddr,String url,String expireTime,String expireHours)  {

    String fileName = "test.html";
    InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
    BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuffer buffer = new StringBuffer();
    String line = "";
        try {
        while ((line = fileReader.readLine()) != null) {
            buffer.append(line);
        }
    } catch (Exception e) {
        System.err.println("读取文件失败:"+ e.getMessage());
    } finally {
            try {
                inputStream.close();
                fileReader.close();
            }catch (Exception ex){

            }
    }
        String out  = buffer.toString();
        out= out.replaceAll("###emailAddr###",emailAddr);
        out= out.replaceAll("###url###",url);
        out= out.replaceAll("###expireTime###",expireTime);
        out= out.replaceAll("###expireHours###",expireHours);
        return out;
    }
}



