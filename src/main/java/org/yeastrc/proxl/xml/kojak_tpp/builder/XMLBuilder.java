package org.yeastrc.proxl.xml.kojak_tpp.builder;

import org.yeastrc.proxl.xml.kojak_tpp.annotations.PSMAnnotationTypes;
import org.yeastrc.proxl.xml.kojak_tpp.annotations.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPReportedPeptide;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPResult;
import org.yeastrc.proxl.xml.kojak_tpp.objects.KojakConfCrosslinker;
import org.yeastrc.proxl.xml.kojak_tpp.reader.TPPAnalysis;
import org.yeastrc.proxl.xml.kojak_tpp.reader.TPPErrorAnalysis;
import org.yeastrc.proxl.xml.kojak_tpp.utils.ModUtils;
import org.yeastrc.proxl.xml.kojak_tpp.utils.PepXMLUtils;
import org.yeastrc.proxl_import.api.xml_dto.*;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Take the populated pLink objects, convert to XML and write the XML file
 * 
 * @author Michael Riffle
 * @date Jul 18, 2016
 *
 */
public class XMLBuilder {

	/**
	 * Build and save the XML document based on the analysis results.
	 *
	 * @param analysis
	 * @param resultsByReportedPeptide
	 * @param pProphetErrorAnalysis
	 * @param iProphetErrorAnalysis
	 * @param outfile
	 * @throws Exception
	 */
	public void buildAndSaveXML(
								 TPPAnalysis analysis,
								 Map<TPPReportedPeptide, Collection<TPPResult>> resultsByReportedPeptide,
								 TPPErrorAnalysis pProphetErrorAnalysis,
								 TPPErrorAnalysis iProphetErrorAnalysis,
			                     File outfile
			                    ) throws Exception {

		
		File fastaFile = analysis.getFastaFile();
		
		// root node of the XML
		ProxlInput proxlInputRoot = new ProxlInput();

		proxlInputRoot.setFastaFilename( analysis.getFASTADatabase() );
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		proxlInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		//
		// Define the sort order
		//
		AnnotationSortOrder annotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( annotationSortOrder );
		
		PsmAnnotationSortOrder psmAnnotationSortOrder = new PsmAnnotationSortOrder();
		annotationSortOrder.setPsmAnnotationSortOrder( psmAnnotationSortOrder );
		
		psmAnnotationSortOrder.getSearchAnnotation().addAll( org.yeastrc.proxl.xml.kojak_tpp.annotations.PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder(analysis.getHasIProphetData()) );
		
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		// add interprophet
		if(analysis.getHasIProphetData()) {

			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
		
			searchProgram.setName(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
			searchProgram.setDisplayName(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
			searchProgram.setVersion(PepXMLUtils.getTPPVersion(analysis));

			{
				PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
				searchProgram.setPsmAnnotationTypes(psmAnnotationTypes);

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
				psmAnnotationTypes.setFilterablePsmAnnotationTypes(filterablePsmAnnotationTypes);
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll(PSMAnnotationTypes.getFilterablePsmAnnotationTypes(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET, analysis.getHasIProphetData()));

			}
		}

		// add peptideprophet
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add(searchProgram);

			searchProgram.setName(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET);
			searchProgram.setDisplayName(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET);
			searchProgram.setVersion(PepXMLUtils.getTPPVersion(analysis));

			{
				PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
				searchProgram.setPsmAnnotationTypes(psmAnnotationTypes);

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
				psmAnnotationTypes.setFilterablePsmAnnotationTypes(filterablePsmAnnotationTypes);
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll(PSMAnnotationTypes.getFilterablePsmAnnotationTypes(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET, analysis.getHasIProphetData()));

			}
		}
		
		
		// add kojak
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add(searchProgram);

			searchProgram.setName(TPPConstants.SEARCH_PROGRAM_NAME_KOJAK);
			searchProgram.setDisplayName(TPPConstants.SEARCH_PROGRAM_NAME_KOJAK);
			searchProgram.setVersion(PepXMLUtils.getKojakVersionFromXML(analysis.getAnalysis()));

			{
				PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
				searchProgram.setPsmAnnotationTypes(psmAnnotationTypes);

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
				psmAnnotationTypes.setFilterablePsmAnnotationTypes(filterablePsmAnnotationTypes);
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll(PSMAnnotationTypes.getFilterablePsmAnnotationTypes(TPPConstants.SEARCH_PROGRAM_NAME_KOJAK, analysis.getHasIProphetData()));

				DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
				psmAnnotationTypes.setDescriptivePsmAnnotationTypes(descriptivePsmAnnotationTypes);
				descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType().addAll(PSMAnnotationTypes.getDescriptivePsmAnnotationTypes(TPPConstants.SEARCH_PROGRAM_NAME_KOJAK));
			}
		}

		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );

		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		xmlVisiblePsmAnnotations.getSearchAnnotation().addAll( PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes(analysis.getHasIProphetData()) );
		
		//
		// Define the linker information
		//
		Linkers linkers = new Linkers();
		proxlInputRoot.setLinkers( linkers );

		Linker linker = new Linker();
		linkers.getLinker().add( linker );

		KojakConfCrosslinker userLinker = analysis.getKojakConfReader().getLinker();

		linker.setName( userLinker.getName() );
		
		CrosslinkMasses masses = new CrosslinkMasses();
		linker.setCrosslinkMasses( masses );
		
		CrosslinkMass xlinkMass = new CrosslinkMass();
		linker.getCrosslinkMasses().getCrosslinkMass().add( xlinkMass );
		xlinkMass.setMass(userLinker.getCrosslinkMass());

		if(userLinker.getMonolinkMasses().size() > 0) {
			MonolinkMasses xMonoLinkMasses = new MonolinkMasses();
			linker.setMonolinkMasses(xMonoLinkMasses);

			for(BigDecimal monolinkMass : userLinker.getMonolinkMasses()) {
				MonolinkMass xMonolinkMass = new MonolinkMass();
				xMonolinkMass.setMass(monolinkMass);

				xMonoLinkMasses.getMonolinkMass().add(xMonolinkMass);
			}
		}

		LinkedEnds xLinkedEnds = new LinkedEnds();
		linker.setLinkedEnds(xLinkedEnds);

		// linked end 1
		{
			LinkedEnd xLinkedEnd = new LinkedEnd();
			xLinkedEnds.getLinkedEnd().add(xLinkedEnd);

			if (userLinker.getLinkableEnd1().getLinkableResidues().size() > 0) {
				Residues xResidues = new Residues();
				xLinkedEnd.setResidues(xResidues);

				for (String residue : userLinker.getLinkableEnd1().getLinkableResidues()) {
					xResidues.getResidue().add(residue);
				}
			}

			if(userLinker.getLinkableEnd1().isLinksProteinCTerminus() || userLinker.getLinkableEnd1().isLinksProteinNTerminus()) {
				ProteinTermini xProteinTermini = new ProteinTermini();
				xLinkedEnd.setProteinTermini(xProteinTermini);

				if(userLinker.getLinkableEnd1().isLinksProteinNTerminus()) {
					ProteinTerminus xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);

					xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ONE);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);
				}

				if(userLinker.getLinkableEnd1().isLinksProteinCTerminus()) {
					ProteinTerminus xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.C);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);
				}
			}
		}

		// linked end 2
		{
			LinkedEnd xLinkedEnd = new LinkedEnd();
			xLinkedEnds.getLinkedEnd().add(xLinkedEnd);

			if (userLinker.getLinkableEnd2().getLinkableResidues().size() > 0) {
				Residues xResidues = new Residues();
				xLinkedEnd.setResidues(xResidues);

				for (String residue : userLinker.getLinkableEnd2().getLinkableResidues()) {
					xResidues.getResidue().add(residue);
				}
			}

			if(userLinker.getLinkableEnd2().isLinksProteinCTerminus() || userLinker.getLinkableEnd2().isLinksProteinNTerminus()) {
				ProteinTermini xProteinTermini = new ProteinTermini();
				xLinkedEnd.setProteinTermini(xProteinTermini);

				if(userLinker.getLinkableEnd2().isLinksProteinNTerminus()) {
					ProteinTerminus xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);

					xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ONE);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);
				}

				if(userLinker.getLinkableEnd2().isLinksProteinCTerminus()) {
					ProteinTerminus xProteinTerminus = new ProteinTerminus();
					xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.C);
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
					xProteinTermini.getProteinTerminus().add(xProteinTerminus);
				}
			}
		}

		
		//
		// Define the static mods
		//
		StaticModifications smods = new StaticModifications();
		proxlInputRoot.setStaticModifications( smods );
		
		for( String moddedResidue : analysis.getKojakConfReader().getStaticModifications().keySet() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( moddedResidue );
				xmlSmod.setMassChange( analysis.getKojakConfReader().getStaticModifications().get( moddedResidue ) );
				
				smods.getStaticModification().add( xmlSmod );
		}
		
		//
		// Add decoy labels (optional)
		//
		DecoyLabels xmlDecoyLabels = new DecoyLabels();
		proxlInputRoot.setDecoyLabels( xmlDecoyLabels );
		
		
		if( analysis.getDecoyIdentifiers() != null ) {
			for( String decoyId : analysis.getDecoyIdentifiers() ) {
				DecoyLabel xmlDecoyLabel = new DecoyLabel();
				xmlDecoyLabels.getDecoyLabel().add( xmlDecoyLabel );				
				xmlDecoyLabel.setPrefix( decoyId);
			}
		}
		
		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );
		

		
		// create a unique set of peptides found, to ensure each one is found in at least 
		// one of the reported proteins
		Collection<String> peptides = new HashSet<>();
		
		// iterate over each distinct reported peptide
		for( TPPReportedPeptide rp : resultsByReportedPeptide.keySet() ) {
			
			peptides.add( rp.getPeptide1().getSequence() );
			if( rp.getPeptide2() != null ) peptides.add( rp.getPeptide2().getSequence() );
			
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( rp.toString() );
			
			if( rp.getType() == TPPConstants.LINK_TYPE_CROSSLINK )
				xmlReportedPeptide.setType( LinkType.CROSSLINK );
			else if( rp.getType() == TPPConstants.LINK_TYPE_LOOPLINK )
				xmlReportedPeptide.setType( LinkType.LOOPLINK );
			else
				xmlReportedPeptide.setType( LinkType.UNLINKED );
			
			Peptides xmlPeptides = new Peptides();
			xmlReportedPeptide.setPeptides( xmlPeptides );
			
			// add in the 1st parsed peptide
			{
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide1().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide1().getModifications() != null && rp.getPeptide1().getModifications().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide1().getModifications().keySet() ) {
						for( BigDecimal modMass : rp.getPeptide1().getModifications().get( position ) ) {
	
							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( modMass );
							xmlModification.setPosition( new BigInteger( String.valueOf( position ) ) );
							xmlModification.setIsMonolink( ModUtils.isMonolink( modMass, analysis.getKojakConfReader() ) );
							
						}
					}
				}
				
				// add in the linked position(s) in this peptide
				if( rp.getType() == TPPConstants.LINK_TYPE_CROSSLINK || rp.getType() == TPPConstants.LINK_TYPE_LOOPLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition1() ) ) );
					
					if( rp.getType() == TPPConstants.LINK_TYPE_LOOPLINK ) {
						
						xmlLinkedPosition = new LinkedPosition();
						xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
						xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
						
					}
				}

				if(rp.getPeptide1().getIsotopeLabel() != null) {
					Peptide.PeptideIsotopeLabels xPeptideIsotopeLabels = new Peptide.PeptideIsotopeLabels();
					xmlPeptide.setPeptideIsotopeLabels(xPeptideIsotopeLabels);

					Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel xPeptideIsotopeLabel = new Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel();
					xPeptideIsotopeLabel.setLabel(rp.getPeptide1().getIsotopeLabel());
					xPeptideIsotopeLabels.setPeptideIsotopeLabel(xPeptideIsotopeLabel);
				}
				
			}
			
			
			// add in the 2nd parsed peptide, if it exists
			if( rp.getPeptide2() != null ) {
				
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide2().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide2().getModifications() != null && rp.getPeptide2().getModifications().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide2().getModifications().keySet() ) {
						for( BigDecimal modMass : rp.getPeptide2().getModifications().get( position ) ) {
							
							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( modMass );
							xmlModification.setPosition( new BigInteger( String.valueOf( position ) ) );
							xmlModification.setIsMonolink( ModUtils.isMonolink( modMass, analysis.getKojakConfReader() ) );
							
						}
					}
				}
				
				// add in the linked position in this peptide
				if( rp.getType() == TPPConstants.LINK_TYPE_CROSSLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
				}

				if(rp.getPeptide2().getIsotopeLabel() != null) {
					Peptide.PeptideIsotopeLabels xPeptideIsotopeLabels = new Peptide.PeptideIsotopeLabels();
					xmlPeptide.setPeptideIsotopeLabels(xPeptideIsotopeLabels);

					Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel xPeptideIsotopeLabel = new Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel();
					xPeptideIsotopeLabel.setLabel(rp.getPeptide2().getIsotopeLabel());
					xPeptideIsotopeLabels.setPeptideIsotopeLabel(xPeptideIsotopeLabel);
				}
			}
			
			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );
			
			// iterate over all PSMs for this reported peptide
			for( TPPResult result : resultsByReportedPeptide.get( rp ) ) {
				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );
				
				xmlPsm.setScanNumber( new BigInteger( String.valueOf( result.getScanNumber() ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( result.getCharge() ) ) );
				xmlPsm.setScanFileName( result.getScanFile() );
				
				if( rp.getType() == TPPConstants.LINK_TYPE_CROSSLINK || rp.getType() == TPPConstants.LINK_TYPE_LOOPLINK )
					xmlPsm.setLinkerMass( result.getLinkerMass() );
				
				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );
				
				if(analysis.getHasIProphetData()) {
					{
						// handle iprophet error
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_ERROR);
						xmlFilterablePsmAnnotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
						xmlFilterablePsmAnnotation.setValue(iProphetErrorAnalysis.getError(result.getInterProphetScore()));
					}

					// handle iprophet score
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE);
						xmlFilterablePsmAnnotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_IPROPHET);
						xmlFilterablePsmAnnotation.setValue(result.getInterProphetScore());
					}
				}

				{
					// handle pprophet error
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

					xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_ERROR);
					xmlFilterablePsmAnnotation.setSearchProgram(TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET);
					xmlFilterablePsmAnnotation.setValue(pProphetErrorAnalysis.getError(result.getPeptideProphetScore()));
				}

				// handle peptideprophet score
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( TPPConstants.SEARCH_PROGRAM_NAME_PPROPHET );
					xmlFilterablePsmAnnotation.setValue( result.getPeptideProphetScore() );
				}
				
				// handle kojak score
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK );
					xmlFilterablePsmAnnotation.setValue( result.getKojakScore() );
				}
				
				// add in the non-filterable descriptive annotations (e.g., calculated mass)
				DescriptivePsmAnnotations xmlDescriptivePsmAnnotations = new DescriptivePsmAnnotations();
				xmlPsm.setDescriptivePsmAnnotations( xmlDescriptivePsmAnnotations );
				
				{
					// handle error ppm
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_PPMERROR );
					xmlDescriptivePsmAnnotation.setSearchProgram( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK );
					
					// try to limit this value to the chosen number of decimal places
					xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getPpmError() ) );
				}
				
				{
					// handle delta mass
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_DELTASCORE );
					xmlDescriptivePsmAnnotation.setSearchProgram( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK );
					
					// try to limit this value to the chosen number of decimal places
					xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getDeltaScore() ) );
				}
				

				
				
			}//end iterating over all PSMs for a reported peptide
			
			
		}// end iterating over distinct reported peptides



		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				proxlInputRoot,
				fastaFile,
				peptides,
				analysis.getDecoyIdentifiers(),
				analysis.getKojakConfReader().getFilter15N()
		);

		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		for( File kojakConfFile : analysis.getKojakConfFiles() ) {
			ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
			xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
			
			xmlConfigurationFile.setSearchProgram( TPPConstants.SEARCH_PROGRAM_NAME_KOJAK );
			xmlConfigurationFile.setFileName( kojakConfFile.getName() );
			xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( kojakConfFile.getAbsolutePath() ) ) );
		}
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain(outfile, proxlInputRoot);
		
	}
	
}
