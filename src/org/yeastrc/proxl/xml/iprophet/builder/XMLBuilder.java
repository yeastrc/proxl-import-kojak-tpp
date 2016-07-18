package org.yeastrc.proxl.xml.iprophet.builder;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.proxl.xml.iprophet.reader.IProphetAnalysis;

import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFiles;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.DefaultVisibleAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;
import org.yeastrc.proxl_import.api.xml_dto.StaticModifications;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmAnnotations;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

/**
 * Take the populated pLink objects, convert to XML and write the XML file
 * 
 * @author Michael Riffle
 * @date Jul 18, 2016
 *
 */
public class XMLBuilder {

	/**
	 * Take the populated pLink objects, convert to XML and write the XML file
	 * 
	 * @param params The PLinkSearchParameters associated with this search
	 * @param results The results parsed from the plink output
	 * @param outfile The file to which the XML will be written
	 * @throws Exception
	 */
	public void buildAndSaveXML( IProphetAnalysis analysis, File outfile ) throws Exception {

		ProxlInput proxlInputRoot = new ProxlInput();

		proxlInputRoot.setFastaFilename( analysis.getFASTADatabase() );
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		proxlInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );
		
		SearchProgram searchProgram = new SearchProgram();
		searchPrograms.getSearchProgram().add( searchProgram );
		
		searchProgram.setName( PLinkConstants.SEARCH_PROGRAM_NAME );
		searchProgram.setDisplayName( PLinkConstants.SEARCH_PROGRAM_NAME );
		searchProgram.setVersion( PLinkConstants.SEARCH_PROGRAM_VERSION );
		
		//
		// Define the annotation types present in PLink data
		//
		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
		
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
		psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
		filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll( PSMAnnotationTypes.getFilterablePsmAnnotationTypes() );
		
		DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
		psmAnnotationTypes.setDescriptivePsmAnnotationTypes( descriptivePsmAnnotationTypes );
		descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType().addAll( PSMAnnotationTypes.getDescriptivePsmAnnotationTypes() );
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		xmlVisiblePsmAnnotations.getSearchAnnotation().addAll( PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() );
		
		//
		// Define the linker information
		//
		Linkers linkers = new Linkers();
		proxlInputRoot.setLinkers( linkers );

		Linker linker = new Linker();
		linkers.getLinker().add( linker );
		
		linker.setName( params.getLinker().getProxlName() );
		
		CrosslinkMasses masses = new CrosslinkMasses();
		linker.setCrosslinkMasses( masses );
		
		CrosslinkMass xlinkMass = new CrosslinkMass();
		linker.getCrosslinkMasses().getCrosslinkMass().add( xlinkMass );
		
		// set the mass for this crosslinker to the calculated mass for the crosslinker, as defined in the properties file
		xlinkMass.setMass( NumberUtils.getRoundedBigDecimal( params.getLinker().getMonoCrosslinkMass() ) );
		
		//
		// Define the static mods
		//
		StaticModifications smods = new StaticModifications();
		proxlInputRoot.setStaticModifications( smods );
		
		for( String modName : ModificationLookupUtils.getStaticModificationNames( params ) ) {
			PLinkModification smod = ModificationLookupUtils.getPLinkModificationFromParameters( modName, params );
			
			// a single defined mod can affect multiple residue types, get them all added
			for( String residue : smod.getResidues() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( residue );
				xmlSmod.setMassChange( NumberUtils.getRoundedBigDecimal( smod.getMonoisotopicMass() ) );
				
				smods.getStaticModification().add( xmlSmod );
			}
		}
		
		//
		// Add decoy labels (optional)
		//
		DecoyLabels xmlDecoyLabels = new DecoyLabels();
		proxlInputRoot.setDecoyLabels( xmlDecoyLabels );
		
		{
			DecoyLabel xmlDecoyLabel = new DecoyLabel();
			xmlDecoyLabels.getDecoyLabel().add( xmlDecoyLabel );
			
			xmlDecoyLabel.setPrefix( "random" );
		}
		
		{
			DecoyLabel xmlDecoyLabel = new DecoyLabel();
			xmlDecoyLabels.getDecoyLabel().add( xmlDecoyLabel );
			
			xmlDecoyLabel.setPrefix( "decoy" );
		}
		
		
		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );
		
		// need to organize all results by distinct reported peptide strings
		Map<PLinkReportedPeptide, Collection<PLinkResult>> resultsByReportedPeptide = new HashMap<PLinkReportedPeptide, Collection<PLinkResult>>();
		for( PLinkResult result : results ) {
			
			if( !resultsByReportedPeptide.containsKey( result.getReportedPeptide() ) )
				resultsByReportedPeptide.put( result.getReportedPeptide(), new ArrayList<PLinkResult>() );
			
			resultsByReportedPeptide.get( result.getReportedPeptide() ).add( result );
		}
		
		// iterate over each distinct reported peptide
		for( PLinkReportedPeptide rp : resultsByReportedPeptide.keySet() ) {
			
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( rp.toString() );
			
			if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK )
				xmlReportedPeptide.setType( LinkType.CROSSLINK );
			else if( rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK )
				xmlReportedPeptide.setType( LinkType.LOOPLINK );
			else
				xmlReportedPeptide.setType( LinkType.UNLINKED );	// monolinked peptide with no cross- or loop-link are considered unlinked (monolinks are considered mods)
			
			Peptides xmlPeptides = new Peptides();
			xmlReportedPeptide.setPeptides( xmlPeptides );
			
			// add in the 1st parsed peptide
			{
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide1().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide1().getMods() != null && rp.getPeptide1().getMods().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide1().getMods().keySet() ) {
						for( PLinkModification mod : rp.getPeptide1().getMods().get( position ) ) {
	
							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( NumberUtils.getRoundedBigDecimal( mod.getMonoisotopicMass() ) );
							xmlModification.setPosition( new BigInteger( String.valueOf( position ) ) );
							xmlModification.setIsMonolink( mod.isMonolink() );
							
						}
					}
				}
				
				// add in the linked position(s) in this peptide
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK || rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition1() ) ) );
					
					if( rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK ) {
						
						xmlLinkedPosition = new LinkedPosition();
						xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
						xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
						
					}
				}
				
			}
			
			
			// add in the 2nd parsed peptide, if it exists
			if( rp.getPeptide2() != null ) {
				
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide2().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide2().getMods() != null && rp.getPeptide2().getMods().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide2().getMods().keySet() ) {
						for( PLinkModification mod : rp.getPeptide2().getMods().get( position ) ) {

							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( NumberUtils.getRoundedBigDecimal( mod.getMonoisotopicMass() ) );
							xmlModification.setPosition( new BigInteger( String.valueOf( position ) ) );
							xmlModification.setIsMonolink( mod.isMonolink() );
							
						}
					}
				}
				
				// add in the linked position in this peptide
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
				}
			}
			
			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );
			
			// iterate over all PSMs for this reported peptide
			for( PLinkResult result : resultsByReportedPeptide.get( rp ) ) {
				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );
				
				xmlPsm.setScanNumber( new BigInteger( String.valueOf( result.getScanNumber() ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( result.getCharge() ) ) );
				
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK || rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK )
					xmlPsm.setLinkerMass( NumberUtils.getRoundedBigDecimal( params.getLinker().getMonoCrosslinkMass() ) );
				
				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );
				
				// handle e-value
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					xmlFilterablePsmAnnotation.setValue( NumberUtils.getScientificNotationBigDecimal( result.getEvalue() ) );
				}
				
				// add in the non-filterable descriptive annotations (e.g., calculated mass)
				DescriptivePsmAnnotations xmlDescriptivePsmAnnotations = new DescriptivePsmAnnotations();
				xmlPsm.setDescriptivePsmAnnotations( xmlDescriptivePsmAnnotations );
				
				{
					// handle calc mass
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_CALC_MASS );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getCalculatedMass() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getCalculatedMass() ) );
					}
				}
				
				{
					// handle delta mass
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_DELTA_MASS );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getDeltaMass() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getDeltaMass() ) );
					}
				}
				
				{
					// handle delta mass ppm
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_PPM );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getDeltaMassPPM() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getDeltaMassPPM() ) );
					}
				}
				
				
			}//end iterating over all PSMs for a reported peptide
			
			
		}// end iterating over distinct reported peptides

		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
		xmlConfigurationFile.setFileName( ( new File( params.getPlinkINI().getFilename() ) ).getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( params.getPlinkINI().getFilename() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain(outfile, proxlInputRoot);
		
	}
	
}
