package org.yeastrc.proxl.xml.iprophet.reader;

import java.util.HashMap;
import java.util.Map;

import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;

import org.yeastrc.proxl.xml.iprophet.objects.*;

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
		
		Map<String, TargetDecoyCounter> scoreCounts = new HashMap<String, TargetDecoyCounter>();
		
		MsmsPipelineAnalysis xmlAnalysis = analysis.getAnalysis();
		
		for( MsmsRunSummary runSummary : xmlAnalysis.getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
							if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
								InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny();
								
								
							}
						}
					}
				}
			}
		}
		
	}
	
	
	
	
	public IProphetAnalysis getAnalysis() {
		return analysis;
	}
	public void setAnalysis(IProphetAnalysis analysis) {
		this.analysis = analysis;
	}
	
	private IProphetAnalysis analysis;
	
	
}
