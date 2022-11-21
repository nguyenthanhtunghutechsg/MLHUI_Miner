package MLHMiner;

public class TidCUL {
	int tid;
	double NU;
	double NRU;
	double PU;
	int PPOS;
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public double getNU() {
		return NU;
	}
	public void setNU(double nU) {
		NU = nU;
	}
	public double getNRU() {
		return NRU;
	}
	public void setNRU(double nRU) {
		NRU = nRU;
	}
	public double getPU() {
		return PU;
	}
	public void setPU(double pU) {
		PU = pU;
	}
	public int getPPOS() {
		return PPOS;
	}
	public void setPPOS(int pPOS) {
		PPOS = pPOS;
	}
	public TidCUL(int tid, double nU, double nRU, double pU, int pPOS) {
		super();
		this.tid = tid;
		NU = nU;
		NRU = nRU;
		PU = pU;
		PPOS = pPOS;
	}
	@Override
	public String toString() {
		return "TidCUL [" + tid + ", " + NU + ", " + NRU + ", " + PU + ", " + PPOS + "]";
	}
	
}
