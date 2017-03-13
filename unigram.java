package cs4740p1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class unigram {
	HashMap<String, Double> map = new HashMap<>();
	HashMap<String, Double> unkmap = new HashMap<>();
	HashMap<String, Double> probMap = new HashMap<>();
	HashMap<String, Double> smoothprobMap = new HashMap<>();
	HashMap<Double, Integer> nMap = new HashMap<>(); //counts of counts
	ArrayList<String> words = new ArrayList<>();
	ArrayList<String> wordswithunk = new ArrayList<>();
	ArrayList<String> test = new ArrayList<>();
	int countall = 0;
	public static final String unk = "<unk>";

	//transfer string to arraylist
	public void stringToAl(String s){
		String[] w = s.split(" ");
		for(String temp:w){
			words.add(temp);
			countall++;
		}

	}

	// put word types and the number of them into the map
	public void add(String s){
		stringToAl(s);
		for(String ss:words){
			String sl = ss.toLowerCase();
			double count = map.containsKey(sl) ? map.get(sl) : 0;
			map.put(sl, count + 1); 
		}
	}

	// get the unigram probability
	public void uniprob(){
		for (HashMap.Entry<String, Double> entry : map.entrySet()) {
			String st = entry.getKey();			
			double num = map.get(st);
			double uniprob = num / countall;
			probMap.put(st, uniprob);

		}
	}

	public String chooseRandom(){

		double r = Math.random();
		double cumulativeProb = 0.0;
		for (Entry<String, Double> entry : probMap.entrySet()) {
			String st = entry.getKey();
			cumulativeProb += probMap.get(st);
			if(r <= cumulativeProb){
				return st;
			}
		}
		return "";
	}

	public String generate(String s){
		add(s);
		uniprob();
		String re = "<s>";    //first pick up a <s>
		while(true){
			String rs = chooseRandom();       //get the next word according to its probability
			//System.out.println(rs);
			if(rs.equals("</s>")){  //as long as it's not </s>, keep doing
				break;
			}
			else{
				re = re + " " + rs;
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
		add(s);
		uniprob();
		String re = given;    //first pick up a <s>
		while(true){
			String rs = chooseRandom();       //get the next word according to its probability
			//System.out.println(rs);
			if(rs.equals("</s>")){  //as long as it's not </s>, keep doing
				break;
			}
			else{
				re = re + " " + rs;
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

    
	public void putnMap(){ //get the nMap:counts of counts 
		for (Entry<String, Double> entry : unkmap.entrySet()) {
			String st = entry.getKey();			
			double num = unkmap.get(st);
			int count = nMap.containsKey(num) ? nMap.get(num) : 0;
			nMap.put(num, count + 1); 
		}
	}
	
	// get the smoothing probability
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
	
	public double perplexity(String p, String s){
		addwithunk(s);
		smoothprob();
		
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
		for (String w : test) {			
			double prob = -Math.log(smoothprobMap.get(w));
			//System.out.println("word in test: " + w + "prob: " + prob);
			sum += prob;
		}
		double pp = Math.exp(sum / test.size());
		return pp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "I am am I you Am I you here";
		unigram a = new unigram();		
		//System.out.println(a.generate(s));
		//String given = "They are him";
		//System.out.println("given word:" + a.generate(given,s));
		String p = "He here";
		System.out.println(a.perplexity(p,s));

	}

}
