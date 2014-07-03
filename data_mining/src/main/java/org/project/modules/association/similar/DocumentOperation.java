package org.project.modules.association.similar;

import java.util.List;
import java.util.Map;

import org.project.modules.association.similar.data.DataLoader;
import org.project.modules.association.similar.data.DataSet;
import org.project.modules.association.similar.data.Document;
import org.project.modules.association.similar.data.DocumentHelper;
import org.project.modules.association.similar.data.DocumentSimilarity;
import org.project.utils.DistanceUtils;
import org.project.utils.WordUtils;

public class DocumentOperation {
	
	public static void calculateTFIDF(List<Document> documents) {
		int docTotalCount = documents.size();
		for (Document document : documents) {
			Map<String, Double> tfidfWords = document.getTfidfWords();
			int wordTotalCount = document.getWords().length;
			Map<String, Integer> docWords = DocumentHelper.docWordsStatistics(document);
			for (String word : docWords.keySet()) {
				double wordCount = docWords.get(word);
				double tf = wordCount / wordTotalCount;
				double docCount = DocumentHelper.wordInDocsStatistics(word, documents) + 1;
				double idf = Math.log(docTotalCount / docCount);
				double tfidf = tf * idf;
				tfidfWords.put(word, tfidf);
			}
			System.out.println("doc " + document.getName() + " finish");
		}
	}
	
	public static void calculateSimilarity(List<Document> documents) {
		for (Document document : documents) {
			String[] topWords = DocumentHelper.docTopNWords(document, 20);
			for (Document odocument : documents) {
				String[] otopWords = DocumentHelper.docTopNWords(odocument, 20);
				String[] allWords = WordUtils.mergeAndRemoveRepeat(topWords, otopWords);
				double[] v1 = DocumentHelper.docWordsVector(document, allWords);
				double[] v2 = DocumentHelper.docWordsVector(odocument, allWords);
				double cosine = DistanceUtils.cosine(v1, v2);
				DocumentSimilarity docSimilarity = new DocumentSimilarity();
				docSimilarity.setDocName1(document.getName());
				docSimilarity.setDocName2(odocument.getName());
				docSimilarity.setVector1(v1);
				docSimilarity.setVector2(v2);
				docSimilarity.setCosine(cosine);
				document.getSimilarities().add(docSimilarity);
			}
			for (DocumentSimilarity similarity : document.getSimilarities()) {
				System.out.println(similarity);
			}
		}
	}

	public static void main(String[] args) {
		String path = "D:\\resources\\01-news-18828";
		DataSet dataSet = DataLoader.load(path);
		calculateTFIDF(dataSet.getDocuments());
		calculateSimilarity(dataSet.getDocuments());
	}
	
}
