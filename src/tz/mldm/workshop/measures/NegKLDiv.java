package tz.mldm.workshop.measures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.web.WebResult;



public class NegKLDiv extends SimilarityMeasure {
	
	private double mi_Q, mi_C;
	
	public NegKLDiv(double mi_Q, double mi_C) {
		this.mi_Q = mi_Q;
		this.mi_C = mi_C;
	}

	
	@Override
	double similarityProb(String Q, String C) {
		String QE = getEnhancedRepresentation(Q);
		String CE = getEnhancedRepresentation(C);
		Set<String> vocabulary = this.getVocabulary(QE+CE);
		
		Iterator<String> vocIter = vocabulary.iterator();
		double prob = .0;
		double prob_left = .0;
		double prob_right = .0;
		while(vocIter.hasNext()) {
			String nextWord = vocIter.next();
			prob_left = getConditionalProb(nextWord, QE, C, mi_Q);

			prob_right = Math.log(getConditionalProb(nextWord, CE, C, mi_C));
			prob += prob_left*prob_right;
			if(Double.isNaN(prob))
				System.out.println("asdad");
		}		
		
		return prob;
	}
	
	double getConditionalProb(String word, String text, String candidateSent, double mi) {
		int f_wText = getFreqInSentence(word, text);
		double prob_wC = getProbInSentence(word, candidateSent);
		int lenText = getLengthOfSentence(text);
		
		double returnProb = (f_wText + mi*prob_wC)/ (double) (lenText+mi);
		if(returnProb==0.0) returnProb=0.0000001;
		return returnProb;
	}
	
	String getEnhancedRepresentation(String s) {
		SearchResponse response = BingSearch.search(s);
		
		String returnStr = "";
		for (WebResult result : response.getWeb().getResults()) {
			returnStr += " "+result.getTitle()+" ";
			returnStr += " "+result.getDescription()+" ";
		}
		
		return returnStr;
		
		
	}
	
	Set getVocabulary(String s) {
		String delims = "[ .,?!]+";
	    String[] vocArray = s.split(delims);
	    Set<String> vocabulary = new HashSet<String>();
	    for(String word : vocArray)
	    	vocabulary.add(word);
	    
	    return vocabulary;
	    	
	}
	
}
