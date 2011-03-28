package tz.mldm.workshop.measures;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GenesisCorpus corpus = new GenesisCorpus("Genesis.txt");
		IBMmodel0 model0 = new IBMmodel0(20, corpus);
		
		String s1 = "God is great!";
		String s2 = "Fegets are great!";
		
		System.out.println(model0.similarityProb(s1, s2));
		
		NegKLDiv modelNegKlDiv = new NegKLDiv(20,1);
		System.out.println(-modelNegKlDiv.similarityProb(s1, s2));

	}

}
