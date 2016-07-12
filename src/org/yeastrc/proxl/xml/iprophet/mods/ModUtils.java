package org.yeastrc.proxl.xml.iprophet.mods;

import org.yeastrc.proxl.xml.iprophet.constants.KojakConstants;

public class ModUtils {

	/**
	 * Get the mass of the modification reported for a given position (starting at 1) in a given peptide
	 * 
	 * @param peptide The peptide sequence
	 * @param position The modded position in the peptide
	 * @param residueMass The mass of the modded residue
	 * 
	 * @return The mass of the modification
	 * 
	 * @throws Exception
	 */
	public static double getModMass( String peptide, int position, double residueMass ) throws Exception {
		
		if( position < 1 || position > peptide.length() )
			throw new Exception( "Position is not in the peptide." );
		
		String residue = String.valueOf( peptide.charAt( position - 1 ) );
		
		if( !KojakConstants.AA_MASS.containsKey( residue ) )
			throw new Exception( "Residue: " + residue + " was not found in KojakConstants." );
		
		return residueMass - KojakConstants.AA_MASS.get( residue );		
	}
	
}
