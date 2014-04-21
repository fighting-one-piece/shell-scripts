package com.netease.gather.common.util;

public class TimeUtil {
	
	private static final int SECOND = 1000;
		
	public static Integer toSecond(final String value) {

		final int number = Integer.parseInt(value.substring(0, value.length() - 1));
	    final int bytes = Unit.valueOf(value.substring(value.length() - 1).toUpperCase()).toSecond(number);
	    if (bytes < 0) return Math.abs(bytes);
	    return bytes;
	}
	
	public static Long toMilliSecond(final String value) {
		return (long)toSecond(value) * SECOND;
	}
	
	enum Unit {
		  D {
			  private static final int ONE_DAY = 86400;
		      @Override
		      int toSecond(final int value) {
		        return ONE_DAY * value;
		      }
	      },
	      H {
	    	  private static final int ONE_HOUR = 3600;
		      @Override
		      int toSecond(final int value) {
		        return ONE_HOUR * value;
		      }
	      },
	      M {
	    	  private static final int ONE_MINUTE = 60;
		      @Override
		      int toSecond(final int value) {
		        return ONE_MINUTE * value;
		      }
	      },
	      S {
	    	  private static final int ONE_SECOND = 1;
	    	  @Override
	    	  int toSecond(int value) {
				return ONE_SECOND * value;
	    	  }
	      };
	      abstract int toSecond(int value);
	  }
}
