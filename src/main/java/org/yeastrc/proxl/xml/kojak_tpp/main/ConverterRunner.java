package org.yeastrc.proxl.xml.kojak_tpp.main;

import org.yeastrc.proxl.xml.kojak_tpp.builder.XMLBuilder;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPReportedPeptide;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPResult;
import org.yeastrc.proxl.xml.kojak_tpp.reader.*;
import org.yeastrc.proxl.xml.kojak_tpp.utils.PepXMLUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ConverterRunner {

	public void runConversion( String pepXMLFilePath,
            String outFilePath,
            String fastaFilePath,
            File[] kojakConfFiles,
            String[] decoyIdentifiers
           ) throws Exception {
		
		///////// Set up config parameters ////////////////////
		
		System.err.print( "Loading configuration..." );
		
		TPPAnalysis analysis = TPPAnalysis.loadAnalysis( pepXMLFilePath );
		analysis.setKojakConfReader( KojakConfReader.getInstance( kojakConfFiles[0].getAbsolutePath() ) );

		if(decoyIdentifiers == null) {
			analysis.setDecoyIdentifiers(new ArrayList<>());
			String confDecoyPrefix = analysis.getKojakConfReader().getDecoyPrefix();
			if(confDecoyPrefix != null) {
				analysis.getDecoyIdentifiers().add(confDecoyPrefix);
			}
		} else {
			analysis.setDecoyIdentifiers(Arrays.asList(decoyIdentifiers));
		}

		analysis.setFastaFile( new File( fastaFilePath ) );
		analysis.setKojakConfFilePaths( Arrays.asList(kojakConfFiles) );

		System.err.println( "Done." );


		///////// Determine if Data contain iProphet Results ////////////////////
		System.err.print( "Detecting iProphet results... " );
		analysis.setHasIProphetData(PepXMLUtils.getHasIProphetData(analysis.getAnalysis()));
		if(analysis.getHasIProphetData())
			System.err.println( "iProphet Data Detected." );
		else
			System.err.println( "No iProphet Data Detected." );
		
		/////////////// Read in the data ////////////////////
		
		System.err.print( "Reading data in from pep XML..." );
		
		// parse the data from the pepXML into a java data structure suitable for writing as ProXL XML
		Map<TPPReportedPeptide, Collection<TPPResult>> resultsByReportedPeptide =
				TPPResultsParser.getInstance().getResultsFromAnalysis( analysis );
		
		System.err.println( "Done." );

		
		
		/////////////////// Perform FDR analysis ////////////////////////

		TPPErrorAnalysis iProphetErrorAnalysis = null;
		if(analysis.getHasIProphetData()) {
			System.err.print("Performing FDR analysis of InterProphet data...");
			iProphetErrorAnalysis = TPPErrorAnalyzer.performPeptideProphetAnalysis(resultsByReportedPeptide, TPPErrorAnalyzer.Type.INTERPROPHET);
			System.err.println("Done.");
		}

		System.err.print("Performing FDR analysis of PeptideProphet data...");
		TPPErrorAnalysis pProphetErrorAnalysis = TPPErrorAnalyzer.performPeptideProphetAnalysis(resultsByReportedPeptide, TPPErrorAnalyzer.Type.PEPTIDEPROPHET);
		System.err.println("Done.");
		
		/////////////////////// Write out XML //////////////////////
		
		System.err.print( "Writing out XML" );

		XMLBuilder builder = new XMLBuilder();
		builder.buildAndSaveXML(analysis, resultsByReportedPeptide, pProphetErrorAnalysis, iProphetErrorAnalysis, new File( outFilePath ) );
		
		System.err.println( "Done." );
		
	}
	
	
}
