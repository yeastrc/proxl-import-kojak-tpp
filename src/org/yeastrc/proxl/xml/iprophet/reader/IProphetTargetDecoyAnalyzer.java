package org.yeastrc.proxl.xml.iprophet.reader;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;

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
public class IProphetTargetDecoyAnalyzer {

	private static IProphetTargetDecoyAnalyzer _INSTANCE;
	public static IProphetTargetDecoyAnalyzer getInstance( IProphetAnalysis analysis ) {
		_INSTANCE = new IProphetTargetDecoyAnalyzer();
		_INSTANCE.setAnalysis( analysis );
		
		return _INSTANCE;
	}
	private IProphetTargetDecoyAnalyzer() { }

	
	/**
	 * Analyze the target/decoy counts in the analysis
	 * 
	 * @throws Exception
	 */
	public void performAnalysis() throws Exception {
		
		Map<BigDecimal, TargetDecoyCounter> scoreCounts = new HashMap<BigDecimal, TargetDecoyCounter>();
		
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

								/*
								// some test output
								//if( score.equals( new BigDecimal( "0.990021" ) ) ) {
								if( score.compareTo( new BigDecimal( "0.990021" ) ) >= 0) {
									System.out.print( "protein: " + searchHit.getProtein() + "\t" );
									
									if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {
										System.out.print( "lp1: " + searchHit.getXlink().getLinkedPeptide().get( 0 ).getProtein() + "\t" );
										
										LinkedPeptide linkedPeptide = searchHit.getXlink().getLinkedPeptide().get( 0 );
										if( linkedPeptide.getAlternativeProtein() != null ) {
											System.out.print( "(" );
											for( AltProteinDataType ap : linkedPeptide.getAlternativeProtein() ) {
												System.out.print( ap + "," );
											}
											System.out.print( ")\t" );
										}
										
										
										System.out.print( "lp2: " + searchHit.getXlink().getLinkedPeptide().get( 1 ).getProtein() + "\t" );
										
										linkedPeptide = searchHit.getXlink().getLinkedPeptide().get( 1 );
										if( linkedPeptide.getAlternativeProtein() != null ) {
											System.out.print( "(" );
											for( AltProteinDataType ap : linkedPeptide.getAlternativeProtein() ) {
												System.out.print( ap + "," );
											}
											System.out.print( ")\t" );
										}
										
									}
									
									System.out.print( "\n" );
								}
								*/
								
								
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
		for( BigDecimal score : scoreList ) {
			TargetDecoyCounter tdc = scoreCounts.get( score );
			
			targetCount += tdc.getTargetCount();
			decoyCount += tdc.getDecoyCount();
			
			tdc.setTargetCount( targetCount );
			tdc.setDecoyCount( decoyCount );
			
			// test output
			//System.out.println( "\t" + score + "\t" + targetCount + "\t" + decoyCount + "\t" + ((double)decoyCount / ( targetCount + decoyCount ) ) );
			
		}
		
		this.scoreCounts = scoreCounts;
	}
	
	/**
	 * Get the FDR corresponding to a given score from this iprophet search, rounded to 4 digits.
	 * 
	 * @param score
	 * @return
	 * @throws Exception If a target decoy analysis hasn't been done, or if the score wasn't found in the search
	 */
	public BigDecimal getFDR( BigDecimal score ) throws Exception {
		
		if( this.scoreCounts == null )
			throw new Exception( "Must call performAnalysis() first." );
		
		if( !this.scoreCounts.containsKey( score ) )
			throw new Exception( "The score: " + score + " was not found in this search." );
		
		TargetDecoyCounter tdc = this.scoreCounts.get( score );
		double fdr = (double)tdc.getDecoyCount() / ( tdc.getDecoyCount() + tdc.getTargetCount() );
		
		BigDecimal retValue = new BigDecimal( fdr );
		retValue.setScale( 4, RoundingMode.HALF_UP );
		
		
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
	
	
}
