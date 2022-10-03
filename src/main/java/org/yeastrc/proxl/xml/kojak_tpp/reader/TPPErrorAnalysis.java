package org.yeastrc.proxl.xml.kojak_tpp.reader;

import java.math.BigDecimal;
import java.util.Map;

public class TPPErrorAnalysis {

	/**
	 * Get the error ( sum of 1-p for this probability or better / total count
	 * of hits with this probability or better )
	 * 
	 * @param score
	 * @throws Exception
	 */
	public BigDecimal getError( BigDecimal score ) throws Exception {
		
		//System.out.println( score );

		
		if( !this.probabilitySums.containsKey( score ) )
			throw new Exception( "The score: " + score + " was not found in this search." );
		
		double totalCount = 0;
		double oneMinusPSum = 0;
		
		for( BigDecimal testScore : this.getProbabilitySums().keySet() ) {
			
			
			if( testScore.compareTo( score ) >= 0 ) {
				
				totalCount += this.probabilitySums.get( testScore ).getTotalCount();
				oneMinusPSum += this.probabilitySums.get( testScore ).getOneMinusPCount();
				
				//System.out.println( totalCount + " " + oneMinusPSum );
				
			}
		}
		
		double error = oneMinusPSum / totalCount;
		
		BigDecimal retValue = BigDecimal.valueOf( error );
		retValue = retValue.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		
		return retValue;
	}
	
	/**
	 * @return the probabilitySums
	 */
	public Map<BigDecimal, ProbabilitySumCounter> getProbabilitySums() {
		return probabilitySums;
	}

	/**
	 * @param probabilitySums the probabilitySums to set
	 */
	protected void setProbabilitySums(Map<BigDecimal, ProbabilitySumCounter> probabilitySums) {
		this.probabilitySums = probabilitySums;
	}

	private Map<BigDecimal, ProbabilitySumCounter> probabilitySums;

}
