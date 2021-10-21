package cn.sinso.DICOMNetwork.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhoucong on 2016/10/19.
 */
public class HttpConnectUtil {
    public static String readContentFromGet(String GET_URL ,HashMap<String,Object> args) throws IOException {
        // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
         String argsStr="";
        Iterator iter = args.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            argsStr+=entry.getKey().toString()+"="+URLEncoder.encode(entry.getValue().toString(),"utf-8")+"&";
        }
       // String getURL = GET_URL + URLEncoder.encode(argsStr.substring(0,argsStr.length()-1), "utf-8");
        String getURL = GET_URL+"?"+argsStr.substring(0,argsStr.length()-1);
        URL getUrl = new URL(getURL);
        // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到服务器
        connection.connect();
        // 取得输入流，并使用Reader读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
      //  System.out.println("=============================");
       // System.out.println("Contents of get request");
       // System.out.println("=============================");
        String result="";
        String lines;
        while ((lines = reader.readLine()) != null) {
            result+=lines;// System.out.println(lines);
        }
        reader.close();
        connection.disconnect(); // 断开连接
        return result;
    }

    public static String readContentFromPost(String POST_URL,HashMap<String,Object> args) throws IOException {
        // Post请求的url，与get不同的是不需要带参数
        String argsStr="";
        Iterator iter = args.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            argsStr+=entry.getKey().toString()+"="+URLEncoder.encode(entry.getValue().toString(),"utf-8")+"&";
        }
        URL postUrl = new URL(POST_URL);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();// 打开连接
        // Output to the connection. Default is false, set to true because post method must write something to the connection
        // 设置是否向connection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true
        connection.setDoOutput(true);// Read from the connection. Default is true.
        connection.setDoInput(true);// Set the post method. Default is GET
        connection.setRequestMethod("POST");// Post cannot use caches Post 请求不能使用缓存
        connection.setUseCaches(false);
        // This method takes effects to every instances of this class. URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
        // connection.setFollowRedirects(true);
        // This methods only takes effacts to this instance.URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
        connection.setInstanceFollowRedirects(true);
        // Set the content type to urlencoded,because we will write some URL-encoded content to the
        // connection. Settings above must be set before connect!
        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
        // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode 进行编码
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //  connection.setRequestProperty("Content-Type", "application/json");
        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，要注意的是connection.getOutputStream会隐含的进行connect。
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection
                .getOutputStream());
        // The URL-encoded contend正文，正文内容其实跟get的URL中'?'后的参数字符串一致
        String content = argsStr.substring(0,argsStr.length()-1);//"firstname=" + URLEncoder.encode("一个大肥人", "utf-8");
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
        out.writeBytes(content);
        out.flush();
        out.close(); // flush and close
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
        String line;
        String result="";
        while ((line = reader.readLine()) != null) {
            result+=line;
        }
        reader.close();
        connection.disconnect();
        return result;
    }
    public static String readContentFromPost(String POST_URL,HashMap<String,Object> args,String filepath) throws IOException {
        // Post请求的url，与get不同的是不需要带参数
        String argsStr="";
        Iterator iter = args.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            argsStr+=entry.getKey().toString()+"="+URLEncoder.encode(entry.getValue().toString(),"utf-8")+"&";
        }
        URL postUrl = new URL(POST_URL);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();// 打开连接
        // Output to the connection. Default is false, set to true because post method must write something to the connection
        // 设置是否向connection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true
        connection.setDoOutput(true);// Read from the connection. Default is true.
        connection.setDoInput(true);// Set the post method. Default is GET
        connection.setRequestMethod("POST");// Post cannot use caches Post 请求不能使用缓存
        connection.setUseCaches(false);
        // This method takes effects to every instances of this class. URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
        // connection.setFollowRedirects(true);
        // This methods only takes effacts to this instance.URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
        connection.setInstanceFollowRedirects(true);
        // Set the content type to urlencoded,because we will write some URL-encoded content to the
        // connection. Settings above must be set before connect!
        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
        // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode 进行编码
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //  connection.setRequestProperty("Content-Type", "application/json");
        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，要注意的是connection.getOutputStream会隐含的进行connect。
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        // The URL-encoded contend正文，正文内容其实跟get的URL中'?'后的参数字符串一致
        String content = argsStr.substring(0,argsStr.length()-1);//"firstname=" + URLEncoder.encode("一个大肥人", "utf-8");
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
        out.writeBytes(content);
//添加文件
        File file= new File(filepath);
        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append("BOUNDARY");
        sb.append("\r\n");
        sb.append("Content-Type: from-data;name=\"file\";filename=\""+file.getName()+"\"");
        sb.append("\r\n");
        sb.append("Content-Type:application/octet-stream");
        sb.append("\r\n");
        sb.append("\r\n");
        out.write(sb.toString().getBytes());
        DataInputStream in1=new DataInputStream(new FileInputStream(file));
        int bytes=0;
        byte[] bufferOut=new byte[1024];
        while ((bytes=in1.read(bufferOut))!=-1){
            out.write(bufferOut,0,bytes);
        }
        out.write("\r\n".getBytes());
        in1.close();

        out.flush();
        out.close(); // flush and close
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
        String line;
        String result="";
        while ((line = reader.readLine()) != null) {
            result+=line;
        }
        reader.close();
        connection.disconnect();
        return result;
    }
    public static String readContentFromChunkedPost(String POST_URL,HashMap<String,Object> args) throws IOException {
        URL postUrl = new URL(POST_URL);
        HttpURLConnection connection = (HttpURLConnection) postUrl
                .openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        /**//*
         * 与readContentFromPost()最大的不同，设置了块大小为5字节
         */
        connection.setChunkedStreamingMode(5);
        connection.connect();
        /**//*
         * 注意，下面的getOutputStream函数工作方式于在readContentFromPost()里面的不同
         * 在readContentFromPost()里面该函数仍在准备http request，没有向服务器发送任何数据
         * 而在这里由于设置了ChunkedStreamingMode，getOutputStream函数会根据connect之前的配置
         * 生成http request头，先发送到服务器。
         */
        DataOutputStream out = new DataOutputStream(connection
                .getOutputStream());
        String content = "firstname=" + URLEncoder.encode("一个大肥人                                                                               " +
                "                                          " +
                "asdfasfdasfasdfaasdfasdfasdfdasfs", "utf-8");
        out.writeBytes(content);
        out.flush();
        out.close(); // 到此时服务器已经收到了完整的http request了，而在readContentFromPost()函数里，要等到下一句服务器才能收到http请求。
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
        out.flush();
        out.close(); // flush and close
        String line;
        String result="";
        while ((line = reader.readLine()) != null) {
            result+=line;
        }
        reader.close();
        connection.disconnect();
        return result;
    }

    public static String sendPostOfJson(String url,String Params)throws IOException{
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        String response="";
        try {
            URL httpUrl = null; //HTTP URL类 用这个类来创建连接
            //创建URL
            httpUrl = new URL(url);
            //建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //POST请求
            out = new OutputStreamWriter(conn.getOutputStream());
            out.write(Params);
            out.flush();
            //读取响应
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes());
                response+=lines;
            }
            reader.close();
            // 断开连接
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){out.close();}
                if(reader!=null){reader.close();}
            }
            catch(IOException ex){ex.printStackTrace();}
        }
        return response;
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static String  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        if(fileName==null){fileName=url.getFile();}
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        System.out.println("info:"+url+" download success");
        return file.getAbsolutePath();


    }
    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    public static String sendHttpPostRequest(String serverUrl,HashMap<String,Object> generalFormFields,String savePath) throws Exception {
        // 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
        String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
        // 向服务器发送post请求
        URL url = new URL(serverUrl/* "http://127.0.0.1:8080/test/upload" */);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        // 头
        String boundary = BOUNDARY;
        // 传输内容
        StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
        // 尾
        String endBoundary = "\r\n--" + boundary + "--\r\n";
        OutputStream out = connection.getOutputStream();
        // 1. 处理文字形式的POST请求
        Iterator iter = generalFormFields.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            contentBody.append("\r\n")
                    .append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey() + "\"")
                    .append("\r\n")
                    .append("\r\n")
                    .append(entry.getValue())
                    .append("\r\n")
                    .append("--")
                    .append(boundary);
        }
        String boundaryMessage1 = contentBody.toString();
        out.write(boundaryMessage1.getBytes("utf-8"));
        // 2. 处理文件上传
//        for (UploadFileItem ufi : filesToBeUploaded)
//        {
        File file = new File(savePath);
            contentBody = new StringBuffer();
            contentBody.append("\r\n")
                    .append("Content-Disposition:form-data; name=\"")
                    .append("Images" + "\"; ") // form中field的名称
                    .append("filename=\"")
                    .append(file.getName() + "\"") // 上传文件的文件名，包括目录
                    .append("\r\n")
                    .append("Content-Type:application/octet-stream")
                    .append("\r\n\r\n");
            String boundaryMessage2 = contentBody.toString();
            out.write(boundaryMessage2.getBytes("utf-8"));
            // 开始真正向服务器写文件

            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[(int) file.length()];
            bytes = dis.read(bufferOut);
            out.write(bufferOut, 0, bytes);
            dis.close();
            contentBody.append("------------HV2ymHFg03ehbqgZCaKO6jyH");
            String boundaryMessage = contentBody.toString();
            out.write(boundaryMessage.getBytes("utf-8"));
            // System.out.println(boundaryMessage);
//        }
        out.write("------------HV2ymHFg03ehbqgZCaKO6jyH--\r\n".getBytes("UTF-8"));
        // 3. 写结尾
        out.write(endBoundary.getBytes("utf-8"));
        out.flush();
        out.close();
        // 4. 从服务器获得回答的内容
        String strLine = "";
        String strResponse = "";
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((strLine = reader.readLine()) != null)
        {
            strResponse += strLine + "\n";
        }
        // System.out.print(strResponse);
        return strResponse;

    }

    public static String sendHttpPostRequest(String serverUrl,HashMap<String,Object> generalFormFields, InputStream inputStream) throws Exception {
        // 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
    try {
        String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
        // 向服务器发送post请求
        URL url = new URL(serverUrl/* "http://127.0.0.1:8080/test/upload" */);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        // 头
        String boundary = BOUNDARY;
        // 传输内容
        StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
        // 尾
        String endBoundary = "\r\n--" + boundary + "--\r\n";
        OutputStream out = connection.getOutputStream();
        // 1. 处理文字形式的POST请求
        Iterator iter = generalFormFields.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            contentBody.append("\r\n")
                    .append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey() + "\"")
                    .append("\r\n")
                    .append("\r\n")
                    .append(entry.getValue())
                    .append("\r\n")
                    .append("--")
                    .append(boundary);
        }
        String boundaryMessage1 = contentBody.toString();
        out.write(boundaryMessage1.getBytes("utf-8"));
        // 2. 处理文件上传
//        for (UploadFileItem ufi : filesToBeUploaded)
//        {
        contentBody = new StringBuffer();
        contentBody.append("\r\n")
                .append("Content-Disposition:form-data; name=\"")
                .append("Images" + "\"; ") // form中field的名称
                .append("filename=\"")
                .append("savePath" + "\"") // 上传文件的文件名，包括目录
                .append("\r\n")
                .append("Content-Type:application/octet-stream")
                .append("\r\n\r\n");
        String boundaryMessage2 = contentBody.toString();
        out.write(boundaryMessage2.getBytes("utf-8"));
        // 开始真正向服务器写文件

        byte[] bufferOut = readInputStream(inputStream);
        out.write(bufferOut, 0, bufferOut.length);

        inputStream.close();

        contentBody.append("------------HV2ymHFg03ehbqgZCaKO6jyH");
        String boundaryMessage = contentBody.toString();
        out.write(boundaryMessage.getBytes("utf-8"));
        // System.out.println(boundaryMessage);
//        }
        out.write("------------HV2ymHFg03ehbqgZCaKO6jyH--\r\n".getBytes("UTF-8"));
        // 3. 写结尾
        out.write(endBoundary.getBytes("utf-8"));
        out.flush();
        out.close();
        // 4. 从服务器获得回答的内容
        String strLine = "";
        String strResponse = "";
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((strLine = reader.readLine()) != null) {
            strResponse += strLine + "\n";
        }
        // System.out.print(strResponse);

        return strResponse;
    }catch (Exception ex){
        throw ex;
    }finally {
        inputStream.close();
    }

    }






}


