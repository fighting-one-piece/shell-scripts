package com.netease.gather.clawerpic.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class DownloadImage {
    
	public static void main(String args[]) throws HttpException, IOException{
        new DownloadImage().download("http://image.club.china.com/twhb/2014/2/27/3316/pictuku/1393474061831_502.jpg");
    }

    //url为图片地址
    public void download(String url) throws HttpException, IOException
    {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);
    	List<Header> headers = new ArrayList<Header>();
    	 //headers.add(new Header("HOST",	"www.lvmama.com"));
    	 //client.getHostConfiguration().getParams().setParameter("http.default-headers", headers); 
    	 get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false)); 
        client.executeMethod(get);
        String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File storeFile = new File("C:/"+name + ".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(storeFile);
        FileOutputStream out = fileOutputStream;
        InputStream stream = get.getResponseBodyAsStream();
        try{
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = stream.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			stream.close();
        } catch (HttpException e) {
        e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
