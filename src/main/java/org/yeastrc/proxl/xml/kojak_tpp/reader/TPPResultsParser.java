package org.yeastrc.proxl.xml.kojak_tpp.reader;

import net.systemsbiology.regis_web.pepxml.*;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;
import org.yeastrc.proteomics.fasta.FASTAEntry;
import org.yeastrc.proteomics.fasta.FASTAFileParser;
import org.yeastrc.proteomics.fasta.FASTAFileParserFactory;
import org.yeastrc.proteomics.fasta.FASTAHeader;
import org.yeastrc.proxl.xml.kojak_tpp.constants.TPPConstants;
import org.yeastrc.proxl.xml.kojak_tpp.constants.KojakConstants;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPPeptide;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPReportedPeptide;
import org.yeastrc.proxl.xml.kojak_tpp.objects.TPPResult;
import org.yeastrc.proxl.xml.kojak_tpp.utils.PepXMLUtils;
import org.yeastrc.proxl.xml.kojak_tpp.utils.ScanParsingUtils;

import java.math.BigDecimal;
import java.util.*;

public class TPPResultsParser {

	private static final TPPResultsParser _INSTANCE = new TPPResultsParser();
	public static TPPResultsParser getInstance() { return _INSTANCE; }
	private TPPResultsParser() { }
	
	/**
	 * Get the results of the analysis back in the form used by proxl:
	 * reported peptides are the keys, and all of the PSMs (and their scores)
	 * that reported that peptide are the values.
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	public Map<TPPReportedPeptide, Collection<TPPResult>> getResultsFromAnalysis(TPPAnalysis analysis ) throws Exception {
		
		Map<TPPReportedPeptide, Collection<TPPResult>> results = new HashMap<TPPReportedPeptide, Collection<TPPResult>>();
		
		for( MsmsRunSummary runSummary : analysis.getAnalysis().getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
							if( analysisResult.getAnalysis().equals( "peptideprophet" ) ) {
								
								// only one peptideprophet result will appear for a search hit, and we are only
								// interested in search hits with a peptideprophet result.
								
								// skip this if it's a decoy
								if( PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) )
									continue;
								
								// get our result
								TPPResult result = getResult(runSummary, spectrumQuery, searchHit, analysis);
								
								// get our reported peptide
								TPPReportedPeptide reportedPeptide = getReportedPeptide(searchHit, analysis);
								
								if( !results.containsKey( reportedPeptide ) )
									results.put( reportedPeptide, new ArrayList<TPPResult>() );
								
								results.get( reportedPeptide ).add( result );

								/*
								 * Kojak reports leucine/isoleucine variations as individual peptide matches in its results
								 * file as tied as rank 1 hits to a spectrum. This is preferred by proxl, however, peptideprophet
								 * and iprophet only score a single rank 1 hit for a spectrum. If we only keep the peptide that
								 * iprophet scored, we may lose valuable information if the leucine->isoleucine variant of that
								 * peptide matched proteins of interest in the FASTA file.
								 * 
								 * To address this, iterate over the other search hits for this search result, and keep all other
								 * rank 1 hits that are merely leucine/isoleucine substitutions of the scored rank 1 hit.
								 */
								Collection<TPPReportedPeptide> otherReportedPeptides = getAllLeucineIsoleucineSubstitutions( reportedPeptide, searchResult, analysis );
								
								for( TPPReportedPeptide otherReportedPeptide : otherReportedPeptides ) {
									if( !results.containsKey( otherReportedPeptide ) )
										results.put( otherReportedPeptide, new ArrayList<TPPResult>() );
									
									results.get( otherReportedPeptide ).add( result );
								}
								
							}
						}
					}
				}
			}
		}
		
		/*
		 * Because it is impossible to know if a reported peptide only maps to decoys or not in peptideprophet results
		 * (since it also lists all proteins that match leucine/isoleucine substitutions as protein hits for a peptide)
		 * we need to confirm whether or not the reported peptides whose leucine/isoleucine substitutions matched
		 * proteins in the FASTA file exclusively match to decoys or not. If they do, remove them.
		 */
		
		Collection<TPPReportedPeptide> reportedPeptidesToConfirm = new HashSet<>();
		reportedPeptidesToConfirm.addAll( results.keySet() );
		
		if( reportedPeptidesToConfirm.size() > 0 ) {
			
			// collection of all protein names we need to confirm
			Collection<String> proteinNames = new HashSet<>();
			
			// cache the relevant protein sequences
			Map<String, String> proteinSequences = new HashMap<>();	
			
			for( TPPReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {
				proteinNames.addAll( reportedPeptide.getPeptide1().getTargetProteins() );
				if( reportedPeptide.getPeptide2() != null )
					proteinNames.addAll( reportedPeptide.getPeptide2().getTargetProteins() );
			}
			
			// build the cache of protein sequences
			try ( FASTAFileParser parser = FASTAFileParserFactory.getInstance().getFASTAFileParser( analysis.getFastaFile() ) ) {

				for (FASTAEntry entry = parser.getNextEntry(); entry != null; entry = parser.getNextEntry() ) {

					for( FASTAHeader header : entry.getHeaders() ) {
		        		
		        		for( String testString : proteinNames ) {
							if (header.getName().startsWith(testString)) {
								proteinSequences.put(header.getName(), entry.getSequence());
							}
						}
		        	}
		        }
			}
			
			// now have cache of relevant protein names and sequences. iterate over the reportedPeptidesToConfirm and
			// remove associated proteins from peptides where that peptide is not actually found in that protein
			for( TPPReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {
				
				for (Iterator<String> i = reportedPeptide.getPeptide1().getTargetProteins().iterator(); i.hasNext();) {
					String protein = i.next();
					boolean foundProtein = false;
					
					for( String cachedProteinName : proteinSequences.keySet() ) {
						if( cachedProteinName.startsWith( protein ) ) {
							if( proteinSequences.get( cachedProteinName ).toLowerCase().contains( reportedPeptide.getPeptide1().getSequence().toLowerCase() ) )
								foundProtein = true;
						}
					}
					
					if( !foundProtein )
						i.remove();
					
				}
				
				
				if( reportedPeptide.getType() == TPPConstants.LINK_TYPE_CROSSLINK ) {
					
					for (Iterator<String> i = reportedPeptide.getPeptide2().getTargetProteins().iterator(); i.hasNext();) {
						String protein = i.next();
						boolean foundProtein = false;
						
						for( String cachedProteinName : proteinSequences.keySet() ) {
							if( cachedProteinName.startsWith( protein ) ) {
								if( proteinSequences.get( cachedProteinName ).toLowerCase().contains( reportedPeptide.getPeptide2().getSequence().toLowerCase() ) )
									foundProtein = true;
							}
						}
						
						if( !foundProtein )
							i.remove();
						
					}
					
				}
				
			}
			
			// now we can iterate over the reportedPeptidesToConfirm and remove any from our results where there are 0
			// targetProteins left for a peptide
			for( TPPReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {
				
				if( reportedPeptide.getPeptide1().getTargetProteins().size() < 1 ) {
					System.out.println( "INFO: Removing " + reportedPeptide + " from results, does not match a target protein." );
					results.remove( reportedPeptide );
				}
				
				else if( reportedPeptide.getType() == TPPConstants.LINK_TYPE_CROSSLINK && reportedPeptide.getPeptide2().getTargetProteins().size() < 1) {
					System.out.println( "INFO: Removing " + reportedPeptide + " from results, does not match a target protein." );
					results.remove( reportedPeptide );
				}
				
			}
			
			
			
		}
		
		return results;
	}

	/**
	 * For a given reported peptide, find all other reported peptides that are leucine<->isoleucine
	 * substituitions of that reported peptide, where those substutions match a  protein
	 * in the FASTA file. Since Kojak reports all such peptides as separate PSMs, this is done by
	 * iterating over the other rank 1 hits for a search result and getting those hits that are
	 * merely leucine<->isoleucine substitutions of the reported peptide. These are guaranteed to
	 * hit a protein in the FASTA file, otherwise Kojak would not have reported them.
	 * 
	 * @param reportedPeptide
	 * @param searchResult
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<TPPReportedPeptide>  getAllLeucineIsoleucineSubstitutions(TPPReportedPeptide reportedPeptide, SearchResult searchResult, TPPAnalysis analysis ) throws Exception {
		
		//System.out.println( "Calling getAllLeucineIsoleucineSubstitutions()" );
		
		Collection<TPPReportedPeptide> reportedPeptides = new HashSet<TPPReportedPeptide>();
				
		for( SearchHit otherSearchHit : searchResult.getSearchHit() ) {
			TPPReportedPeptide otherReportedPeptide = getReportedPeptide( otherSearchHit, analysis );

			// if they're not the same type, there's no match
			if( reportedPeptide.getType() != otherReportedPeptide.getType() )
				continue;
			
			// don't return the same reported peptide that was passed in
			if( reportedPeptide.equals( otherReportedPeptide ) ) continue;

			// perform test by substitution all Is and Ls with =s and comparing for string equality
			String testSequence = reportedPeptide.toString();
			testSequence = testSequence.replaceAll( "I", "=" );
			testSequence = testSequence.replaceAll( "L", "=" );			
			
			String otherSequence = otherReportedPeptide.toString();
			otherSequence = otherSequence.replaceAll( "I", "=" );
			otherSequence = otherSequence.replaceAll( "L", "=" );
			
			
			if( testSequence.equals( otherSequence ) ) {
				
				//System.out.println( "Adding " + otherReportedPeptide );
				
				reportedPeptides.add( otherReportedPeptide );
			} else {
				
				if( otherReportedPeptide.getType() == TPPConstants.LINK_TYPE_CROSSLINK ) {
					
					// if we're testing a crosslink, be sure to test the other possible arrangement of peptides 1 and 2
					
					// switch peptides 1 and 2
					TPPPeptide tmpPeptide = otherReportedPeptide.getPeptide1();
					otherReportedPeptide.setPeptide1( otherReportedPeptide.getPeptide2() );
					otherReportedPeptide.setPeptide2( tmpPeptide );
					
					otherSequence = otherReportedPeptide.toString();
					otherSequence = otherSequence.replaceAll( "I", "=" );
					otherSequence = otherSequence.replaceAll( "L", "=" );
					
					if( testSequence.equals( otherSequence ) ) {
						
						// switch back
						otherReportedPeptide.setPeptide2( otherReportedPeptide.getPeptide1() );
						otherReportedPeptide.setPeptide1( tmpPeptide );
						
						reportedPeptides.add( otherReportedPeptide );
					}
					
				}
				
			}
			
		}
		
		
		return reportedPeptides;
	}
	
	/**
	 * For a given search hit, return a collection of strings that are target protein names
	 * reported by iProphet. Decoy names are filtered out.
	 * 
	 * @param searchHit
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<String> getTargetProteinsForSearchHit( SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		Collection<String> targetProteins = new HashSet<>();
		
		String protein = searchHit.getProtein();
		if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), protein ) )
			targetProteins.add( protein );
		
		if( searchHit.getAlternativeProtein() != null ) {
			for( AltProteinDataType altProtein  : searchHit.getAlternativeProtein() ) {
				if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), altProtein.getProtein() ) )
					targetProteins.add( altProtein.getProtein() );
			}
		}
		
		return targetProteins;
	}
	
	/**
	 * For a given search hit, return a collection of strings that are target protein names
	 * reported by iProphet. Decoy names are filtered out.
	 * 
	 * @param linkedPeptide
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<String> getTargetProteinsForLinkedPeptide( LinkedPeptide linkedPeptide, TPPAnalysis analysis ) throws Exception {
		Collection<String> targetProteins = new HashSet<>();
		
		String protein = linkedPeptide.getProtein();
		if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), protein ) )
			targetProteins.add( protein );
		
		if( linkedPeptide.getAlternativeProtein() != null ) {
			for( AltProteinDataType altProtein  : linkedPeptide.getAlternativeProtein() ) {
				if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), altProtein.getProtein() ) )
					targetProteins.add( altProtein.getProtein() );
			}
		}
		
		return targetProteins;
	}
	
	
	/**
	 * Get the IProphetReportedPeptide for the given SearchHit
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private TPPReportedPeptide getReportedPeptide(SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		
		int type = PepXMLUtils.getHitType( searchHit );
		
		if( type == TPPConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit, analysis );
		
		if( type == TPPConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit, analysis );
		
		return getUnlinkedReportedPeptide( searchHit, analysis );
		
	}

	/**
	 * Get the IProphetReportedPeptide for a crosslink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private TPPReportedPeptide getCrosslinkReportedPeptide(SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		
		//System.out.println( searchHit.getPeptide() );
		//System.out.println( "\t" + searchHit.getXlinkType() );
		
		TPPReportedPeptide reportedPeptide = new TPPReportedPeptide();
		reportedPeptide.setType( TPPConstants.LINK_TYPE_CROSSLINK );
				
		for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {
			
			int peptideNumber = 0;
			if( reportedPeptide.getPeptide1() == null ) {
				peptideNumber = 1;
			} else if( reportedPeptide.getPeptide2() == null ) {
				peptideNumber = 2;
			} else {
				throw new Exception( "Got more than two linked peptides." );
			}
			
			
			//System.out.println( "\t\t" + linkedPeptide.getPeptide() );
			//System.out.println( "\t\tpeptide num: " + peptideNumber );
			
			TPPPeptide peptide = getPeptideFromLinkedPeptide( linkedPeptide, analysis );
			int position = 0;
			
			for( NameValueType nvt : linkedPeptide.getXlinkScore() ) {
								
				if( nvt.getName().equals( "link" ) ) {
					
					//System.out.println( "\t\t" + nvt.getValueAttribute() );
					
					if( position == 0 )
						position = Integer.valueOf( nvt.getValueAttribute() );
					else
						throw new Exception( "Got more than one linked position in peptide." );
				}
			}
			
			if( position == 0 )
				throw new Exception( "Could not find linked position in peptide." );
			
			
			if( peptideNumber == 1 ) {
				reportedPeptide.setPeptide1( peptide );
				reportedPeptide.setPosition1( position );
			} else {
				reportedPeptide.setPeptide2( peptide );
				reportedPeptide.setPosition2( position );
			}
			
			
		}
		
		
		// ensure peptides and positions are consistently ordered so that any two reported peptides containing the same
		// two peptides and linked positions are recognized as the same
		
		if( reportedPeptide.getPeptide1().toString().compareTo( reportedPeptide.getPeptide2().toString() ) > 0 ) {

			// swap them
			TPPPeptide tpep = reportedPeptide.getPeptide1();
			int tpos = reportedPeptide.getPosition1();
			
			reportedPeptide.setPeptide1( reportedPeptide.getPeptide2() );
			reportedPeptide.setPosition1( reportedPeptide.getPosition2() );
			
			reportedPeptide.setPeptide2( tpep );
			reportedPeptide.setPosition2( tpos );
		} else if( reportedPeptide.getPeptide1().toString().compareTo( reportedPeptide.getPeptide2().toString() ) == 0 ) {
			
			// peptides are the same, should we swap positions?
			if( reportedPeptide.getPosition1() > reportedPeptide.getPosition2() ) {
				int tpos = reportedPeptide.getPosition1();
				
				reportedPeptide.setPosition1( reportedPeptide.getPosition2() );
				reportedPeptide.setPosition2( tpos );
			}
			
		}
		
		return reportedPeptide;
	}
	
	/**
	 * Get the IProphetReportedPeptide for a looplink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private TPPReportedPeptide getLooplinkReportedPeptide(SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		
		//System.out.println( searchHit.getPeptide() );
		//System.out.println( "\t" + searchHit.getXlinkType() );

		
		TPPReportedPeptide reportedPeptide = new TPPReportedPeptide();
		
		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( TPPConstants.LINK_TYPE_LOOPLINK );
		
		// add in the linked positions
		Xlink xl = searchHit.getXlink();
		
		for( NameValueType nvt : xl.getXlinkScore() ) {
			if( nvt.getName().equals( "link" ) ) {
				
				//System.out.println( "\t\t" + nvt.getValueAttribute() );
				
				if( reportedPeptide.getPosition1() == 0 )
					reportedPeptide.setPosition1( Integer.valueOf( nvt.getValueAttribute() ) );
				else if( reportedPeptide.getPosition2() == 0 )
					reportedPeptide.setPosition2( Integer.valueOf( nvt.getValueAttribute() ) );
				else
					throw new Exception( "Got more than 2 linked positions for looplink." );
			}
		}
		
		if( reportedPeptide.getPosition1() == 0 || reportedPeptide.getPosition2() == 0 )
			throw new Exception( "Did not get two positions for looplink." );
		
		if( reportedPeptide.getPosition1() > reportedPeptide.getPosition2() ) {
			int tpos = reportedPeptide.getPosition1();
			
			reportedPeptide.setPosition1( reportedPeptide.getPosition2() );
			reportedPeptide.setPosition2( tpos );
		}
		
		
		return reportedPeptide;
	}

	/**
	 * Get the IProphetReportedPeptide for an unlinked result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private TPPReportedPeptide getUnlinkedReportedPeptide(SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		
		TPPReportedPeptide reportedPeptide = new TPPReportedPeptide();
		
		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( TPPConstants.LINK_TYPE_UNLINKED );
		
		return reportedPeptide;
	}
	
	/**
	 * Get the IProphetPeptide from the searchHit. Includes the peptide sequence and any mods.
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private TPPPeptide getPeptideFromSearchHit(SearchHit searchHit, TPPAnalysis analysis ) throws Exception {
		
		TPPPeptide peptide = new TPPPeptide();
		
		peptide.setSequence( searchHit.getPeptide() );
		
		peptide.setTargetProteins( getTargetProteinsForSearchHit( searchHit, analysis ) );
		
		ModInfoDataType modInfo = searchHit.getModificationInfo();
		
		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();
			
			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {

				if(mam.getVariable() != null) {

					int position = mam.getPosition().intValue();
					BigDecimal variableModMass = BigDecimal.valueOf(mam.getVariable());

					if (!mods.containsKey(position))
						mods.put(position, new HashSet<BigDecimal>());

					mods.get(position).add(variableModMass);
				}
			}
			
			peptide.setModifications( mods );
		}

		if(analysis.getKojakConfReader().getFilter15N() != null) {
			if(searchHit.getProtein() != null && searchHit.getProtein().startsWith(analysis.getKojakConfReader().getFilter15N())) {
				peptide.setIsotopeLabel(analysis.getKojakConfReader().getFilter15N());
			}
		}
				
		return peptide;
	}

	/**
	 * Get the IProphetPeptide from the searchHit. Includes the peptide sequence and any mods.
	 *
	 * @param linkedPeptide
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private TPPPeptide getPeptideFromLinkedPeptide(LinkedPeptide linkedPeptide, TPPAnalysis analysis ) throws Exception {
		
		TPPPeptide peptide = new TPPPeptide();
		
		peptide.setSequence( linkedPeptide.getPeptide() );
		
		peptide.setTargetProteins( getTargetProteinsForLinkedPeptide( linkedPeptide, analysis ) );
		
		
		ModInfoDataType modInfo = linkedPeptide.getModificationInfo();
		
		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();
			
			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {

				if(mam.getVariable() != null) {
					int position = mam.getPosition().intValue();
					BigDecimal modMass = BigDecimal.valueOf(mam.getVariable());

					if (!mods.containsKey(position))
						mods.put(position, new HashSet<>());

					mods.get(position).add(modMass);
				}
			}
			
			peptide.setModifications( mods );
		}

		if(analysis.getKojakConfReader().getFilter15N() != null) {
			if(linkedPeptide.getProtein() != null && linkedPeptide.getProtein().startsWith(analysis.getKojakConfReader().getFilter15N())) {
				peptide.setIsotopeLabel(analysis.getKojakConfReader().getFilter15N());
			}
		}
				
		return peptide;
	}
	
	
	/**
	 * Get the PSM result for the given spectrum query and search hit.
	 * 
	 * @param spectrumQuery
	 * @param searchHit
	 * @return
	 * @throws Exception If any of the expected scores are not found
	 */
	private TPPResult getResult(MsmsRunSummary runSummary, SpectrumQuery spectrumQuery, SearchHit searchHit, TPPAnalysis analysis) throws Exception {
		
		TPPResult result = new TPPResult();
		
		result.setScanFile( ScanParsingUtils.getFilenameFromReportedScan( spectrumQuery.getSpectrum() ) + runSummary.getRawData() );
		
		
		
		result.setScanNumber( (int)spectrumQuery.getStartScan() );
		result.setCharge( spectrumQuery.getAssumedCharge().intValue() );
		
		// if this is a crosslink or looplink, get the mass of the linker
		int type = PepXMLUtils.getHitType( searchHit );
		if( type == TPPConstants.LINK_TYPE_CROSSLINK || type == TPPConstants.LINK_TYPE_LOOPLINK ) {
			Xlink xl = searchHit.getXlink();
			result.setLinkerMass( xl.getMass() );
		}
		
		// get the kojak scores
		for( NameValueType score : searchHit.getSearchScore() ) {
			if( score.getName().equals( KojakConstants.NAME_KOJAK_SCORE ) ) {
				result.setKojakScore( new BigDecimal( score.getValueAttribute() ) );
			}
			
			else if( score.getName().equals( KojakConstants.NAME_DELTA_SCORE ) ) {
				result.setDeltaScore( new BigDecimal( score.getValueAttribute() ) );
			}
			
			else if( score.getName().equals( KojakConstants.NAME_PPM_ERROR ) ) {
				result.setPpmError( new BigDecimal( score.getValueAttribute() ) );
			}			
		}

		// get the scores for peptideprophet and iprophet
		for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
			
			if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
				InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny().get( 0 );
				result.setInterProphetScore( ipresult.getProbability() );
			}
			
			else if( analysisResult.getAnalysis().equals( "peptideprophet" ) ) {
				PeptideprophetResult ppresult = (PeptideprophetResult) analysisResult.getAny().get( 0 );
				result.setPeptideProphetScore( ppresult.getProbability() );
			}
		}
		
		
		if( result.getDeltaScore() == null )
			throw new Exception( "Missing delta score for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getPpmError() == null )
			throw new Exception( "Missing PPM error for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getKojakScore() == null )
			throw new Exception( "Missing kojak score for result: " + spectrumQuery.getSpectrum() );

		if(analysis.getHasIProphetData() && result.getInterProphetScore() == null )
			throw new Exception( "Missing iprophet score for result: " + spectrumQuery.getSpectrum() );
		
		if( result.getPeptideProphetScore() == null )
			throw new Exception( "Missing peptideprophet score for result: " + spectrumQuery.getSpectrum() );
		
		
		return result;
		
	}
	
}
