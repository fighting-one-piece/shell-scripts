package org.project.modules.association.apriori;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Apriori {

	private double minsup = 0.6;// 最小支持度
	private double minconf = 0.2;// 最小置信度
	
	// 注意使用IdentityHashMap，否则由于关联规则产生存在键值相同的会出现覆盖
	private IdentityHashMap ruleMap = new IdentityHashMap();
	private String[] trainSet = { "abc", "abc", "acde", "bcdf", "abcd", "abcdf" };// 事务集合，可以根据需要从构造函数里传入
	private int itemCounts = 0;// 候选1项目集大小,即字母的个数
	private TreeSet[] frequencySet = new TreeSet[40];// 频繁项集数组，[0]:代表1频繁集...
	private TreeSet maxFrequency = new TreeSet();// 最大频繁集
	private TreeSet candidate = new TreeSet();// 1候选集
	private TreeSet candidateSet[] = new TreeSet[40];// 候选集数组
	private int frequencyIndex;

	public Apriori() {
		init();
	}

	public Apriori(String[] transSet) {
		this.trainSet = transSet;
		init();
	}
	
	private void init() {
		maxFrequency = new TreeSet();
		itemCounts = counts();// 初始化1候选集的大小
		// 初始化其他两个
		for (int i = 0; i < itemCounts; i++) {
			frequencySet[i] = new TreeSet();
			candidateSet[i] = new TreeSet();
		}
		candidateSet[0] = candidate;
	}

	public int counts() {
		String temp1 = null;
		char temp2 = 'a';
		// 遍历所有事务集String 加入集合，set自动去重了
		for (int i = 0; i < trainSet.length; i++) {
			temp1 = trainSet[i];
			for (int j = 0; j < temp1.length(); j++) {
				temp2 = temp1.charAt(j);
				candidate.add(String.valueOf(temp2));
			}
		}
		return candidate.size();
	}

	public void item1_gen() {
		String temp1 = "";
		double m = 0;
		Iterator temp = candidateSet[0].iterator();
		while (temp.hasNext()) {
			temp1 = (String) temp.next();
			m = count_sup(temp1);
			// 符合条件的加入 1候选集
			if (m >= minsup * trainSet.length) {
				frequencySet[0].add(temp1);
			}
		}
	}

	public double count_sup(String x) {
		int temp = 0;
		for (int i = 0; i < trainSet.length; i++) {
			for (int j = 0; j < x.length(); j++) {
				if (trainSet[i].indexOf(x.charAt(j)) == -1)
					break;
				else if (j == (x.length() - 1))
					temp++;
			}
		}
		return temp;
	}

	public void canditate_gen(int k) {
		String y = "", z = "", m = "";
		char c1 = 'a', c2 = 'a';
		Iterator temp1 = frequencySet[k - 2].iterator();
		Iterator temp2 = frequencySet[0].iterator();
		TreeSet h = new TreeSet();
		while (temp1.hasNext()) {
			y = (String) temp1.next();
			c1 = y.charAt(y.length() - 1);
			while (temp2.hasNext()) {
				z = (String) temp2.next();
				c2 = z.charAt(0);
				if (c1 >= c2)
					continue;
				else {
					m = y + z;
					h.add(m);
				}
			}
			temp2 = frequencySet[0].iterator();
		}
		candidateSet[k - 1] = h;
	}

	// k候选集=>k频繁集
	public void frequent_gen(int k) {
		String s1 = "";
		Iterator ix = candidateSet[k - 1].iterator();
		while (ix.hasNext()) {
			s1 = (String) ix.next();
			if (count_sup(s1) >= (minsup * trainSet.length)) {
				frequencySet[k - 1].add(s1);
			}
		}
	}

	public boolean is_frequent_empty(int k) {
		if (frequencySet[k - 1].isEmpty())
			return true;
		else
			return false;
	}

	public boolean included(String s1, String s2) {
		for (int i = 0; i < s1.length(); i++) {
			if (s2.indexOf(s1.charAt(i)) == -1)
				return false;
			else if (i == s1.length() - 1)
				return true;
		}
		return true;
	}

	public void maxfrequent_gen() {
		int i, j;
		Iterator iterator, iterator1, iterator2;
		String temp = "", temp1 = "", temp2 = "";
		for (i = 1; i < frequencyIndex; i++) {
			maxFrequency.addAll(frequencySet[i]);
		}
		// for (i = 0; i < frequencyIndex; i++) {
		// iterator1 = frequencySet[i].iterator();
		// while (iterator1.hasNext()) {
		// temp1 = (String) iterator1.next();
		// for (j = i + 1; j < frequencyIndex; j++) {
		// iterator2 = frequencySet[j].iterator();
		// while (iterator2.hasNext()) {
		// temp2 = (String) iterator2.next();
		// if (included(temp1, temp2))
		// maxFrequency.remove(temp1);
		// }
		// }
		// }
		// }
	}

	public void print_maxfrequent() {
		Iterator iterator = maxFrequency.iterator();
		System.out.print("产生规则频繁项集：");
		while (iterator.hasNext()) {
			System.out.print(toDigit((String) iterator.next()) + "\t");
		}
		System.out.println();
	}

	public void rulePrint() {
		String x, y;
		double temp = 0;
		Set hs = ruleMap.keySet();
		Iterator iterator = hs.iterator();
		StringBuffer sb = new StringBuffer();
		System.out.println("关联规则：");
		while (iterator.hasNext()) {
			x = (String) iterator.next();
			y = (String) ruleMap.get(x);
			temp = (count_sup(x + y) / count_sup(x));

			// x = toDigit(x);
			// y = toDigit(y);
			System.out.println(x + (x.length() < 5 ? "\t" : "") + "-->" + y
					+ "\t" + temp);
			sb.append("  " + x + (x.length() < 5 ? "\t" : "") + "-->" + y
					+ "\t" + temp + "\t\n");
		}
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter("Asr.txt");
			bw = new BufferedWriter(fw);
			bw.write("最小支持度 minsup = " + minsup);
			bw.newLine();
			bw.write("最小置信度 minconf = " + minconf);
			bw.newLine();
			bw.write("产生关联规则如下: ");
			bw.newLine();
			bw.write(sb.toString());
			// bw.newLine();
			if (bw != null)
				bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void subGen(String s) {
		String x = "", y = "";
		for (int i = 1; i < (1 << s.length()) - 1; i++) {
			for (int j = 0; j < s.length(); j++) {
				if (((1 << j) & i) != 0) {
					x += s.charAt(j);
				}
			}
			for (int j = 0; j < s.length(); j++) {
				if (((1 << j) & (~i)) != 0) {
					y += s.charAt(j);
				}
			}
			if (count_sup(x + y) / count_sup(x) >= minconf) {
				ruleMap.put(x, y);
			}
			x = "";
			y = "";
		}
	}

	public void ruleGen() {
		String s;
		Iterator iterator = maxFrequency.iterator();
		while (iterator.hasNext()) {
			s = (String) iterator.next();
			subGen(s);
		}
	}

	// for test
	public void print1() {
		Iterator temp = candidateSet[0].iterator();
		while (temp.hasNext())
			System.out.println(temp.next());
	}

	// for test
	public void print2() {
		Iterator temp = frequencySet[0].iterator();
		while (temp.hasNext())
			System.out.println((String) temp.next());
	}

	// for test
	public void print3() {
		canditate_gen(1);
		frequent_gen(2);
		Iterator temp = candidateSet[1].iterator();
		Iterator temp1 = frequencySet[1].iterator();
		while (temp.hasNext())
			System.out.println("候选" + (String) temp.next());
		while (temp1.hasNext())
			System.out.println("频繁" + (String) temp1.next());
	}

	public void print_canditate() {
		for (int i = 0; i < frequencySet[0].size(); i++) {
			Iterator ix = candidateSet[i].iterator();
			Iterator iy = frequencySet[i].iterator();
			System.out.print("候选集" + (i + 1) + ":");
			while (ix.hasNext()) {
				System.out.print((String) ix.next() + "\t");
				// System.out.print(toDigit((String) ix.next()) + "\t");
			}
			System.out.print("\n" + "频繁集" + (i + 1) + ":");
			while (iy.hasNext()) {
				System.out.print((String) iy.next() + "\t");
				// System.out.print(toDigit((String) iy.next()) + "\t");
			}
			System.out.println();
		}
	}

	private String toDigit(String str) {
		if (str != null) {
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				temp.append(((int) c - 65) + " ");
			}
			return temp.toString();
		} else {
			return null;
		}
	}

	public String[] getTrans_set() {
		return trainSet;
	}

	public void setTrans_set(String[] transSet) {
		transSet = transSet;
	}

	public double getMinsup() {
		return minsup;
	}

	public void setMinsup(double minsup) {
		this.minsup = minsup;
	}

	public double getMinconf() {
		return minconf;
	}

	public void setMinconf(double minconf) {
		this.minconf = minconf;
	}

	public void run() {
		int k = 1;
		item1_gen();
		do {
			k++;
			canditate_gen(k);
			frequent_gen(k);
		} while (!is_frequent_empty(k));
		frequencyIndex = k - 1;
		print_canditate();
		maxfrequent_gen();
		print_maxfrequent();
		ruleGen();
		rulePrint();
	}
	
	public static void main(String[] args) {
		Apriori ap = new Apriori();
		ap.run();
	}

}
