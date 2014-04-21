package com.netease.gather.common.constants;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author tangh
 * 通过config.properties获得相应属性
 */
public class Config {

  private static final String CONFIG_FILE = "config.properties";

  private static Config instance = null;

  private static Map map = null;

  /**
   * 私有构造，不能直接new
   */
  private Config() {
    InputStream in = null;
    try {
    	Properties properties = new Properties();
  		ClassPathResource res=new ClassPathResource(CONFIG_FILE);
      in = res.getInputStream();
      properties.load(in);
      if(properties.size()!=0){
          map=new HashMap(properties);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e) {
      	e.printStackTrace();
      }
    }
  }

  /**
   * 初始化
   * @return
   */
  public static Config init() {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  /**
   * 获得Row
   * @return
   */
  public Map getMap(){
  	return this.map;
  }
  
  /**
   * @param configKey
   * @return
   */
  public String get(String configKey) {
    return (String) map.get(configKey);
  }

}
