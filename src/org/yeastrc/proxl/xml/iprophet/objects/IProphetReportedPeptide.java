package org.yeastrc.proxl.xml.iprophet.objects;

import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;

public class IProphetReportedPeptide {
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public boolean equals( Object o ) {

		if( !( o instanceof IProphetReportedPeptide) )
			return false;
		
		return this.toString().equals( ((IProphetReportedPeptide)o).toString() );
	}

	public String toString() {
		
		if( this.getType() == IProphetConstants.LINK_TYPE_UNLINKED ) {
			return this.getPeptide1().toString();
		} else if( this.getType() == IProphetConstants.LINK_TYPE_LOOPLINK ) {
			return this.getPeptide1().toString() + "(" + this.getPosition1() + "," + this.getPosition2() + ")";
		} else if( this.getType() == IProphetConstants.LINK_TYPE_CROSSLINK ) {
			return this.getPeptide1().toString() + "(" + this.getPosition1() + ")" + "-" +
        		   this.getPeptide2().toString() + "(" + this.getPosition2() + ")";
		}
		
		return "Error: unknown peptide type";
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public IProphetPeptide getPeptide1() {
		return peptide1;
	}

	public void setPeptide1(IProphetPeptide peptide1) {
		this.peptide1 = peptide1;
	}

	public IProphetPeptide getPeptide2() {
		return peptide2;
	}

	public void setPeptide2(IProphetPeptide peptide2) {
		this.peptide2 = peptide2;
	}

	public int getPosition1() {
		return position1;
	}

	public void setPosition1(int position1) {
		this.position1 = position1;
	}

	public int getPosition2() {
		return position2;
	}

	public void setPosition2(int position2) {
		this.position2 = position2;
	}
	
	private int type;
	private IProphetPeptide peptide1;
	private IProphetPeptide peptide2;
	private int position1;
	private int position2;
	
}
