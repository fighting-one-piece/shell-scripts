package org.project.modules.decisiontree.node;

import java.io.Serializable;

import org.project.modules.decisiontree.data.Data;
import org.project.modules.decisiontree.data.Instance;

public abstract class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public abstract Object classify(Data data);
	
	public abstract Object classify(Instance... instances);
	
}
