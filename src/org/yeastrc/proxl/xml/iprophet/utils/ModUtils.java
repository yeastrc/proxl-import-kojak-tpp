package org.yeastrc.proxl.xml.iprophet.utils;

import java.math.BigDecimal;

import org.yeastrc.proxl.xml.iprophet.reader.KojakConfReader;

public class ModUtils {

	/**
	 * Checks to see if the reported modification mass (the mass of the modification, itself) matches
	 * the mass used for a monolink mass in the kojak configuration files. Given that these two values
	 * may have different numbers of decimal places, the value with the large number of decimal places
	 * is re-scaled using HALF_UP rounding (e.g. grade school rounding) to have the same number of
	 * decimal places as the smaller of the two, then compared.
	 * 
	 * @param modMass
	 * @param kojakConf
	 * @return
	 * @throws Exception
	 */
	public static boolean isMonolink( BigDecimal modMass, KojakConfReader kojakConf ) throws Exception {
		
		if( kojakConf.getMonolinkMasses() == null || kojakConf.getMonolinkMasses().size() < 1 )
			return false;
		
		
		int modMassDecimalPlaces = getDecimalPlaces( modMass );
		
		
		for( BigDecimal monolinkMass : kojakConf.getMonolinkMasses() ) {
			int monolinkMassDecimalPlaces = getDecimalPlaces( monolinkMass );
			
			int dp = getMin( modMassDecimalPlaces, monolinkMassDecimalPlaces );
			
			BigDecimal bd1 = modMass.setScale( dp, BigDecimal.ROUND_HALF_UP );
			BigDecimal bd2 = monolinkMass.setScale( dp, BigDecimal.ROUND_HALF_UP );
			
			if( bd1.equals( bd2 ) )
				return true;
			
		}
		
		return false;
	}

	
	
	private static int getDecimalPlaces( BigDecimal bd ) {
		String[] fields = bd.toString().split( "\\." );
		int bdDecimalPlaces = 0;
		if( fields.length == 2 ) {
			bdDecimalPlaces = fields[ 1 ].length();
		}
		
		return bdDecimalPlaces;
	}
	
	private static int getMin( int x, int y ) {
		return x < y ? x : y;
	}
	
}
