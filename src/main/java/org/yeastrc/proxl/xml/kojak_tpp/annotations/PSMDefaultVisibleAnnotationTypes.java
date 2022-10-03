package org.yeastrc.proxl.xml.kojak_tpp.annotations;

import org.yeastrc.proxl.xml.kojak_tpp.constants.IProphetConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

public class PSMDefaultVisibleAnnotationTypes {

	/**
	 * Get the default visibile annotation types for iProphet data
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
