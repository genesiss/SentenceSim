package tz.mldm.workshop.measures;

import rita.wordnet.RiWordnet;

public class Measurements {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//TEST
		RiWordnet wordnet = new RiWordnet();	//wordnet objekt
		String word = "pink";	//trenutna beseda
		String[] poss = wordnet.getPos(word);	//pridobimo vse možne oblike besede (samostalnik, glagol, pridevnik..)
		String[] synonyms;
		for(int i = 0; i < poss.length; i++) {	//gremo čez vse oblike in iščemo sinonime za posamezno obliko
			synonyms = wordnet.getAllSynonyms(word, poss[i], 3);
			System.out.print(poss[i]+": ");
			for(String s : synonyms)	System.out.print(s+" ");
			System.out.println();
		}
		//
		
		Corpus corpus = new GenesisCorpus("documents/Genesis.txt");	//read corpus
		
		IBMmodel0 model0 = new IBMmodel0(0.3, corpus);	//initialize model0 (from Similarity Measures for Tracking Information Flow)
		NegKLDiv modelNegKlDiv = new NegKLDiv(0.2,0.8);	//initialize Negative KL divergence (from Similarity Measures for Short Segments of Text)
		
		String s1 = "God created the Earth";
		String s2 = "The Earth was created by God";
		
		//measure similarity between s1 and s2
		System.out.println(model0.similarityProb(s1, s2));	
		System.out.println(modelNegKlDiv.similarityProb(s1, s2));
		
		

	}

}
