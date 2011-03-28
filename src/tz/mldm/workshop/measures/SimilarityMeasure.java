package tz.mldm.workshop.measures;

import java.util.HashSet;
import java.util.Set;

public abstract class SimilarityMeasure {
	
	
	/**
	 * Get frequency of a word in given sentence.
	 * @param word
	 * @param sentence
	 * @return
	 */
	//TODO splitanje po vseh locilih
	static int getFreqInSentence(String word, String sentence) {
		int freq = 0;
		for(String term : sentence.split(" "))
			if(term.equalsIgnoreCase(word))
				freq++;
		return freq;
	}
	
	static double getProbInSentence(String word, String sentence) {
		int f = getFreqInSentence(word, sentence);
		int numW = getNumOfAllWords(sentence);
		
		return(f/(double) numW);
	}
	
	static int getNumOfAllWords(String s) {
		String delims = "[ .,?!]+";
		return s.split(delims).length;
	}

	
	static int getNumOfUniqueWords(String s) {
		Set<String> set = new HashSet<String>();
		String delims = "[ .,?!]+";
		String[] words = s.split(delims);
		for(String w : words) {
			w = w.toLowerCase();
			set.add(w);
		}
		return set.size();
	}
	
	/**
	 * Measure the similarity between s1 and s2.
	 * @param s1
	 * @param s2
	 * @return
	 */
	abstract double similarityProb(String s1, String s2);
	
	/**
	 * Get number of words in sentence s.
	 * @param s
	 * @return	number of words in sentence s
	 */
	static int getLengthOfSentence(String s) {
		return s.split(" ").length;
	}
	
	/**
	 * Returns words at position pos in sentence s
	 * @param s sentence
	 * @param pos position
	 * @return word at position pos in sentence s
	 */
	static String wordAtPos(String s, int pos) {
		String[] words =  s.split(" ");
		return words[pos-1];
	}
	
	
	
	

}
