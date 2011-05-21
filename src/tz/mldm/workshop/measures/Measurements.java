package tz.mldm.workshop.measures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.OverlapCoefficient;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;



public class Measurements {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try{
		    FileWriter fstream = new FileWriter("plot");
		    FileWriter fstream2 = new FileWriter("out");
		    fstream.close();
		    fstream2.close();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		TreeMap<String, String[]> data = readData("data");
		Corpus corpus = new GenesisCorpus("documents/Genesis.txt");	//read corpus
		IBMmodel0 model0 = new IBMmodel0(0.2, corpus);	//initialize model0 (from Similarity Measures for Tracking Information Flow)
		NegKLDiv modelNegKlDiv = new NegKLDiv(0.2,0.8);	//initialize Negative KL divergence (from Similarity Measures for Short Segments of Text)
		AbstractStringMetric levenshtein =  new Levenshtein();
		AbstractStringMetric cosine =  new CosineSimilarity();
		AbstractStringMetric overlap =  new OverlapCoefficient();
		AbstractStringMetric soundex =  new Soundex();
		
		printFirstLine("plot");
		
		
		Iterator<String> keys = data.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			String s1 = data.get(key)[0];
			String s2 = data.get(key)[1];
			
			String[] s1Tokens = SimilarityMeasure.getTokens(s1, new StandardAnalyzer(Version.LUCENE_CURRENT));
			String[] s2Tokens = SimilarityMeasure.getTokens(s2, new StandardAnalyzer(Version.LUCENE_CURRENT));
			String[] s1POS = SimilarityMeasure.getPOS(s1Tokens);
			String[] s2POS = SimilarityMeasure.getPOS(s2Tokens);
			double[] s1probs = SimilarityMeasure.getProbs(s1Tokens);
			double[] s2probs = SimilarityMeasure.getProbs(s2Tokens);
			HashMap<String, String[]> s1synonyms = SimilarityMeasure.getSynonyms(s1Tokens, s1probs, s1POS);
			HashMap<String, String[]> s2synonyms = SimilarityMeasure.getSynonyms(s2Tokens, s2probs, s2POS);
			
			double model0Score = model0.similarityProbSynonyms(s1Tokens, s2Tokens, s1synonyms, s2synonyms);
			double negKLscore = modelNegKlDiv.similarityProbSynonyms(s1Tokens, s2Tokens, s1synonyms, s2synonyms, s1POS, s2POS);
			float leve = levenshtein.getSimilarity(s1, s2);
			float cos = cosine.getSimilarity(s1, s2);
			float ovlap = overlap.getSimilarity(s1, s2);
			float sound = soundex.getSimilarity(s1, s2);
			
			printToFile("out", model0Score, negKLscore, leve, cos, ovlap, sound, key);
			printForPlot("plot", Double.parseDouble(key.split(" ")[1])/4, model0Score, 1/(negKLscore*-1), leve, cos, ovlap, sound);
			System.out.println(key.split(" ")[0]+" Done!");
			
		}

		
		
	}



	private static void printForPlot(String filename, double defa, double model0, double negKL, float leve, float cos, float ovlap, float sound) {
		try{
		    FileWriter fstream = new FileWriter(filename, true);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(defa+" "+model0+" "+negKL+" "+leve+" "+cos+" "+ovlap+" "+sound+"\n");
		    out.close();
		    }
		catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		}
		
	}



	private static void printFirstLine(String filename) {
		try{
		    FileWriter fstream = new FileWriter(filename, true);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write("default "+"model0 "+"negKL "+"Levenstein "+"Cosine "+"Overlap "+"Sound "+"\n");
		    out.close();
		    }
		catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		}
		
	}



	private static void printToFile(String filename, double model0Score, double negKLscore, float leve, float cos, float ovlap, float sound, String key) {
		
		try{
		    FileWriter fstream = new FileWriter(filename, true);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(key.split(" ")[0]+"\ndefault: "+Double.parseDouble(key.split(" ")[1])/4+"\nmodel0: "+model0Score+"\nnegKL: "+1/(negKLscore*-1)+"\nLevenstein: "+leve+"\nCosine: "+cos+"\nOverlap: "+ovlap+"\nSound: "+sound+"\n\n");
		    out.close();
		    }
		catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		}
	}



	private static TreeMap<String, String[]> readData(String filename) {
		
		TreeMap<String, String[]> map = new TreeMap<String, String[]>();
		
		try {
		    BufferedReader in = new BufferedReader(new FileReader(filename));
		    String str;
		    while ((str = in.readLine()) != null) {
		        String key = str;
		        String arr[] = new String[2];
		        arr[0] = in.readLine();
		        arr[1] = in.readLine();
		        map.put(key, arr);
		        in.readLine();
		    }
		    in.close();
		}
		catch (IOException e) {
		}
		
		return map;
		
	}

}
