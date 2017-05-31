## Convert STIX-2.1 to Neo4j csv file format 

This application **StixToNeo4j**, converts STIX-2.1 objects and relations from json and zip files into a csv Neo4j representation. 

The Structured Threat Information Expression [STIX-2.1](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit#) 
is a language for expressing cyber threat and observable information.

[Neo4j](https://neo4j.com/) "is a highly scalable native graph database that leverages data 
relationships as first-class entities, helping enterprises build intelligent applications 
to meet today’s evolving data challenges."
In essence, a graph database and processing engine that is used here for storing Stix objects 
and their relationships.
 
This application uses the [ScalaStix](https://github.com/workingDog/scalastix) library
to convert Stix domain objects (SDO) and relationships (SRO) to Neo4j csv format ready for 
bulk import into a Neo4j graph database. 
              
### References
 
1) [Neo4j](https://neo4j.com/)

2) [Neo4j import-tool](http://neo4j.com/docs/operations-manual/3.2/tutorial/import-tool/) 

3) [ScalaStix](https://github.com/workingDog/scalastix)

4) [STIX-2.1](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit)

### Dependencies

Depends on the scala [ScalaStix](https://github.com/workingDog/scalastix) library
(included in the "lib" directory) and the **doimport.sh** script to import the csv files into Neo4j db.

Java 8 is also required.

### Installation and packaging

The easiest way to compile and package the application from source is to use [SBT](http://www.scala-sbt.org/).
To assemble the application and all its dependencies into a single jar file type:

    sbt assembly

This will produce "stixtoneo4j-1.0.jar" in the "./target/scala-2.12" directory.

For convenience a **"stixtoneo4j-1.0.jar"** file is in the "distrib" directory.

### Usage

Once you have the jar file, simply type at the prompt:
 
    java -jar stixtoneo4j-1.0.jar --csv stix_file.json output_dir
    or
    java -jar stixtoneo4j-1.0.jar --zip stix_file.zip output_dir
 
where "--csv" determines the conversion format, "stix_file.json" is the Stix file containing a 
bundle of Stix objects you want to convert, and "output_dir" is the destination output directory 
with the new format results. If the output directory is absent, the output is directed to the current 
directory.
 
If the input file is a zip file with one or more files containing bundles of Stix objects,
the output file will also be a zip file with results.
 
Note: on macOS, when using "Compress" from the "Finder" menu, the resulting zip file may contain 
  extra "__MACOSX/" directories that should be removed by typing in a terminal:
  
      zip -d the_file_name.zip __MACOSX/\*
 
Once the Neo4j csv files are generated, use the **doimport.sh** script to bulk import the files into 
a Neo4j graph database. 
 
 
### Status

not ready do not use

Using Scala 2.12, Java 8 and SBT-0.13.15.


