package org.yeastrc.proxl.xml.iprophet.reader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
import net.systemsbiology.regis_web.pepxml.InterprophetResult;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;
import net.systemsbiology.regis_web.pepxml.NameValueType;
import net.systemsbiology.regis_web.pepxml.PeptideprophetResult;

import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;
import org.yeastrc.proxl.xml.iprophet.constants.KojakConstants;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetReportedPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetResult;
import org.yeastrc.proxl.xml.iprophet.utils.ModUtils;
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
								
								// only one interprophet result will appear for a search hit, and we are only
								// interested in search hits with an interprophet result.
								
								String sequence = searchHit.getPeptide();
								if( sequence.equals( "RIDLAGR" ) ) {
									
									String hitSummary = "";
									
									hitSummary += "peptide: " + searchHit.getPeptide() + "\n";
									hitSummary += "protein: " + searchHit.getProtein() + "\n";
									
									if( searchHit.getAlternativeProtein() != null ) {
										for( AltProteinDataType altProtein : searchHit.getAlternativeProtein() ) {
											hitSummary += "alt protein: " + altProtein.getProtein() + "\n";
										}
									}
									
									throw new Exception( "Decoy status is: " + PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) + " for\n" + hitSummary );
								}
								
								// skip this if it's a decoy
								if( PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) )
									continue;
								
								// get our result
								IProphetResult result = getResult( spectrumQuery, searchHit );
								
								// skip if the probability is 0 (another way to check for decoys)
								if( result.getInterProphetScore().compareTo( new BigDecimal( "0" ) ) == 0  )
									continue;
								
								// get our reported peptide
								IProphetReportedPeptide reportedPeptide = getReportedPeptide( searchHit, analysis );
								
								if( !results.containsKey( reportedPeptide ) )
									results.put( reportedPeptide, new ArrayList<IProphetResult>() );
								
								results.get( reportedPeptide ).add( result );								
							}
						}
					}
				}
			}
		}
		
		return results;
	}
	
	/**
	 * Get the IProphetReportedPeptide for the given SearchHit
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getReportedPeptide( SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		
		int type = PepXMLUtils.getHitType( searchHit );
		
		if( type == IProphetConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit, analysis );
		
		if( type == IProphetConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit, analysis );
		
		return getUnlinkedReportedPeptide( searchHit, analysis );
		
	}

	/**
	 * Get the IProphetReportedPeptide for a crosslink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getCrosslinkReportedPeptide( SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		
		System.out.println( searchHit.getPeptide() );
		System.out.println( "\t" + searchHit.getXlinkType() );
		
		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_CROSSLINK );
				
		for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {
			
			int peptideNumber = 0;
			if( reportedPeptide.getPeptide1() == null ) {
				peptideNumber = 1;
			} else if( reportedPeptide.getPeptide2() == null ) {
				peptideNumber = 2;
			} else {
				throw new Exception( "Got more than two linked peptides." );
			}
			
			
			System.out.println( "\t\t" + linkedPeptide.getPeptide() );
			System.out.println( "\t\tpeptide num: " + peptideNumber );
			
			IProphetPeptide peptide = getPeptideFromLinkedPeptide( linkedPeptide, analysis );
			int position = 0;
			
			for( NameValueType nvt : linkedPeptide.getXlinkScore() ) {
								
				if( nvt.getName().equals( "link" ) ) {
					
					System.out.println( "\t\t" + nvt.getValueAttribute() );
					
					if( position == 0 )
						position = Integer.valueOf( nvt.getValueAttribute() );
					else
						throw new Exception( "Got more than one linked position in peptide." );
				}
			}
			
			if( position == 0 )
				throw new Exception( "Could not find linked position in peptide." );
			
			
			if( peptideNumber == 1 ) {
				reportedPeptide.setPeptide1( peptide );
				reportedPeptide.setPosition1( position );
			} else {
				reportedPeptide.setPeptide2( peptide );
				reportedPeptide.setPosition2( position );
			}
			
		}
		
		return reportedPeptide;
	}
	
	/**
	 * Get the IProphetReportedPeptide for a looplink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getLooplinkReportedPeptide( SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		
		System.out.println( searchHit.getPeptide() );
		System.out.println( "\t" + searchHit.getXlinkType() );

		
		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();
		
		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_LOOPLINK );
		
		// add in the linked positions
		Xlink xl = searchHit.getXlink();
		
		for( NameValueType nvt : xl.getXlinkScore() ) {
			if( nvt.getName().equals( "link" ) ) {
				
				System.out.println( "\t\t" + nvt.getValueAttribute() );
				
				if( reportedPeptide.getPosition1() == 0 )
					reportedPeptide.setPosition1( Integer.valueOf( nvt.getValueAttribute() ) );
				else if( reportedPeptide.getPosition2() == 0 )
					reportedPeptide.setPosition2( Integer.valueOf( nvt.getValueAttribute() ) );
				else
					throw new Exception( "Got more than 2 linked positions for looplink." );
			}
		}
		
		if( reportedPeptide.getPosition1() == 0 || reportedPeptide.getPosition2() == 0 )
			throw new Exception( "Did not get two positions for looplink." );
		
		return reportedPeptide;
	}

	/**
	 * Get the IProphetReportedPeptide for an unlinked result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getUnlinkedReportedPeptide( SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		
		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();
		
		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_UNLINKED );
		
		return reportedPeptide;
	}
	
	/**
	 * Get the IProphetPeptide from the searchHit. Includes the peptide sequence and any mods.
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetPeptide getPeptideFromSearchHit( SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		
		IProphetPeptide peptide = new IProphetPeptide();
		
		peptide.setSequence( searchHit.getPeptide() );
		
		ModInfoDataType modInfo = searchHit.getModificationInfo();
		
		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();
			
			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {
				
				int position = mam.getPosition().intValue();
				String residue = peptide.getSequence().substring( position - 1, position );
				
				double massDifferenceDouble = mam.getMass() - KojakConstants.AA_MASS.get( residue );
				BigDecimal massDifference = BigDecimal.valueOf( massDifferenceDouble );
				massDifference = massDifference.setScale( 6, BigDecimal.ROUND_HALF_UP );

				// don't add static mods as mods
				if( ModUtils.isStaticMod(residue, massDifference, analysis.getKojakConfReader() ) )
					continue;
				
				if( !mods.containsKey( position ) )
					mods.put( position, new HashSet<BigDecimal>() );
				
				mods.get( position ).add( massDifference );				
			}
			
			peptide.setModifications( mods );			
		}
				
		return peptide;
	}
	
	/**
	 * Get the IProphetPeptide from the searchHit. Includes the peptide sequence and any mods.
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetPeptide getPeptideFromLinkedPeptide( LinkedPeptide linkedPeptide, IProphetAnalysis analysis ) throws Exception {
		
		IProphetPeptide peptide = new IProphetPeptide();
		
		peptide.setSequence( linkedPeptide.getPeptide() );
		
		ModInfoDataType modInfo = linkedPeptide.getModificationInfo();
		
		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();
			
			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {
				
				int position = mam.getPosition().intValue();
				String residue = peptide.getSequence().substring( position - 1, position );
				
				double massDifferenceDouble = mam.getMass() - KojakConstants.AA_MASS.get( residue );
				BigDecimal massDifference = BigDecimal.valueOf( massDifferenceDouble );
				massDifference = massDifference.setScale( 6, BigDecimal.ROUND_HALF_UP );

				// don't add static mods as mods
				if( ModUtils.isStaticMod(residue, massDifference, analysis.getKojakConfReader() ) )
					continue;
				
				if( !mods.containsKey( position ) )
					mods.put( position, new HashSet<BigDecimal>() );
				
				mods.get( position ).add( massDifference );				
			}
			
			peptide.setModifications( mods );			
		}
				
		return peptide;
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
		result.setScanNumber( (int)spectrumQuery.getStartScan() );
		result.setCharge( spectrumQuery.getAssumedCharge().intValue() );
		
		// if this is a crosslink or looplink, get the mass of the linker
		int type = PepXMLUtils.getHitType( searchHit );
		if( type == IProphetConstants.LINK_TYPE_CROSSLINK || type == IProphetConstants.LINK_TYPE_LOOPLINK ) {
			Xlink xl = searchHit.getXlink();
			result.setLinkerMass( xl.getMass() );
		}
		
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
