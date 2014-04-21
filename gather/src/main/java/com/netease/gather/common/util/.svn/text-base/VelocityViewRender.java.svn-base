package com.netease.gather.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.StringWriter;

public class VelocityViewRender
        implements InitializingBean
{
    protected Log logger = LogFactory.getLog(getClass());
    private VelocityEngine velocityEngine;

    public VelocityEngine getVelocityEngine()
    {
        return this.velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine paramVelocityEngine)
    {
        this.velocityEngine = paramVelocityEngine;
    }
    public void afterPropertiesSet()
            throws Exception
    {
    }
    /**
     * 依据模板与参数生成静态文件
     * @param desUrl 目标路径
     * @param vmUrl  模板路径
     * @param characterCode  编码
     * @param paramVelocityContext  参数
     * @throws Exception
     */
    public void render(String desUrl, String vmUrl, String characterCode, VelocityContext paramVelocityContext)
            throws Exception
    {
        Template localTemplate = getVelocityEngine().getTemplate(vmUrl);
        StringWriter localStringWriter = new StringWriter();
        long l = System.currentTimeMillis();
        localTemplate.merge(paramVelocityContext, localStringWriter);
        File localFile = new File(desUrl);
        logger.debug("来源模板文件为："+vmUrl);
        logger.debug("生成静态文件到："+desUrl);
        if (!localFile.getParentFile().exists())
            localFile.getParentFile().mkdirs();
        localFile = FileUtil.createFile(desUrl);
        if (StringUtil.isEmpty(characterCode))
            characterCode = "GBK";
        FileUtils.writeStringToFile(localFile, localStringWriter.toString(), characterCode);
        localStringWriter.close();
    }

    /**
     * 依据字符串生成静态文件，对应于存在数据库中的模板
     * @param desUrl
     * @param name
     * @param body
     * @param characterCode
     * @param paramVelocityContext
     * @throws Exception
     */
    public void stringRender(String desUrl,String name,String body, String characterCode, VelocityContext paramVelocityContext) throws Exception{
        StringResourceRepository vsRepository = StringResourceLoader.getRepository();
        vsRepository.putStringResource(name, body);
        render(desUrl,name,characterCode,paramVelocityContext);
    }
}
