package org.yeastrc.proxl.xml.iprophet.main;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proxl.xml.iprophet.builder.XMLBuilder;
import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;
import org.yeastrc.proxl.xml.iprophet.reader.KojakConfReader;


/**
 * Run the program.
 * @author Michael Riffle
 * @date July, 2016
 *
 */
public class MainProgram {
	
	public void convertSearch( String pepXMLFilePath,
			                   String outFilePath,
			                   String fastaFilePath,
			                   Vector<String> kojakConfFilePaths,
			                   String linkerName,
			                   Vector<String> decoyIdentifiers
			                  ) throws Exception {
		
		IProphetAnalysis analysis = IProphetAnalysis.loadAnalysis( pepXMLFilePath );
		
		analysis.setDecoyIdentifiers( decoyIdentifiers );
		analysis.setKojakConfReader( KojakConfReader.getInstance( kojakConfFilePaths.get( 0 ) ) );
		analysis.setFastaFile( new File( fastaFilePath ) );
		analysis.setKojakConfFilePaths( kojakConfFilePaths );
		analysis.setLinkerName( linkerName );
		
		XMLBuilder builder = new XMLBuilder();
		builder.buildAndSaveXML(analysis, new File( outFilePath ) );		
	}
	
	public static void main( String[] args ) throws Exception {
		
		if( args.length < 1 || args[ 0 ].equals( "-h" ) ) {
			printHelp();
			System.exit( 0 );
		}
		
		CmdLineParser cmdLineParser = new CmdLineParser();
        
		CmdLineParser.Option pepXMLOpt = cmdLineParser.addStringOption( 'x', "pepxml" );	
		CmdLineParser.Option outfileOpt = cmdLineParser.addStringOption( 'o', "out-file" );	
		CmdLineParser.Option kojakConfOpt = cmdLineParser.addStringOption( 'k', "kojak-conf" );	
		CmdLineParser.Option fastaOpt = cmdLineParser.addStringOption( 'f', "fasta-file" );
		CmdLineParser.Option linkerOpt = cmdLineParser.addStringOption( 'l', "linker-name" );
		CmdLineParser.Option decoyOpt = cmdLineParser.addStringOption( 'd', "decoy-string" );

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
        
		/*
		 * Parse the pepXML file option
		 */
        String pepXMLFilePath = (String)cmdLineParser.getOptionValue( pepXMLOpt );
        if( pepXMLFilePath == null || pepXMLFilePath.equals( "" ) ) {
        	System.err.println( "Must specify a pepXML file. See help:\n" );
        	printHelp();
        	
        	System.exit( 1 );
        }
        
        File pepXMLFile = new File( pepXMLFilePath );
        if( !pepXMLFile.exists() ) {
        	System.err.println( "The pepXML file: " + pepXMLFilePath + " does not exist." );
        	System.exit( 1 );
        }
        
        if( !pepXMLFile.canRead() ) {
        	System.err.println( "Can not read pepXML file: " + pepXMLFilePath );
        	System.exit( 1 );
        }
        
        
        /*
         * Parse the output file option
         */
        String outFilePath = (String)cmdLineParser.getOptionValue( outfileOpt );
        if( outFilePath == null || outFilePath.equals( "" ) ) {
        	System.err.println( "Must specify an output file. See help:\n" );
        	printHelp();
        	
        	System.exit( 1 );
        }
        File outFile = new File( outFilePath );
        if( outFile.exists() ) {
        	System.err.println( "The output file: " + outFilePath + " already exists." );
        	System.exit( 1 );
        }
        
        
        /*
         * Parse the kojak conf files options
         */
        @SuppressWarnings("unchecked")
		Vector<String> kojakConfFilePaths = (Vector<String>)cmdLineParser.getOptionValues( kojakConfOpt );
        if( kojakConfFilePaths == null || kojakConfFilePaths.size() < 1 ) {
        	System.err.println( "Must specify at least one kojak conf file. See help:\n" );
        	printHelp();
        	
        	System.exit( 1 );
        }
        
        for( String kojakConfFilePath : kojakConfFilePaths ) {
        	File kojakConfFile = new File( kojakConfFilePath );
        	
            if( !kojakConfFile.exists() ) {
            	System.err.println( "The kojak conf file: " + kojakConfFile + " does not exist." );
            	System.exit( 1 );
            }
            
            if( !kojakConfFile.canRead() ) {
            	System.err.println( "Can not read kojak conf file: " + pepXMLFilePath );
            	System.exit( 1 );
            }
            
        }
        
        
        /*
         * Parse the linker name option
         */
		String linkerName = (String)cmdLineParser.getOptionValue( linkerOpt );
        if( linkerName == null ) {
        	System.err.println( "Must specify a linker name. See help:\n" );
        	printHelp();
        	
        	System.exit( 1 );
        }

        
        /*
         * Parse the decoy strings option
         */
        @SuppressWarnings("unchecked")
		Vector<String> decoyNames = (Vector<String>)cmdLineParser.getOptionValues( decoyOpt );
        if( decoyNames == null || decoyNames.size() < 1 ) {
        	System.err.println( "\nWARNING: No decoy identifiers given. Assuming all results are targets." );
        }
        
        
        /*
         * Parse the fasta file option
         */
        String fastaFilePath = (String)cmdLineParser.getOptionValue( fastaOpt );
        if( fastaFilePath == null || fastaFilePath.equals( "" ) ) {
        	System.err.println( "Must specify a fasta file. See help:\n" );
        	printHelp();
        	
        	System.exit( 1 );
        }
        
        File fastaFile = new File( fastaFilePath );
        if( !fastaFile.exists() ) {
        	System.err.println( "The fasta file: " + fastaFilePath + " does not exist." );
        	System.exit( 1 );
        }
        
        if( !fastaFile.canRead() ) {
        	System.err.println( "Can not read fasta file: " + fastaFilePath );
        	System.exit( 1 );
        }
        
        
        System.err.println( "Converting pepXML to ProXL XML with the following parameters:" );
        System.err.println( "\tpepXML path: " + pepXMLFilePath );
        System.err.println( "\toutput file path: " + outFilePath );
        System.err.println( "\tfasta file path: " + fastaFilePath );
        System.err.println( "\tkojak conf file paths: " );
        for( String kojakConfFilePath : kojakConfFilePaths ) {
        	 System.err.println( "\t\t" + kojakConfFilePath );
        }
     
        System.err.println( "\tlinker name: " + linkerName );
        System.err.println( "\tdecoyIdentifiers: " + StringUtils.join( decoyNames, "," ) );        
        
        /*
         * Run the conversion
         */
        MainProgram mp = new MainProgram();
        mp.convertSearch( pepXMLFilePath,
        		          outFilePath,
        		          fastaFilePath,
        		          kojakConfFilePaths,
        		          linkerName,
        		          decoyNames
        		         );

        
        System.err.println( "Done." );        
        System.exit( 0 );        
	}
	
	public static void printHelp() {
		
		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "help.txt" ) ) ) ) {
			
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
			
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
	}
}
