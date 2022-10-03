package org.yeastrc.proxl.xml.kojak_tpp.utils;

import org.yeastrc.proxl.xml.kojak_tpp.reader.KojakConfReader;

import java.math.BigDecimal;

public class ModUtils {

	/**
	 * Checks to see if the reported modification mass (the mass of the modification, itself) matches
	 * the mass used for a monolink mass in the kojak configuration files. The test is done after rounding
	 * each mass to three decimal places
	 * 
	 * @param modMass
	 * @param kojakConf
	 * @return
	 * @throws Exception
	 */
	public static boolean isMonolink( BigDecimal modMass, KojakConfReader kojakConf ) throws Exception {
		
		if( kojakConf.getMonolinkMasses() == null || kojakConf.getMonolinkMasses().size() < 1 )
			return false;
		
		
		modMass = modMass.setScale( 3, BigDecimal.ROUND_HALF_UP );
		
		for( BigDecimal monolinkMass : kojakConf.getMonolinkMasses() ) {
			
			monolinkMass = monolinkMass.setScale( 3, BigDecimal.ROUND_HALF_UP );
			
			if( modMass.equals( monolinkMass ) )
				return true;
			
		}
		
		return false;
	}
	
	/**
	 * Checks to see if the reported modMass matches a static mod. Does this by rounding the mod mass to
	 * three decimal places and comparing to the static mods in the Kojak conf file (also rounded to three
	 * decimal places).
	 * 
	 * @param residue
	 * @param modMass
	 * @param kojakConf
	 * @return
	 * @throws Exception
	 */
	public static boolean isStaticMod( String residue, BigDecimal modMass, KojakConfReader kojakConf ) throws Exception {
		
		if( kojakConf.getStaticModifications() == null || kojakConf.getStaticModifications().keySet().size() < 1 ||
				!kojakConf.getStaticModifications().containsKey( residue ) )
			return false;
		
		
		modMass = modMass.setScale( 3, BigDecimal.ROUND_HALF_UP );
		
		BigDecimal staticModMass = kojakConf.getStaticModifications().get( residue );
		staticModMass = staticModMass.setScale( 3, BigDecimal.ROUND_HALF_UP );
		
		if( staticModMass.equals( modMass ) )
			return true;
		
				
		return false;
	}
	
}
