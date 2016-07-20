package org.yeastrc.proxl.xml.iprophet.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KojakConfReader {

	public static KojakConfReader getInstance( String filename ) {
		return new KojakConfReader( filename );
	}
	
	private KojakConfReader( String filename ) {
		this.file = new File( filename );
	}	
	private KojakConfReader() { }
	
	private void parseFile() throws Exception {
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader( new FileReader( this.file ) );
			String line = br.readLine();
			
			this.monolinkMasses = new ArrayList<>();
			this.crosslinkMasses = new ArrayList<>();
			this.staticModifications = new HashMap<>();
			
			while( line != null ) {
				
				String[] fields = line.split( "\\s+" );
				
				if( fields[ 0 ].equals( "mono_link" ) && fields[ 1 ].equals( "=" ) ) {
					this.monolinkMasses.add( new BigDecimal( fields[ fields.length - 1 ] ) );
				}
				
				else if( fields[ 0 ].equals( "cross_link" ) && fields[ 1 ].equals( "=" ) ) {
					this.crosslinkMasses.add( new BigDecimal( fields[ fields.length - 1 ] ) );
				}
				
				else if( fields[ 0 ].equals( "fixed_modification" ) && fields[ 1 ].equals( "=" ) ) {
					this.staticModifications.put( fields[ 2 ], new BigDecimal( fields[ 3 ] ) );
				}
				
				line = br.readLine();
			}
		} finally {
			if( br != null ) br.close();
		}
	}
	
	
	
	
	public Collection<BigDecimal> getMonolinkMasses() throws Exception {
		if( this.monolinkMasses == null )
			this.parseFile();
		
		return monolinkMasses;
	}



	public Collection<BigDecimal> getCrosslinkMasses() throws Exception {
		if( this.crosslinkMasses == null )
			this.parseFile();
		
		return crosslinkMasses;
	}



	public Map<String, BigDecimal> getStaticModifications()throws Exception {
		if( this.staticModifications == null )
			this.parseFile();
		
		return staticModifications;
	}

	private File file;
	private Collection<BigDecimal> monolinkMasses;
	private Collection<BigDecimal> crosslinkMasses;
	private Map<String, BigDecimal> staticModifications;
	
}
