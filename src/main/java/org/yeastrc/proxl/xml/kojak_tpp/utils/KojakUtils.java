package org.yeastrc.proxl.xml.kojak_tpp.utils;

import org.yeastrc.proxl.xml.kojak_tpp.constants.KojakConstants;

public class KojakUtils {

    /**
     * Find and report Kojak's programmed monoisotopic mass for the given single-letter amino acid.
     *
     * Note: Will return a mass of 0 if the amino acid cannot be found. This is done because users
     * will search using a non canonical amino acid code with a static mod to search for non-standard
     * residue masses. The static mod in these cases is the total mass of the residue + mod of interest.
     *
     * @param residue
     * @return
     */
    public static double getResidueMass(String residue) {
        if( KojakConstants.AA_MASS.containsKey( residue ) ) {
            return KojakConstants.AA_MASS.get( residue );
        }

        return 0;
    }

}
