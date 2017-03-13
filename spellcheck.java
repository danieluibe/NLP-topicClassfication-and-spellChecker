package cs4740p1;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class spellcheck {
	HashMap<String, Integer> confusionset = new HashMap<>();
	HashMap<String, Integer> confusionpair = new HashMap<>();
	HashMap<Integer, Integer> prepost = new HashMap<>();
	ArrayList<String> prewords = new ArrayList<>();
	ArrayList<String> postwords = new ArrayList<>();
	static int confusew = 0;
	static int correctw = 0;
	public String txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public String[] FilePathGet(String s) {
		File file = new File(s);
		String filepath[];
		filepath = file.list();
		return filepath;
	}

	public String connectfile(String f){
		String re = "";
		String filepath[] = FilePathGet(f);
		for (int k = 0; k < filepath.length; k++) {
			File file = new File(f + filepath[k]);
			re = re + " " + txt2String(file);
		}
		return re;
	}

	public String preprocess(String re) {

		String[] ssplit = re.split(" ");
		String afterpre = "";
		for (String s : ssplit) {

			if (s.length() == 0)
				s = " ";
			if (!Character.isLetterOrDigit(s.charAt(0)) && !s.equals(",") && !s.equals("?") && !s.equals(".")
					&& !s.equals("...") && !s.equals(":") && !s.equals("(") && !s.equals(")") && !s.equals("!")
					&& !s.equals(";"))
				s = " ";
			if (s.contains("@")) {

				s = " ";
			}
			if (s.equals(".") || s.equals("?") || s.equals("...") || s.equals("!")) {
				s += " </S> <S> ";
			}
			afterpre = afterpre + " " + s;
		}
		afterpre = afterpre.replaceAll("( )+", " ");
		// System.out.println(afterpre);
		return afterpre;
	}

	public void putconfmap(){
		File conf = new File("C:/Cornell/cs4740/project1/data_corrected/spell_checking_task_v2/confusion_set.txt");

		try{
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String s = null;
			while((s = br.readLine())!=null){
				if(s.length()!=0){
					s = s.replaceAll("( )+", " ");

					String[] a= s.split(" ");
					if(a.length > 1){
						confusionset.put(a[0], 1);
						confusionset.put(a[1], 1);
						String n = a[0] + " " + a[1];
						confusionpair.put(n,2);	
					}
				}
			}
			br.close();    
		}catch(Exception e){
			e.printStackTrace();
		}

//		for (Entry<String, Integer> entry : confusionpair.entrySet()) {
//			String st = entry.getKey();			
//			System.out.println(st);
//		}

	}

	public ArrayList<String> searchpair(String l){
		ArrayList<String> substi = new ArrayList<>();
		for (Entry<String, Integer> entry : confusionpair.entrySet()) {
			String st = entry.getKey(); 
			String[] p = st.split(" ");
			if(p[0].equals(l)){
				substi.add(p[1]);
			}
			if(p[1].equals(l)){
				substi.add(p[0]);
			}
		}
		return substi;
	}

	public String correct(String s, String corpus){
		HashMap<Double,String> substistring = new HashMap<>();

		String[] sp = s.split(" ");
		fourthgram d1 = new fourthgram();
		double opp = d1.perplexity(preprocess(s), corpus); //only preprocess when calculating perplexity
		substistring.put(opp, s); //origin string
		//System.out.println("original string:"+ s + " pp:" + opp);
		for(int i=0; i<sp.length; i++){
			String l = sp[i].toLowerCase();
			if(confusionset.containsKey(l)){
				
				ArrayList<String> substiwords = searchpair(l);				
				//System.out.println("l:"+ l);
//				for(String sub : substiwords){	
//					System.out.println("suball:"+ sub);
//				}
				for(String sub : substiwords){
					//System.out.println("thissub:"+ sub);
					String n = "";
					for(int j=0; j<i; j++){
						n += sp[j] + " ";
					}
					n += sub + " ";
					for(int j=i+1; j<sp.length; j++){
						n += sp[j] + " ";
					}
					n = n.trim();
					fourthgram d = new fourthgram();
					double pp = d.perplexity(preprocess(n), corpus);
					//System.out.println("substring:"+ n + " pp:" + pp);
					substistring.put(pp, n);  //get pp of every substitute string
				}				
			}
		}

		double temp = 1000;
		for (Entry<Double, String> entry : substistring.entrySet()) {
			double st = entry.getKey();
			temp = Math.min(temp, st);
		}
		String newst = substistring.get(temp);
		int correctnum = getcorrectnum(s, newst);
		correctw += correctnum;
		return newst;		
	}

	public int getcorrectnum(String o, String n){
		int correctnum = 0;
		String[] oa = o.split(" ");
		String[] na = n.split(" ");		
		for(int i=0; i<oa.length;i++){
			if(confusionset.containsKey(oa[i])){
				confusew++;
				//System.out.println("confusew in process:" + confusew);
				if(oa[i].equals(na[i])){
					correctnum++;
					//System.out.println("correctnum in process:" + correctnum);
				}
			}
		}
		return correctnum;
	}
	public String traverse(String s, String corpus){
		putconfmap();           //let the confusion set in
		String[] sp = s.split(" ");
		int i = 0;
		int num = 4;            //the context range(word) 
		while(i < sp.length){
			//System.out.println("i"+i + "; s[i]" + sp[i]);
			String l = sp[i].toLowerCase();
			if(confusionset.containsKey(l)){     //if the word is a confusion word
				String origin = getsubstring(sp,i,num);   //get the word with its context
				//System.out.println("origin:" + origin);
				String modi = correct(origin, corpus);   //get the string with only certain words changed
				//System.out.println("modi:" + modi);
				sp = getback(sp, i, num, modi);   // insert it into original location
				i = i + num + 1;   //continue checking
			}else{
				i++;
			}
		}
		System.out.println("confusew:" + confusew);
		System.out.println("correctw:" + correctw);
		double accuracy = (double)correctw / (double) confusew;
		System.out.println("accuracy:" + accuracy);
		String re = "";                //connect it into a string
		for(int t = 0; t < sp.length; t++){
			re += sp[t] + " ";
		}
		return re.trim();
	}

	public String getsubstring(String[] sp, int m, int num){
		String re = "";	
		for(int i = m-num; i <= m + num; i++){
			if(i>=0 && i<sp.length){
				re += sp[i] + " ";
			}else{
				re += "";
			}
		}
		return re.trim();
	}

	public String[] getback(String[] sp, int m, int num, String modi){
		String[] modia = modi.split(" ");
		int j = 0;
		for(int i = m-num; i <= m + num; i++){
			if(i>=0 && i<sp.length){
				sp[i] = modia[j];
				j++;
			}

		}
		return sp;
	}

	public String connectfileWithPercentage(String f,  double d) {
		String re = "";
		String FilePath[] = FilePathGet(f);
		//System.out.println(f);
		for (int i = 0; i < (int) (FilePath.length * d); i++) {
			File File = new File(f + FilePath[i]);
			re = re + " " + txt2String(File);
		}
		return re;
	}
	
	public void evaluate(){
		String file = "C:/Cornell/cs4740/project1/data_corrected/spell_checking_task_v2/religion/train_docs/";
		double percentage = 0.8;
		String corpus = preprocess(connectfileWithPercentage(file, percentage));
		
		String[] testfile = FilePathGet(file);
		int fnWithP = (int)(testfile.length * percentage);
		for(int i = fnWithP; i < testfile.length; i++)           //each development set in each topic
		{
			String txtname = file + testfile[i];
			System.out.println("file name:" + txtname);
			File txtfile = new File(txtname);
			String testdata = txt2String(txtfile);
			traverse(testdata, corpus);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		spellcheck a = new spellcheck();
        //a.evaluate();
		String file = "C:/Cornell/cs4740/project1/data_corrected/spell_checking_task_v2/religion/train_docs/";		
		String corpus = a.preprocess(a.connectfile(file));
		String testf = "C:/Cornell/cs4740/project1/data_corrected/spell_checking_task_v2/religion/test_modified_docs/";
		String[] testfile = a.FilePathGet(testf); 
		for(String sf : testfile){
			if(!sf.equals("religion_file0_modified.txt")&&!sf.equals("religion_file1_modified.txt")&&!sf.equals("religion_file2_modified.txt")&&!sf.equals("religion_file10_modified.txt")&&!sf.equals("religion_file11_modified.txt")&&!sf.equals("religion_file12_modified.txt")&&!sf.equals("religion_file13_modified.txt")&&!sf.equals("religion_file14_modified.txt")&&!sf.equals("religion_file15_modified.txt")&&!sf.equals("religion_file16_modified.txt")&&!sf.equals("religion_file17_modified.txt")&&!sf.equals("religion_file18_modified.txt")&&!sf.equals("religion_file19_modified.txt")&&!sf.equals("religion_file20_modified.txt")&&!sf.equals("religion_file21_modified.txt")&&!sf.equals("religion_file22_modified.txt")&&!sf.equals("religion_file23_modified.txt")&&!sf.equals("religion_file24_modified.txt")&&!sf.equals("religion_file25_modified.txt")&&!sf.equals("religion_file26_modified.txt")&&!sf.equals("religion_file27_modified.txt")&&!sf.equals("religion_file28_modified.txt")&&!sf.equals("religion_file29_modified.txt")&&!sf.equals("religion_file30_modified.txt")&&!sf.equals("religion_file31_modified.txt")&&!sf.equals("religion_file32_modified.txt")&&!sf.equals("religion_file33_modified.txt")&&!sf.equals("religion_file34_modified.txt")&&!sf.equals("religion_file35_modified.txt")&&!sf.equals("religion_file36_modified.txt")&&!sf.equals("religion_file37_modified.txt")&&!sf.equals("religion_file38_modified.txt")&&!sf.equals("religion_file39_modified.txt")){
			System.out.println(sf);
			String txtname = testf + sf;
			File txtfile = new File(txtname);
			String testdata = a.txt2String(txtfile);
			String modi = a.traverse(testdata, corpus);
			try( PrintWriter out = new PrintWriter("C:/Cornell/cs4740/project1/data_corrected/spell_checking_task_v2/religion/test_docs/"+sf)){
			    out.println(modi);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		
		
		

	}

}

