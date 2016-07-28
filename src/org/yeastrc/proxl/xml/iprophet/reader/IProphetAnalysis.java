package org.yeastrc.proxl.xml.iprophet.reader;

import java.io.File;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;

public class IProphetAnalysis {

	/**
	 * Load the data contained in the supplied pepXML file.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static IProphetAnalysis loadAnalysis( String filename ) throws Exception {
		
		File pepXMLFile = new File( filename );
		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis)jaxbUnmarshaller.unmarshal(pepXMLFile);
		
		IProphetAnalysis ipa = new IProphetAnalysis();
		ipa.setAnalysis( msAnalysis );
		return ipa;
		
	}
	
	
	private MsmsPipelineAnalysis analysis;
	private Collection<String> decoyIdentifiers;
	private String fastaDatabaseName;
	private KojakConfReader kojakConfReader;
	private File fastaFile;
	
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

	public String getFastaDatabase() {
		return fastaDatabaseName;
	}

	public void setFastaDatabase(String fastaDatabase) {
		this.fastaDatabaseName = fastaDatabase;
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

	/**
	 * Get the name of the FASTA file used in this search. It is assumed that, if multiple
	 * msms run summary elements are present, that they all use the same FASTA file. Only
	 * the first one is looked at.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFASTADatabase() throws Exception {
		if( this.fastaDatabaseName == null ) {
			
			String localPath = this.getAnalysis().getMsmsRunSummary().get( 0 ).getSearchSummary().get( 0 ).getSearchDatabase().getLocalPath();
			if( localPath == null )
				throw new Exception( "Could not determine local path for FASTA file used in analysis." );
			
			File fastaFile = new File( localPath );
			this.fastaDatabaseName = fastaFile.getName();			
		}
		
		return this.fastaDatabaseName;
	}
	
	
	
}
