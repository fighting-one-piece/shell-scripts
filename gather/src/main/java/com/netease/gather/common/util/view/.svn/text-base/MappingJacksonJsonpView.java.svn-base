package com.netease.gather.common.util.view;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MappingJacksonJsonpView extends AbstractView
{
  public static final String DEFAULT_CONTENT_TYPE = "text/javascript";
  private ObjectMapper objectMapper = new ObjectMapper();
  private String callbackParameterName = "_callback";

  private JsonEncoding encoding = JsonEncoding.UTF8;

  private boolean prefixJson = false;
  private Set<String> renderedAttributes;
  private boolean disableCaching = true;

  public MappingJacksonJsonpView()
  {
    setContentType("application/json");
  }

  public void setObjectMapper(ObjectMapper objectMapper)
  {
    Assert.notNull(objectMapper, "'objectMapper' must not be null");
    this.objectMapper = objectMapper;
  }
  public String getCallbackParameterName() {
	return callbackParameterName;
  }
	
  public void setCallbackParameterName(String callbackParameterName) {
	this.callbackParameterName = callbackParameterName;
  }

  public void setEncoding(JsonEncoding encoding)
  {
    Assert.notNull(encoding, "'encoding' must not be null");
    this.encoding = encoding;
  }

  public void setPrefixJson(boolean prefixJson)
  {
    this.prefixJson = prefixJson;
  }

  public Set<String> getRenderedAttributes()
  {
    return this.renderedAttributes;
  }

  public void setRenderedAttributes(Set<String> renderedAttributes)
  {
    this.renderedAttributes = renderedAttributes;
  }

  public void setDisableCaching(boolean disableCaching)
  {
    this.disableCaching = disableCaching;
  }

  protected void prepareResponse(HttpServletRequest request, HttpServletResponse response)
  {
    response.setContentType(getContentType());
    response.setCharacterEncoding(this.encoding.getJavaName());
    if (this.disableCaching) {
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
      response.addDateHeader("Expires", 1L);
    }
  }

  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Object value = filterModel(model);
    Writer writer = response.getWriter();
    JsonGenerator generator =
      this.objectMapper.getJsonFactory().createJsonGenerator(writer);
	String callback = request.getParameter(callbackParameterName);
	if (callback!=null) {
		callback = StringEscapeUtils.escapeJavaScript(callback);
		callback = StringEscapeUtils.escapeHtml(callback);
		callback = StringEscapeUtils.escapeSql(callback);
		writer.append(callback);
		writer.append("(");
	    if (this.prefixJson) {
	        generator.writeRaw("{} && ");
	      }
	    this.objectMapper.writeValue(generator, value);
		writer.append(");");
	}else{
	    if (this.prefixJson) {
	        generator.writeRaw("{} && ");
	      }
	    this.objectMapper.writeValue(generator, value);
	}

  }

  protected Object filterModel(Map<String, Object> model)
  {
    Map result = new HashMap(model.size());
    Set renderedAttributes = 
      !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();
    for (Entry entry : model.entrySet()) {
      if ((!(entry.getValue() instanceof BindingResult)) && (renderedAttributes.contains(entry.getKey()))) {
        result.put((String)entry.getKey(), entry.getValue());
      }
    }
    return result;
  }
}