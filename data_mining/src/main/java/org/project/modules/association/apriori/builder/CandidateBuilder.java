package org.project.modules.association.apriori.builder;

import org.project.modules.association.apriori.data.Data;

public class CandidateBuilder implements Builder {

	@Override
	public Object build(Object object) {
		if (!(object instanceof Data)) {
			return null;
		}
		Data data = (Data) object;
		
		return data;
	}

}
