package org.yeastrc.proxl.xml.kojak_tpp.reader;

import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPReportedPeptide;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * PeptideProphet does not report a score with inherent meaning, at least towards the end of providing
 * a filter for its probability score that is applicable to every search performed. This utility class provides
 * a means for analyzing then determining the FDR associated with the probability score from a given search.
 * 
 * @author mriffle
 *
 */
public class TPPErrorAnalyzer {

	public enum Type {
		INTERPROPHET,
		PEPTIDEPROPHET
	}

	
	/**
	 * Analyze the target/decoy counts in the analysis
	 * 
	 * @throws Exception
	 */
	public static TPPErrorAnalysis performPeptideProphetAnalysis( Map<TPPReportedPeptide, Collection<TPPResult>> tppResults, Type type ) {
		
		Map<BigDecimal, ProbabilitySumCounter> probabilitySums = new HashMap<BigDecimal, ProbabilitySumCounter>();
		
		/*
		 * First, compile a count for targets and decoys for each score reported for all PSMs
		 */
		
		for( TPPReportedPeptide tppRp : tppResults.keySet() ) {
			
			for( TPPResult TPPResult : tppResults.get( tppRp ) ) {
				
				
				BigDecimal score = null;

				if(type == Type.INTERPROPHET)
					score = TPPResult.getInterProphetScore();
				else if(type == Type.PEPTIDEPROPHET)
					score = TPPResult.getPeptideProphetScore();

				ProbabilitySumCounter psc = null;
				
				if( probabilitySums.containsKey( score ) ) {
					psc = probabilitySums.get( score );
				} else {
					psc = new ProbabilitySumCounter();
					probabilitySums.put( score,  psc );
				}
				
				psc.setpCount( psc.getpCount() + score.doubleValue() );
				psc.setOneMinusPCount( psc.getOneMinusPCount() + ( 1.0 - score.doubleValue() ) );
				psc.setTotalCount( psc.getTotalCount() + 1 );
				
			}
			
		}
		
		TPPErrorAnalysis ppa = new TPPErrorAnalysis();
		ppa.setProbabilitySums( probabilitySums );
		
		return ppa;
	}
}
