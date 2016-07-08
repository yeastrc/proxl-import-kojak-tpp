package org.yeastrc.proxl.xml.iprophet.main;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;
import java.util.Collection;


/**
 * Run the program.
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class MainProgram {

	
	public void convertSearch(  ) throws Exception {
		
		
	}
	
	public static void main( String[] args ) throws Exception {
		
		if( args.length < 1 || args[ 0 ].equals( "-h" ) ) {
			printHelp();
			System.exit( 0 );
		}
		
		CmdLineParser cmdLineParser = new CmdLineParser();
        
		CmdLineParser.Option plinkINIOpt = cmdLineParser.addStringOption( 'i', "ini" );	
		CmdLineParser.Option outfileOpt = cmdLineParser.addStringOption( 'o', "out" );	
		CmdLineParser.Option binDirectoryOpt = cmdLineParser.addStringOption( 'b', "bin" );	
		CmdLineParser.Option dataDirectoryOpt = cmdLineParser.addStringOption( 'd', "data" );

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
		
        String iniFile = (String)cmdLineParser.getOptionValue( plinkINIOpt );
        String outFile = (String)cmdLineParser.getOptionValue( outfileOpt );
        String binDirectory = (String)cmdLineParser.getOptionValue( binDirectoryOpt );
        String dataDirectory = (String)cmdLineParser.getOptionValue( dataDirectoryOpt );
        
        MainProgram mp = new MainProgram();
        mp.convertSearch( );
        
	}
	
	/**
	 * Print helpt to STD OUT
	 */
	private static void printHelp() {
		
		System.out.println( "Description: Convert the results of a pLink analysis to a ProXL XML file suitable for import into ProXL." );
		System.out.println( "             Designed to work only with single, label-free searches that used a single cross-linker.\n" );
		
		System.out.println( "Usage: java -jar plink2ProxlXML.jar -i path -o path [-b path ] [-d path]\n" );

		System.out.println( "Example: java -jar plink2ProxlXML.jar -i c:\\plink_runs\\pLink.ini -o \"c:\\out put\\mySearch.proxl.xml\"" );
		System.out.println( "         java -jar plink2ProxlXML.jar -i c:/plink_runs/search/pLink.ini -o c:/output/mySearch.proxl.xml");
		System.out.println( "         java -jar plink2ProxlXML.jar -i /dir/pLink.ini -o /dir/mySearch.proxl.xml -b /dir/plink_install/release -d /dir/searches/search\n");
		
		System.out.println( "Options:" );
		System.out.println( "\t-i\t[Required] Full path to pLink.ini file used in the search." );
		System.out.println( "\t-o\t[Required] Full path to use for the outputfile (including file name)." );
		System.out.println( "\t-b\t[Optional] Full path to the pLink binary directory, where modify.ini and xlink.ini may be found." );
		System.out.println( "\t\t           If not present, value from pLink.ini is used." );
		System.out.println( "\t-d\t[Optional] Full path to the data output directory for pLink results. This directory typically" );
		System.out.println( "\t\t           contains 0.index, 1.sample, and 2.report directories." );
		System.out.println( "\t\t           If not present, value from pLink.ini is used." );
		
	}
}
