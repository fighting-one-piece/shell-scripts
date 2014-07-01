package org.project.modules.association.tfidf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.modules.association.tfidf.data.Data;
import org.project.modules.association.tfidf.data.DataLoader;
import org.project.modules.association.tfidf.data.Document;
import org.project.modules.association.tfidf.data.DocumentSimilarity;
import org.project.utils.DistanceUtils;
import org.project.utils.WordUtils;

public class TFIDF {

	public static void main(String[] args) {
		Data data = DataLoader.load("D:\\resources\\01-news-18828");
		List<Document> documents = data.getDocuments();
		int doc_len = documents.size();
		Map<String, Map<String, Double>> map = new HashMap<String, Map<String,Double>>();
		for (Document document : documents) {
			Map<String, Double> tfidfWords = document.getTfidfWords();
			map.put(document.getName(), tfidfWords);
			String[] words = document.getWords();
			int word_len = words.length;
			Map<String, Integer> wordMap = document.wordStatistics();
			for (String word : wordMap.keySet()) {
				double count = wordMap.get(word);
				double tf = count / word_len;
				double docCount = data.wordDocStatistics(word) + 1;
				double idf = Math.log(doc_len / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " finish");
		}
		for (Document document : documents) {
			String[] topWords = document.topWords(20);
			for (Document odocument : documents) {
				String[] otopWords = odocument.topWords(20);
				String[] allWords = WordUtils.mergeAndRemoveRepeat(topWords, otopWords);
				double[] v1 = document.vector(allWords);
				double[] v2 = odocument.vector(allWords);
				double cosine = DistanceUtils.cosine(v1, v2);
				DocumentSimilarity docSimilarity = new DocumentSimilarity();
				docSimilarity.setDocName1(document.getName());
				docSimilarity.setDocName2(odocument.getName());
				docSimilarity.setVector1(v1);
				docSimilarity.setVector2(v2);
				docSimilarity.setCosine(cosine);
				data.getSimilarities().add(docSimilarity);
			}
		}
		for (DocumentSimilarity similarity : data.getSimilarities()) {
			System.out.println(similarity);
		}
	}
	
}
