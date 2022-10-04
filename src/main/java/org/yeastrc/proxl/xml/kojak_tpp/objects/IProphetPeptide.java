package org.yeastrc.proxl.xml.kojak_tpp.objects;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class IProphetPeptide {

	/**
	 * Get the string representation of this peptide that includes mods, in the form of:
	 * PEP[12.29,15.99]TI[12.2932]DE
	 */
	public String toString() {
		
		String str = "";
		
		for( int i = 1; i <= this.getSequence().length(); i++ ) {
			String r = String.valueOf( this.getSequence().charAt( i - 1 ) );
			str += r;
			
			if( this.getModifications() != null ) {
				List<String> modsAtPosition = new ArrayList<String>();
				
				if( this.getModifications().get( i ) != null ) {
					for( BigDecimal mod : this.getModifications().get( i ) ) {
						modsAtPosition.add( mod.setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() );
					}
					
					if( modsAtPosition.size() > 0 ) {
	
						// sort these strings on double values
						Collections.sort( modsAtPosition, new Comparator<String>() {
						       public int compare(String s1, String s2) {
						           return Double.valueOf( s1 ).compareTo( Double.valueOf( s2 ) );
						        }
						});
						
						String modsString = StringUtils.join( modsAtPosition, "," );
						str += "[" + modsString + "]";
					}
				}
			}
		}

		if(this.getIsotopeLabel() != null) {
			str += "-" + this.getIsotopeLabel();
		}
		
		return str;
	}
	
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public Map<Integer, Collection<BigDecimal>> getModifications() {
		return modifications;
	}
	public void setModifications(Map<Integer, Collection<BigDecimal>> modifications) {
		this.modifications = modifications;
	}
	public Collection<String> getTargetProteins() {
		return targetProteins;
	}
	public void setTargetProteins(Collection<String> targetProteins) {
		this.targetProteins = targetProteins;
	}

	public String getIsotopeLabel() {
		return isotopeLabel;
	}

	public void setIsotopeLabel(String isotopeLabel) {
		this.isotopeLabel = isotopeLabel;
	}

	private String sequence;
	private Map<Integer, Collection<BigDecimal>> modifications;
	private Collection<String> targetProteins;
	private String isotopeLabel;
}
