package com.kodekutters.neo4j

import com.kodekutters.stix._

/**
  * a writer for Neo4j
  */
trait NeoWriter {
  def init(): Unit

  def writeHeaders(): Unit

  def writeToFile(outFile: String, line: String): Unit

  def writeToRelFile(outFile: String, line: String): Unit

  def closeAll: Unit

}

object NeoWriter {
  val objectRefs = "object_refs"
  val observedDataRefs = "observed_data_refs"
  val whereSightedRefs = "where_sighted_refs"
  val markingObjRefs = "marking_object_refs"
  val createdByRefs = "created_by"

  // list of all relationship types
  val relationTypes = Seq(KillChainPhase.`type`, ExternalReference.`type`, GranularMarking.`type`,
    objectRefs, observedDataRefs, whereSightedRefs, Sighting.`type`, Relationship.`type`,
    markingObjRefs, createdByRefs)

  // list of all node type names
  val nodeTypes = Seq(AttackPattern.`type`, Identity.`type`, Campaign.`type`,
    CourseOfAction.`type`, Indicator.`type`, IntrusionSet.`type`,
    Malware.`type`, ObservedData.`type`, Report.`type`, ThreatActor.`type`,
    Tool.`type`, Vulnerability.`type`, objectRefs, observedDataRefs, whereSightedRefs,
    MarkingDefinition.`type`, markingObjRefs, LanguageContent.`type`, KillChainPhase.`type`,
    ExternalReference.`type`, GranularMarking.`type`)
}
