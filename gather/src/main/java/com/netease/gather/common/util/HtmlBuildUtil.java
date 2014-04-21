package com.netease.gather.common.util;

import com.netease.gather.common.context.ScheduleContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class HtmlBuildUtil {
    private static Logger logger = LoggerFactory.getLogger(HtmlBuildUtil.class);

    private static VelocityViewRender velocityViewRender;

    static {
        velocityViewRender = ScheduleContext.BF.getBean("velocityViewRender", VelocityViewRender.class);
    }

    private static Map<String, Object> contextRow = new HashMap<String, Object>();

    static {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ClassPathResource("velocitytoolbox.xml").getInputStream());

            NodeList nl = doc.getElementsByTagName("tool");
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);

                Node n = node.getElementsByTagName("key").item(0);
                String key = n.getTextContent();
                n = node.getElementsByTagName("scope").item(0);
                String scope = n.getTextContent();
                n = node.getElementsByTagName("class").item(0);
                String clazz = n.getTextContent();
                addTool(key, scope, clazz);
            }
        } catch (Exception localException) {
            logger.error("解析toolbox.xml文件出错！", localException);
        }
    }

    private static void addTool(String key, String scope, String clazz) {
        if (!("application".equals(scope)))
            return;
        try {
            Object localObject = Class.forName(clazz).newInstance();
            contextRow.put(key, localObject);
        } catch (Exception localException) {
            logger.error("实例化类"+clazz+"出错！", localException);
        }
    }

    public static void buildHTML(String htmlPath, String vmname,String vmbody, String encode, Map<String, Object> paramRow){
        logger.debug("htmlpath:{},vmName:{},vmBody{},encode:{}", new Object[] { htmlPath, vmname,vmbody, encode });

        paramRow.putAll(contextRow);
        VelocityContext localVelocityContext = new VelocityContext(paramRow);

        localVelocityContext.put("row", paramRow);

        try {
            velocityViewRender.stringRender(htmlPath,vmname,vmbody, encode, localVelocityContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void buildHTML(String htmlPath, String vmPath, String encode, Map<String, Object> paramRow) {
        logger.debug("htmlpath:{},vmPath:{},encode:{}", new Object[] { htmlPath, vmPath, encode });

        paramRow.putAll(contextRow);
        VelocityContext localVelocityContext = new VelocityContext(paramRow);

        localVelocityContext.put("row", paramRow);

        try {
            velocityViewRender.render(htmlPath, vmPath, encode, localVelocityContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static String getHTML(String vmPath, Map<String, Object> paramRow) {
        logger.debug("vmPath:{}", vmPath);

        paramRow.putAll(contextRow);
        VelocityContext localVelocityContext = new VelocityContext(paramRow);

        String ret = null;
        StringWriter sw = null;
        try {
            Template template = velocityViewRender.getVelocityEngine().getTemplate(vmPath);
            sw = new StringWriter();
            long l = System.currentTimeMillis();
            template.merge(localVelocityContext, sw);
            logger.debug("获取模板数据合成消耗时间：{}毫秒.", System.currentTimeMillis() - l);
            ret = sw.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sw != null)
                try {
                    sw.close();
                } catch (IOException e) {
                }
        }

        return ret;
    }
}
