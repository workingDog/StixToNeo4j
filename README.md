## Convert STIX-2.1 to Neo4j csv file format 


### No longer maintained, see [StixToNeoDB](https://github.com/workingDog/StixToNeoDB) or [StixLoader](https://github.com/workingDog/stixloader) instead.


This application **StixToNeo4j**, converts [STIX-2.1](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit#) 
objects and relations from json and zip files into a csv [Neo4j](https://neo4j.com/)  representation. 

The [OASIS](https://www.oasis-open.org/) open standard Structured Threat Information Expression [STIX-2.1](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit#) 
is a language for expressing cyber threat and observable information.

[Neo4j](https://neo4j.com/) "is a highly scalable native graph database that leverages data 
relationships as first-class entities, helping enterprises build intelligent applications 
to meet today’s evolving data challenges."
In essence, a graph database and processing engine that is used here for storing Stix objects 
and their relationships.
 
The aim is to be able to store all Stix objects and their 
relationships information into a Neo4j graph database. 
**StixToNeo4j** converts STIX-2.1 domain objects (SDO) and relationships (SRO) to Neo4j csv format ready for 
bulk import into a Neo4j graph database using the [Neo4j import-tool](http://neo4j.com/docs/operations-manual/3.2/tutorial/import-tool/). 
           
Note that the Neo4j import tool is for the initial setup of a database, not for adding new nodes and relations to an existing database.           
          
### References
 
1) [Neo4j](https://neo4j.com/)

2) [Neo4j import-tool](http://neo4j.com/docs/operations-manual/3.2/tutorial/import-tool/) 

3) [ScalaStix](https://github.com/workingDog/scalastix)

4) [STIX-2.1](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit)

### Dependencies and requirements

Depends on the scala [ScalaStix](https://github.com/workingDog/scalastix) library
(included in the "lib" directory) and the **doimport.sh** script to import the csv files into Neo4j db.

Java 8 is required to run **StixToNeo4j**. 

Neo4j-3.2.1 is required to be installed to use the **doimport.sh** script.

### Installation and packaging

The easiest way to compile and package the application from source is to use [SBT](http://www.scala-sbt.org/).
To assemble the application and all its dependencies into a single jar file type:

    sbt assembly

This will produce "stixtoneo4j-1.0.jar" in the "./target/scala-2.12" directory.

For convenience a **"stixtoneo4j-1.0.jar"** file is in the "distrib" directory ready for use.

### Usage

Creating a Neo4j graph database from a file of Stix objects requires two steps.
First convert the Stix objects to Neo4j csv format using **StixToNeo4j**, 
then import those csv files into Neo4j using the **doimport.sh** script. 

Converting the Stix objects to Neo4j csv format, simply type:
 
    java -jar stixtoneo4j-1.0.jar --csv stix_file.json output_dir
    or
    java -jar stixtoneo4j-1.0.jar --zip stix_file.zip output_dir
 
With the **--csv** option the input file "stix_file.json" must contain a single 
bundle of Stix objects you want to convert. The "output_dir" is the destination output directory 
with the csv formatted results. If the output directory is absent, the output is directed to the current 
directory.
 
With the **--zip** option the input file must be a zip file with one or more entry files 
containing a single bundle of Stix objects in each. The output files in this case 
will also be zip files with csv formatted results.
 
Once the Neo4j csv files are generated, use the **doimport.sh** script (see the provided macOS example) to bulk import the files into 
a Neo4j graph database. 
See [Neo4j import-tool](http://neo4j.com/docs/operations-manual/3.2/tutorial/import-tool/) for how to use and 
customize the import tool for your OS.

#### For very large files

To process very large files use the following options:

    java -jar stixtoneo4j-1.0.jar --csvx stix_file.json output_dir
    or
    java -jar stixtoneo4j-1.0.jar --zipx stix_file.zip output_dir

With the **--csvx** option the input file must contain a Stix object on one line 
ending with a new line. Similarly when using the **--zipx** option, each input zip file entries must 
contain a Stix object on one line ending with a new line. When using these options 
the processing is done one line at a time.
 
 
### Status

never finished, use [StixToNeoDB](https://github.com/workingDog/StixToNeoDB) instead.

Using Scala 2.12, Java 8 and SBT-0.13.15.


