package org.yeastrc.proxl.xml.iprophet.reader;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
import net.systemsbiology.regis_web.pepxml.NameValueType;
import net.systemsbiology.regis_web.pepxml.PeptideprophetResult;

import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;
import org.yeastrc.proxl.xml.iprophet.constants.KojakConstants;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetReportedPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetResult;
import org.yeastrc.proxl.xml.iprophet.utils.PepXMLUtils;
import org.yeastrc.proxl.xml.iprophet.utils.ScanParsingUtils;

public class IProphetResultsParser {

	private static final IProphetResultsParser _INSTANCE = new IProphetResultsParser();
	public static IProphetResultsParser getInstance() { return _INSTANCE; }
	private IProphetResultsParser() { }
	
	/**
	 * Get the results of the analysis back in the form used by proxl:
	 * reported peptides are the keys, and all of the PSMs (and their scores)
	 * that reported that peptide are the values.
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	public Map<IProphetReportedPeptide, Collection<IProphetResult>> getResultsFromAnalysis( IProphetAnalysis analysis ) throws Exception {
		
		Map<IProphetReportedPeptide, Collection<IProphetResult>> results = new HashMap<IProphetReportedPeptide, Collection<IProphetResult>>();
		
		for( MsmsRunSummary runSummary : analysis.getAnalysis().getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
							if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
								InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny();
								
								// skip this if it's a decoy
								if( PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) )
									continue;
								
								// get our result
								IProphetResult result = getResult( spectrumQuery, searchHit );
								
								// skip if the probability is 0 (another way to check for decoys)
								if( result.getInterProphetScore().equals( new BigDecimal( "0" ) ) )
									continue;
								
								// get our reported peptide
								IProphetReportedPeptide reportedPeptide = getReportedPeptide( searchHit );
								
								
								
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
	private IProphetReportedPeptide getReportedPeptide( SearchHit searchHit ) throws Exception {
		
		int type = IProphetConstants.LINK_TYPE_CROSSLINK;
		
		if( type == IProphetConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit );
		
		if( type == IProphetConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit );
		
		return getUnlinkedReportedPeptide( searchHit );
		
	}

	
	private IProphetReportedPeptide getCrosslinkReportedPeptide( SearchHit searchHit ) throws Exception {
		
		return null;
	}
	
	private IProphetReportedPeptide getLooplinkReportedPeptide( SearchHit searchHit ) throws Exception {
		
		return null;
	}
	
	private IProphetReportedPeptide getUnlinkedReportedPeptide( SearchHit searchHit ) throws Exception {
		
		return null;
	}
	
	/**
	 * Get the IProphetPeptide from the searchHit. Includes the peptide sequence and any mods.
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetPeptide getPeptideFromSearchHit( SearchHit searchHit ) throws Exception {
		
		IProphetPeptide peptide = new IProphetPeptide();
		
		peptide.setSequence( searchHit.getPeptide() );
		
		ModInfoDataType modInfo = searchHit.getModificationInfo();
		
		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();
			
			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {
				
				int position = mam.getPosition().intValue();
				String residue = peptide.getSequence().substring( position + 1, position + 2 );
				
				double massDifferenceDouble = KojakConstants.AA_MASS.get( residue ) - mam.getMass();
				
				
			}
			
		}
		
		
		
		return null;
	}
	
	/**
	 * Get the PSM result for the given spectrum query and search hit.
	 * 
	 * @param spectrumQuery
	 * @param searchHit
	 * @return
	 * @throws Exception If any of the expected scores are not found
	 */
	private IProphetResult getResult( SpectrumQuery spectrumQuery, SearchHit searchHit ) throws Exception {
		
		IProphetResult result = new IProphetResult();
		
		result.setScanFile( ScanParsingUtils.getFilenameFromReportedScan( spectrumQuery.getSpectrum() ) );
		result.setScanNumber( ScanParsingUtils.getChargeFromReportedScan( spectrumQuery.getSpectrum() ) );
		
		// get the kojak scores
		for( NameValueType score : searchHit.getSearchScore() ) {
			if( score.getName().equals( KojakConstants.NAME_KOJAK_SCORE ) ) {
				result.setKojakScore( new BigDecimal( score.getValueAttribute() ) );
			}
			
			else if( score.getName().equals( KojakConstants.NAME_DELTA_SCORE ) ) {
				result.setDeltaScore( new BigDecimal( score.getValueAttribute() ) );
			}
			
			else if( score.getName().equals( KojakConstants.NAME_PPM_ERROR ) ) {
				result.setPpmError( new BigDecimal( score.getValueAttribute() ) );
			}			
		}

		// get the scores for peptideprophet and iprophet
		for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
			
			if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
				InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny();
				result.setInterProphetScore( ipresult.getProbability() );
			}
			
			else if( analysisResult.getAnalysis().equals( "peptideprophet" ) ) {
				PeptideprophetResult ppresult = (PeptideprophetResult) analysisResult.getAny();
				result.setPeptideProphetScore( ppresult.getProbability() );
			}
		}
		
		
		if( result.getDeltaScore() == null )
			throw new Exception( "Missing delta score for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getPpmError() == null )
			throw new Exception( "Missing PPM error for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getKojakScore() == null )
			throw new Exception( "Missing kojak score for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getInterProphetScore() == null )
			throw new Exception( "Missing iprophet score for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getPeptideProphetScore() == null )
			throw new Exception( "Missing peptideprophet score for result: " + spectrumQuery.getSpectrum() );
		
		
		return result;
		
	}
	
}
