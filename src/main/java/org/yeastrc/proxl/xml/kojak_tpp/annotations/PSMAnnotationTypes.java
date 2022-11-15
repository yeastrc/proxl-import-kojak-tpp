package org.yeastrc.proxl.xml.kojak_tpp.annotations;

import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypes {

	// Kojak scores
	public static final String KOJAK_ANNOTATION_TYPE_SCORE = "Score";
	public static final String KOJAK_ANNOTATION_TYPE_DELTASCORE = "delta Score";
	public static final String KOJAK_ANNOTATION_TYPE_PPMERROR = "PPM error";
	
	// iProphet scores
	public static final String IPROPHET_ANNOTATION_TYPE_SCORE = "IProphet Score";
	public static final String IPROPHET_ANNOTATION_TYPE_ERROR = "IProphet FDR";
	
	// PeptideProphet scores
	public static final String PPROPHET_ANNOTATION_TYPE_SCORE = "PProphet Score";
	public static final String PPROPHET_ANNOTATION_TYPE_ERROR = "PProphet FDR";


	/**
	 * Get the list of filterable PSM annotation types in StavroX data
	 * @return
	 */
	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes(String programName, Boolean hasIProphetData) {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		if( programName.equals( TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( IPROPHET_ANNOTATION_TYPE_SCORE );
				type.setDescription( "iProphet Probability Score" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );
	
				
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( IPROPHET_ANNOTATION_TYPE_ERROR );
				type.setDescription( "Error rate calculated from iProphet probability scores." );
				type.setDefaultFilterValue( new BigDecimal( "0.01" ) );
				type.setDefaultFilter( true );
				type.setFilterDirection( FilterDirectionType.BELOW );
				
				types.add( type );
			}
		}

		else if( programName.equals( TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PPROPHET_ANNOTATION_TYPE_SCORE );
				type.setDescription( "PeptideProphet Probability Score" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );
	
				
				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PPROPHET_ANNOTATION_TYPE_ERROR );
				type.setDescription( "Error rate calculated from PeptideProphet probability scores." );
				type.setDefaultFilterValue( new BigDecimal( "0.01" ) );

				if(!hasIProphetData)
					type.setDefaultFilter(true);
				else
					type.setDefaultFilter(false);

				type.setFilterDirection( FilterDirectionType.BELOW );

				types.add( type );
			}
		}
		
		else if( programName.equals( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_SCORE );
				type.setDescription( "Kojak Score" );
				type.setDefaultFilterValue( new BigDecimal( "1" ) );
				type.setDefaultFilter( false );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}
		}
		
		return types;
	}
	
	/**
	 * Get the list of descriptive (non-filterable) PSM annotation types in Kojak data
	 * @return
	 */
	public static List<DescriptivePsmAnnotationType> getDescriptivePsmAnnotationTypes( String programName ) {
		List<DescriptivePsmAnnotationType> types = new ArrayList<DescriptivePsmAnnotationType>();
		
		if( programName.equals( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK ) ) {
			{
				DescriptivePsmAnnotationType type = new DescriptivePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_DELTASCORE );
				type.setDescription( type.getName() );
				
				types.add( type );
			}
			
			{
				DescriptivePsmAnnotationType type = new DescriptivePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_PPMERROR );
				type.setDescription( type.getName() );
				
				types.add( type );
			}
		}

		
		return types;		
	}
	
}
