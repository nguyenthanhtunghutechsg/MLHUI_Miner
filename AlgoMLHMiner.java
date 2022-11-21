package MLHMiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgoMLHMiner {

	double minUtil;
	Dataset dataset;
	double twus[];
	int itemCount[];
	ArrayList<CUL[]> oneItemCURsPerLevel;
	List<double[][]> EUCSPerLevel;
	long can = 0;
	int newItemCount;
	Map<Integer, Integer> mapItemToLevel;
	Map<Integer, List<Integer>> mapItemToAncestor;
	public int transCount = 0;
	long count = 0;
	Taxonomy taxonomy;

	long startTimestamp;// thá»�i gian báº¯t Ä‘áº§u cháº¡y
	long endTimestamp;// thá»�i gian káº¿t thÃºc

	public AlgoMLHMiner() {

	}

	public void runAlgorithm(String inputTransaction, String inputTaxonomy, String output, Double minUtility)
			throws IOException {
		startTimestamp = System.currentTimeMillis();
		MemoryLogger.getInstance().reset();
		// read the input file- Ä‘á»�c dá»¯ liá»‡u tá»« file vÃ o dataset
		dataset = new Dataset(inputTransaction, Integer.MAX_VALUE);
		// AncestorOfItem = new List[dataset.getMaxItem() + 1];
		mapItemToAncestor = new HashMap<Integer, List<Integer>>();
		taxonomy = new Taxonomy(inputTaxonomy, dataset);// 45492//136477/90985
		mapItemToLevel = new HashMap<Integer, Integer>();
		oneItemCURsPerLevel = new ArrayList<CUL[]>();
		EUCSPerLevel = new ArrayList<double[][]>();
		this.minUtil = minUtility;
		// if the user choose to save to file
		// create object for writing the output file
		calculateTWU();
		int maxLevel = getMaxLevel(mapItemToLevel);
		ArrayList<ArrayList<Integer>> itemsToKeepPerLevel = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < maxLevel; i++) {
			itemsToKeepPerLevel.add(new ArrayList<Integer>());
			itemCount = new int[maxLevel];

		}
		for (int j = 1; j < twus.length; j++) {
			if (twus[j] >= minUtil) {
				itemsToKeepPerLevel.get(mapItemToLevel.get(j) - 1).add(j);
			}
		}

		insertionSort(itemsToKeepPerLevel, twus);
		// System.out.println(itemsToKeepPerLevel.get(0).toString());
		ArrayList<int[]> oldNameToNewNamesPerLevel = new ArrayList<int[]>();
		ArrayList<int[]> newNamesToOldNamesPerLevel = new ArrayList<int[]>();

		for (int i = 0; i < maxLevel; i++) {
			ArrayList<Integer> itemsToKeep = itemsToKeepPerLevel.get(i);
			int ItemLevel = itemsToKeep.size();
			itemCount[i] = ItemLevel;
			double[][] EUCS = new double[ItemLevel + 1][ItemLevel + 1];
			EUCSPerLevel.add(EUCS);
			int[] oldNameToNewNames = new int[dataset.getMaxItem() + 1];
			// This structure will store the old name corresponding to each new name
			int[] newNamesToOldNames = new int[dataset.getMaxItem() + 1];
			int currentName = 1;

			// For each item in increasing order of TWU
			for (int j = 0; j < itemsToKeep.size(); j++) {
				// get the item old name
				int item = itemsToKeep.get(j);
				// give it the new name
				oldNameToNewNames[item] = currentName;
				// remember its old name
				newNamesToOldNames[currentName] = item;
				// replace its old name by the new name in the list of promising items
				itemsToKeep.set(j, currentName);
				// increment by one the current name so that
				currentName++;
			}
			oldNameToNewNamesPerLevel.add(oldNameToNewNames);
			newNamesToOldNamesPerLevel.add(newNamesToOldNames);
		}
		// oldNameToNewNames = new int[dataset.getMaxItem() + 1];
		// This structure will store the old name corresponding to each new name
		// newNamesToOldNames = new int[dataset.getMaxItem() + 1];
		// We will now give the new names starting from the name "1"

		ArrayList<HashMap<String, Integer>> HTPerLevel = new ArrayList<HashMap<String, Integer>>();
		int dem = 0;
		for (int i = 0; i < maxLevel; i++) {
			HashMap<String, Integer> HT = new HashMap<String, Integer>();
			HTPerLevel.add(HT);
			CUL[] oneItemCURs = new CUL[itemCount[i]];
			for (int j = 0; j < itemCount[i]; j++) {
				oneItemCURs[j] = new CUL();
				oneItemCURs[j].setItem(j + 1);
			}
			oneItemCURsPerLevel.add(oneItemCURs);
		}

		for (int i = 0; i < dataset.getTransactions().size(); i++) {
			Transaction transaction = dataset.getTransactions().get(i);
			transaction.setLevelTransaction(maxLevel);
			transaction.removeUnpromisingItems(oldNameToNewNamesPerLevel, mapItemToAncestor, mapItemToLevel);
			// System.out.println(transaction.itemLevelToStringAll(0));
		}
		int tid = 0;
		int countdup = 0;
		for (Transaction tran : dataset.transactions) {
			tid++;
			for (int i = 0; i < maxLevel; i++) {
				if (tran.listTransactionUtility.get(i) == 0) {
					continue;
				}
				double ru = 0;
				String newT = tran.itemLevelToString(i);
				Object dupPos = HTPerLevel.get(i).get(newT);
				ArrayList<Integer> itemInTransactionInLevel = tran.ListItemPerLevel.get(i);
				ArrayList<Double> UtilityInTransactionInLevel = tran.ListUtilityPerLevel.get(i);
				if (dupPos == null) {

					int xk = itemInTransactionInLevel.get(itemInTransactionInLevel.size() - 1);
					int viTri = oneItemCURsPerLevel.get(i)[xk - 1].tidList.size();
					HTPerLevel.get(i).put(newT, viTri++);
					for (int j = itemInTransactionInLevel.size() - 1; j >= 0; j--) {
						int pPos = -1;
						int item = itemInTransactionInLevel.get(j);
						double nU = UtilityInTransactionInLevel.get(j);
						if (j > 0) {
							pPos = oneItemCURsPerLevel.get(i)[itemInTransactionInLevel.get(j - 1) - 1].tidList.size();
						}
						TidCUL tidCur = new TidCUL(tid, nU, ru, 0, pPos);
						oneItemCURsPerLevel.get(i)[item - 1].tidList.add(tidCur);
						oneItemCURsPerLevel.get(i)[item - 1].NU += nU;
						oneItemCURsPerLevel.get(i)[item - 1].NRU += ru;
						ru = ru + nU;
					}
				} else {
					int pos = (int) dupPos;
					int item;
					if (i == 0) {
						countdup++;
					}
					for (int j = itemInTransactionInLevel.size() - 1; j >= 0; j--) {
						item = itemInTransactionInLevel.get(j);
						double nU = UtilityInTransactionInLevel.get(j);
						TidCUL tidcur = oneItemCURsPerLevel.get(i)[item - 1].tidList.get(pos);
						tidcur.setNU(tidcur.NU + nU);
						tidcur.setNRU(tidcur.NRU + ru);
						pos = tidcur.getPPOS();
						oneItemCURsPerLevel.get(i)[item - 1].NU += nU;
						oneItemCURsPerLevel.get(i)[item - 1].NRU += ru;
						ru = ru + nU;
					}
				}
			}

		}
		System.out.println(countdup);
		for (Transaction tran : dataset.transactions) {
			for (int l = 0; l < maxLevel; l++) {
				ArrayList<Integer> itemInTransactionInLevel = tran.ListItemPerLevel.get(l);
				ArrayList<Integer> UtilityInTransactionInLevel = tran.ListItemPerLevel.get(l);
				int soluong = itemInTransactionInLevel.size();
				double tu = tran.listTransactionUtility.get(l);
				for (int i = 0; i < soluong - 1; i++)
					for (int j = i + 1; j < soluong; j++)
						EUCSPerLevel.get(l)[itemInTransactionInLevel.get(i)][itemInTransactionInLevel.get(j)] += tu;
			}

		}

		MemoryLogger.getInstance().checkMemory();
		for (int l = maxLevel - 1; l < maxLevel; l++) {
			exploreSearchTree(null, oneItemCURsPerLevel.get(l), l);
		}
		MemoryLogger.getInstance().checkMemory();
		endTimestamp = System.currentTimeMillis();

	}

	public void exploreSearchTree(int[] R, CUL[] CULs, int level) {
		int sizeCUL = CULs.length;
		int n;
		if (R == null)
			n = 0;
		else
			n = R.length;
		for (int i = 0; i < sizeCUL; i++) {
			CUL cul = CULs[i];
			int x = cul.item;
			int U = cul.NU + cul.CU;// dÃ²ng nÃ y cÃ³ váº¥n Ä‘á»�

			int a[] = new int[n + 1];
			if (n != 0)
				System.arraycopy(R, 0, a, 0, n);
			a[n] = x;
			can++;
			if (U >= minUtil) {
				count++;

			}
			int RU = cul.NRU + cul.CRU;
			if (U + RU >= minUtil)// U-Prune
			{
				CUL[] exCULs = constructCUR(x, CULs, i + 1, level);
				if (exCULs != null)
					exploreSearchTree(a, exCULs, level);// dong nay cÃ³ váº¥n Ä‘á»�
			}
		}
		MemoryLogger.getInstance().checkMemory();

	}

	private CUL[] constructCUR(int x, CUL[] CULs, int st, int level) {
		// System.out.println(x);
		// int n=CULs.length;
		int sz = CULs.length - st;// so CUL ket hop voi CUL[x]
		int extSz = sz; // so exCULs thá»±c sá»± tá»“n táº¡i
		CUL[] exCULs = new CUL[sz];

		int ey[] = new int[sz];
		double LAU[] = new double[sz];
		double CUTIL[] = new double[sz];
		double giamCRU[] = new double[sz];

		CUL culX = CULs[st - 1];
		for (int j = 0; j < sz; j++) {
			CUL cul = CULs[st + j];
			if (cul == null) {
				exCULs[j] = null;
				extSz--;
				continue;
			}
			int y = cul.item;
			if (EUCSPerLevel.get(level)[x][y] < minUtil) {
				exCULs[j] = null;
				extSz--;
				giamCRU[j] = cul.CU - cul.CPU;
			} else {
				CUL newCUL = new CUL();
				// tÃ­nh ngay CU, CRU, CPU- ko phan biet lÃ  k=2 hay ba
				newCUL.item = y;
				// newCUL.CU=0; newCUL.CRU=0; newCUL.CPU=0;//dang che

				newCUL.CU = culX.CU + cul.CU - culX.CPU;
				newCUL.CRU = cul.CRU;
				newCUL.CPU = culX.CU;// dang che

				exCULs[j] = newCUL;

				ey[j] = 0;
				LAU[j] = culX.CU + culX.CRU + culX.NRU + culX.NU;
				CUTIL[j] = culX.CU + culX.CRU;
			}
		}

		HashMap<String, Integer> HT = new HashMap<String, Integer>();
		for (TidCUL tidcul : culX.tidList) {
			StringBuffer newT = new StringBuffer();
			int mang[] = new int[extSz];
			int count = 0;
			// int itemcuoi=-1;

			for (int j = 0; j < sz; j++) {
				if (exCULs[j] == null)
					continue;
				int countEyTidList = CULs[st + j].tidList.size();
				while (ey[j] < countEyTidList && CULs[st + j].tidList.get(ey[j]).tid < tidcul.tid)
					ey[j]++;
				if (ey[j] < countEyTidList && CULs[st + j].tidList.get(ey[j]).tid == tidcul.tid) {
					newT.append(j);
					newT.append(' ');
					mang[count] = j;
					count++;
					// itemcuoi=j;

				} else// khong cÃ³ tid nÃ o
				{
					LAU[j] = LAU[j] - tidcul.NU - tidcul.NRU;
					if (LAU[j] < minUtil) {
						// System.out.println("La ne" + LAU[j]);
						exCULs[j] = null;// LA-prune
						giamCRU[j] = CULs[st + j].CU - CULs[st + j].CPU;
						extSz--;
					}
				}
			}

			if (count == 0)
				continue;

			if (extSz == count) {
				double nru = 0;
				for (int j = sz - 1; j >= 0; j--) {
					if (exCULs[j] == null)
						continue;
					CUL ey_cul = CULs[st + j];
					double NPU_tidcul = tidcul.PU;

					double NU_ey_cul = ey_cul.tidList.get(ey[j]).NU;
					exCULs[j].CU += tidcul.NU + NU_ey_cul - NPU_tidcul;
					exCULs[j].CRU += nru;
					exCULs[j].CPU += tidcul.NU;
					nru = nru + NU_ey_cul - NPU_tidcul;
				}
			} else {
				String tt = newT.toString();
				Object dupPos = HT.get(tt);

				if (dupPos == null)// chÆ°a cÃ³ trong HT
				{
					int vitri = exCULs[mang[count - 1]].tidList.size();
					HT.put(tt, vitri);
					int nru = 0;
					for (int j = count - 1; j >= 0; j--) {
						int vvvt = mang[j];
						// CUL culht=CULs[st+vvvt];
						int vt = ey[vvvt];//
						TidCUL tidCULht = CULs[st + vvvt].tidList.get(vt);

						double NPU = tidcul.PU;// bá»Ÿi vi khong Ä‘Ã³ng
						int addtid = tidcul.tid;
						double addnU = tidcul.NU + tidCULht.NU - NPU;
						// int addnRU=tidCULht.NRU;
						int addnRU = nru;
						double addpU = tidcul.NU;
						int addpPOS = -1;
						if (j > 0)
							addpPOS = exCULs[mang[j - 1]].tidList.size();

						// TidCUL tidCULAdd=new TidCUL(addtid, addnU, addnRU, addpU, addpPOS);
						TidCUL tidCULAdd = new TidCUL(addtid, addnU, addnRU, addpU, addpPOS);
						nru += addnU - addpU;
						CUL dangxuly = exCULs[vvvt];
						dangxuly.tidList.add(tidCULAdd);
						// Cap nhat láº¡i cÃ¡c thÃ´ng tin cá»§a dangxuly (NU, NRU, CU/CRU/CPU)
						dangxuly.NU += addnU;
						dangxuly.NRU += addnRU;
						// CRU vÃ  CPU, CU khong Ä‘á»•i vÃ¬ X khong dong

					}
				} else// Ä‘Ã£ tá»“n táº¡i trong HT
				{
					int pos = (int) dupPos;
					double nru = 0;

					for (int j = count - 1; j >= 0; j--) {
						int vvvt = mang[j];// itemcuoi
						CUL culCu = CULs[st + vvvt];
						CUL culht = exCULs[vvvt];
						TidCUL update = culht.tidList.get(pos);
						///
						double NPU = tidcul.PU;
						update.NU += tidcul.NU + culCu.tidList.get(ey[vvvt]).NU - NPU;
						update.NRU += nru;
						update.PU += tidcul.NU;

						culht.NU += tidcul.NU + culCu.tidList.get(ey[vvvt]).NU - NPU;
						culht.NRU += nru;

						nru = nru + culCu.tidList.get(ey[vvvt]).NU - NPU;
						pos = culht.tidList.get(pos).PPOS;
					}
				}
			}

			// cap nhat láº¡i ********** can luu Ã½
			// chi cá»™ng thÃªm khi ma tidcul chÆ°a exCULs[j].item thÃ´i chÆ°
			double them = tidcul.NU + tidcul.NRU;

			for (int j = 0; j < sz; j++)
				if (exCULs[j] != null)// if(isCuls[j])
					CUTIL[j] += them;

		} // end for

		// CUL[] kq =new CUL[extSz];
		int i = 0;
		for (int j = 0; j < sz; j++) {

			if (exCULs[j] != null && CUTIL[j] >= minUtil)// de kiem tra Ä‘Ã£ bá»‹
			{
				// int t=exCULs[j].CU + exCULs[j].CRU +exCULs[j].NU +exCULs[j].NRU;
				// System.out.print(" " + t);

				exCULs[i] = exCULs[j];
				// exCULs[j]=null;
				i++;
			}
		}

		if (i == 0)
			return null;
		CUL[] kq = new CUL[i];
		System.arraycopy(exCULs, 0, kq, 0, i);
		return kq;

	}

	public void calculateTWU() {

		// Initialize utility bins for all items
		twus = new double[dataset.getMaxItem() + 1];

		// Scan the database to fill the utility bins
		// For each transaction
		transCount = dataset.getTransactions().size();
		for (int tid = 0; tid < transCount; tid++) {

			Transaction transaction = dataset.getTransactions().get(tid);
			ArrayList<Integer> ancestantExist = new ArrayList<Integer>();

			for (int i = 0; i < transaction.getItems().length; i++) { // for each item, add the transaction utility to
																		// its TWU
				Integer item = transaction.getItems()[i];
				double transactionUtility = transaction.getTu();
				Double twu = twus[item]; // get the current TWU of that item

				// add the utility of the item in the current transaction to its twu
				twus[item] = twu + transactionUtility;

				ArrayList<Integer> ancestor = new ArrayList<Integer>();

				ancestor.add(item);
				if (mapItemToAncestor.get(item) == null) {
					Integer itemCopy = item;
//					for (int m = 0; m < taxonomy.size(); m++) {
//
//						Integer childItem = taxonomy.child(m);								
//						Integer parentItem = taxonomy.parent(m);
//						
//						if (childItem.intValue() == itemCopy.intValue()) {
//							ancestor.add(parentItem);
//							if (!ancestantExist.contains(parentItem)) {
//								ancestantExist.add(parentItem);
//								Double twuParent = twus[parentItem];
//								twuParent += transactionUtility;
//								twus[parentItem] = twuParent;
//							}
//							itemCopy = parentItem;
//						}
//					}
					while (itemCopy != null) {
						Integer childItem = itemCopy;
						Integer parentItem = taxonomy.MapdataParent.get(childItem);
						if (parentItem != null) {
							ancestor.add(parentItem);
							if (!ancestantExist.contains(parentItem)) {
								ancestantExist.add(parentItem);
								Double twuParent = twus[parentItem];
								twuParent = (twuParent == null) ? transactionUtility : transactionUtility + twuParent;
								twus[parentItem] = twuParent;
							}
						}
						itemCopy = parentItem;
					}
					int k = ancestor.size();
					for (int j = ancestor.size() - 1; j >= 0; j--, k--) {
						if (mapItemToLevel.get(ancestor.get(j)) == null) {
							mapItemToLevel.put(ancestor.get(j), k);
						} else {
							if (k < mapItemToLevel.get(ancestor.get(j))) {
								mapItemToLevel.put(ancestor.get(j), k);
							}

						}
					}

					for (int itemKey = 0; itemKey < ancestor.size(); itemKey++) {
						List<Integer> itemValue = new ArrayList<>();
						for (int listValue = itemKey; listValue < ancestor.size(); listValue++) {
							itemValue.add(ancestor.get(listValue));

						}
						mapItemToAncestor.put(ancestor.get(itemKey), itemValue);
					}
				} else {
					List<Integer> listAncestorOfItem = mapItemToAncestor.get(item);

					for (int k = 1; k < listAncestorOfItem.size(); k++) {
						if (!ancestantExist.contains(listAncestorOfItem.get(k))) {
							ancestantExist.add(listAncestorOfItem.get(k));
							Double twuParent = twus[listAncestorOfItem.get(k)];
							twuParent += transaction.getTu();
							twus[listAncestorOfItem.get(k)] = twuParent;
						}
					}
				}
			}
		}
	}

	public static void insertionSort(ArrayList<ArrayList<Integer>> itemList, double[] ArrayTWU) {
		// the following lines are simply a modified an insertion sort
		for (List<Integer> items : itemList) {
			for (int j = 1; j < items.size(); j++) {
				Integer itemJ = items.get(j);
				int i = j - 1;
				Integer itemI = items.get(i);

				// we compare the twu of items i and j
				double comparison = ArrayTWU[itemI] - ArrayTWU[itemJ];
				// if the twu is equal, we use the lexicographical order to decide whether i is
				// greater
				// than j or not.
				if (comparison == 0) {
					comparison = itemI - itemJ;
				}

				while (comparison > 0) {
					items.set(i + 1, itemI);

					i--;
					if (i < 0) {
						break;
					}

					itemI = items.get(i);
					comparison = ArrayTWU[itemI] - ArrayTWU[itemJ];
					// if the twu is equal, we use the lexicographical order to decide whether i is
					// greater
					// than j or not.
					if (comparison == 0) {
						comparison = itemI - itemJ;
					}
				}
				items.set(i + 1, itemJ);
			}
		}
	}

	private static Integer getMaxLevel(Map<Integer, Integer> map) {
		if (map == null)
			return null;
		int length = map.size();
		Collection<Integer> c = map.values();
		Object[] obj = c.toArray();
		Arrays.sort(obj);
		return Integer.parseInt(obj[length - 1].toString());
	}

	public void printStats() {

		System.out.println("========== MLHMiner ============");
		System.out.println(" minUtil = " + minUtil);
		System.out.println(" High utility itemsets count: " + count);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Can ~: " + can);
		System.out.println(" Memory ~: " + Math.round(MemoryLogger.getInstance().getMaxMemory()));
		System.out.println("=====================================");
		/*
		 * Main.recordString=Main.recordString+(int)minUtil+"	"+(endTimestamp -
		 * startTimestamp)+"		"+Math.round(MemoryLogger.getInstance().getMaxMemory
		 * ()) +"	"+highUtilityItemsets.getItemsetsCount();
		 * Main.recordString=Main.recordString+"\n";
		 */
		// highUtilityItemsets.printItemsets();
	}

}
