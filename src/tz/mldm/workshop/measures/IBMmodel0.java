package tz.mldm.workshop.measures;

import java.util.HashMap;

public class IBMmodel0 extends SimilarityMeasure {
	
	private double mi;
	private Corpus corpus;

	/**
	 * Constructs an IBMmodel0 with parameters mi and corpus.
	 * @param mi parameter mi
	 * @param corpus Corpus
	 */
	public IBMmodel0(double mi, Corpus corpus) {
		super();
		this.mi=mi;
		this.corpus=corpus;
	}
	
	public double similarityProb(String s1, String s2) {
		double prob = .0;
		s1=s1.toLowerCase();
		s2=s2.toLowerCase();
		for(int i = 1; i <= getLengthOfSentence(s1); i++) {
			prob += (getFreqInSentence(wordAtPos(s1, i), s2) + this.mi*corpus.getProbWordCorpus(wordAtPos(s1, i))) / (double) (getLengthOfSentence(s2) + this.mi);
		}
		return prob;
	}
	
	public double similarityProbSynonyms(String[] s1, String[] s2, HashMap<String, String[]> synMapS1, HashMap<String, String[]> synMapS2) {
		double prob = .0;
		for(int i = 0; i < s1.length; i++) {
			prob += (getFreqInSentence(s1[i], s2, synMapS1) + this.mi*corpus.getProbWordCorpus(synMapS1.get(s1[i]))) / (double) (s2.length + this.mi);
		}
		return prob;
	}
	
}
