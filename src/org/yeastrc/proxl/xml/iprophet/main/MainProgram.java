package org.yeastrc.proxl.xml.iprophet.main;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;

import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;
import org.yeastrc.proxl.xml.iprophet.reader.IProphetTargetDecoyAnalyzer;


/**
 * Run the program.
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class MainProgram {

	private static String testPepXMLFile = "C:\\Users\\mriffle\\Desktop\\interact-RJAZ205_XLSecondScoreFrac.ipro.pep.xml";
	
	public void convertSearch( String pepXMLFile ) throws Exception {
		
		IProphetAnalysis analysis = IProphetAnalysis.loadAnalysis( testPepXMLFile );
		analysis.setDecoyIdentifier( "rand" );
		
		IProphetTargetDecoyAnalyzer analyzer = IProphetTargetDecoyAnalyzer.getInstance( analysis );
		analyzer.performAnalysis();
		
	}
	
	public static void main( String[] args ) throws Exception {
		
		(new MainProgram()).convertSearch( null );
		
		/*
		if( args.length < 1 || args[ 0 ].equals( "-h" ) ) {
			printHelp();
			System.exit( 0 );
		}
		
		CmdLineParser cmdLineParser = new CmdLineParser();
        
		CmdLineParser.Option pepXMLOpt = cmdLineParser.addStringOption( 'x', "pepxml" );	
		CmdLineParser.Option outfileOpt = cmdLineParser.addStringOption( 'o', "out" );	


        // parse command line options
        try { cmdLineParser.parse(args); }
        catch (IllegalOptionValueException e) {
        	printHelp();
            System.exit( 1 );
        }
        catch (UnknownOptionException e) {
           printHelp();
           System.exit( 1 );
        }
		
        String pepXMLFile = (String)cmdLineParser.getOptionValue( pepXMLOpt );
        String outFile = (String)cmdLineParser.getOptionValue( outfileOpt );
        
        MainProgram mp = new MainProgram();
        mp.convertSearch( pepXMLFile );
        */
	}
	
	/**
	 * Print helpt to STD OUT
	 */
	private static void printHelp() {
		

		
	}
}
