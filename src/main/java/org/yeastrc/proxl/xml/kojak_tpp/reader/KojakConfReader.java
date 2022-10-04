package org.yeastrc.proxl.xml.kojak_tpp.reader;

import org.yeastrc.proxl.xml.kojak_tpp.objects.KojakConfCrosslinker;
import org.yeastrc.proxl.xml.kojak_tpp.objects.KojakConfCrosslinkerBuilder;
import org.yeastrc.proxl.xml.kojak_tpp.objects.KojakConfCrosslinkerLinkableEnd;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for reading the values from a Kojak conf file. Once a value is
 * queried, it will read the file once and store the values for future
 * reference.
 * 
 * @author mriffle
 *
 */
public class KojakConfReader {

	public static KojakConfReader getInstance( String filename ) {
		return new KojakConfReader( filename );
	}
	
	private KojakConfReader( String filename ) {
		this.file = new File( filename );
	}	
	private KojakConfReader() { }

	/**
	 * Get the linkable end defined by the linkable end field of a Kojak conf file predefined linker
	 * Example: nK or protein n-terminus and lysine
	 * @param field
	 * @return
	 * @throws Exception
	 */
	private KojakConfCrosslinkerLinkableEnd getCrosslinkerLinkableEndFromConfField(String field) throws Exception {
		if(field.length() < 1) {
			throw new Exception("Got empty field for a linkable end definition in kojak conf file.");
		}

		boolean proteinNTerminusLinkable = false;
		boolean proteinCTerminusLinkable = false;
		Collection<String> linkableResidues = new HashSet<>();

		for (int i = 0; i < field.length(); i++) {

			char residue = field.charAt(i);

			if(residue == 'n') {
				proteinNTerminusLinkable = true;
			} else if(residue == 'c') {
				proteinCTerminusLinkable = true;
			} else {
				linkableResidues.add(String.valueOf(residue));
			}
		}

		return new KojakConfCrosslinkerLinkableEnd(linkableResidues, proteinNTerminusLinkable, proteinCTerminusLinkable);
	}

	/**
	 * Parse a line in the Kojak conf file that defines a pre-defined linker. Will be in the form of:
	 * [ID Num] [Name] [Quenching Reagent] [Target A] [Target B] [XL Mass] [MonoMasses A] [MonoMasses B] [Cleavage_Product_Masses]
	 * @param line
	 * @return
	 * @throws Exception
	 */
	private KojakConfCrosslinker getCrosslinkerFromConfLine(String line) throws Exception {
		String[] fields = line.split("\\s+");
		if(fields.length != 9) {
			throw new Exception("Got invalid crosslinker definition line. Expected 9 fields. Got: " + line);
		}

		String name = fields[1];

		KojakConfCrosslinkerLinkableEnd linkableEnd1 = null;
		KojakConfCrosslinkerLinkableEnd linkableEnd2 = null;
		try {
			linkableEnd1 = getCrosslinkerLinkableEndFromConfField(fields[3]);
			linkableEnd2 = getCrosslinkerLinkableEndFromConfField(fields[4]);
		} catch(Throwable t) {
			System.err.println("Got error processing linkable end from Kojak conf line: " + line);
			throw t;
		}

		BigDecimal crosslinkMass = new BigDecimal(fields[5]);

		Collection<BigDecimal> monolinkMasses = new HashSet<>();

		if(!(fields[6].equals("x"))) {
			for (String monolinkMassString : fields[6].split(",")) {
				monolinkMasses.add(new BigDecimal(monolinkMassString));
			}
		}

		if(!(fields[7].equals("x"))) {
			for (String monolinkMassString : fields[7].split(",")) {
				monolinkMasses.add(new BigDecimal(monolinkMassString));
			}
		}

		Collection<BigDecimal> cleavageProductMasses = new HashSet<>();
		boolean isCleavableLinker = false;
		if(!(fields[8].equals("x"))) {
			isCleavableLinker = true;

			for (String cleavageProductMassString : fields[8].split(",")) {
				cleavageProductMasses.add(new BigDecimal(cleavageProductMassString));
			}
		}

		KojakConfCrosslinkerBuilder linkerBuilder = new KojakConfCrosslinkerBuilder();
		linkerBuilder.setIsCleavableLinker(isCleavableLinker);
		linkerBuilder.setCrosslinkMass(crosslinkMass);
		linkerBuilder.setName(name);
		linkerBuilder.setCleavageProductMasses(cleavageProductMasses);
		linkerBuilder.setLinkableEnd1(linkableEnd1);
		linkerBuilder.setLinkableEnd2(linkableEnd2);
		linkerBuilder.setMonolinkMasses(monolinkMasses);

		return linkerBuilder.createKojakConfCrosslinker();
	}

	/**
	 * Get the Kojak cross-linker defined at the given index in the [XL_PARAMS] section of the conf file
	 * Section looks like:
	 *
	 * [XL_PARAMS]
	 * 1   BS3/DSS  NH2       nK  nK  138.068074  155.094629             x      x
	 * 2   BS3/DSS  NH2+H2O   nK  nK  138.068074  155.094629,156.078644  x  x
	 * 3   BS3/DSS  Tris      nK  nK  138.068074  259.141973             x  x
	 * 4   BS3/DSS  Tris+H2O  nK  nK  138.068074  259.141973,156.078644  x  x
	 * 5   DSSO     NH2       nK  nK  158.003765  175.030314             x  54.010565,85.982635,103.993200
	 * 6   DSSO     NH2+H2O   nK  nK  158.003765  175.030314,176.014330  x  54.010565,85.982635,103.993200
	 * 7   DSSO     Tris      nK  nK  158.003765  279.077658             x  54.010565,85.982635,103.993200
	 * 8   DSSO     Tris+H2O  nK  nK  158.003765  279.077658,176.014330  x  54.010565,85.982635,103.993200
	 * 9   PhoX     NH2       nK  nK  209.972     226.998                x  x
	 * 10  PhoX     NH2+H2O   nK  nK  209.972     226.998,227.982        x  x
	 * [END_XL_PARAMS]
	 *
	 * @param is
	 * @param index
	 * @return
	 * @throws Exception
	 */
	private KojakConfCrosslinker getCrosslinkerFromConfUsingIndex(InputStream is, int index) throws Exception {
		boolean inXLParamsSection = false;

		try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {

				if(inXLParamsSection) {
					String[] fields = line.split("\\s");
					try {
						if(Integer.parseInt(fields[0]) == index) {
							return getCrosslinkerFromConfLine(line);
						}
					} catch(Exception e) { ; }

				} else if(line.equals("[XL_PARAMS]")) {
					if(inXLParamsSection) {
						throw new Exception("Got two [XL_PARAMS] sections in params file.");
					} else {
						inXLParamsSection = true;
						continue;
					}
				}
			}
		}

		throw new Exception("Could not find predefined cross-linker for index: " + index);
	}

	/**
	 * Read through the conf file and get the index of the predefined cross-linker. If none is defined
	 * returns null.
	 *
	 * @param is
	 * @return
	 * @throws Exception
	 */
	Integer getPredefinedCrosslinkIndexFromConf(InputStream is) throws Exception {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.startsWith("predefined_crosslink")) {
					Pattern p = Pattern.compile("^predefined_crosslink\\s+=\\s*(\\d+).*$");
					Matcher m = p.matcher(line);
					if (m.matches()) {
						return Integer.parseInt(m.group(1));
					} else {
						throw new Exception("Got invalid format for predefined_crosslink line. Got: " + line);
					}
				}
			}
		}

		return null;
	}

	String[] getFieldsFromKeyValuePairInConf(String line) {
		// strip off training comment
		String fixed_line = line.replaceAll("\\s*#.*$", "");
		String[] kv = fixed_line.split("\\s*=\\s*");
		String[] fields = kv[1].split("\\s+");

		return fields;
	}

	/**
	 * Find and parse the lines that define a custom linker in the Kojak conf file. Should look like:
	 *
	 * cross_link = nK nK 158.003765 DSSO     #Typical DSSO crosslinker settings
	 * mono_link = nK 176.014330              #DSSO_H20_monolink
	 * mono_link = nK 175.030314              #DSSO_NH2_monolink
	 * xl_cleavage_product_mass = 54.010565   #DSSO cleavage product mass #1
	 * xl_cleavage_product_mass = 85.982635   #DSSO cleavage product mass #2
	 * xl_cleavage_product_mass = 103.993200  #DSSO cleavage product mass #3
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private KojakConfCrosslinker getCustomDefinedCrosslinkerFromConf(InputStream is) throws Exception {
		String name = null;
		KojakConfCrosslinkerLinkableEnd linkableEnd1 = null, linkableEnd2 = null;
		BigDecimal crosslinkMass = null;
		Collection<BigDecimal> monolinkMasses = new HashSet<>();
		Collection<BigDecimal> cleavageProductMasses = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {

				if(line.startsWith("cross_link")) {

					String[] fields = getFieldsFromKeyValuePairInConf(line);

					if(fields.length != 4) {
						throw new Exception("Invalid format defining cross_link. Got: " + line);
					}

					name = fields[3];
					crosslinkMass = new BigDecimal(fields[2]);

					linkableEnd1 = getCrosslinkerLinkableEndFromConfField(fields[0]);
					linkableEnd2 = getCrosslinkerLinkableEndFromConfField(fields[1]);

				} else if(line.startsWith("mono_link")) {

					String[] fields = getFieldsFromKeyValuePairInConf(line);

					if(fields.length != 2) {
						throw new Exception("Invalid format defining mono_link. Got: " + line);
					}

					monolinkMasses.add(new BigDecimal(fields[1]));

				} else if(line.startsWith("xl_cleavage_product_mass")) {

					String[] fields = getFieldsFromKeyValuePairInConf(line);

					if(fields.length != 1) {
						throw new Exception("Invalid format defining xl_cleavage_product_mass. Got: " + line);
					}

					cleavageProductMasses.add(new BigDecimal(fields[1]));
				}
			}
		}

		if(name == null || name.length() < 1) {
			throw new Exception("Could not get custom-defined cross-linker from conf file. No name was found.");
		}

		if(linkableEnd1 == null || linkableEnd2 == null) {
			throw new Exception("Could not get custom-defined cross-linker from conf file. A linkable end was not found.");
		}

		if(crosslinkMass == null) {
			throw new Exception("Could not get custom-defined cross-linker from conf file. Cross-linker mass was not found.");
		}

		KojakConfCrosslinkerBuilder linkerBuilder = new KojakConfCrosslinkerBuilder();
		linkerBuilder.setIsCleavableLinker(cleavageProductMasses.size() > 0);
		linkerBuilder.setCrosslinkMass(crosslinkMass);
		linkerBuilder.setName(name);
		linkerBuilder.setCleavageProductMasses(cleavageProductMasses);
		linkerBuilder.setLinkableEnd1(linkableEnd1);
		linkerBuilder.setLinkableEnd2(linkableEnd2);
		linkerBuilder.setMonolinkMasses(monolinkMasses);

		return linkerBuilder.createKojakConfCrosslinker();
	}

	/**
	 * Get the Kojak cross-linker defined in the Kojak conf file. First checks if a predefined
	 * cross-linker is being used. If so, always will use it. Otherwise looks for a custom-defined
	 * cross-linker.
	 *
	 * @param is
	 * @return
	 */
	private KojakConfCrosslinker getCrosslinkerFromConf(File confFile) throws Exception {

		Integer predefinedCrosslinkIndex = getPredefinedCrosslinkIndexFromConf(new FileInputStream(confFile));
		if(predefinedCrosslinkIndex != null) {
			return getCrosslinkerFromConfUsingIndex(new FileInputStream(confFile), predefinedCrosslinkIndex);
		}

		return getCustomDefinedCrosslinkerFromConf(new FileInputStream(confFile));
	}

	/**
	 * Parse fixed (static) modifications from the Kojak conf file. Has form:
	 *
	 * fixed_modification = C 57.02146
	 *
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private  Map<String, BigDecimal> getStaticModificationsFromConf(InputStream is) throws Exception {

		Map<String, BigDecimal> mods = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if(line.startsWith("fixed_modification ")) {
					String[] fields = getFieldsFromKeyValuePairInConf(line);

					if(fields.length != 2) {
						throw new Exception("Got invalid syntax for fixed_modification. Got: " + line);
					}
					mods.put( fields[ 0 ], new BigDecimal( fields[ 1 ] ) );
				}
			}
		}

		return mods;
	}

	private String get15NFilterFromConf(InputStream is) throws Exception {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if(line.startsWith("15N_filter")) {
					String[] fields = getFieldsFromKeyValuePairInConf(line);

					if(fields.length != 1) {
						throw new Exception("Got invalid syntax for 15N_filter. Got: " + line);
					}
					return fields[0];
				}
			}
		}

		return null;
	}

	private void parseFile() throws Exception {

		this.linker = getCrosslinkerFromConf(this.file);
		this.staticModifications = getStaticModificationsFromConf(new FileInputStream(this.file));
		this.filter15N = get15NFilterFromConf(new FileInputStream(this.file));
	}

	public KojakConfCrosslinker getLinker() throws Exception {
		if( this.linker == null )
			this.parseFile();

		return linker;
	}

	public Map<String, BigDecimal> getStaticModifications() throws Exception {
		if( this.linker == null )
			this.parseFile();
		
		return staticModifications;
	}

	public String getFilter15N() throws Exception {
		if( this.linker == null )
			this.parseFile();

		return filter15N;
	}

	public File getFile() {
		return file;
	}



	private File file;
	private KojakConfCrosslinker linker;
	private Map<String, BigDecimal> staticModifications;
	private String filter15N;
	
}
