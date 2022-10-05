package org.yeastrc.proxl.xml.kojak_tpp.main;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proxl.xml.kojak_tpp.constants.ConverterConstants;
import org.yeastrc.proxl.xml.kojak_tpp.constants.IProphetConstants;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * Run the program.
 * @author Michael Riffle
 * @date July, 2016
 *
 */
@CommandLine.Command(name = "java -jar " + ConverterConstants.CONVERSION_PROGRAM_NAME,
		mixinStandardHelpOptions = true,
		version = ConverterConstants.CONVERSION_PROGRAM_NAME + " " + ConverterConstants.CONVERSION_PROGRAM_VERSION,
		sortOptions = false,
		synopsisHeading = "%n",
		descriptionHeading = "%n@|bold,underline Description:|@%n%n",
		optionListHeading = "%n@|bold,underline Options:|@%n",
		description = "Convert the results of a Kojak + TPP analysis to a ProXL XML file suitable for import into ProXL.\n\n" +
				"More info at: " + ConverterConstants.CONVERSION_PROGRAM_URI
)
public class MainProgram implements Runnable {

	@CommandLine.Option(names = { "-x", "--pepxml" }, required = true, description = "The full path to the pepXML file.")
	private File pepXMLFile;

	@CommandLine.Option(names = { "-k", "--kojak-conf" }, required = true, description = "The full path to a Kojak configuration (params) file. Use multiple times to specify multiple files.")
	private File[] kojakConfFiles;

	@CommandLine.Option(names = { "-f", "--fasta-file" }, required = true, description = "The full path to the FASTA file used for the search.")
	private File fastaFile;

	@CommandLine.Option(names = { "-o", "--out-file" }, required = true, description = "Full path to use for the ProXL XML output file (including file name).")
	private File outFile;

	@CommandLine.Option(names = { "-d", "--decoy-string" }, required = false, description = "[Optional] Override the value for the decoy prefix found in the Kojak conf file. May be used multiple times to specify multiple decoy strings.")
	private String[] decoyString;

	@CommandLine.Option(names = { "-i", "--import-filter" }, required = false, description = "[Optional] Only PSMs with an error <= this" +
			" value will be imported into ProXL. Default" +
			" is 0.05. Set to 1 or more to disable" +
			" import filtering.")
	private BigDecimal importFilterCutoff;

	@CommandLine.Option(names = { "-v", "--verbose" }, required = false, description = "[Optional] If present, complete error messages will be printed. Useful for debugging errors.")
	private boolean verboseRequested = false;
	public void run()  {
		
		printRuntimeInfo();
        
        if( !pepXMLFile.exists() ) {
        	System.err.println( "The pepXML file: " + pepXMLFile.getAbsolutePath() + " does not exist." );
        	System.exit( 1 );
        }
        
        if( !pepXMLFile.canRead() ) {
        	System.err.println( "Can not read pepXML file: " + pepXMLFile.getAbsolutePath() );
        	System.exit( 1 );
        }

        if( outFile.exists() ) {
        	System.err.println( "The output file: " + outFile.getAbsolutePath() + " already exists." );
        	System.exit( 1 );
        }

        for( File kojakConfFile : kojakConfFiles) {

            if( !kojakConfFile.exists() ) {
            	System.err.println( "The kojak conf file: " + kojakConfFile + " does not exist." );
            	System.exit( 1 );
            }
            
            if( !kojakConfFile.canRead() ) {
            	System.err.println( "Can not read kojak conf file: " + kojakConfFile.getAbsolutePath() );
            	System.exit( 1 );
            }
            
        }

        if( decoyString == null || decoyString.length < 1 ) {
        	System.err.println( "\nWARNING: No decoy identifiers given. Assuming all results are targets." );
        }

        
        if( !fastaFile.exists() ) {
        	System.err.println( "The fasta file: " + fastaFile.getAbsolutePath() + " does not exist." );
        	System.exit( 1 );
        }
        
        if( !fastaFile.canRead() ) {
        	System.err.println( "Can not read fasta file: " + fastaFile.getAbsolutePath() );
        	System.exit( 1 );
        }

        
        System.err.println( "Converting pepXML to ProXL XML with the following parameters:" );
        System.err.println( "\tpepXML path: " + pepXMLFile.getAbsolutePath() );
        System.err.println( "\toutput file path: " + outFile.getAbsolutePath() );
        System.err.println( "\tfasta file path: " + fastaFile.getAbsolutePath() );
        System.err.println( "\timport cutoff: " + ( importFilterCutoff == null ? IProphetConstants.DEFAULT_IMPORT_CUTOFF : importFilterCutoff.toString() ) );
        System.err.println( "\tkojak conf file paths: " );
        for( File kojakConfFile : kojakConfFiles) {
        	 System.err.println( "\t\t" + kojakConfFile.getAbsolutePath() );
        }
     
        System.err.println( "\tdecoy overrides: " + StringUtils.join( decoyString, "," ) );
        
        /*
         * Run the conversion
         */

        try {
	        ConverterRunner cr = new ConverterRunner();
	        cr.runConversion( pepXMLFile.getAbsolutePath(),
	        		          outFile.getAbsolutePath(),
	        		          fastaFile.getAbsolutePath(),
	        		          kojakConfFiles,
	        		          decoyString,
	        		          importFilterCutoff
	        		         );
        } catch( Throwable t ) {

        	System.err.println( "Got error during conversion: " + t.getMessage() );

			if(verboseRequested) {
				t.printStackTrace();
			}

        }

	}

	public static void main( String[] args ) {

		CommandLine.run(new MainProgram(), args);

	}
	
	/**
	 * Print runtime info to STD ERR
	 * @throws Exception 
	 */
	public static void printRuntimeInfo() {

		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "run.txt" ) ) ) ) {

			String line = null;
			while ( ( line = br.readLine() ) != null ) {

				line = line.replace( "{{URL}}", ConverterConstants.CONVERSION_PROGRAM_URI );
				line = line.replace( "{{VERSION}}", ConverterConstants.CONVERSION_PROGRAM_VERSION );

				System.err.println( line );
				
			}
			
			System.err.println( "" );

		} catch ( Exception e ) {
			System.out.println( "Error printing runtime information." );
		}
	}
}
