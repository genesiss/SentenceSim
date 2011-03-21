package tz.mldm.workshop.measures;

public class IBMmodel0 implements SimilarityMeasure {

	@Override
	public int getFreqInSentence(String word, String sentence) {
		int freq = 0;
		for(String term : sentence.split(" "))
			if(term.equalsIgnoreCase(word))
				freq++;
		return freq;
	}

	@Override
	public double similarityProb(String s1, String s2) {
		double prob = 0.00;
		for(int i = 1; i <= getLengthOfSentence(s1); i++) {
			prob += getFreqInSentence(wordAtPos(s1, i), s2) / (double) getLengthOfSentence(s2);
		}
		return prob;
	}

	@Override
	public int getLengthOfSentence(String s) {
		return s.split(" ").length;
	}

	@Override
	public String wordAtPos(String s, int pos) {
		String[] words =  s.split(" ");
		return words[pos-1];
	}

}
