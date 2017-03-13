package cs4740p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

public class ppevaluation {
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

	public String connectfile(String f) {
		String re = "";
		String filepath[] = FilePathGet(f);
		for (int k = 0; k < filepath.length; k++) {
			File file = new File(f + filepath[k]);
			re = re + " " + txt2String(file);
		}
		return re;
	}

	public String connectfileWithPercentage(String f,  double d) {
		String re = "";
		String FilePath[] = FilePathGet(f);
		System.out.println(f);
		for (int i = 0; i < (int) (FilePath.length * d); i++) {
			File File = new File(f + FilePath[i]);
			re = re + " " + txt2String(File);
			//System.out.println("file length: " + (int) (FilePath.length * d)+"file: " + FilePath[i] + "was input");
		}
		return re;
	}

	public int minimumpp(double[] a) {
		double temp = Double.MAX_VALUE;
		int mindex = 0;
		for(int i=0; i<a.length; i++){
			if(a[i] < temp){
				temp = a[i];
				mindex = i;
			}
		}
		return mindex;
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int fnWithP = 0;
		int filenumber = 0;
		ppevaluation a = new ppevaluation();
		
		String co = "C:/Cornell/cs4740/project1/data_corrected/classification task/";
		String[] coa = a.FilePathGet(co);
		double percentage = 0.8;  //do classification with certain percentage

		//get 7 corpus
		String[] corpusArray = new String[7];
		String[] topicname = new String[7];
		int x = 0;
		for(String coam : coa){               
			if(!coam.equals("test_for_classification")){
				String f = co + coam + "/train_docs/";
				System.out.println("1 corpus index:" + x + " ;name:" + coam);
				corpusArray[x] = a.preprocess(a.connectfileWithPercentage(f,percentage));
				topicname[x] = coam;

				x++;
			}
		}

		for (String coam : coa){
			if(!coam.equals("test_for_classification")){           //7 topics
				String topictxt = co + coam + "/train_docs/";
				String[] file = a.FilePathGet(topictxt);
				fnWithP = (int)(file.length*percentage);
				filenumber = file.length-fnWithP;
				double numrightu = 0.0;
				double numrightb = 0.0;
				double numrightt = 0.0;
				double numrightf = 0.0;
				System.out.println("2 corpus name:" + coam);
				for(int i = fnWithP; i< file.length;i++)           //each development set in each topic
				{
					String txtname = topictxt +file[i];
					System.out.println("file name:" + txtname);
					File txtfile = new File(txtname);
					String testdata = a.preprocess(a.txt2String(txtfile));
					double[] pptopicu = new double[7];
					double[] pptopicb = new double[7];
					double[] pptopictri = new double[7];
					double[] pptopicf = new double[7];
					for(int j=0; j<7; j++){
						unigram u = new unigram();
						double ppu = u.perplexity(testdata, corpusArray[j]);
						//System.out.println("uni pp in corpus:" + j + " is:" + ppu);
						pptopicu[j] = ppu;
						
						bigram b = new bigram();
						double ppb = b.perplexity(testdata, corpusArray[j]);
						//System.out.println("bi pp in corpus:" + j + " is:" + ppb);
						pptopicb[j] = ppb;
						
						trigram c = new trigram();
						double ppc = c.perplexity(testdata, corpusArray[j]);
						//System.out.println("tri pp in corpus:" + j + " is:" + ppc);
						pptopictri[j] = ppc;
						
						fourthgram f = new fourthgram();
						double ppf = f.perplexity(testdata, corpusArray[j]);
						//System.out.println("four pp in corpus:" + j + " is:" + ppf);
						pptopicf[j] = ppf;
					}

					int topic2 = a.minimumpp(pptopicu);
					//System.out.println("topic uni: "+ topic2);
					if(topicname[topic2].equals(coam)){
						numrightu++;
					}
					int topic3 = a.minimumpp(pptopicb);
					//System.out.println("topic bi: "+ topic3);
					if(topicname[topic3].equals(coam)){
						numrightb++;
					}
					
					int topict = a.minimumpp(pptopictri);
					//System.out.println("topic tri: "+ topict);
					if(topicname[topict].equals(coam)){
						numrightt++;
					}
					
					int topicf = a.minimumpp(pptopicf);
					//System.out.println("topic tri: "+ topicf);
					if(topicname[topicf].equals(coam)){
						numrightf++;
					}
					
				}
				System.out.println("numrightu: "+ numrightu);
				System.out.println("numrightb: "+ numrightb);
				System.out.println("numrightt: "+ numrightt);
				System.out.println("numrightt: "+ numrightf);

				System.out.println("filenumber: "+ filenumber);
				double rightperu =  numrightu/filenumber;
				double rightperb =  numrightb/filenumber;
				double rightpert =  numrightt/filenumber;
				double rightperf =  numrightf/filenumber;

				System.out.println("The accuracy of unigram model classfication in corpus:" + coam +" is: " + rightperu);
				System.out.println("The accuracy of bigram model classfication in corpus:" + coam +" is: " + rightperb);
				System.out.println("The accuracy of trigram model classfication in corpus:" + coam +" is: " + rightpert);
				System.out.println("The accuracy of fourthgram model classfication in corpus:" + coam +" is: " + rightperf);
			}
		}

	}
}


