package com.netease.gather.summary;

import com.netease.gather.common.util.Html2Text;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;

//LIXUSIGN
public class SummaryFacade {
    
	public static String auto(String text){
		
		if(text == null) return BasicMatrixCompute.NIL_STR;
		String resultStr = BasicMatrixCompute.NIL_STR;
		String result = BasicMatrixCompute.NIL_STR;
		text = Html2Text.tidySentence(text, true);
		if(text.length() > BasicMatrixCompute.SOURCE_BASIC_LENGTH){
			if(text.contains(BasicMatrixCompute.HEXIN_TISHI))
				result = splitSummary(text);
			if(BasicMatrixCompute.NO_SIGN.equalsIgnoreCase(result) || BasicMatrixCompute.NIL_STR.equalsIgnoreCase(result)){
				result = autoSummary(text);
			}
			else if(BasicMatrixCompute.NIL_STR.equalsIgnoreCase(result))
				result = null;
			String[] strs = result.split(BasicMatrixCompute.ZW_JH);
			StringBuffer sb = new StringBuffer();
			if(strs != null && strs.length > 0){
				Integer length = BasicMatrixCompute.SOURCE_BASIC_LENGTH;
				for(int i = 0 ; i < strs.length ; i++){
					String one = strs[i];
					int oneLength = one.length() + BasicMatrixCompute.ZW_JH.length();
					if(length > oneLength){
						length -= oneLength; 
						sb.append(strs[i]).append(BasicMatrixCompute.ZW_JH);
					}
				}
			}
			resultStr = sb.toString();
		}else
			resultStr = text;
		
		if (BasicMatrixCompute.NIL_STR.equalsIgnoreCase(resultStr)) {
			String legn = text.substring(0, text.length() > BasicMatrixCompute.LENGTH_LIMIT ? BasicMatrixCompute.LENGTH_LIMIT : text.length());
			int index = NumberUtils.max(new int[] { legn.lastIndexOf(BasicMatrixCompute.ZW_JH), legn.lastIndexOf(BasicMatrixCompute.ZW_WH) }) + 1;
			resultStr = legn.substring(0, index);
		}
		return resultStr;
	}
	
	private static String splitSummary(String text){
		
		int startIndex = text.indexOf(BasicMatrixCompute.HEXIN_TISHI) + (BasicMatrixCompute.HEXIN_TISHI.length() + 1);
		String temp = text.substring(startIndex);
		int endIndex_e0 = temp.indexOf(BasicMatrixCompute._P_E0);
		int endIndex_e1 = temp.indexOf(BasicMatrixCompute._P_E1);
		int endIndex = -1;
		if(endIndex_e0 > 0 && endIndex_e1 > 0)
			endIndex = endIndex_e0 < endIndex_e1 ? endIndex_e0 : endIndex_e1; 
		else if(endIndex_e0 < 0 && endIndex_e1 < 0)
			return BasicMatrixCompute.NO_SIGN; 
		else
			endIndex = endIndex_e0 > endIndex_e1 ? endIndex_e0 : endIndex_e1;
		temp = temp.substring(0, endIndex);
		return temp;
	}
	
	private static String autoSummary(String text){
		
		List<String> senten = new ArrayList<String>();
        StringBuffer buffer = new StringBuffer();
        if(text.contains(BasicMatrixCompute.ZW_JH)){
    		String[] strs = text.split(BasicMatrixCompute.ZW_JH);
    		for(String one : strs){
    			if(one.length() > BasicMatrixCompute.BASIC_LENGTH){
    				senten.add(one + BasicMatrixCompute.ZW_JH);
    				buffer.append(one + BasicMatrixCompute.ZW_JH);
    			}
    		}
    	}

        Summarizer s = new Summarizer(senten);
        List<String> results = s.summar();
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        for (int i = 0 ; i < results.size() ; i++) {
        	map.put(i, buffer.indexOf(results.get(i)));
        }
        
        List<Map.Entry<Integer, Integer>> mappingList = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet()); 
		Collections.sort(mappingList, new Comparator<Map.Entry<Integer, Integer>>(){ 
			   public int compare(Map.Entry<Integer,Integer> m1,Map.Entry<Integer,Integer> m2){ 
			    return m1.getValue().compareTo(m2.getValue()); 
			   } 
		});
		if(senten.size() == 0){
			return BasicMatrixCompute.NIL_STR;
		}
		String f = senten.get(0);
		int i1 = f.indexOf(BasicMatrixCompute.ZW_LYH);
		int i2 = f.indexOf(BasicMatrixCompute.ZW_RYH);
		boolean sign = true;
		if(i1 >= 0 && i2 >= 0 && i1 < i2){
			int temp = f.substring(i1,i2+1).length();
			if((double)temp / f.length() > BasicMatrixCompute.FIRST_RADIO){
				sign = false;
			}
		}
		StringBuffer sb = new StringBuffer();
		if(!f.contains(BasicMatrixCompute.YW_WH) && !f.contains(BasicMatrixCompute.ZW_WH) && f.length() > BasicMatrixCompute.BASIC_FIRST_LENGTH && sign){
			sb.append(f.trim());
		}
		
		int count = 0;
		List<String> repeatSet = new ArrayList<String>();
        for(int i = 0 ; i < mappingList.size() ; i++){
        	Map.Entry<Integer, Integer> entry = mappingList.get(i);
        	String one = results.get(entry.getKey());
        	if(!f.equalsIgnoreCase(one) && one.length() > BasicMatrixCompute.BASIC_FIRST_LENGTH && !one.contains(BasicMatrixCompute.YW_WH) && !repeatSet.contains(one)){
        		count += one.length();
            	if(count < BasicMatrixCompute.LENGTH_LIMIT){
            		repeatSet.add(one);
            	}else if(count > BasicMatrixCompute.LENGTH_LIMIT){
            		break;
            	}	
        	}
        }
        Iterator<String> iterator_repeatSet = repeatSet.iterator();
        for(;iterator_repeatSet.hasNext();){
        	String one = iterator_repeatSet.next();
        	sb.append(one.trim());
        }
        return sb.toString();
	}
}