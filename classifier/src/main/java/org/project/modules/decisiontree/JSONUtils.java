package org.project.modules.decisiontree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JSONUtils {

	@SuppressWarnings("rawtypes")
	public static String object2json(Object obj) {
		StringBuffer sb = new StringBuffer();
		if (obj == null)
			sb.append("\"\"");
		else if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Boolean || obj instanceof Short || obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal || obj instanceof BigInteger || obj instanceof Byte)
			sb.append("\"").append(string2json(obj.toString())).append("\"");
		else if (obj instanceof Map)
			sb.append(map2json((Map)obj));
		else 
			sb.append(generateJsonByObject(obj));
		return sb.toString();
	}

	@SuppressWarnings({"rawtypes"})
	public static String map2json(Map map) {
		StringBuffer json = new StringBuffer();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Iterator it = map.keySet().iterator(); it.hasNext(); json.append(",")) {
				Object key = it.next();
				json.append(object2json(key).toUpperCase());
				json.append(":");
				json.append(object2json(map.get(key)));
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case 34: // '"'
				sb.append("\\\"");
				break;
			case 92: // '\\'
				sb.append("\\\\");
				break;
			case 8: // '\b'
				sb.append("\\b");
				break;
			case 12: // '\f'
				sb.append("\\f");
				break;
			case 10: // '\n'
				sb.append("\\n");
				break;
			case 13: // '\r'
				sb.append("\\r");
				break;
			case 9: // '\t'
				sb.append("\\t");
				break;
			case 47: // '/'
				sb.append("\\/");
				break;
			default:
				if (ch >= 0 && ch <= '\037') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++)
						sb.append('0');
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
				break;
			}
		}
		return sb.toString();
	}

	public static String generateJsonByObject(Object object) {
		return JSONObject.fromObject(object).toString();
	}

	public static String generateJsonByObjectAndConfig(Object object, final String[] properties){
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
//		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {

			@Override
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if(null==properties || properties.length==0){
					return false;
				}
				for(String property : properties){
					if(property.equals(arg1)){
						return true;
					}
				}
				return false;
			}
		});
		return JSONObject.fromObject(object,jsonConfig).toString();
	}

	public static String generateJsonByCollection(Collection<?> collection){
		return JSONArray.fromObject(collection).toString();
	}

	public static String generateJsonByCollectionAndConfig(Collection<?> collection, final String[] properties){
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
//		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if(null == properties || properties.length == 0) {
					return false;
				}
				for(String property : properties){
					if(property.equals(arg1)){
						return true;
					}
				}
				return false;
			}
		});
		return JSONArray.fromObject(collection, jsonConfig).toString();
	}
	
	public static JSONObject parseJsonData(String jsonData) {
		return JSONObject.fromObject(jsonData);
	}
	
	public static void main(String[] args) {
		Tree t_1 = new Tree("t_1");
		t_1.setChild("t_1_1", "t_1_1");
		Tree t_2 = new Tree("t_2");
		t_2.setChild("t_2_1", "t_2_1");
		Tree t_3 = new Tree("t_3");
		t_2.setChild("t_2_2", t_3);
		t_1.setChild("t_1_2", t_2);
		
		
	}
	
	public static void t2j(Tree tree, StringBuilder sb) {
		
	}

}
