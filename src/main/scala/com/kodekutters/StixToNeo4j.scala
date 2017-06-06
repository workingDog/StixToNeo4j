package com.kodekutters

import com.kodekutters.neo4j.Neo4jConverter

import scala.language.implicitConversions
import scala.language.postfixOps

/**
  * converts a Stix json file containing STIX objects, or
  * a Stix zip file containing one or more of entry files,
  * into neo4j csv files format
  *
  * @author R. Wathelet May 2017
  *
  */
object StixToNeo4j {

  val usage =
    """Usage:
       java -jar stixconvert-1.0.jar --csv stix_file.json output_dir
        or
       java -jar stixconvert-1.0.jar --zip stix_file.zip output_dir

       the options --csvx and --zipx can also be used for large files""".stripMargin

  /**
    * converts a Stix json file containing STIX objects, or
    * a Stix zip file containing one or more of entry files,
    * into neo4j csv files format
    */
  def main(args: Array[String]) {
    if (args.isEmpty)
      println(usage)
    else {
      val outDir: String = if (args.length == 3) args(2) else ""
      args(0) match {
        case "--csv" => Neo4jConverter(args(1), outDir).convertBundleFile()
        case "--zip" => Neo4jConverter(args(1), outDir).convertBundleZipFile()
        case "--csvx" => Neo4jConverter(args(1), outDir).convertStixFile()
        case "--zipx" => Neo4jConverter(args(1), outDir).convertStixZipFile()
        case x => println("unknown format: " + x + "\n"); println(usage)
      }
    }
  }

}


