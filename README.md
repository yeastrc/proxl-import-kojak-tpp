iProphet (TPP) to Proxl XML Converter
=============================

Use this program to convert the results of a Kojak + iProphet (Trans Proteomic Pipeline or TPP)
cross-linking analysis to Proxl XML suitable for import into the proxl web application.

How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/proxl-import-iprophet/releases).
2. Run the program ``java -jar iProphet2ProxlXML.jar`` with no arguments to see the possible parameters.

For more information on importing data into Proxl, please see the [Proxl Import Documentation](http://proxl-web-app.readthedocs.io/en/latest/using/upload_data.html).

More Information About Proxl
-----------------------------
For more information about Proxl, visit http://proxl-ms.org/.


Command line documentation
---------------------------

Usage:
  java -jar iProphet2Proxl.jar -x pepXML file path -o output file path
                               -f fasta file path -l linker name
                               -k kojak conf file [-k kojak conf file...]
                               -d decoy string [-d decoy string...]
  
 Options:
  
     -x or --pepxml       : Required. The full path to the pepXML file
     
     -o or --out-file     : Required. Full path (including file name) to which
                            to write the Proxl XML
  
     -k or --kojak-conf   : Required. Full path to kojak configuration files
                            used to generate the Kojak results. More than one
                            may be specified using multiple -k paramters.
                          
     -f or --fasta-file   : Required. Full path to the fasta file used in
                            the search.
                          
     -l or --linker-name  : Required. The name of the linker (e.g., edc or dss)
                            used in the experiment.
                           
     -d or --decoy-string : Optional. The string to use to identify decoy
                            protein matches. For example decoy, random,
                            or reversed. If option is not present, all hits
                            are assumed to be targets. May be specified
                            multiple times with multiple -d parameters.
                            
                            All protein names containing any of the
                            given decoy strings will be considered
                            decoy hits.
 
 Example:
 
  java -jar iProphet2Proxl.jar -x ./results.pep.xml -o ./results.proxl.xml
  -f /data/mass_spec/yeast.fa -l dss -k ./kojak1.conf -k ./kojak2.conf
  -d random -d rand1 -d rand0

  More information: https://github.com/yeastrc/proxl-import-iprophet

  
