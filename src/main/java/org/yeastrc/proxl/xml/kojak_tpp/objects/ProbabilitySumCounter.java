package org.yeastrc.proxl.xml.kojak_tpp.objects;

public class ProbabilitySumCounter {

	private double pSum = 0;
	private double oneMinusPSum = 0;
	
	public double getpCount() {
		return pSum;
	}
	public void setpCount(double pCount) {
		this.pSum = pCount;
	}
	public double getOneMinusPCount() {
		return oneMinusPSum;
	}
	public void setOneMinusPCount(double oneMinusPCount) {
		this.oneMinusPSum = oneMinusPCount;
	}
	
}
