package org.yeastrc.proxl.xml.kojak_tpp.annotations;

import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;
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

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder(Boolean hasIProphetData) {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		if(hasIProphetData) {
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_ERROR);
				annotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
				annotations.add(annotation);
			}

			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE);
				annotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
				annotations.add(annotation);
			}
		} else {
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_ERROR);
				annotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET);
				annotations.add(annotation);
			}

			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_SCORE);
				annotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET);
				annotations.add(annotation);
			}
		}
		
		return annotations;
	}
}
