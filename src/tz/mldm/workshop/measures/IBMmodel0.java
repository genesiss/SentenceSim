package tz.mldm.workshop.measures;

public class IBMmodel0 extends SimilarityMeasure {
	
	private double mu;
	private Corpus corpus;

	public IBMmodel0(double mu, Corpus corpus) {
		this.mu=mu;
		this.corpus=corpus;
	}
	
	public  double similarityProb(String s1, String s2) {
		double prob = 0.00;
		s1=s1.toLowerCase();
		s2=s2.toLowerCase();
		for(int i = 1; i <= getLengthOfSentence(s1); i++) {
			prob += (getFreqInSentence(wordAtPos(s1, i), s2) + this.mu*corpus.getProbWordCorpus(wordAtPos(s1, i))) / (double) (getLengthOfSentence(s2) + this.mu);
		}
		return prob;
	}
	
	
}
