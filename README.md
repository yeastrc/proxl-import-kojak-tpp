Kojak + TPP to Proxl XML Converter
==================================

Use this program to convert the results of a Kojak + Trans Proteomic Pipeline (TPP)
cross-linking analysis to Proxl XML suitable for import into the proxl web application.

How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/proxl-import-kojak-tpp/releases).
2. Run the program ``java -jar kojakTPPToProxl.jar`` with no arguments to see the possible parameters.

For more information on importing data into Proxl, please see the [Proxl Import Documentation](http://proxl-web-app.readthedocs.io/en/latest/using/upload_data.html).

More Information About Proxl
-----------------------------
For more information about Proxl, visit http://proxl-ms.org/.


Command line documentation
---------------------------

```
java -jar kojakTPPToProxl.jar [-hvV] -f=<fastaFile> -o=<outFile>
                              -x=<pepXMLFile> [-d=<decoyString>]...
                              -k=<kojakConfFiles> [-k=<kojakConfFiles>]...

Description:

Convert the results of a Kojak + TPP analysis to a ProXL XML file suitable for
import into ProXL.

More info at: https://github.com/yeastrc/proxl-import-iprophet

Options:
  -x, --pepxml=<pepXMLFile>  The full path to the pepXML file.
  -k, --kojak-conf=<kojakConfFiles>
                             The full path to a Kojak configuration (params) file.
                               Use multiple times to specify multiple files.
  -f, --fasta-file=<fastaFile>
                             The full path to the FASTA file used for the search.
  -o, --out-file=<outFile>   Full path to use for the ProXL XML output file
                               (including file name).
  -d, --decoy-string=<decoyString>
                             [Optional] Override the value for the decoy prefix
                               found in the Kojak conf file. May be used multiple
                               times to specify multiple decoy strings.
  -v, --verbose              [Optional] If present, complete error messages will be
                               printed. Useful for debugging errors.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```
 
 Example:
 
  `java -jar kojakTPPToProxl.jar -x ./results.pep.xml -o ./results.proxl.xml
  -f /data/mass_spec/yeast.fa -k ./kojak1.conf -k ./kojak2.conf
  -d random -d rand1 -d rand0`

  
