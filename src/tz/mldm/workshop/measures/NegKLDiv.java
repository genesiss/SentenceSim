package tz.mldm.workshop.measures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.web.WebResult;

public class NegKLDiv extends SimilarityMeasure {
	
	private double mi_Q, mi_C;
	
	/**
	 * Constructs a NegKLDiv object with parameters miQ and miC.
	 * @param mi_Q
	 * @param mi_C
	 */
	public NegKLDiv(double mi_Q, double mi_C) {
		super();
		this.mi_Q = mi_Q;
		this.mi_C = mi_C;	
	}
	
	double similarityProbSynonyms(String[] s1, String[] s2, HashMap<String, String[]> synMapS1, HashMap<String, String[]> synMapS2, String[] s1POS, String[] s2POS) {
		String Q = "";
		String C = "";
		
		for(int i = 0; i < s1.length; i++) {
			if(s1POS[i].contains("NN"))
				Q += s1[i]+" ";
		}
		for(int i = 0; i < s2.length; i++) {
			if(s2POS[i].contains("NN"))
				C += s2[i]+" ";
		}
		
		String QE = getEnhancedRepresentation(Q);	//enhanced representation of query string Q
		String CE = getEnhancedRepresentation(C);	//enhanced representation of candidate string C
		
		Set<String> vocabulary = this.getVocabulary(QE+" "+CE);	//vocabulary = {words in QE} U {words in CE}
		
		Iterator<String> vocIter = vocabulary.iterator();
		double prob = .0;
		double prob_left = .0;	//P(w|Q)
		double LogProb_right = .0;	//log(P(w|C)
		while(vocIter.hasNext()) {	//for every word in vocabulary
			String nextWord = vocIter.next();
			
			prob_left = getConditionalProb(nextWord, QE, C, mi_Q);	//calculate left probability
			LogProb_right = Math.log(getConditionalProb(nextWord, CE, C, mi_C)+Double.MIN_VALUE);	//calculate log of right probability
			
			prob += prob_left*LogProb_right;	
		}
		
		return prob;
		
	}

	
	@Override
	double similarityProb(String Q, String C) {
		String QE = getEnhancedRepresentation(Q);	//enhanced representation of query string Q
		String CE = getEnhancedRepresentation(C);	//enhanced representation of candidate string C
		Set<String> vocabulary = this.getVocabulary(QE+CE);	//vocabulary = {words in QE} U {words in CE}
		
		Iterator<String> vocIter = vocabulary.iterator();
		double prob = .0;
		double prob_left = .0;	//P(w|Q)
		double LogProb_right = .0;	//log(P(w|C)
		while(vocIter.hasNext()) {	//for every word in vocabulary
			String nextWord = vocIter.next();
			
			prob_left = getConditionalProb(nextWord, QE, C, mi_Q);	//calculate left probability
			LogProb_right = Math.log(getConditionalProb(nextWord, CE, C, mi_C)+Double.MIN_VALUE);	//calculate log of right probability
			
			prob += prob_left*LogProb_right;	
		}
		
		return prob;
	}
	
	/**
	 * Function calculates [(frequency of word in text)+mi*P(word|candidateSent)] / [|text|+mi] 
	 * @param word w
	 * @param text	QE
	 * @param candidateSent C
	 * @param mi mi_q
	 * @return
	 */
	double getConditionalProb(String word, String text, String candidateSent, double mi) {
		int f_wText = getFreqInSentence(word, text);
		double prob_wC = getProbInSentence(word, candidateSent);
		int lenText = getLengthOfSentence(text);
		
		double returnProb = (f_wText + mi*prob_wC)/ (double) (lenText+mi);
		return returnProb;
	}
	
	/**
	 * Returns 50 query descriptions as returned by Bing.
	 * @param s query
	 * @return
	 */
	String getEnhancedRepresentation(String s) {
		SearchResponse response = BingSearch.search(s, 0L);	//submits the query and gets the result
		
		String returnStr = s;
		for (WebResult result : response.getWeb().getResults()) {
			//returnStr += " "+result.getTitle()+" ";
			returnStr += " "+result.getDescription()+" ";	//append search results
		}
		
		return returnStr;
		
		
	}
	
	/**
	 * Build a set of words in s.
	 * @param s
	 * @return
	 */
	Set<String> getVocabulary(String s) {
		String delims = "[ .,?!]+";
	    String[] vocArray = s.split(delims);
	    Set<String> vocabulary = new HashSet<String>();
	    for(String word : vocArray)
	    	vocabulary.add(word);
	    
	    return vocabulary;
	    	
	}
	
}
