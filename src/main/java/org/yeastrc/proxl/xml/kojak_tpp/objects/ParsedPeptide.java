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

package org.yeastrc.proxl.xml.kojak_tpp.objects;

import java.util.Map;


/**
 * Represents a peptide parsed from a reported peptide, which had the form of:
 * PEP[123.22]TIDE. Includes the naked sequence and the parsed modifications
 * 
 * @author mriffle
 *
 */
public class ParsedPeptide {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modMap == null) ? 0 : modMap.hashCode());
		result = prime * result + ((nakedSequence == null) ? 0 : nakedSequence.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParsedPeptide other = (ParsedPeptide) obj;
		if (modMap == null) {
			if (other.modMap != null)
				return false;
		} else if (!modMap.equals(other.modMap))
			return false;
		if (nakedSequence == null) {
			if (other.nakedSequence != null)
				return false;
		} else if (!nakedSequence.equals(other.nakedSequence))
			return false;
		return true;
	}
	
	private String nakedSequence;
	private Map<Integer, Double> modMap;
	
	public String getNakedSequence() {
		return nakedSequence;
	}
	public void setNakedSequence(String nakedSequence) {
		this.nakedSequence = nakedSequence;
	}
	public Map<Integer, Double> getModMap() {
		return modMap;
	}
	public void setModMap(Map<Integer, Double> modMap) {
		this.modMap = modMap;
	}
	
}
