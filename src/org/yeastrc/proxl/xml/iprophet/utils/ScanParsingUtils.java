package org.yeastrc.proxl.xml.iprophet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some utility methods for parsing scan variables from the reported scan information in plink results files.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class ScanParsingUtils {

	/**
	 * Get the charge from the reported scan. E.g.: Q_2013_1010_RJ_07.14315.14315.4
	 * Would return 4
	 * @param reportedScan The reported scan from the plink results file, in the form of Q_2013_1010_RJ_07.14315.14315.4
	 * @return The charge parsed from the scan
	 * @throws Exception
	 */
	public static int getChargeFromReportedScan( String reportedScan ) throws Exception {
		String[] fields = reportedScan.split( "\\." );
		
		if( fields.length < 4 )
			throw new Exception( "Got unexpected syntax for reported scan string: " + reportedScan );
		
		return Integer.parseInt( fields[ fields.length - 1 ] );
	}
	
	/**
	 * Get the scan number from the reported scan. E.g.: Q_2013_1010_RJ_07.14315.14315.4
	 * Would return 14315. Always uses the second to last value after spliting on "."
	 * @param reportedScan The reported scan from the plink results file, in the form of Q_2013_1010_RJ_07.14315.14315.4
	 * @return The scan number
	 * @param reportedScan
	 * @return
	 * @throws Exception
	 */
	public static int getScanNumberFromReportedScan( String reportedScan ) throws Exception {
		String[] fields = reportedScan.split( "\\." );
		
		if( fields.length < 4 )
			throw new Exception( "Got unexpected syntax for reported scan string: " + reportedScan );
		
		return Integer.parseInt( fields[ fields.length - 2 ] );
	}
	
	/**
	 * Get the name of the scan file from the reported scan. E.g. QEP2_2016_0121_RJ_68_205_comet.00965.00965.3
	 * would return QEP2_2016_0121_RJ_68_205_comet
	 * 
	 * @param reportedScan
	 * @return
	 * @throws Exception
	 */
	public static String getFilenameFromReportedScan( String reportedScan ) throws Exception {
		
		Pattern r = Pattern.compile( "^(.+)\\.\\d+\\.\\d+\\.\\d+$" );
		Matcher m = r.matcher( reportedScan );
		
		if( m.matches() ) {
			return m.group( 1 );
		} else {
			return null;
		}
		
	}
	
}
