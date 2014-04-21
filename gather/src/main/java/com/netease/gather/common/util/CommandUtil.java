package com.netease.gather.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 命令行执行
 */
public class CommandUtil {

  private static Log log = LogFactory.getLog(CommandUtil.class);

  /**
   * 默认,打出屏幕显示
   */
  public static String exec(String cmd) {
    return exec(cmd, true);
  }

  public static boolean process(String cmd, String nullStr) {
    return exec(cmd, false) != null;
  }

  /** 命令行执行指令 */
  public static String exec(String cmd, boolean getStr) {
    StringBuffer result = new StringBuffer();
    BufferedReader stdout = null;
    log.info(cmd);
    try {
      Process process = Runtime.getRuntime().exec(cmd);
      if (getStr) {
        stdout = new BufferedReader(new InputStreamReader(process
            .getInputStream()));
        String line;
        while ((line = stdout.readLine()) != null) {
          log.info(line);
          result.append(line).append("\n");
        }
      }
    } catch (Exception e) {
      result.append(e.getMessage());
      e.printStackTrace();
    }
    return result.toString();
  }

  public static boolean process(String cmd) {
    BufferedReader stdout = null;
    //log.info(cmd);
    try {
      Process process = Runtime.getRuntime().exec(cmd);
      stdout = new BufferedReader(new InputStreamReader(process
          .getInputStream()));
      String line;
      while ((line = stdout.readLine()) != null) {
        log.info(line);
      }
      int exitValue = process.waitFor();
      return exitValue == 0 ? true : false;
    } catch (Exception e) {
      log.error(e.toString());
      e.printStackTrace();
      return false;
    }
  }
}
