package org.yeastrc.proxl.xml.kojak_tpp.annotations;

import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

public class PSMDefaultVisibleAnnotationTypes {

	/**
	 * Get the default visibile annotation types for iProphet data
	 * @return
	 */
	public static List<SearchAnnotation> getDefaultVisibleAnnotationTypes(Boolean hasIProphetData) {
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
