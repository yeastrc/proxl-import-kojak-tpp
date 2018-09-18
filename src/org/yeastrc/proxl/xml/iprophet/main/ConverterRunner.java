package org.yeastrc.proxl.xml.iprophet.main;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.yeastrc.proxl.xml.iprophet.builder.XMLBuilder;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetReportedPeptide;
import org.yeastrc.proxl.xml.iprophet.objects.IProphetResult;
import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;
import org.yeastrc.proxl.xml.iprophet.reader.IProphetResultsParser;
import org.yeastrc.proxl.xml.iprophet.reader.KojakConfReader;
import org.yeastrc.proxl.xml.iprophet.reader.TPPErrorAnalysis;
import org.yeastrc.proxl.xml.iprophet.reader.TPPErrorAnalyzer;

public class ConverterRunner {

	// quickly get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }

	public void runConversion( String pepXMLFilePath,
            String outFilePath,
            String fastaFilePath,
            Vector<String> kojakConfFilePaths,
            String linkerName,
            Vector<String> decoyIdentifiers,
            BigDecimal importCutoff
           ) throws Exception {
		
		
		///////// Set up config parameters ////////////////////
		
		System.err.print( "Loading configuration..." );
		
		IProphetAnalysis analysis = IProphetAnalysis.loadAnalysis( pepXMLFilePath );
		
		analysis.setDecoyIdentifiers( decoyIdentifiers );
		analysis.setKojakConfReader( KojakConfReader.getInstance( kojakConfFilePaths.get( 0 ) ) );
		analysis.setFastaFile( new File( fastaFilePath ) );
		analysis.setKojakConfFilePaths( kojakConfFilePaths );
		analysis.setLinkerName( linkerName );
		if( importCutoff != null )
			analysis.setImportFilter( importCutoff );

		System.err.println( "Done." );
		
		
		
		/////////////// Read in the data ////////////////////
		
		System.err.print( "Reading data in from pep XML..." );
		
		// parse the data from the pepXML into a java data structure suitable for writing as ProXL XML
		Map<IProphetReportedPeptide, Collection<IProphetResult>> resultsByReportedPeptide = 
				IProphetResultsParser.getInstance().getResultsFromAnalysis( analysis );
		
		System.err.println( "Done." );

		
		
		/////////////////// Perform FDR analysis ////////////////////////
		
		System.err.print( "Performing FDR analysis of iProphet data..." );

		TPPErrorAnalysis errorAnalysis = TPPErrorAnalyzer.performPeptideProphetAnalysis( resultsByReportedPeptide );		
		
		System.err.println( "Done." );
		
		
		/////////////////////// Write out XML //////////////////////
		
		System.err.print( "Writing out XML" );

		XMLBuilder builder = new XMLBuilder();
		builder.buildAndSaveXML(analysis, resultsByReportedPeptide, errorAnalysis, new File( outFilePath ) );
		
		System.err.println( "Done." );
		
	}
	
	
}
