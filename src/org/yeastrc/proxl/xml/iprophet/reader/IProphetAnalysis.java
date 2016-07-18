package org.yeastrc.proxl.xml.iprophet.reader;

import java.io.File;

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
	private String decoyIdentifier;
	private String fastaDatabase;
	
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

	/**
	 * The string used to identify a decoy hit. If this string is present at all in the protein name, it is considered a decoy.
	 * @return
	 */
	public String getDecoyIdentifier() {
		return decoyIdentifier;
	}

	public void setDecoyIdentifier(String decoyIdentifier) {
		this.decoyIdentifier = decoyIdentifier;
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
		if( this.fastaDatabase == null ) {
			
			String localPath = this.getAnalysis().getMsmsRunSummary().get( 0 ).getSearchSummary().get( 0 ).getSearchDatabase().getLocalPath();
			if( localPath == null )
				throw new Exception( "Could not determine local path for FASTA file used in analysis." );
			
			File fastaFile = new File( localPath );
			this.fastaDatabase = fastaFile.getName();			
		}
		
		return this.fastaDatabase;
	}
	
	
	
}
