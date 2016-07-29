package org.yeastrc.proxl.xml.iprophet.annotations;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

/**
 * The default order by which to sort the results.
 * 
 * @author mriffle
 *
 */
public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();
		
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_ERROR );
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
