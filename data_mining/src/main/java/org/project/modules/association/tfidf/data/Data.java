package org.project.modules.association.tfidf.data;

import java.util.ArrayList;
import java.util.List;

public class Data {

	private List<Document> documents = null;
	
	private List<DocumentSimilarity> similarities = null;

	public List<Document> getDocuments() {
		if (null == documents) {
			documents = new ArrayList<Document>();
		}
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public List<DocumentSimilarity> getSimilarities() {
		if (null == similarities) {
			similarities = new ArrayList<DocumentSimilarity>();
		}
		return similarities;
	}

	public void setSimilarities(List<DocumentSimilarity> similarities) {
		this.similarities = similarities;
	}

	public int wordDocStatistics(String word) {
		int sum = 0;
		for (Document document : getDocuments()) {
			if (document.containWord(word)) {
				sum += 1;
			}
		}
		return sum;
	}
	
}
