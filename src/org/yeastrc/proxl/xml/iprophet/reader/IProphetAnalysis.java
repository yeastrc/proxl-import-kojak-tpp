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

	
	
	
}
