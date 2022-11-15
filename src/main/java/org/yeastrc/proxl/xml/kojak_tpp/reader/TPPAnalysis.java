package org.yeastrc.proxl.xml.kojak_tpp.reader;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;

public class TPPAnalysis {

	/**
	 * Load the data contained in the supplied pepXML file.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static TPPAnalysis loadAnalysis(String filename ) throws Exception {
		
		File pepXMLFile = new File( filename );
		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis)jaxbUnmarshaller.unmarshal(pepXMLFile);
		
		TPPAnalysis ipa = new TPPAnalysis();
		ipa.setAnalysis( msAnalysis );
		return ipa;
		
	}
	
	
	private MsmsPipelineAnalysis analysis;
	private Collection<String> decoyIdentifiers;
	private KojakConfReader kojakConfReader;
	private File fastaFile;
	private Collection<File> kojakConfFiles;
	private BigDecimal importFilter = new BigDecimal( TPPConstants.DEFAULT_IMPORT_CUTOFF );
	private Boolean hasIProphetData = false;
	
	/**
	 * Get the root element of the pepXML file as a JAXB object
	 * 
	 * @return
	 */
	public MsmsPipelineAnalysis getAnalysis() {
		return analysis;
	}

	private void setAnalysis(MsmsPipelineAnalysis analysis) {
		this.analysis = analysis;
	}

	public Collection<String> getDecoyIdentifiers() {
		return decoyIdentifiers;
	}

	public void setDecoyIdentifiers(Collection<String> decoyIdentifiers) {
		this.decoyIdentifiers = decoyIdentifiers;
	}

	public File getFastaFile() {
		return fastaFile;
	}

	public void setFastaFile(File fastaFile) {
		this.fastaFile = fastaFile;
	}

	public KojakConfReader getKojakConfReader() {
		return kojakConfReader;
	}

	public void setKojakConfReader(KojakConfReader kojakConfReader) {
		this.kojakConfReader = kojakConfReader;
	}
	

	public Collection<File> getKojakConfFiles() {
		return kojakConfFiles;
	}

	public void setKojakConfFilePaths(Collection<File> kojakConfFiles) {
		this.kojakConfFiles = kojakConfFiles;
	}

	/**
	 * Get the name of the FASTA file used in this search. It is assumed that, if multiple
	 * msms run summary elements are present, that they all use the same FASTA file. Only
	 * the first one is looked at.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFASTADatabase() throws Exception {		
		return this.getFastaFile().getName();
	}

	public BigDecimal getImportFilter() {
		return importFilter;
	}

	public void setImportFilter(BigDecimal importFilter) {
		this.importFilter = importFilter;
	}

	public Boolean getHasIProphetData() {
		return hasIProphetData;
	}

	public void setHasIProphetData(Boolean hasIProphetData) {
		this.hasIProphetData = hasIProphetData;
	}
}
