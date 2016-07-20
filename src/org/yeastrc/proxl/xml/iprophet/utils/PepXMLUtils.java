package org.yeastrc.proxl.xml.iprophet.utils;

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
	 * Check whether the searchHit is a decoy. For crosslinks, this is a decoy only if both linked peptides only
	 * match to decoy proteins. For unlinked and looplinks, this is a decoy only if all associated protein names
	 * are decoy names. 
	 * 
	 * @param decoyString
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static boolean isDecoy( String decoyString, SearchHit searchHit ) throws Exception {
		
		// testing crosslinks (is more involved)
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {
			
			// if either of the linked peptides are decoy hits, the xlink is a decoy
			if( isDecoy( decoyString, searchHit.getXlink().getLinkedPeptide().get( 0 ) ) ||
				isDecoy( decoyString, searchHit.getXlink().getLinkedPeptide().get( 1 ) ) )
					return true;
			
			return false;
		}
		
		if( searchHit.getProtein() == null ) {
			System.out.println( searchHit );
			return false;
		}
		
		// if we got here, the type is either unlinked or looplinks, the test is the same
		if( !isUnmapped( searchHit.getProtein() ) &&
			!PepXMLUtils.caseInsensitiveStringContains( decoyString, searchHit.getProtein() ) ) {
			return false;
		}
		
		
		// if any of the alternative proteins listed are not decoy proteins, this is not a decoy
		if( searchHit.getAlternativeProtein() != null && searchHit.getAlternativeProtein().size() > 0 ) {
			for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {
				if( !PepXMLUtils.caseInsensitiveStringContains( decoyString, ap.getProtein() ) ) {
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
	private static boolean isDecoy( String decoyString, LinkedPeptide linkedPeptide ) throws Exception {
		
		if( !PepXMLUtils.caseInsensitiveStringContains( decoyString, linkedPeptide.getProtein() ) ) {
			return false;
		}
		
		if( linkedPeptide.getAlternativeProtein() != null && linkedPeptide.getAlternativeProtein().size() > 0 ) {
			for( AltProteinDataType ap : linkedPeptide.getAlternativeProtein() ) {
				if( !PepXMLUtils.caseInsensitiveStringContains( decoyString, ap.getProtein() ) ) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	private static boolean caseInsensitiveStringContains( String testString, String containingString ) {
		return Pattern.compile(Pattern.quote(testString), Pattern.CASE_INSENSITIVE).matcher(containingString).find();
	}
	
	private static boolean isUnmapped( String testString ) {
		return PepXMLUtils.caseInsensitiveStringContains( IProphetConstants.UNMAPPED_STRING, testString );
	}
	
}
