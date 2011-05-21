package tz.mldm.workshop.measures;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public abstract class SimilarityMeasure {
	
	InputStream modelIn = null;
	POSModel model = null;
	static POSTaggerME tagger = null;

	
	public SimilarityMeasure() {
		try {
			modelIn = new FileInputStream("en-pos-maxent.bin");
			model = new POSModel(modelIn);
			tagger = new POSTaggerME(model);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	  
	  
	
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
	 * Get frequency of a word in a given array of words.
	 * @param word
	 * @param sentence
	 * @return
	 */
	static int getFreqInSentence(String word, String[] sentence, HashMap<String, String[]> synMap) {
		int freq = 0;
		for(String syn : synMap.get(word)) {
			for(String s : sentence)
				if(syn.equalsIgnoreCase(s))
					freq++;
		}
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
	
	/**
	 * Returns parts of speech for input tokens.
	 * @param in
	 * @return
	 */
	static String[] getPOS(String[] in) {
		return tagger.tag(in);
	}
	
	/**
	 * Returns probabilities of parts of speech for input tokens.
	 * @param in
	 * @return
	 */
	static double[] getProbs(String[] in) {
		String[] tags =  tagger.tag(in);
		return tagger.probs();
	}
	
	/**
	 * Returns hashmap of synonyms.
	 * @param tkns
	 * @param probs
	 * @param tags
	 * @return
	 */
	static HashMap<String, String[]> getSynonyms(String[] tkns, double[] probs, String[] tags) {
		HashMap<String, String[]> synMap = new HashMap<String, String[]>();
		String[] synonyms;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		for(int i = 0; i < tags.length; i++) {
			if(probs[i] < 0.8)	{
				synMap.put(tkns[i], new String[]{tkns[i]});
				continue;
			}
			
			if(tags[i].contains("NN")) {
				Synset[] synsets = database.getSynsets(tkns[i], SynsetType.NOUN);
				if(synsets.length == 0) {
					synMap.put(tkns[i], new String[]{tkns[i]});
					continue;
				}
				NounSynset nounSynset = (NounSynset)(synsets[0]);
				synonyms = nounSynset.getWordForms();
				synonyms = toLowerChars(synonyms);
				synMap.put(tkns[i], synonyms);
			}
			
			else if(tags[i].contains("JJ")) {
				Synset[] synsets = database.getSynsets(tkns[i], SynsetType.ADJECTIVE);
				if(synsets.length == 0) {
					synMap.put(tkns[i], new String[]{tkns[i]});
					continue;
				}
				AdjectiveSynset adjSynset = (AdjectiveSynset)(synsets[0]);
				synonyms = adjSynset.getWordForms();
				synonyms = toLowerChars(synonyms);
				synMap.put(tkns[i], synonyms);
			}
			
			else if(tags[i].contains("VB")) {
				Synset[] synsets = database.getSynsets(tkns[i], SynsetType.VERB);
				if(synsets.length == 0) {
					synMap.put(tkns[i], new String[]{tkns[i]});
					continue;
				}
				VerbSynset verbSynset = (VerbSynset)(synsets[0]);
				synonyms = verbSynset.getWordForms();
				synonyms = toLowerChars(synonyms);
				synMap.put(tkns[i], synonyms);
			}
			
			else if(tags[i].contains("RB")) {
				Synset[] synsets = database.getSynsets(tkns[i], SynsetType.ADVERB);
				if(synsets.length == 0) {
					synMap.put(tkns[i], new String[]{tkns[i]});
					continue;
				}
				AdverbSynset adverbSynset = (AdverbSynset)(synsets[0]);
				synonyms = adverbSynset.getWordForms();
				synonyms = toLowerChars(synonyms);
				synMap.put(tkns[i], synonyms);
			}
			synMap.put(tkns[i], new String[]{tkns[i]});
		}
		
		HashMap<String, String[]> normSynMap = new HashMap<String, String[]>();
		Iterator<String> it = synMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String[] val = synMap.get(key);
			HashSet<String> hs = new HashSet<String>();
			for(String s : val) hs.add(s);
			String[] newArr = new String[hs.size()];
			hs.toArray(newArr);
			normSynMap.put(key, newArr);
		}
		
		return normSynMap;
	}
	
	/**
	 * String array to lower shars.
	 * @param synonyms
	 * @return
	 */
	private static String[] toLowerChars(String[] synonyms) {
		String[] s = new String[synonyms.length];
		for(int i = 0; i < synonyms.length; i++) {
			s[i] = synonyms[i].toLowerCase();
		}
		return s;
	}



	/**
	 * Tokenizes given text, removes common words.
	 * @param text
	 * @return
	 */
	static String[] getTokens(String text, Analyzer analyzer) {
		TokenStream tkStream = analyzer.tokenStream("FIELDNAME", new StringReader(text));
		TermAttribute termAttribute = tkStream.getAttribute(TermAttribute.class);
		
		ArrayList<String> tokens = new ArrayList<String>();
		
		try {
			while (tkStream.incrementToken()) {
			    tokens.add(termAttribute.term().toLowerCase());
			}
			tkStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		String[] toReturn = new String[tokens.size()];
		int i = 0;
		for(String s : tokens) {
			toReturn[i] = s;
			i++;
		}		
		return toReturn;
	}
	
	
	

}
