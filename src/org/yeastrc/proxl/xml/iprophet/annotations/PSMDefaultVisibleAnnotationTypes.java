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
		
		/*
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_SCORE );
			annotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_CALC_MASS );
			annotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_DELTA_MASS );
			annotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
			annotations.add( annotation );
		}
		*/
		
		return annotations;
	}
	
}
