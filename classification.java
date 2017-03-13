package cs4740p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

public class classification {
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
		//System.out.println(f);
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
		classification a = new classification();
		String co = "C:/Cornell/cs4740/project1/data_corrected/classification task/";
		String[] coa = a.FilePathGet(co);

		//get 7 corpus
		String[] corpusArray = new String[7];
		String[] topicname = new String[7];
		int x = 0;
		for(String coam : coa){               
			if(!coam.equals("test_for_classification")){
				String f = co + coam + "/train_docs/";
				corpusArray[x] = a.preprocess(a.connectfile(f));
				topicname[x] = coam;
				x++;
			}
		}
		String te = "C:/Cornell/cs4740/project1/data_corrected/classification task/test_for_classification/";

		for(int i = 0;i<=249;i++)           //each development set in each topic
		{
			String txtname = te +"file_"+i+".txt";
			File txtfile = new File(txtname);
			String testdata = a.preprocess(a.txt2String(txtfile));
			double[] pptopictri = new double[7];
			for(int j=0; j<7; j++){             
				fourthgram c = new fourthgram();
				double ppc = c.perplexity(testdata, corpusArray[j]);			
				pptopictri[j] = ppc;	
			}
			int topict = a.minimumpp(pptopictri);
			System.out.println("file_" +i+ ".txt"+"," + topict);
		}
	}
}



