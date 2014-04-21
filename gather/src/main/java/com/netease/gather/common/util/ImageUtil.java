package com.netease.gather.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
	private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	public static Map<String, Integer> getPicHeightAWidth(String url){
        HttpClient httpclient = new DefaultHttpClient();

        BufferedImage sourceImg = null;
        try{
            HttpGet httpget = new HttpGet(url);
            httpget.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            httpget.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity resEntity = response.getEntity();
            sourceImg = javax.imageio.ImageIO.read(resEntity.getContent());
            EntityUtils.consume(resEntity);

//            sourceImg = javax.imageio.ImageIO.read(new URL(url));
        }catch (Exception e){
			logger.error(e.getMessage() + " when url is: " + url, e);
        }finally{
            if(sourceImg != null)
                sourceImg.flush();
            //释放连接
            httpclient.getConnectionManager().shutdown();
        }

        if(sourceImg != null){
            int width = sourceImg.getWidth();
            int height = sourceImg.getHeight();
            Map<String, Integer> resultMap = new HashMap<String, Integer>();
            resultMap.put("width",width);
            resultMap.put("height",height);
            return resultMap;
        }
        return null;
    }
	
	public static Map<String, Integer> getPicHeightAWidth(InputStream is){
		BufferedImage sourceImg = null;
		try{
			sourceImg = javax.imageio.ImageIO.read(is);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(sourceImg != null)
				sourceImg.flush();
		}
		
		if(sourceImg != null){
			int width = sourceImg.getWidth();
			int height = sourceImg.getHeight();
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			resultMap.put("width",width);
			resultMap.put("height",height);
			return resultMap;
		}
		return null;
	}
	
	public static byte[] getPicByteArr(InputStream is){
		HttpClient httpclient = new DefaultHttpClient();
		
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		try{
			byte[] arr=new byte[1000];
			int len=0;
			while((len=is.read(arr))!=-1){
				bos.write(arr, 0, len);
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			//释放连接
			httpclient.getConnectionManager().shutdown();
		}
		
		return bos.toByteArray();
	}
	
	public static byte[] getPicByteArr(String url){
		HttpClient httpclient = new DefaultHttpClient();
		
		try{
			HttpGet httpget = new HttpGet(url);
			httpget.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpget.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			InputStream is=resEntity.getContent();
			
			return getPicByteArr(is);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			return null;
		}finally{
			//释放连接
			httpclient.getConnectionManager().shutdown();
		}
	}
	
}
