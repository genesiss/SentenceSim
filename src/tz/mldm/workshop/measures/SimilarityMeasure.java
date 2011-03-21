package tz.mldm.workshop.measures;

public interface SimilarityMeasure {
	
	
	/**
	 * Get frequency of a word in given sentence.
	 * @param word
	 * @param sentence
	 * @return
	 */
	int getFreqInSentence(String word, String sentence);
	
	/**
	 * Measure the similarity between s1 and s2.
	 * @param s1
	 * @param s2
	 * @return
	 */
	double similarityProb(String s1, String s2);
	
	/**
	 * Get number of words in sentence s.
	 * @param s
	 * @return	number of words in sentence s
	 */
	int getLengthOfSentence(String s);
	
	/**
	 * Returns words at position pos in sentence s
	 * @param s sentence
	 * @param pos position
	 * @return word at position pos in sentence s
	 */
	String wordAtPos(String s, int pos);
	
	
	
	

}
