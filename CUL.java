package MLHMiner;


import java.util.ArrayList;
import java.util.List;

public class CUL {
	int item;
	int NU;
	int NRU;
	int CU;
	int CRU;
	int CPU;
	List<TidCUL> tidList;
	
	public CUL()
	{
		tidList=new ArrayList<TidCUL>();
		NU=0;
		NRU=0;
		CU=0;
		CRU=0;
		CPU=0;
	}
	
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public int getNU() {
		return NU;
	}
	public void setNU(int nU) {
		NU = nU;
	}
	public int getNRU() {
		return NRU;
	}
	public void setNRU(int nRU) {
		NRU = nRU;
	}
	public int getCU() {
		return CU;
	}
	public void setCU(int cU) {
		CU = cU;
	}
	public int getCRU() {
		return CRU;
	}
	public void setCRU(int cRU) {
		CRU = cRU;
	}
	public int getCPU() {
		return CPU;
	}
	public void setCPU(int cPU) {
		CPU = cPU;
	}
	public List<TidCUL> getTidList() {
		return tidList;
	}
	public void setTidList(List<TidCUL> tidList) {
		this.tidList = tidList;
	}
	public int getNPU()
	{
		int tong=0;
		for(TidCUL tid:tidList)
			tong+=tid.PU;
		return tong;
	}

	@Override
	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append("CUL [item=" + item + ", NU=" + NU + ", NRU=" + NRU + ", CU=" + CU + ", CRU=" + CRU + ", CPU=" + CPU
				+ "]");
		str.append(System.getProperty("line.separator"));
		for(TidCUL tid:tidList)
		{
			str.append(tid.toString());
			str.append(System.getProperty("line.separator"));
		}
		return str.toString();
	}
	

}
