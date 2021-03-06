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
		NegKLDiv modelNegKlDiv = new NegKLDiv(0.4,0.6);	//initialize Negative KL divergence (from Similarity Measures for Short Segments of Text)
		AbstractStringMetric levenshtein =  new Levenshtein();
		AbstractStringMetric cosine =  new CosineSimilarity();
		AbstractStringMetric overlap =  new OverlapCoefficient();
		AbstractStringMetric soundex =  new Soundex();
		
		printFirstLine("plot");
		
		float corr[][] = new float[5][65];
		
		Iterator<String> keys = data.keySet().iterator();
		int i = 0;
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
			
			double myOverlap = model0.similarityProb(s1, s2);
			
			corr[0][i] = (float) Double.parseDouble(key.split(" ")[1])/4;
			corr[1][i] = (float) model0Score;
			corr[2][i] = (float) negKLscore;
			corr[3][i] = ovlap;
			corr[4][i] = (float) myOverlap;
			printToFile("out", model0Score, negKLscore, leve, cos, ovlap, sound, key);
			//printForPlot("plot", Double.parseDouble(key.split(" ")[1])/4, model0Score, negKLscore);
			System.out.println(key.split(" ")[0]+" Done!");
			
			i++;
			
		}
		
		corr[2] = scaleIt(corr[2], 1, 0);
		
		for(i = 0; i < corr[1].length; i++) {
			printForPlot("plot", corr[0][i], corr[1][i], corr[2][i], corr[3][i]);
		}
		
		double corrModel0 = correlation(corr[0], corr[1]);
		double corrNegKL = correlation(corr[2], corr[0]);
		double corrOvlap = correlation(corr[3], corr[0]);
		double corrMyOvlap = correlation(corr[4], corr[0]);

		System.out.println("corrModel0: "+corrModel0);
		System.out.println("corrNegKL: "+corrNegKL);
		System.out.println("corrOvlap: "+corrOvlap);
		System.out.println("corrMyOvlap: "+corrMyOvlap);
		
		
	}

	private static double correlation(float[] point1, float[] point2) {	
		float suma = 0;
		float sumb = 0;
		float sumaSq = 0;
		float sumbSq = 0;
		float pSum = 0;
		int n = point1.length;
		for (int i = 0; i < point1.length; i++) {
			suma = suma + point1[i];
			sumb = sumb + point2[i];
			sumaSq = sumaSq + point1[i] * point1[i];
		    sumbSq = sumbSq + point2[i] * point1[i];
		    pSum = pSum + point1[i] * point2[i];
		}
		double numerator = pSum - suma * sumb / n;
		double denominator = Math.sqrt((sumaSq - suma * suma / n) * (sumbSq - sumb * sumb / n));
		return numerator / denominator;

	}

	private static float[] scaleIt (float[] vals, float toMax, float toMin) {
		
		float fromMin = Float.POSITIVE_INFINITY;
		float fromMax = Float.NEGATIVE_INFINITY;
		
		for(int i = 0; i < vals.length; i++) {
			if(vals[i] > fromMax)	fromMax = vals[i];
			if(vals[i] < fromMin)	fromMin = vals[i];
		}
		
		for(int i = 0; i < vals.length; i++) {
			vals[i] = ((vals[i]-fromMin)/(fromMax-fromMin))*(toMax-toMin)+toMin;
		}
		
		return vals;
	}

	private static void printForPlot(String filename, double defa, double model0, double negKL, double ovLap) {
		try{
		    FileWriter fstream = new FileWriter(filename, true);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(defa+" "+model0+" "+negKL+" "+ovLap+"\n");
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
		    out.write("default "+"model0 "+"negKL "+"ovlap: "+"\n");
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
		    out.write(key.split(" ")[0]+"\ndefault: "+Double.parseDouble(key.split(" ")[1])/4+"\nmodel0: "+model0Score+"\nnegKL: "+negKLscore+"\nLevenstein: "+leve+"\nCosine: "+cos+"\nOverlap: "+ovlap+"\nSound: "+sound+"\n\n");
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
