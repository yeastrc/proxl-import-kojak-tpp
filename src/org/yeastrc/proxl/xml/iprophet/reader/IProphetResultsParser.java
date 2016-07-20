package org.yeastrc.proxl.xml.iprophet.reader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;

import org.yeastrc.proxl.xml.iprophet.objects.IProphetReportedPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetResult;

public class IProphetResultsParser {

	private static final IProphetResultsParser _INSTANCE = new IProphetResultsParser();
	public static IProphetResultsParser getInstance() { return _INSTANCE; }
	private IProphetResultsParser() { }
	
	public Map<IProphetReportedPeptide, Collection<IProphetResult>> getResultsFromAnalysis( IProphetAnalysis analysis ) throws Exception {
		
		Map<IProphetReportedPeptide, Collection<IProphetResult>> results = new HashMap<IProphetReportedPeptide, Collection<IProphetResult>>();
		
		for( MsmsRunSummary runSummary : analysis.getAnalysis().getMsmsRunSummary() ) {
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
		
		return null;
	}
	
	
}
