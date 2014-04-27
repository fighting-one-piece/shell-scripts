package org.project.modules.decisiontree.builder;

import org.project.modules.decisiontree.data.Data;

public interface Builder {

	/**
	 * 构造决策树
	 * @param data
	 * @return
	 */
	public Object build(Data data);
}
