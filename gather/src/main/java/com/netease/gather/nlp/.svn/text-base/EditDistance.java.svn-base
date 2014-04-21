package com.netease.gather.nlp;

//自tagcloud工程
public class EditDistance {

	private static int Minimum(int a, int b, int c) {
		
		int mi;
		mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;
	}

	public static int getTitleDistance(String s, String t) {
		
		int d[][]; 
		int n; 
		int m; 
		int i; 
		int j; 
		char s_i; 
		char t_j; 
		int cost; 
		n = s.length();
		m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}
		for (i = 1; i <= n; i++) {
			s_i = s.charAt(i - 1);
			for (j = 1; j <= m; j++) {
				t_j = t.charAt(j - 1);
				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}
				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}
		return d[n][m];

	}
	
	//only for test
	public static void main(String[] args){
		
		String one = "1111";
		String two = "11122";
		int i = getTitleDistance(one,two);
		int tempTitle = one.length() > two.length() ? one.length() : two.length();
		System.out.println(((double)i)/(double)tempTitle);
	}
}
