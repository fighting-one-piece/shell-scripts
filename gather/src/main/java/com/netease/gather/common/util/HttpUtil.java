package com.netease.gather.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * User: ykxu
 * Date: 12-9-3
 * Time: 下午2:07
 */
public class HttpUtil {
    private static final Logger logger = Logger.getLogger(HttpUtil.class);

    public static String uploadFile(InputStream is,String url){

        HttpClient httpclient = new DefaultHttpClient();

        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            HttpPost httppost = new HttpPost(url);
            ByteArrayBody bab = new ByteArrayBody(imgdata, "filename");
//            InputStreamBody isb = new InputStreamBody(is, "filename");
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("Filedata", bab);

            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            StringBuffer sb=new StringBuffer();
            BufferedReader red=new BufferedReader(new InputStreamReader(resEntity.getContent(),"gbk"));
            String line;
            while((line=red.readLine())!=null){
                sb.append(line);
            }
            EntityUtils.consume(resEntity);
            return sb.toString();
        } catch (Exception e) {
            logger.error(e,e);
        } finally {
            //释放连接
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }

    public static int checkURL(String url){

        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            StatusLine code = response.getStatusLine();
            return code.getStatusCode();
        } catch (Exception e) {
            logger.error(e,e);
        } finally {
            //释放连接
            httpclient.getConnectionManager().shutdown();
        }

        return 0;
    }

    //去掉所有ssl验证
    public static String getURL(String url,String charsetName,Map<String,String> header){

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  10000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  10000);
        String content = "";
        try {
            httpclient = WebClientDevWrapper.wrapClient(httpclient);
            HttpGet httpget = new HttpGet(url);
            if(header!=null&&!header.isEmpty()){
                for(Map.Entry<String,String> entry:header.entrySet()){
                    httpget.setHeader(entry.getKey(),entry.getValue());
                }
            }
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            StringBuilder sb = new StringBuilder();
            BufferedReader red = new BufferedReader(new InputStreamReader(entity.getContent(), charsetName));
            String line;
            while ((line = red.readLine()) != null) {
                sb.append(line);
            }
            EntityUtils.consume(entity);
            content = sb.toString();
        } catch (Exception e) {
            logger.error(e,e);
        } finally {
            //释放连接
            httpclient.getConnectionManager().shutdown();
        }

        return content;
    }

    public static void main(String[] args){
        HttpUtil.checkURL("http://img3.cache.netease.com/photo/0011/2012-09-25/8C7CM4SL53DA0011.jpg");
        String html = HttpUtil.getURL("http://news.163.com/13/1205/10/9FAS6RVE00014JB6.html", "GBK", null);
        String url = CommonUtil.getPicFromHtml(html);
        logger.info(url);
    }
}
