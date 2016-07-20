package org.yeastrc.proxl.xml.iprophet.annotations;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl.xml.iprophet.constants.*;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

public class PSMDefaultVisibleAnnotationTypes {

	/**
	 * Get the default visibile annotation types for StavroX data
	 * @return
	 */
	public static List<SearchAnnotation> getDefaultVisibleAnnotationTypes() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();
		
		
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_ERROR);
			annotation.setSearchProgram( IProphetConstants.SEARCH_PROGRAM_NAME_IPROPHET );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE );
			annotation.setSearchProgram( IProphetConstants.SEARCH_PROGRAM_NAME_IPROPHET );
			annotations.add( annotation );
		}
		
		return annotations;
	}
	
}
