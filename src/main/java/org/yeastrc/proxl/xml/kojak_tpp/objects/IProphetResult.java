package org.yeastrc.proxl.xml.kojak_tpp.objects;

import java.math.BigDecimal;

public class IProphetResult {

	private String scanFile;
	private int scanNumber;
	private int charge;
	private BigDecimal kojakScore;
	private BigDecimal deltaScore;
	private BigDecimal ppmError;
	private BigDecimal interProphetScore;
	private BigDecimal peptideProphetScore;
	private BigDecimal linkerMass;
	
	public String getScanFile() {
		return scanFile;
	}
	public void setScanFile(String scanFile) {
		this.scanFile = scanFile;
	}
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public BigDecimal getKojakScore() {
		return kojakScore;
	}
	public void setKojakScore(BigDecimal kojakScore) {
		this.kojakScore = kojakScore;
	}
	public BigDecimal getDeltaScore() {
		return deltaScore;
	}
	public void setDeltaScore(BigDecimal deltaScore) {
		this.deltaScore = deltaScore;
	}
	public BigDecimal getPpmError() {
		return ppmError;
	}
	public void setPpmError(BigDecimal ppmError) {
		this.ppmError = ppmError;
	}
	public BigDecimal getInterProphetScore() {
		return interProphetScore;
	}
	public void setInterProphetScore(BigDecimal interProphetScore) {
		this.interProphetScore = interProphetScore;
	}
	public BigDecimal getPeptideProphetScore() {
		return peptideProphetScore;
	}
	public void setPeptideProphetScore(BigDecimal peptideProphetScore) {
		this.peptideProphetScore = peptideProphetScore;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public BigDecimal getLinkerMass() {
		return linkerMass;
	}
	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
	
	
	
}
