
package org.project.modules.association.apriori;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Apriori01 {

	private int minSup;
	
	private List<Set<Integer>> records;
	
	private String output;
	
	private List<List<ItemSet>> result = new ArrayList<List<ItemSet>>();
	
	public Apriori01(double minDegree, String input, String output) {
		this.output = output;
		init(input);
		if (records.size() == 0) {
			System.err.println("不符合计算条件。退出！");
            System.exit(1);
		}
		minSup = (int) (minDegree * records.size());
	}
	
	private void init(String path) {
		
	}
}



class ItemSet {

    TreeSet<Integer> item;

    int support;

    List<ItemCon> ics = new ArrayList<ItemCon>(); // 关联规则结果

    ItemSet(ItemSet is) {
        this.item = new TreeSet<Integer>(is.item);
    }

    ItemSet() {
        item = new TreeSet<Integer>();
    }

    ItemSet(int i, int v) {
        this();
        merge(i);
        setValue(v);
    }

    void setValue(int i) {
        this.support = i;
    }

    void merge(int i) {
        item.add(i);
    }

    void calcon(ItemCon ic) {
        ics.add(ic);
    }

    boolean isMerge(ItemSet other) {
        if (other == null || other.item.size() != item.size())
            return false;
        // 前k-1项相同，最后一项不同，满足连接条件
/*
         * Iterator<Integer> i = item.headSet(item.last()).iterator();
         * Iterator<Integer> o =
         * other.item.headSet(other.item.last()).iterator(); while (i.hasNext()
         * && o.hasNext()) if (i.next() != o.next()) return false;
         */

        Iterator<Integer> i = item.iterator();
        Iterator<Integer> o = other.item.iterator();
        int n = item.size();
        while (i.hasNext() && o.hasNext() && --n > 0)
            if (i.next() != o.next())
                return false;
        return !(item.last() == other.item.last());
    }
}

class ItemCon {

    Integer i;

    List<Integer> li;

    double confidence1;

    double confidence2;

    ItemCon(Integer i, List<Integer> li) {
        this.i = i;
        this.li = li;
    }

    void setC1(double c1) {
        this.confidence1 = c1;
    }

    void setC2(double c2) {
        this.confidence2 = c2;

    }


}
