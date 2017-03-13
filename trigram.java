package cs4740p1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class trigram {

	HashMap<String, Double> unkmap = new HashMap<>();
	HashMap<String, Double> unkbimap = new HashMap<>();
	HashMap<String, Double> unktrimap = new HashMap<>();
	HashMap<Double, Double> ntriMap = new HashMap<>(); 

	HashMap<String, Double> smoothtriprobMap = new HashMap<>();
	HashMap<String, Double> starttrinum = new HashMap<>();
	HashMap<String, Double> starttrisum = new HashMap<>();
	HashMap<Double, Integer> nMap = new HashMap<>(); 
	HashMap<Double, Double> nbiMap = new HashMap<>(); 
	HashMap<String, Double> probMap = new HashMap<>();
	HashMap<String, Double> smoothprobMap = new HashMap<>();

	HashMap<String, Double> smoothbiprobMap = new HashMap<>();
	
	HashMap<String, Double> startnum = new HashMap<>();
	HashMap<String, Double> startsum = new HashMap<>();
	ArrayList<String> words = new ArrayList<>();
	ArrayList<String> wordswithunk = new ArrayList<>();
	ArrayList<String> biWordswithunk = new ArrayList<>();
	ArrayList<String> triWordswithunk = new ArrayList<>();
	ArrayList<String> test = new ArrayList<>();
	public static final String unk = "<unk>";
	public static final String unseen3 = "unseen";
	public static final String unseen2 = "unseen";

	
	public void putbiwithunk(){
		for(int i = 0; i < wordswithunk.size() - 1; i++){
			String pps = wordswithunk.get(i) + " " + wordswithunk.get(i+1);
			biWordswithunk.add(pps);
		} 
	}
	
	public void puttriwithunk(){
		for(int i = 0; i < wordswithunk.size() - 2; i++){
			String pps = wordswithunk.get(i) + " " + wordswithunk.get(i+1)+ " " + wordswithunk.get(i+2);
			triWordswithunk.add(pps);
		} 
	}


	
	
	public void addwithunk(String s){   //replace the first occurrence of every word type with <unk>
		String[] n = s.split(" ");
		for(String nw : n){
			String nwl = nw.toLowerCase();
			if(words.contains(nwl)){
				wordswithunk.add(nwl);
			}else{
				words.add(nwl);
				wordswithunk.add(unk);
			}
		}
		
		for(String l : wordswithunk){
			double count = unkmap.containsKey(l) ? unkmap.get(l) : 0;
			unkmap.put(l, count + 1);
		}
//		for (Entry<String, Double> entry : unkmap.entrySet()) {
//			String st = entry.getKey(); 
//			System.out.println("words in unkmap:" + st + "; num:" + unkmap.get(st));
//		}
	}

	public void biAddwithunk(String ns){
		putbiwithunk();
		for(String ss:biWordswithunk){
			String sl = ss.toLowerCase();
			double count = unkbimap.containsKey(sl) ? unkbimap.get(sl) : 0;
			unkbimap.put(sl, count + 1); 
		}

	}
	
	public void triAddwithunk(String ns){   //3 words
		
		puttriwithunk();
		
		for(String ss:triWordswithunk){
			String sl = ss.toLowerCase();
			double count = unktrimap.containsKey(sl) ? unktrimap.get(sl) : 0;
			unktrimap.put(sl, count + 1); 
		}
		for (Entry<String, Double> entry : unktrimap.entrySet()) {
			String st = entry.getKey(); 
			//System.out.println("words in unktrimap:" + st + "; num:" + unktrimap.get(st));
		}
	}
    
	public void putnMap(){ //get the nMap:counts of counts 
		for (Entry<String, Double> entry : unkmap.entrySet()) {
			String st = entry.getKey();			
			double num = unkmap.get(st);
			int count = nMap.containsKey(num) ? nMap.get(num) : 0;
			nMap.put(num, count + 1); 
		}

	}
	
	public void putnbiMap(){ //get the nMap:counts of counts for biwords
		int sum = 0;
		for (Entry<String, Double> entry : unkbimap.entrySet()) {
			String st = entry.getKey();			
			double num = unkbimap.get(st);
			double count = nbiMap.containsKey(num) ? nbiMap.get(num) : 0;
			nbiMap.put(num, count + 1); //all ever seen biwords
		}
		
		double n0 = (double)unkmap.size() * (double)unkmap.size() - (double)unkbimap.size();

		nbiMap.put((double) 0, n0);

	}
	public void smoothprob(){
		putnMap();
		double newsum = 0;
		for (Entry<String, Double> entry : unkmap.entrySet()) {
			String st = entry.getKey();			
			double num = unkmap.get(st);
			if(num < 5){   //use c*		
				
				num = (num + 1) * nMap.get(num + 1) / nMap.get(num);
			}
			unkmap.put(st, num);  //update the unkmap
			newsum += num;
		}
		for (Entry<String, Double> entry : unkmap.entrySet()) {
			String st = entry.getKey();			
			double num = unkmap.get(st);
			double smoothprob = num / newsum;
			smoothprobMap.put(st, smoothprob);
		}
	}
	// get the smoothing probability
	public void smoothbiprob(){
		putnbiMap();
	    
		for (Entry<String, Double> entry : unkbimap.entrySet()) {
			String st = entry.getKey();			
			double num = unkbimap.get(st);
			if(num < 5){   //use c*				
				num = (num + 1) * nbiMap.get(num + 1) / nbiMap.get(num);
			}
			unkbimap.put(st, num);  //update the unkbimap			
		}
		double cstar = nbiMap.get((double)1) / nbiMap.get((double)0);
        

		
		for (Entry<String, Double> entry : unkbimap.entrySet()) {
			String biword = entry.getKey();
			String firword = biword.split(" ")[0];
			double count = startnum.containsKey(firword) ? startnum.get(firword) : 0;
			startnum.put(firword, count + 1); 
			double newc = unkbimap.get(biword);
			double sum = startsum.containsKey(firword) ? startsum.get(firword) : 0;
			startsum.put(firword, sum + newc);		     
		}
		
		for (Entry<String, Double> entry : startsum.entrySet()) {
		    String firword = entry.getKey();
		    double sum = startsum.get(firword);
		    sum += (unkmap.size() - startnum.get(firword)) * cstar;
		    startsum.put(firword, sum);
		}
		for (Entry<String, Double> entry : unkbimap.entrySet()) {
			String st = entry.getKey();			
			double num = unkbimap.get(st);
			double dom = startsum.get(st.split(" ")[0]);
			double smoothprob =  num / dom;
			
			smoothbiprobMap.put(st, smoothprob);
			
		}
		unkbimap.put(unseen2, cstar);

	}
	
	public void putntriMap(){ //get the nMap:counts of counts for triwords
		
		for (Entry<String, Double> entry : unktrimap.entrySet()) {
			String st = entry.getKey();			
			double num = unktrimap.get(st);
			double count = ntriMap.containsKey(num) ? ntriMap.get(num) : 0;
			ntriMap.put(num, count + 1); //all ever seen biwords
		}
		
		double n0 = (double)unkmap.size() * (double)unkmap.size() * (double)unkmap.size() - (double)unktrimap.size();

		ntriMap.put((double) 0, n0);
		for (Entry<Double, Double> entry : ntriMap.entrySet()) {
			double st = entry.getKey(); 
			//System.out.println("words in nmap:" + st + "; num:" + nMap.get(st));
		}
	}
	
	// get the smoothing probability
	public void smoothtriprob(){
		putntriMap();
	
		for (Entry<String, Double> entry : unktrimap.entrySet()) {
			String st = entry.getKey();			
			double num = unktrimap.get(st);
			if(num < 5){   //use c*				
				num = (num + 1) * ntriMap.get(num + 1) / ntriMap.get(num);
			}
			unktrimap.put(st, num);  //update the unkbimap
			//newsum += num;
		}
		double cstar = ntriMap.get((double)1) / ntriMap.get((double)0);
		
		for (Entry<String, Double> entry : unktrimap.entrySet()) {
			String triword = entry.getKey();
			String firtwoword = triword.split(" ")[0] + " " + triword.split(" ")[1];
			double count = starttrinum.containsKey(firtwoword) ? starttrinum.get(firtwoword) : 0;
			starttrinum.put(firtwoword, count + 1); 
			double newc = unktrimap.get(triword);
			double sum = starttrisum.containsKey(firtwoword) ? starttrisum.get(firtwoword) : 0;
			starttrisum.put(firtwoword, sum + newc);		     
		}
		
		for (Entry<String, Double> entry : starttrisum.entrySet()) {
		    String firtwoword = entry.getKey();
		    double sum = starttrisum.get(firtwoword);
		    sum += (unkmap.size() - starttrinum.get(firtwoword)) * cstar;
		    starttrisum.put(firtwoword, sum);
		}

		for (Entry<String, Double> entry : unktrimap.entrySet()) {
			String st = entry.getKey();			
			double num = unktrimap.get(st);
			String bi = st.split(" ")[0] + " " + st.split(" ")[1];
			double dom = starttrisum.get(bi);
			double smoothprob =  num / dom;
			
			smoothtriprobMap.put(st, smoothprob);
			
		}
		unktrimap.put(unseen3, cstar);
	}
	
	public double perplexity(String p, String s){
		addwithunk(s);
		String ns = ""; //new string with unk
		for(String l : wordswithunk){
			ns = l + " ";
		}
		ns = ns.trim();
		biAddwithunk(ns);
		triAddwithunk(ns);
		
		smoothprob();
		smoothbiprob();
		smoothtriprob();
		
		String[] winp = p.split(" ");

		for(String w : winp){
			String wl = w.toLowerCase();
			if(!unkmap.containsKey(wl)){ //replace the new word in test data with <unk>
				test.add(unk);
			}else{
				test.add(wl);
			}
		}
		double sum = 0;
		double prob = 0;
		
		for(int i=0; i<test.size()-2;i++){
			String pps = test.get(i) + " " + test.get(i+1)+ " " + test.get(i+2);
			String lasttwo = test.get(i+1) + " " + test.get(i+2);
			String lastone = test.get(i+2);
			if(smoothtriprobMap.containsKey(pps)){
				
				//double prob1 = 0.9*smoothtriprobMap.get(pps) + 0.1*smoothbiprobMap.get(lasttwo);
				prob = -Math.log(smoothtriprobMap.get(pps));
				//prob = -Math.log(prob1);
			}else{
				
				
				if(smoothbiprobMap.containsKey(lasttwo)){
				   prob = -Math.log(smoothbiprobMap.get(lasttwo));
				}else{
					
				   prob = -Math.log(smoothprobMap.get(lastone));
				}
				
				
			}
			sum += prob;
		}

		double pp = Math.exp(sum / test.size());
		return pp;
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "<s> I saw him i saw her It saw I Saw him I . </s>";
		trigram a = new trigram();
		//System.out.println(a.generate(s));
		//String given = "They are him";
		//System.out.println("given word:" + a.generate(given,s));
		String p = "He here";
		System.out.println(a.perplexity(p,s));
	}

}

