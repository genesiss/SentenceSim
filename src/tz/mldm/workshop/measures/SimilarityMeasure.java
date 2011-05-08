package tz.mldm.workshop.measures;

import java.util.HashSet;
import java.util.Set;

public abstract class SimilarityMeasure {
	
	
	/**
	 * Get frequency of a word in a given sentence.
	 * @param word
	 * @param sentence
	 * @return
	 */
	static int getFreqInSentence(String word, String sentence) {
		int freq = 0;
		String delims = "[ .,?!]+";
		for(String term : sentence.split(delims))
			if(term.equalsIgnoreCase(word))
				freq++;
		return freq;
	}
	
	/**
	 * Get a probability of word in a given sentence. Probability is calculated
	 * as frequency/(all words in the sentence).
	 * @param word
	 * @param sentence
	 * @return
	 */
	static double getProbInSentence(String word, String sentence) {
		int f = getFreqInSentence(word, sentence);
		int numW = getNumOfAllWords(sentence);
		
		return(f/(double) numW);
	}
	
	/**
	 * Get a number of all words in a sentence.
	 * @param s
	 * @return
	 */
	static int getNumOfAllWords(String s) {
		String delims = "[ .,?!]+";
		return s.split(delims).length;
	}

	/**
	 * Get number of unique words in sentence.
	 * @param s
	 * @return
	 */
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
		String delims = "[ .,?!]+";
		return s.split(delims).length;
	}
	
	/**
	 * Returns words at position pos in sentence s
	 * @param s sentence
	 * @param pos position
	 * @return word at position pos in sentence s
	 */
	static String wordAtPos(String s, int pos) {
		String delims = "[ .,?!]+";
		String[] words =  s.split(delims);
		return words[pos-1];
	}
	
	
	
	

}
