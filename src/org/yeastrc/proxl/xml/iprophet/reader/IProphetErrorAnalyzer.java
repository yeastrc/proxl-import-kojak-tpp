package org.yeastrc.proxl.xml.iprophet.reader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;

import org.yeastrc.proxl.xml.iprophet.objects.ProbabilitySumCounter;
import org.yeastrc.proxl.xml.iprophet.objects.TargetDecoyCounter;
import org.yeastrc.proxl.xml.iprophet.utils.PepXMLUtils;

/**
 * InterProphet does not report a score with inherent meaning, at least towards the end of providing
 * a filter for its probability score that is applicable to every search performed. This utility class provides
 * a means for analyzing then determining the FDR associated with the probability score from a given search.
 * 
 * @author mriffle
 *
 */
public class IProphetErrorAnalyzer {

	private static IProphetErrorAnalyzer _INSTANCE;
	public static IProphetErrorAnalyzer getInstance( IProphetAnalysis analysis ) {
		_INSTANCE = new IProphetErrorAnalyzer();
		_INSTANCE.setAnalysis( analysis );
		
		return _INSTANCE;
	}
	private IProphetErrorAnalyzer() { }

	
	/**
	 * Analyze the target/decoy counts in the analysis
	 * 
	 * @throws Exception
	 */
	public void performAnalysis() throws Exception {
		
		Map<BigDecimal, TargetDecoyCounter> scoreCounts = new HashMap<BigDecimal, TargetDecoyCounter>();
		Map<BigDecimal, ProbabilitySumCounter> probabilitySums = new HashMap<BigDecimal, ProbabilitySumCounter>();
		
		MsmsPipelineAnalysis xmlAnalysis = analysis.getAnalysis();

		/*
		 * First, compile a count for targets and decoys for each score reported for all PSMs
		 */
		for( MsmsRunSummary runSummary : xmlAnalysis.getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
							if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
								InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny();
								
								BigDecimal score = ipresult.getProbability();
								
								ProbabilitySumCounter psc = null;
								
								if( probabilitySums.containsKey( score ) ) {
									psc = probabilitySums.get( score );
								} else {
									psc = new ProbabilitySumCounter();
									probabilitySums.put( score,  psc );
								}
													
								psc.setpCount( psc.getpCount() + score.doubleValue() );
								psc.setOneMinusPCount( psc.getOneMinusPCount() + ( 1.0 - score.doubleValue() ) );
								
								boolean isDecoy = PepXMLUtils.isDecoy( analysis.getDecoyIdentifier(), searchHit );
								
								TargetDecoyCounter tdc = null;
								if( scoreCounts.containsKey( score ) )
									tdc = scoreCounts.get( score );
								else {
									tdc = new TargetDecoyCounter();
									scoreCounts.put( score,  tdc );
								}
								
								
								if( isDecoy ) {
									tdc.setDecoyCount( tdc.getDecoyCount() + 1 );
								} else {
									tdc.setTargetCount( tdc.getTargetCount() + 1 );
								}
								
							}
						}
					}
				}
			}
		}
		
		
		
		/*
		 * Then, order all of those scores, least to greatest, and calculate a cumulative counts of targets and
		 * decoys reported for a given score or better.
		 */
		
		List<BigDecimal> scoreList = new ArrayList<BigDecimal>( scoreCounts.keySet() );
		Collections.sort( scoreList, Collections.reverseOrder() );
		
		int targetCount = 0;
		int decoyCount = 0;
		
		double pSum = 0;
		double oneMinusPSum = 0;
		
		for( BigDecimal score : scoreList ) {
			
			ProbabilitySumCounter psc = probabilitySums.get( score );
			pSum += psc.getpCount();
			oneMinusPSum += psc.getOneMinusPCount();
			
			psc.setpCount( pSum );
			psc.setOneMinusPCount( oneMinusPSum );
			
			
			
			TargetDecoyCounter tdc = scoreCounts.get( score );
			
			targetCount += tdc.getTargetCount();
			decoyCount += tdc.getDecoyCount();
			
			tdc.setTargetCount( targetCount );
			tdc.setDecoyCount( decoyCount );
						
		}
		
		this.scoreCounts = scoreCounts;
		this.probabilitySums = probabilitySums;
	}
	
	/**
	 * Get the FDR based on decoy counts for a given probability score--calculated as # decoys / # targets.
	 * Note, that it is possible for this to be over 1 when there are more decoys than targets found at
	 * a given score.
	 * 
	 * @param score
	 * @return
	 * @throws Exception If a target decoy analysis hasn't been done, or if the score wasn't found in the search
	 */
	public BigDecimal getSimpleDecoyFDR( BigDecimal score ) throws Exception {
		
		if( this.scoreCounts == null )
			throw new Exception( "Must call performAnalysis() first." );
		
		if( !this.scoreCounts.containsKey( score ) )
			throw new Exception( "The score: " + score + " was not found in this search." );
		
		TargetDecoyCounter tdc = this.scoreCounts.get( score );
		double fdr = (double)tdc.getDecoyCount() / ( tdc.getTargetCount() );
		
		BigDecimal retValue = new BigDecimal( fdr );
		retValue = retValue.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		
		
		return retValue;
	}
	
	/**
	 * Get the error ( numincorr / ( numincorr + numcorr) ) associated with a given probability score, as calculated
	 * by the TPP
	 * 
	 * @param score
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getError( BigDecimal score ) throws Exception {
		
		if( this.probabilitySums == null )
			throw new Exception( "Must call performAnalysis() first." );
		
		if( !this.probabilitySums.containsKey( score ) )
			throw new Exception( "The score: " + score + " was not found in this search." );
		
		ProbabilitySumCounter psc = this.probabilitySums.get( score );
		double error = (double)psc.getOneMinusPCount() / ( psc.getpCount() + psc.getOneMinusPCount() );
		
		BigDecimal retValue = BigDecimal.valueOf( error );
		retValue = retValue.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		
		return retValue;
	}
	
	
	
	
	public IProphetAnalysis getAnalysis() {
		return analysis;
	}
	public void setAnalysis(IProphetAnalysis analysis) {
		this.analysis = analysis;
	}
	
	private IProphetAnalysis analysis;
	private Map<BigDecimal, TargetDecoyCounter> scoreCounts;
	private Map<BigDecimal, ProbabilitySumCounter> probabilitySums;
	
	
}
