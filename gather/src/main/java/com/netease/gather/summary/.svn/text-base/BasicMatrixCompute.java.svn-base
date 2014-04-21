package com.netease.gather.summary;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
//LIXUSIGN
public abstract class BasicMatrixCompute {
	
	public static final String ZW_JH 				= "。";
	public static final String ZW_LYH				= "“";
	public static final String ZW_RYH				= "”";
	public static final String YW_WH				= "?";
	public static final String ZW_WH				= "？";
	public static final String NIL_STR				= "";
	public static final String SPACE_ZZ				= "\\s+";
	public static final String SPACE_LA				= " ";
	public static final String HEXIN_TISHI			= "核心提示";
	public static final String SOURCE_TITLE			= "原标题";
	public static final String _P_S0				= "<p>";
	public static final String _P_S1				= "<P>";
	public static final String _P_E0				= "</p>";
	public static final String _P_E1				= "</P>";
	public static final String _P					= "p";
	public static final String _STRONG				= "strong";
	public static final String _B_S0				= "<strong>";
	public static final String _B_E0				= "</strong>";
	public static final String _F_CENTER			= "f_center";
	public static final String _T_BAODAO			= "报道";
	public static final String _T_DIAN				= "日电";
	public static final String[] UNEXCEPTWORD		= {"但是"};
	public static final Integer BASIC_LENGTH 		= 5;
	public static final Integer BASIC_FIRST_LENGTH 	= 10;
	public static final Integer LENGTH_LIMIT		= 150;
	public static final Double  FIRST_RADIO	 		= 0.6D;
	public static final Double  SIMLAR_THREADHOLD 	= 0.000001;
	public static final Double  SL					= 0.1D;
	public static final Integer SOURCE_BASIC_LENGTH	= 127;
	
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public static final String YES_SIGN	= "YS";
	public static final String NO_SIGN	= "NS";
	
	public static String[] getSegment(String text) {
    	
    	IKSegmenter segmenter = null;
    	StringReader reader_ = new StringReader(text);
		segmenter = new IKSegmenter(reader_,true);
		Lexeme lexeme = null;
		Set<String> temp = new HashSet<String>();
		try {
			lexeme = segmenter.next();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (lexeme != null) {
				temp.add(new String(lexeme.getLexemeText()));
				lexeme = segmenter.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return list2Array(temp);
    }
	
	public static String[] list2Array(Set<String> list){
		
		Iterator<String> iterator = list.iterator();
		String[] reArray = new String[list.size()];
		int i = 0;
		for(;iterator.hasNext();i++){
			String c = iterator.next();
			reArray[i] = c;
		}
		return reArray;
	}
	
	static class IDFUtil{
		
		public static Map<String, Double> calIDF(List<String> ws,List<String> texts) {
	       
			Map<String, Double> idf = new HashMap<String, Double>();
	        Map<String, Integer> df = new HashMap<String, Integer>();
	        for (String w: ws) 
	            df.put(w, 0);
	        for (String text: texts) {
	            Set<String> pre = new HashSet<String>();
	            for (String w: BasicMatrixCompute.getSegment(text)) {
	            	pre.add(w);
	            }
	            for (String w: pre) {
	                df.put(w, df.get(w) + 1);
	            }
	        }
	        for (String w: ws) {
	            idf.put(w, Math.log(texts.size() * 1.0 / df.get(w)));
	        }
	        return idf;
	    }
	}
	
	static class MCal{
		
		public static double[] power(double[][] sM,int size) {
			
			double e = 0.001;
			int m = 100;
			int p = 2;
			double[][] cM = trans(sM);
			double[][] cV = new double[size][1];
			double[][] pV;
			for (int i = 0; i < size; ++i) {
				cV[i][0] = 1.0 / size;
			}
			for (int i = 0; i < m; ++i) {
				pV = cV;
				cV = mult(cM, cV);
				double er = 0;
				for (int j = 0; j < size; ++j) {
					er += Math.pow(cV[j][0] - pV[j][0], p);
				}
				if (er < Math.pow(e, p)) {
					break;
				}
			}
			double[] result = new double[size];
			for (int i = 0; i < size; ++i) {
				result[i] = cV[i][0];
			}
			return result;
		}
		
		public static double[][] mult(double[][] a, double[][] b) {
			
			if (a.length == 0 || b.length == 0) {
				return null;
			}
			if (a[0].length != b.length) {
				return null;
			}
			double[][] result = new double[a.length][b[0].length];
			for (int i = 0; i < a.length; ++i) {
				for (int j = 0; j < b[0].length; ++j) {
					double sum = 0;
					for (int k = 0; k < b.length; ++k) {
						sum += a[i][k] * b[k][j];
					}
					result[i][j] = sum;
				}
			}
			return result;
		}

		public static double[][] trans(double[][] m) {
			
			if (m.length == 0) {
				return null;
			}
			double[][] result = new double[m[0].length][m.length];
			for (int i = 0; i < result.length; ++i) {
				for (int j = 0; j < result[i].length; ++j) {
					result[i][j] = m[j][i];
				}
			}
			return result;
		}
		
		public static double[][] trans(double[][] sim,double h) {

			double[][] p = new double[sim.length][sim[0].length];
			boolean con = false;
			for (int i = 0; i < sim.length; ++i) {
				double sum = 0;
				for (int j = 0; j < sim[i].length; ++j) {
					if (sim[i][j] > h) {
						if (con) {
							p[i][j] = sim[i][j];
							sum += sim[i][j];
						} else {
							p[i][j] = 1;
							sum += 1;
						}
					} else {
						p[i][j] = 0;
					}
				}
				for (int j = 0; j < sim[i].length; ++j) {
					p[i][j] /= sum;
				}
			}
			return p;
		}
	}
	
	static class Pair<T> implements Comparable<Pair<T>> {
	       
    	T data;
        double score;
        
        public Pair(T d, double s) {
            
        	data = d;
            score = s;
        }
        public int compareTo(Pair<T> other) {
            
        	double d = score - other.score;
            if (d > BasicMatrixCompute.SIMLAR_THREADHOLD) {
                return 1;
            } else if (d < (-1) * BasicMatrixCompute.SIMLAR_THREADHOLD) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
