package org.project.modules.classifier.decisiontree.builder;

import java.util.List;

import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;

public class BuilderAbstractImpl implements Builder {

	@Override
	public Object build(Data data) {
		return null;
	}
	
	protected Object handleInstances(List<Instance> instances) {
		if (instances.size() == 1) {
			return instances.get(0).getCategory();
		} else if (instances.size() > 1) {
			boolean isEqual = true;
			Object category = instances.get(0).getCategory();
			for (Instance instance : instances) {
				if (!category.equals(instance.getCategory())) {
					isEqual = false;
				}
			}
			if (isEqual) return category;
		}
		return null;
	}

}
