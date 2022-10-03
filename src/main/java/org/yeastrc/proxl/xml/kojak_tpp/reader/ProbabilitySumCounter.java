package org.yeastrc.proxl.xml.kojak_tpp.reader;

public class ProbabilitySumCounter {

	private double pSum = 0;
	private double oneMinusPSum = 0;
	private int totalCount = 0;
	
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
	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}
	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
}
