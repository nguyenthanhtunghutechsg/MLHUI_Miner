package MLHMiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class Transaction {
	int items[];
	double utilities[];
	double tu;
	
	
	public static int[] tempItems = new int[2000];
	public static double[] tempUtilities = new double[2000];
	public ArrayList<ArrayList<Integer>> ListItemPerLevel = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Double>> ListUtilityPerLevel = new ArrayList<ArrayList<Double>>();
	public ArrayList<Double> listTransactionUtility = new ArrayList<Double>();
	public Transaction(int[] items, double[] utilities, double tu) {
		super();
		this.items = items;
		this.utilities = utilities;
		this.tu=tu;
		
	}
	public void setLevelTransaction(int Levelmax) {
		for (int i = 0; i < Levelmax; i++) {
			ListItemPerLevel.add(new ArrayList<Integer>());
			ListUtilityPerLevel.add(new ArrayList<Double>());
			listTransactionUtility.add(0d);	
		}
	}
	public void AddItemToTransaction(int Level,int item,double Utility) {
		ListItemPerLevel.get(Level-1).add(item);
		ListUtilityPerLevel.get(Level-1).add(Utility);
		listTransactionUtility.set(Level-1, listTransactionUtility.get(Level-1)+Utility);
	}

	public int[] getItems() {
		return items;
	}

	public void setItems(int[] items) {
		this.items = items;
	}

	public double[] getUtilities() {
		return utilities;
	}

	public void setUtilities(double[] utilities) {
		this.utilities = utilities;
	}

	public double getTu() {
		return tu;
	}

	public void setTu(int tu) {
		this.tu = tu;
	}

	@Override
	public String toString() {
		StringBuffer strBuff=new StringBuffer();
		int n=items.length;
		for(int i=0;i<n;i++)
 			strBuff.append(items[i] + "["+utilities[i]+"] ");
		strBuff.append(":" + tu + "\n");
		return strBuff.toString(); 
	}
	
	public void removeUnpromisingItems(ArrayList<int[]> oldNamesToNewNames,Map<Integer, List<Integer>> mapItemToAncestor,Map<Integer, Integer> mapItemToLevel) {
    	int i = 0;
    	Map<Integer,Double> mapItemToUtility = new HashMap<Integer, Double>();
    	for(int j=0; j< items.length;j++) {
    		int item = items[j];    		
    		// Convert from old name to new name
    		mapItemToUtility.put(item, utilities[j]);
    		List<Integer> listParent = mapItemToAncestor.get(item);
    		for (int k = 1; k < listParent.size(); k++) {
				int parentItem = listParent.get(k);
				Double UtilityOfParent = mapItemToUtility.get(parentItem);
				if (UtilityOfParent==null) {
					UtilityOfParent = utilities[j];
				}
				else {
					UtilityOfParent+=utilities[j];
				}
				mapItemToUtility.put(parentItem, UtilityOfParent);
			}
    	}
    	for (int j : mapItemToUtility.keySet()) {
			int level = mapItemToLevel.get(j);
			if (oldNamesToNewNames.get(level-1)[j]!=0)  {
				this.ListItemPerLevel.get(level-1).add(oldNamesToNewNames.get(level-1)[j]);
				this.ListUtilityPerLevel.get(level-1).add(mapItemToUtility.get(j));
				this.listTransactionUtility.set(level-1,this.listTransactionUtility.get(level-1)+mapItemToUtility.get(j));
			}
		}
    	// Sort by increasing TWU values
    	insertionSort();
	}
	
	public void insertionSort(){
		for (int level= 0 ;level < ListItemPerLevel.size();level++) {
			ArrayList<Integer> itemsList = ListItemPerLevel.get(level);
			ArrayList<Double> utilitysList = ListUtilityPerLevel.get(level);
			for(int j=1; j< itemsList.size(); j++){
				int itemJ = itemsList.get(j);
				double utilityJ = utilitysList.get(j);
				int i = j - 1;
				for(; i>=0 && (itemsList.get(i)  > itemJ); i--){
					itemsList.set(i+1,itemsList.get(i));
					utilitysList.set(i+1,utilitysList.get(i));
				}
				itemsList.set(i+1, itemJ);
				utilitysList.set(i+1, utilityJ);
			}
		}
	}
	
	public String itemToString()
	{
		StringBuffer str=new StringBuffer();
		for(int i:items)
			str.append(i +" ");
		return str.toString();
	}
	public String itemLevelToString(int level) {
		StringBuffer str=new StringBuffer();
		for (int i : ListItemPerLevel.get(level)) {
			str.append(i +" ");
		}
		return str.toString();
	}
	public String itemLevelToStringAll(int level) {
		StringBuffer str=new StringBuffer();
		for (int i : ListItemPerLevel.get(level)) {
			str.append(i +" ");
		}
		str.append(":");
		str.append(listTransactionUtility.get(level).intValue());
		str.append(":");
		for (double i : ListUtilityPerLevel.get(level)) {
			str.append((int)i +" ");
		}
		return str.toString();
	}
}
