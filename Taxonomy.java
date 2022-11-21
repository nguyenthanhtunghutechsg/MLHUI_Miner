package MLHMiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

// Taxonomy
// --------
// Class to store a dataset's taxonomy on memory for quick access
// 
// Coded by Trinh D.D. Nguyen, 
// Version 1.0 - Sep 2020
public class Taxonomy {

	public class Tuple {				// representing a tuple consists of parent node and its child
		Integer parent = 0;
		Integer child = 0;
		
		Tuple(Integer p, Integer c) {	// default constructor
			parent = p;
			child = c;
		}
	}	

	ArrayList<Tuple>	taxonomy;		// our taxonomy 
	ArrayList<Integer>	parents;		// list of all parent nodes
	
	// default constructor
	public Taxonomy() { 		
		MapdataParent = new HashMap<Integer, Integer>();
		taxonomy = new ArrayList<>();
		parents = new ArrayList<>();
	}
	public HashMap<Integer, Integer> MapdataParent;
	// another constructor
	public Taxonomy(String filename,Dataset dataset) throws IOException { 
		MapdataParent = new HashMap<Integer, Integer>();
		taxonomy = new ArrayList<>();
		parents = new ArrayList<>();
	
		load(filename,dataset);
	}
	
	// add a tuple to the taxonomy 
	public void add(Integer p, Integer c) {
		taxonomy.add(new Tuple(p, c));
		
		if (!parents.contains(p)) {
			parents.add(p);
		}
	}
	
	// load taxonomy from text file
	public void load(String filename,Dataset dataset) throws IOException {
		BufferedReader	reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		String			line;

		try {
			while ((line = reader.readLine())!=null) {			// scanning through the text file
			
				if (line.isEmpty() == true || line.charAt(0)=='#' || line.charAt(0)=='@') { 
					continue;									// skipping comments and empty lines
				}
											
				String	tokens[] = line.split(",");				// splitting string using ','														
				Integer	child = Integer.parseInt(tokens[0]);	// child comes first								
				Integer	parent = Integer.parseInt(tokens[1]);	// then its parent							
				if (parent>dataset.getMaxItem()) {
					dataset.setMaxItem(parent);
				}
				add(parent, child);
				MapdataParent.put(child, parent);
				// then add this tuple into the list
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader != null) { 
				reader.close(); 
			}
		}
	}
	
	// access index-th parent
	public Integer parent(int index) {
		return taxonomy.get(index).parent;
	}
	
	// access index-th child
	public Integer child(int index) {
		return taxonomy.get(index).child;
	}

	// access index-th tuple
	public Tuple get(int index) {
		return taxonomy.get(index);
	}

	// return the total tuples in the taxonomy 
	public int size() {
		return taxonomy.size();
	}
	
	// return the number of parent nodes in the taxonomy - for statistics only
	public int parentCount() {
		return parents.size();
	}

}
