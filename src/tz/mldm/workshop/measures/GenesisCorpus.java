package tz.mldm.workshop.measures;

import java.io.*;
import java.util.HashMap;

public class GenesisCorpus implements Corpus {
	
	private int numAllWords;
	private static HashMap<String, Integer> contents=null;
	
	public GenesisCorpus(String dirPath) {
		this.readCorpus(dirPath);
	}
	
	private HashMap<String,Integer> createHashMap(String contents) {
		HashMap<String,Integer> hm=new HashMap<String,Integer>();
		String delims = "[ .,?!]+";
	    String[] contentsArray = contents.split(delims);
	    this.numAllWords = contentsArray.length;
		
	    for(String token:contentsArray) {
	    	token=token.toLowerCase();
	    	if(hm.containsKey(token)) {
	    		hm.put(token, hm.get(token)+1);
	    	}
	    	else
	    		hm.put(token, 1);
	    }
	    
	    return hm;
	}
	
	@Override
	public void readCorpus(String dirPath) {
		try {
		    BufferedReader in = new BufferedReader(new FileReader(dirPath));
		    String str;
		    String contents="";
		    
		    while ((str = in.readLine()) != null) {
		    	contents += str;
		    }
		    this.contents = createHashMap(contents);
		    in.close();  
		} catch (IOException e) {
		}
	}

	@Override
	public int getNumOfAllWords() {
		return this.numAllWords;
	}

	@Override
	public int getNumOfUniqueWords() {
		return this.contents.size();
	}

	@Override
	public int getFreqWord(String word) {
		if(this.contents.containsKey(word))
			return this.contents.get(word);
		else 
			return 0;
	}

	@Override
	public double getProbWordCorpus(String word) {
		return getFreqWord(word) / (double)getNumOfAllWords();
	}
	
}
