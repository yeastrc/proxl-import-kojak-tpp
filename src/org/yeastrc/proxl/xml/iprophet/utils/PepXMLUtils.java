package org.yeastrc.proxl.xml.iprophet.utils;

import java.util.Collection;
import java.util.regex.Pattern;

import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;
import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;

import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
import net.systemsbiology.regis_web.pepxml.InterprophetSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.AnalysisSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;

public class PepXMLUtils {

	public static final String XLINK_TYPE_LOOPLINK = "loop";
	public static final String XLINK_TYPE_CROSSLINK = "xl";
	public static final String XLINK_TYPE_UNLINKED = "na";
	
	/**
	 * Given a peptide sequence such as PIPTLDE, return all sequences that represent
	 * all possible combinations of leucine and isoleucine substitutions. E.g.:
	 * 
	 * PIPTLDE
	 * PLPTLDE
	 * PIPTIDE
	 * PLPTIDE
	 * 
	 * @param sequence
	 * @return
	 */
	public Collection<String> getAllLeucineIsoleucineTransormations( String sequence ) {
		
		return null;
	}
	
	/**
	 * Get the type of link represented by the search hit
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static int getHitType( SearchHit searchHit ) throws Exception {
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {
			return IProphetConstants.LINK_TYPE_CROSSLINK;
		}
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_LOOPLINK ) ) {
			return IProphetConstants.LINK_TYPE_LOOPLINK;
		}
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_UNLINKED ) ) {
			return IProphetConstants.LINK_TYPE_UNLINKED;
		}
		
		throw new Exception( "Unknown link type in pepxml: " + searchHit.getXlinkType() );
		
	}
	
	/**
	 * Get the version number associated with this interprophet search
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	public static String getVersion( IProphetAnalysis analysis ) throws Exception {
		
		String version = "Unknown";
		
		for( AnalysisSummary analysisSummary : analysis.getAnalysis().getAnalysisSummary() ) {
			
			try {
				version = ((InterprophetSummary)(analysisSummary.getAny() )).getVersion();
				break;
			} catch (Exception e ) { ; }			
		}
		
		return version;		
	}

	/**
	 * Return true if any of the supplied names contain one of the decoy strings
	 * @param decoyStrings
	 * @param name
	 * @return
	 */
	public static boolean isDecoyName( Collection<String> decoyStrings, String name ) {
		
		for( String decoyName : decoyStrings ) {
			if( PepXMLUtils.caseInsensitiveStringContains( decoyName,  name ) )
				return true;
		}
		
		return false;
	}
	
	
	/**
	 * Check whether the searchHit is a decoy. For crosslinks, this is a decoy only if both linked peptides only
	 * match to decoy proteins. For unlinked and looplinks, this is a decoy only if all associated protein names
	 * are decoy names. 
	 * 
	 * @param decoyString
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static boolean isDecoy( Collection<String> decoyStrings, SearchHit searchHit ) throws Exception {
		
		//System.out.print( "Testing \"" + searchHit.getProtein() + "\"... " );
		
		// testing crosslinks (is more involved)
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {
			
			// if either of the linked peptides are decoy hits, the xlink is a decoy
			if( isDecoy( decoyStrings, searchHit.getXlink().getLinkedPeptide().get( 0 ) ) ||
				isDecoy( decoyStrings, searchHit.getXlink().getLinkedPeptide().get( 1 ) ) ) {
				
					return true;
			}
			
			return false;
		}
		
		if( searchHit.getProtein() == null ) {
			throw new Exception( "Got null for protein on search hit?" );
		}
		
		// if we got here, the type is either unlinked or looplinks, the test is the same
		if( !isDecoyName( decoyStrings, searchHit.getProtein() ) ) {
			return false;
		}
		
		
		// if any of the alternative proteins listed are not decoy proteins, this is not a decoy
		if( searchHit.getAlternativeProtein() != null && searchHit.getAlternativeProtein().size() > 0 ) {
			for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {
				//System.out.print( ap.getProtein() + "... " );
				if( !isDecoyName( decoyStrings, ap.getProtein() ) ) {
					return false;
				}
			}
		}
		
		return true;	// if we get here, all names for all associated proteins contained the decoy identifier string
	}
	
	/**
	 * Check whether the supplied linked peptide is a decoy.
	 * 
	 * @param decoyString
	 * @param linkedPeptide
	 * @return
	 * @throws Exception
	 */
	private static boolean isDecoy( Collection<String> decoyStrings, LinkedPeptide linkedPeptide ) throws Exception {
		
		if( !isDecoyName( decoyStrings, linkedPeptide.getProtein() ) ) {
			return false;
		}
		
		if( linkedPeptide.getAlternativeProtein() != null && linkedPeptide.getAlternativeProtein().size() > 0 ) {
			for( AltProteinDataType ap : linkedPeptide.getAlternativeProtein() ) {
				if( !isDecoyName( decoyStrings, ap.getProtein() ) ) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	private static boolean caseInsensitiveStringContains( String testString, String containingString ) {
		return Pattern.compile(Pattern.quote(testString), Pattern.CASE_INSENSITIVE).matcher(containingString).find();
	}
	
}
