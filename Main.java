package MLHMiner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static String recordString="";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String dataset = "connect";
		String trans = dataset+".txt";
		String taxonomy = dataset+"Taxonomy.txt";		
		double minutil =50300000;
		//String outputPath ="MLHMiner_output"+Time+".txt";
		//String Path = "C:\\Users\\Administrator\\Desktop\\Result\\"+dataset+"\\"+outputPath;
		for (int i = 0; i < 6; i++) {
			System.gc();
			AlgoMLHMiner mlhuiminer = new AlgoMLHMiner();
			mlhuiminer.runAlgorithm(trans, taxonomy, null, minutil);
			mlhuiminer.printStats();
			minutil-=100000;
		}
	}
}