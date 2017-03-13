package cs4740p1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class bigram {
	HashMap<String, Double> map = new HashMap<>();
	HashMap<String, Double> unkmap = new HashMap<>();
	HashMap<String, Double> startnum = new HashMap<>();
	HashMap<String, Double> startsum = new HashMap<>();
	HashMap<String, Double> bimap = new HashMap<>();
	HashMap<String, Double> unkbimap = new HashMap<>();
	HashMap<Double, Integer> nMap = new HashMap<>(); 
	HashMap<Double, Double> nbiMap = new HashMap<>(); 
	HashMap<String, Double> probMap = new HashMap<>();
	HashMap<String, Double> smoothprobMap = new HashMap<>();
	HashMap<String, Double> biprobmap = new HashMap<>();
	HashMap<String, Double> smoothbiprobMap = new HashMap<>();
	ArrayList<String> words = new ArrayList<>();
	ArrayList<String> wordswithunk = new ArrayList<>();
	ArrayList<String> biWords = new ArrayList<>();
	ArrayList<String> biWordswithunk = new ArrayList<>();
	ArrayList<String> test = new ArrayList<>();
	int countall = 0;
	public static final String unk = "<unk>";
	public static final String unseen = "unseen";

	public void stringToAl(String s){
		String[] w = s.split(" ");
		for(String temp:w){
			words.add(temp);
			countall++;
		}

	}


	public void add(String s){
		stringToAl(s);
		for(String ss:words){
			String sl = ss.toLowerCase();
			double count = map.containsKey(sl) ? map.get(sl) : 0;
			map.put(sl, count + 1); 
		}
		for (Entry<String, Double> entry : map.entrySet()) {
			String st = entry.getKey(); 
			//System.out.println("words:" + st + "; num:" + map.get(st));

		}
	}

	// transfer the string to array, each string in the array has two continuous words
	public void putbi(){
		for(int i = 0; i < words.size() - 1; i++){
			String pps = words.get(i) + " " + words.get(i+1);
			biWords.add(pps);
		} 
	}	
	
	public void putbiwithunk(){
		for(int i = 0; i < wordswithunk.size() - 1; i++){
			String pps = wordswithunk.get(i) + " " + wordswithunk.get(i+1);
			biWordswithunk.add(pps);
		} 
	}



	public void biAdd(String s){
		putbi();
		for(String ss:biWords){
			String sl = ss.toLowerCase();
			double count = bimap.containsKey(sl) ? bimap.get(sl) : 0;
			bimap.put(sl, count + 1); 
		}
	}


	public void biprob(){  //s1 s2 ¡ª¡ª P(s2 | s1)
		for (Entry<String, Double> entry : bimap.entrySet()) {
			String st = entry.getKey();
			
			double num = bimap.get(st);
			double dom = map.get(st.split(" ")[0]);
			double prob = num / dom;
			biprobmap.put(st, prob);
			//System.out.println("key:" + st+" prob:"+prob);
		}
	}

	public String chooseRandom(String last){

		double r = Math.random();
		double cumulativeProb = 0.0;
		for (Entry<String, Double> entry : biprobmap.entrySet()) {
			String st = entry.getKey();
			if(st.split(" ")[0].equals(last)){
				cumulativeProb += biprobmap.get(st);
				if(r <= cumulativeProb){
					return st;
				}
			}
		}
		return "";
	}

	public String generate(String s){
		add(s);   //put map
		biAdd(s);  // put bimap
		biprob();  // put biprobmap
		String re = "<s>";    //first pick up a <s>
		while(true){
			String last = re.substring(re.lastIndexOf(" ")+1); // get the last word in the sentence
			String rs = chooseRandom(last);       //get the next word according to its probability
			String next = rs.split(" ")[1];
			//System.out.println("picked words:" + rs);
			if(next.equals("</s>")){  //as long as it's not </s>, keep doing
				re = re + " " + next;
				break;
			}
			else{
				re = re + " " + next;
			}
		}
		re = re.replace("<s>", ""); // get rid of all markers
		re = re.replace("</s>", "");
		re = re.trim(); //get rid of the leading space 
		if(re.length()>0){   //in case the only word picked is </s>
			re = re.substring(0, 1).toUpperCase() + re.substring(1);
		}
		re = re.replaceAll("( )+", " ");  //<s> picked may cause more spaces, this will turn them into one space
		return re;
	}
	
	public String generate(String given, String s){
		add(s);   //put map
		biAdd(s);  // put bimap
		biprob();  // put biprobmap
		String re = given;    
		while(true){
			String last = re.substring(re.lastIndexOf(" ")+1).toLowerCase(); // get the last word in the sentence
			//System.out.println("picked words:" + last);
			if(!map.containsKey(last)){
				return re;
			}
			String rs = chooseRandom(last);       //get the next word according to its probability
			
			String next = rs.split(" ")[1];
			//System.out.println("picked words:" + rs);
			if(next.equals("</s>")){  //as long as it's not </s>, keep doing
				re = re + " " + next;
				break;
			}
			else{
				re = re + " " + next;
			}
		}
		re = re.replace("<s>", ""); // get rid of all markers
		re = re.replace("</s>", "");
		re = re.trim(); //get rid of the leading space 
		if(re.length()>0){   //in case the only word picked is </s>
			re = re.substring(0, 1).toUpperCase() + re.substring(1);
		}
		re = re.replaceAll("( )+", " ");  //<s> picked may cause more spaces, this will turn them into one space
		return re;
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
	}

	public void biAddwithunk(String ns){
		putbiwithunk();
		for(String ss:biWordswithunk){
			String sl = ss.toLowerCase();
			double count = unkbimap.containsKey(sl) ? unkbimap.get(sl) : 0;
			unkbimap.put(sl, count + 1); 
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
		unkbimap.put(unseen, cstar);

	}
	
	public double perplexity(String p, String s){
		addwithunk(s);
		String ns = ""; //new string with unk
		for(String l : wordswithunk){
			ns = l + " ";
		}
		ns = ns.trim();
		biAddwithunk(ns);
		smoothprob();
		smoothbiprob();
		
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
		double prob;
		
		for(int i=0; i<test.size()-1;i++){
			String pps = test.get(i) + " " + test.get(i+1);
			if(smoothbiprobMap.containsKey(pps)){
				prob = -Math.log(smoothbiprobMap.get(pps));
			}else{
				//double prob1 = unkbimap.get(unseen)/startsum.get(test.get(i));
				double prob1 = 0.4*smoothprobMap.get(test.get(i + 1));
				prob = -Math.log(prob1);
				
			}
			sum += prob;
		}
		
		double pp = Math.exp(sum / test.size());
		return pp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "<s> I saw him i saw her It saw I Saw them I . </s>";
		bigram a = new bigram();
		//System.out.println(a.generate(s));
		//String given = "They are him";
		//System.out.println("given word:" + a.generate(given,s));
		String p = "He here";
		System.out.println(a.perplexity(p,s));
	}

}
