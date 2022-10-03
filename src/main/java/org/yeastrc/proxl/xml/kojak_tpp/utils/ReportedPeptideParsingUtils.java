/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.proxl.xml.kojak_tpp.utils;

import org.yeastrc.proxl.xml.kojak_tpp.objects.ParsedPeptide;

import java.util.HashMap;
import java.util.Map;

public class ReportedPeptideParsingUtils {

	/**
	 * Get a ParsedPeptide, which is a peptide parsed from a reported peptide with the
	 * form of PEP[138.32]TIDE. Makes parsed mods and peptide sequence available.
	 * @param peptideSequence
	 * @return
	 */
	public static ParsedPeptide parsePeptide(String peptideSequence ) {
		
		StringBuilder nakedSequence = new StringBuilder();
		Map<Integer, Double> modMap = new HashMap<>();

		int peptidePosition = 0;
		StringBuilder numberBuild = null;
		
		for (int i = 0; i < peptideSequence.length(); i++){
		    char c = peptideSequence.charAt(i);
		    
		    if( c == '[' ) {
		    
		    	if( numberBuild != null )
		    		throw new IllegalArgumentException( "Invalid peptide format. Got: " + peptideSequence );   // got a [ while already building a number
		    	
		    	numberBuild = new StringBuilder();
		    	
		    } else if( c == ']' ) {
		    	
		    	if( numberBuild == null )
		    		throw new IllegalArgumentException( "Invalid peptide format. Got: " + peptideSequence );	// got a ] without a preceding [
		    	
		    	if( !(numberBuild.toString()).matches( "^-?\\d+(\\.\\d+)?$") )
		    		throw new IllegalArgumentException( "Invalid peptide format. Got: " + peptideSequence );	// did not get a valid number for the mod mass
		    	
		    	modMap.put( peptidePosition, Double.parseDouble( numberBuild.toString() ) );
		    	numberBuild = null;
		    	
		    } else if( numberBuild != null ) {
		    	
		    	numberBuild.append( c );
		    	
		    } else {
		    	
		    	if( !Character.isUpperCase( c ) )
		    		throw new IllegalArgumentException( "Invalid peptide format. Got: " + peptideSequence );	// did not get an uppercase letter while building the sequence

		    	
		    	peptidePosition++;
		    	nakedSequence.append( c );
		    }
		}
		
		if( numberBuild != null )
			throw new IllegalArgumentException( "Invalid peptide format. Got: " + peptideSequence );
		
		if( modMap.containsKey( 0 ) )
			throw new IllegalArgumentException( "Invalid peptide format. Cannot start with a mod. Got: " + peptideSequence );
		
		ParsedPeptide pp = new ParsedPeptide();
		pp.setModMap( modMap );
		pp.setNakedSequence( nakedSequence.toString() );
		
		return pp;
	}
	
}
