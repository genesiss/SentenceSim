package tz.mldm.workshop.measures;


//should be an abstract class!
public interface Corpus {
	
	/**
	 * Reads corpus from directory dirPath.
	 * @param dirPath
	 * @return read Corpus
	 */
	void readCorpus(String dirPath);
	
	/**
	 * Get number of all words in corpus
	 * @return number of all words in corpus
	 */
	int getNumOfAllWords();
	
	/**
	 * Get number of unique words in corpus
	 * @return number of unique words in corpus
	 */
	int getNumOfUniqueWords();
	
	/**
	 * Get frequency of a word in a given Corpus
	 * @param word	Word we are looking for in a corpus
	 * @return	frequency of the word in a given corpus
	 */
	int getFreqWord(String word);
	
	/**
	 * Get probability of a word in given corpus.
	 * @param word
	 * @return
	 */
	double getProbWordCorpus(String word);
	
	/**
	 * Get probability of words in given corpus.
	 * @param synonyms
	 * @return
	 */
	double getProbWordCorpus(String[] synonyms);

}
