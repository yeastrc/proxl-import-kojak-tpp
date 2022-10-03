package org.yeastrc.proxl.xml.kojak_tpp.annotations;

import org.yeastrc.proxl.xml.kojak_tpp.constants.IProphetConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

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
