package com.kodekutters.neo4j

import java.io.{File, IOException, PrintWriter}

import com.kodekutters.stix._

import scala.collection.mutable

object Neo4jWriter {
  val objectRefs = "object_refs"
  val observedDataRefs = "observed_data_refs"
  val whereSightedRefs = "where_sighted_refs"

  // list of all relationship types
  val relationTypes = Seq(KillChainPhase.`type`, ExternalReference.`type`, GranularMarking.`type`,
    objectRefs, observedDataRefs, whereSightedRefs, Sighting.`type`, Relationship.`type`)

  // list of all node type names
  val nodeTypes = Seq(AttackPattern.`type`, Identity.`type`, Campaign.`type`,
    CourseOfAction.`type`, Indicator.`type`, IntrusionSet.`type`,
    Malware.`type`, ObservedData.`type`, Report.`type`, ThreatActor.`type`,
    Tool.`type`, Vulnerability.`type`, objectRefs, observedDataRefs, whereSightedRefs,
    MarkingDefinition.`type`, LanguageContent.`type`, KillChainPhase.`type`,
    ExternalReference.`type`, GranularMarking.`type`)

}

class Neo4jWriter(outDir: String) {

  // a map containing a writer for for each type in nodeTypes using the type as the file name
  private val printWriters = mutable.Map[String, PrintWriter]()

  // a map containing a writer for for each type in relationTypes using the type as the file name
  private val printRelWriters = mutable.Map[String, PrintWriter]()

  /**
    * create all required csv files
    */
  def init(): Unit = {
    // create a file and a writer for it, for each type in nodeTypes and use the type as the file name
    printWriters ++= (for (p <- Neo4jWriter.nodeTypes)
      yield p -> new PrintWriter(new File(outDir + p + ".csv"))).toMap[String, PrintWriter]

    // create a file and a writer for it, for each relations in relationTypes and use the type_rel as the file name
    printRelWriters ++= (for (p <- Neo4jWriter.relationTypes)
      yield p -> new PrintWriter(new File(outDir + p + "_rel.csv"))).toMap[String, PrintWriter]

  }

  /**
    * write the required headers into all csv files
    */
  def writeHeaders(): Unit = {
    try {
      Neo4jHeaders.nodeHeaders.foreach(f => printWriters(f._1).write(f._2 + "\n"))
      Neo4jHeaders.relHeaders.foreach(f => printRelWriters(f._1).write(f._2 + "\n"))
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToFile(outFile: String, line: String): Unit = {
    try {
      printWriters(outFile).write(line + "\n")
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToRelFile(outFile: String, line: String): Unit = {
    try {
      printRelWriters(outFile).write(line + "\n")
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def closeAll = {
    printWriters.foreach(p => p._2.close())
    printRelWriters.foreach(p => p._2.close())
  }

}
