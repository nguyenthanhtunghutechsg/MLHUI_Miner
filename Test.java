package testTravel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Test {

	int count;
	Map<Integer, Integer> mapItemToParent;
	Map<Integer, ArrayList<Integer>> mapItemToChild;
	Map<Integer, ArrayList<Integer>> mapItemToAncestor;
	Map<Integer, Integer> mapItemToLevel;
	HashSet<Integer> Items;

	public Test() {
		count = 0;
		mapItemToParent = new HashMap<>();
		mapItemToChild = new HashMap<>();
		mapItemToAncestor = new HashMap<>();
		mapItemToLevel = new HashMap<>();
		int n = 2;
		int level = 3;
		int count = 0;
		for (int i = 1; i <= level; i++) {
			count += Math.pow(n, i);
		}
		for (int i = count; i > 0; i--) {
			int key = i;
			int value = key % n == 0 ? key / n - 1 : key / n;
			if (value != 0) {
				mapItemToParent.put(key, value);
			}
		}
		Items = new HashSet<>();
		mapItemToParent.entrySet().forEach((entry) -> {
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			Items.add(key);
			Items.add(value);
			if (mapItemToChild.get(value) == null) {
				mapItemToChild.put(value, new ArrayList<>());
			}
			mapItemToChild.get(value).add(key);
		});
		Items.forEach((item) -> {
			Integer itemCurent = item;
			ArrayList<Integer> itemAncestor = new ArrayList<>();
			while (itemCurent != null) {
				itemCurent = mapItemToParent.get(itemCurent);
				if (itemCurent != null) {
					itemAncestor.add(itemCurent);
				}
			}
			mapItemToAncestor.put(item, itemAncestor);
			mapItemToLevel.put(item, itemAncestor.size() + 1);
		});
	}

	public void run() {
		List<Integer> listItemLevel1 = new ArrayList<>();
		mapItemToLevel.entrySet().forEach((item) -> {
			if (item.getValue() == 1) {
				listItemLevel1.add(item.getKey());
			}
		});
		Combine(new int[0], 0, listItemLevel1);
	}

	public void Combine(int Prefix[], int prefixLength, List<Integer> ListInput) {

		for (int i = 0; i < ListInput.size(); i++) {
			Integer item = ListInput.get(i);
			Print(Prefix, prefixLength, item);
			int newPrefix[] = new int[prefixLength + 1];
			System.arraycopy(Prefix, 0, newPrefix, 0, prefixLength);
			newPrefix[prefixLength] = item;
			if (mapItemToChild.get(item) != null) {
				ExtensionTaxonomy(newPrefix);
			}

			List<Integer> newList = new ArrayList<>();
			for (int j = i + 1; j < ListInput.size(); j++) {
				newList.add(ListInput.get(j));
			}

			Combine(newPrefix, prefixLength + 1, newList);
		}
	}

	public int SumArray(int[] array) {
		int sum = 0;
		for (int i : array) {
			sum += i;
		}
		return sum;
	}
	public void UpdateChild(int arrayIndex[], int Prefix[],int ExtensionTaxonomyArray[]) {
		for (int i = 0; i < Prefix.length; i++) {
			ExtensionTaxonomyArray[i] = mapItemToChild.get(Prefix[i]).get(arrayIndex[i]);
		}
	}

	public void ExtensionTaxonomy(int Prefix[]) {
		int ExtensionTaxonomyArray[] = new int[Prefix.length];
		int arrayIndex[] = new int[Prefix.length];
		int ArrayIndexReverse[] = new int[Prefix.length];
		boolean check = false;
		for (int i = 0; i < Prefix.length; i++) {
			int item = Prefix[i];
			ExtensionTaxonomyArray[i] = mapItemToChild.get(item).get(0);
			arrayIndex[i] = 0;
			ArrayIndexReverse[i] = mapItemToChild.get(item).size()-1;
		}
		while(!check) {
			PrintWithOutExtension(ExtensionTaxonomyArray);
			if (mapItemToChild.get(ExtensionTaxonomyArray[0]) != null) {
				ExtensionTaxonomy(ExtensionTaxonomyArray);
			}
			int j = Prefix.length-1;
			List<Integer> ExtensionList = new ArrayList<>();
			for (int i = 0; i < Prefix.length; i++) {
				List<Integer> childList = mapItemToChild.get(Prefix[i]);
				for (int k = arrayIndex[i]+1; k <= ArrayIndexReverse[i]; k++) {
					ExtensionList.add(childList.get(k));
				}
			}
			Combine(ExtensionTaxonomyArray, ExtensionTaxonomyArray.length, ExtensionList);
			
			while(j>=0&&arrayIndex[j]==ArrayIndexReverse[j]) {
				j--;
			}
			if(j < 0) {
				check = true; 
			}else {
				arrayIndex[j]++;
				for (int k = j + 1; k < Prefix.length; k++) {
					arrayIndex[k] = 0;   
				}
				UpdateChild(arrayIndex,Prefix,ExtensionTaxonomyArray);
				
			}
			
		}

	}

	public boolean CheckParent(int Itemset[], int NumberOfParent) {
		HashSet<Integer> SetParent = new HashSet<>();
		for (int i = 0; i < Itemset.length; i++) {
			int j = Itemset[i];
			SetParent.add(mapItemToParent.get(j));
		}
		return SetParent.size() == NumberOfParent;

	}

	public void Print(int Prefix[], int prefixLength, Integer X) {
		count++;
		String output = count + ": ";
		for (int i = 0; i < prefixLength; i++) {
			output += Prefix[i] + " ";
		}
		output += X;
		System.out.println(output);
	}

	public void PrintWithOutExtension(int Prefix[]) {
		count++;
		String output = count + ": ";
		for (int i = 0; i < Prefix.length; i++) {
			output += Prefix[i] + " ";
		}
		System.out.println(output);
	}

}