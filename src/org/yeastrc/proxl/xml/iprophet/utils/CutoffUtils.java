package org.yeastrc.proxl.xml.iprophet.utils;

import java.util.List;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.AnalysisSummary;

import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;

public class CutoffUtils {

	/**
	 * Get the iProphet probability score corresponding with the desired FDR.
	 * 
	 * @param analysis
	 * @param FDR
	 * @return
	 * @throws Exception
	 */
	public static Double getFDRForScore( IProphetAnalysis analysis, double FDR ) throws Exception {
		
		if( FDR <= 0 || FDR >= 1 )
			throw new Exception( "Invalid FDR." );
		
		MsmsPipelineAnalysis xmlAnalysis = analysis.getAnalysis();
		List<AnalysisSummary> xmlSummaries = xmlAnalysis.getAnalysisSummary();
		
		return null;

		
	}
	
}
