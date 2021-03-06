package com.kodekutters.neo4j

import java.io.File
import java.util.UUID

import com.kodekutters.stix._
import com.kodekutters.stix.Bundle
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.io.Source
import scala.language.implicitConversions
import scala.language.postfixOps
import com.kodekutters.neo4j.Neo4jZipReader._
import scala.collection.JavaConverters._

/**
  * converts Stix-2.1 objects and relationships into Neo4j csv format
  *
  * @author R. Wathelet May 2017
  *
  *         ref: https://github.com/workingDog/scalastix
  *         ref: https://neo4j.com/docs/operations-manual/current/tools/import/file-header-format/
  */
object Neo4jConverter {
  // must use this constructor, class is private
  def apply(inFile: String, outDir: String) = new Neo4jConverter(inFile, outDir)
}

/**
  * converts Stix-2.1 objects (nodes) and relationships (edges) into Neo4j csv format
  */
class Neo4jConverter private(inFile: String, outD: String) {

  private val outDir = if (outD.endsWith("/")) outD else outD + "/"

  // a writer for writing the results, either plain or zip
  private var neoWriter: NeoWriter = _

  // generate a unique random id
  private def newId = UUID.randomUUID().toString

  // convert a Stix object according to its type
  private def convertObj(obj: StixObj) = {
    obj match {
      case stix if stix.isInstanceOf[SDO] => convertSDO(stix.asInstanceOf[SDO])
      case stix if stix.isInstanceOf[SRO] => convertSRO(stix.asInstanceOf[SRO])
      case stix if stix.isInstanceOf[StixObj] => convertStixObj(stix.asInstanceOf[StixObj])
      case stix => // do nothing for now
    }
  }

  /**
    * read a bundle of Stix objects from the input file,
    * convert it to neo4j csv format and
    * write the results to csv files in the output directory
    */
  def convertBundleFile(): Unit = {
    // the file writer
    neoWriter = new Neo4jFileWriter(outDir)
    // create all the required csv files
    neoWriter.init()
    // write the headers into them
    neoWriter.writeHeaders()
    // read a STIX bundle from the inFile
    val jsondoc = Source.fromFile(inFile).mkString
    // create a bundle object from it, convert and write it out
    decode[Bundle](jsondoc) match {
      case Left(failure) => println("\n-----> ERROR reading bundle in file: " + inFile)
      case Right(bundle) => bundle.objects.foreach(convertObj(_))
    }
    // all done, close all files
    neoWriter.closeAll
  }

  /**
    * read Stix bundles from the input zip file,
    * convert them to neo4j csv format and
    * write the results to zip files in the output directory.
    *
    * The input zip file must contain one or more file entries with extension .json
    * and with a single bundle in each.
    *
    */
  def convertBundleZipFile(): Unit = {
    // the zip file writer
    neoWriter = new Neo4jZipWriter(outDir)
    // create all the required csv zip files
    neoWriter.init()
    // write the headers into them
    neoWriter.writeHeaders()
    // get the input zip file
    val rootZip = new java.util.zip.ZipFile(new File(inFile))
    // for each entry file
    rootZip.entries.asScala.filter(_.getName.toLowerCase.endsWith(".json")).foreach(f => {
      // read a bundle from the file entry
      loadBundle(rootZip.getInputStream(f)) match {
        case Some(bundle) => bundle.objects.foreach(convertObj(_))
        case None => println("-----> ERROR invalid bundle JSON in zip file: \n")
      }
    })
    // all done, close all files
    neoWriter.closeAll
  }

  /**
    * For processing very large text files.
    *
    * read Stix objects one by one from the input file,
    * convert them to neo4j csv format and
    * write the results to csv files in the output directory
    *
    * The input file must contain a Stix object on one line ending with a new line.
    *
    */
  def convertStixFile(): Unit = {
    // the file writer
    neoWriter = new Neo4jFileWriter(outDir)
    // create all the required csv files
    neoWriter.init()
    // write the headers into them
    neoWriter.writeHeaders()
    // read a STIX object from the inFile, one line at a time
    for (line <- Source.fromFile(inFile).getLines) {
      // create a Stix object from it, convert and write it out
      decode[StixObj](line) match {
        case Left(failure) => println("\n-----> ERROR reading StixObj in file: " + inFile + " line: " + line)
        case Right(stixObj) => convertObj(stixObj)
      }
    }
    // all done, close all files
    neoWriter.closeAll
  }

  /**
    * For processing very large zip files.
    *
    * read Stix objects one by one from the input zip file,
    * convert them to neo4j csv format and
    * write the results to zip files in the output directory
    *
    * There can be one or more file entries in the zip file,
    * each file must have the extension .json.
    *
    * Each entry file must have a Stix object on one line ending with a new line.
    *
    */
  def convertStixZipFile(): Unit = {
    // the zip file writer
    neoWriter = new Neo4jZipWriter(outDir)
    // create all the required csv zip files
    neoWriter.init()
    // write the headers into them
    neoWriter.writeHeaders()
    // get the input zip file
    val rootZip = new java.util.zip.ZipFile(new File(inFile))
    // for each entry file
    rootZip.entries.asScala.filter(_.getName.toLowerCase.endsWith(".json")).foreach(f => {
      // get the lines from the entry file
      val inputLines = Source.fromInputStream(rootZip.getInputStream(f)).getLines
      // read a Stix object from the inputLines, one line at a time
      for (line <- inputLines) {
        // create a Stix object from it, convert and write it out
        decode[StixObj](line) match {
          case Left(failure) => println("\n-----> ERROR reading StixObj in file: " + f.getName + " line: " + line)
          case Right(stixObj) => convertObj(stixObj)
        }
      }
    })
    // all done, close all files
    neoWriter.closeAll
  }

  def convertSDO(x: SDO) = {
    // common elements
    val labelsString = toStringArray(x.labels)
    val granular_markings_ids = toIdArray(x.granular_markings)
    val external_references_ids = toIdArray(x.external_references)
    val object_marking_refs_arr = toStringIds(x.object_marking_refs)
    val commonPart = x.id.toString() + "," + x.`type` + "," + x.created.time + "," + x.modified.time + "," +
      x.revoked.getOrElse("") + "," + labelsString + "," + x.confidence.getOrElse("") + "," +
      external_references_ids + "," + clean(x.lang.getOrElse("")) + "," + object_marking_refs_arr + "," +
      granular_markings_ids + "," + x.created_by_ref.getOrElse("")
    val endPart = "SDO" + ";" + asCleanLabel(x.`type`)
    // write the external_references
    writeExternRefs(x.id.toString(), x.external_references, external_references_ids)
    // write the granular_markings
    writeGranulars(x.id.toString(), x.granular_markings, granular_markings_ids)
    // write the created-by relation
    writeCreatedBy(x.id.toString(), x.created_by_ref)

    x.`type` match {

      case AttackPattern.`type` =>
        val y = x.asInstanceOf[AttackPattern]
        val kill_chain_phases_ids = toIdArray(y.kill_chain_phases)
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," + kill_chain_phases_ids + "," + endPart
        neoWriter.writeToFile(AttackPattern.`type`, line)
        writeKillPhases(y.id.toString(), y.kill_chain_phases, kill_chain_phases_ids)

      case Identity.`type` =>
        val y = x.asInstanceOf[Identity]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.identity_class) + "," + toStringArray(y.sectors) + "," +
          clean(y.contact_information.getOrElse("")) + "," + clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(Identity.`type`, line)

      case Campaign.`type` =>
        val y = x.asInstanceOf[Campaign]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.objective.getOrElse("")) + "," + toStringArray(y.aliases) + "," +
          clean(y.first_seen.getOrElse("").toString) + "," + clean(y.last_seen.getOrElse("").toString) + "," +
          clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(Campaign.`type`, line)

      case CourseOfAction.`type` =>
        val y = x.asInstanceOf[CourseOfAction]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(CourseOfAction.`type`, line)

      case IntrusionSet.`type` =>
        val y = x.asInstanceOf[IntrusionSet]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," +
          toStringArray(y.aliases) + "," + y.first_seen.getOrElse("").toString + "," +
          y.last_seen.getOrElse("").toString + "," + toStringArray(y.goals) + "," +
          clean(y.resource_level.getOrElse("")) + "," +
          clean(y.primary_motivation.getOrElse("")) + "," +
          toStringArray(y.secondary_motivations) + "," + endPart
        neoWriter.writeToFile(IntrusionSet.`type`, line)

      case Malware.`type` =>
        val y = x.asInstanceOf[Malware]
        val kill_chain_phases_ids = toIdArray(y.kill_chain_phases)
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," + kill_chain_phases_ids + "," + endPart
        neoWriter.writeToFile(Malware.`type`, line)
        writeKillPhases(y.id.toString(), y.kill_chain_phases, kill_chain_phases_ids)

      case Report.`type` =>
        val y = x.asInstanceOf[Report]
        val object_refs_ids = toIdArray(y.object_refs)
        val line = commonPart + "," + clean(y.name) + "," + y.published + "," + object_refs_ids + "," + clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(Report.`type`, line)
        writeObjRefs(y.id.toString(), y.object_refs, object_refs_ids, NeoWriter.objectRefs)
        writeObjRefRel(y.id.toString(), y.object_marking_refs, NeoWriter.markingObjRefs)

      case ThreatActor.`type` =>
        val y = x.asInstanceOf[ThreatActor]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," +
          toStringArray(y.aliases) + "," + toStringArray(y.roles) + "," + toStringArray(y.goals) + "," +
          clean(y.sophistication.getOrElse("")) + "," + clean(y.resource_level.getOrElse("")) + "," +
          clean(y.primary_motivation.getOrElse("")) + "," + toStringArray(y.secondary_motivations) + "," +
          toStringArray(y.personal_motivations) + "," + endPart
        neoWriter.writeToFile(ThreatActor.`type`, line)

      case Tool.`type` =>
        val y = x.asInstanceOf[Tool]
        val kill_chain_phases_ids = toIdArray(y.kill_chain_phases)
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," +
          kill_chain_phases_ids + "," + clean(y.tool_version.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(Tool.`type`, line)
        writeKillPhases(y.id.toString(), y.kill_chain_phases, kill_chain_phases_ids)

      case Vulnerability.`type` =>
        val y = x.asInstanceOf[Vulnerability]
        val line = commonPart + "," + clean(y.name) + "," + clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(Vulnerability.`type`, line)

      case Indicator.`type` =>
        val y = x.asInstanceOf[Indicator]
        val kill_chain_phases_ids = toIdArray(y.kill_chain_phases)
        val line = commonPart + "," + clean(y.name.getOrElse("")) + "," + clean(y.description.getOrElse("")) + "," +
          clean(y.pattern) + "," + y.valid_from.toString() + "," +
          clean(y.valid_until.getOrElse("").toString) + "," + kill_chain_phases_ids + "," + endPart
        neoWriter.writeToFile(Indicator.`type`, line)
        writeKillPhases(y.id.toString(), y.kill_chain_phases, kill_chain_phases_ids)

      // todo  objects: Map[String, Observable],
      case ObservedData.`type` =>
        val y = x.asInstanceOf[ObservedData]
        val line = commonPart + "," +
          y.first_observed.toString() + "," + y.last_observed.toString() + "," +
          y.number_observed + "," + clean(y.description.getOrElse("")) + "," + endPart
        neoWriter.writeToFile(ObservedData.`type`, line)

      case _ => // do nothing for now
    }
  }

  // the Relationship and Sighting
  def convertSRO(x: SRO) = {
    // common elements
    val labelsString = toStringArray(x.labels)
    val granular_markings_ids = toIdArray(x.granular_markings)
    val external_references_ids = toIdArray(x.external_references)
    val object_marking_refs_arr = toStringIds(x.object_marking_refs)
    val commonPart = x.id.toString() + "," + x.created.time + "," + x.modified.time + "," +
      x.revoked.getOrElse("") + "," + labelsString + "," + x.confidence.getOrElse("") + "," +
      external_references_ids + "," + clean(x.lang.getOrElse("")) + "," + object_marking_refs_arr + "," +
      granular_markings_ids + "," + x.created_by_ref.getOrElse("")
    // write the external_references
    writeExternRefs(x.id.toString(), x.external_references, external_references_ids)
    // write the granular_markings
    writeGranulars(x.id.toString(), x.granular_markings, granular_markings_ids)
    // write the created-by relation
    writeCreatedBy(x.id.toString(), x.created_by_ref)
    // write the marking refs relations
    writeObjRefRel(x.id.toString(), x.object_marking_refs, NeoWriter.markingObjRefs)

    if (x.isInstanceOf[Relationship]) {
      val y = x.asInstanceOf[Relationship]
      // the SRO node
      val lineNode = x.id.toString() + "," + x.`type` + "," + "SRO;RelationshipNode"
      neoWriter.writeToFile(NeoWriter.relationshipNode, lineNode)
      // the relation
      val line = y.source_ref.toString + "," + y.target_ref.toString() + "," + asCleanLabel(y.relationship_type) + "," +
        clean(y.description.getOrElse("")) + "," + commonPart
      neoWriter.writeToRelFile(Relationship.`type`, line)
    }
    else { // must be a Sighting todo ----> target_ref  observed_data_refs heading
      val y = x.asInstanceOf[Sighting]
      // the SRO node
      val lineNode = x.id.toString() + "," + x.`type` + "," + "SRO;SightingNode"
      neoWriter.writeToFile(NeoWriter.sightingNode, lineNode)
      val observed_data_ids = toIdArray(y.observed_data_refs)
      val where_sighted_refs_ids = toIdArray(y.where_sighted_refs)
      val line = y.sighting_of_ref.toString + "," + y.sighting_of_ref.toString + "," + "sighting" + "," +
        y.first_seen.getOrElse("").toString + "," + y.last_seen.getOrElse("").toString + "," +
        y.count.getOrElse("") + "," + y.summary.getOrElse("") + "," +
        observed_data_ids + "," + where_sighted_refs_ids + "," +
        clean(y.description.getOrElse("")) + "," + commonPart
      neoWriter.writeToRelFile(Sighting.`type`, line)
      writeObjRefRel(y.id.toString(), y.observed_data_refs, NeoWriter.observedDataRefs)
      writeObjRefRel(y.sighting_of_ref.toString, y.where_sighted_refs, NeoWriter.whereSightedRefs)
    }
  }

  // convert MarkingDefinition and LanguageContent
  def convertStixObj(stixObj: StixObj) = {

    stixObj match {

      case x: MarkingDefinition =>
        val definition_id = newId
        val granular_markings_ids = toIdArray(x.granular_markings)
        val external_references_ids = toIdArray(x.external_references)
        val object_marking_refs_arr = toStringIds(x.object_marking_refs)
        val line = x.id.toString() + "," + x.`type` + "," + x.created.time + "," +
          clean(x.definition_type) + "," + definition_id + "," +
          external_references_ids + "," + object_marking_refs_arr + "," +
          granular_markings_ids + "," + x.created_by_ref.getOrElse("") + ",SixObj" + ";" + asCleanLabel(x.`type`)
        // write the external_references
        writeExternRefs(x.id.toString(), x.external_references, external_references_ids)
        // write the granular_markings
        writeGranulars(x.id.toString(), x.granular_markings, granular_markings_ids)
        // write the marking object definition
        writeMarkingObjRefs(x.id.toString(), x.definition, definition_id)
        // write the created-by relation
        writeCreatedBy(x.id.toString(), x.created_by_ref)
        // write the marking refs
        writeObjRefRel(x.id.toString(), x.object_marking_refs, NeoWriter.markingObjRefs)
        neoWriter.writeToFile(MarkingDefinition.`type`, line)

      // todo <----- contents: Map[String, Map[String, String]]
      case x: LanguageContent =>
        val granular_markings_ids = toIdArray(x.granular_markings)
        val external_references_ids = toIdArray(x.external_references)
        val line = x.id.toString() + "," + x.`type` + "," + x.created.time + "," + x.modified.time + "," +
          x.object_modified + "," + x.object_ref.toString() + "," + toStringArray(x.labels) + "," + x.revoked.getOrElse("") + "," +
          external_references_ids + "," + toStringIds(x.object_marking_refs) + "," +
          granular_markings_ids + "," + x.created_by_ref.getOrElse("") + ",SixObj" + ";" + asCleanLabel(x.`type`)
        // write the external_references
        writeExternRefs(x.id.toString(), x.external_references, external_references_ids)
        // write the granular_markings
        writeGranulars(x.id.toString(), x.granular_markings, granular_markings_ids)
        // write the created-by relation
        writeCreatedBy(x.id.toString(), x.created_by_ref)
        // write the marking refs
        writeObjRefRel(x.id.toString(), x.object_marking_refs, NeoWriter.markingObjRefs)
        neoWriter.writeToFile(LanguageContent.`type`, line)

    }
  }

  //--------------------------------------------------------------------------------------------

  // write the marking object
  def writeMarkingObjRefs(idString: String, definition: MarkingObject, definition_id: String) = {
    val mark: String = definition match {
      case s: StatementMarking => clean(s.statement) + ",statement"
      case s: TPLMarking => clean(s.tlp.value) + ",tlp"
      case _ => ""
    }
    neoWriter.writeToFile(NeoWriter.markingObjRefs, idString + "," + mark)
    // write the markingObj relationships with the given id
    neoWriter.writeToRelFile(NeoWriter.markingObjRefs, idString + "," + definition_id + ",HAS_MARKING_OBJECT")
  }

  // write the kill_chain_phases
  def writeKillPhases(idString: String, kill_chain_phases: Option[List[KillChainPhase]], kill_chain_phases_ids: String) = {
    val killphases = for (s <- kill_chain_phases.getOrElse(List.empty))
      yield clean(s.kill_chain_name) + "," + clean(s.phase_name) + "," + asCleanLabel(s.`type`)
    if (killphases.nonEmpty) {
      val kp = (kill_chain_phases_ids.split(";") zip killphases).map({ case (a, b) => a + "," + b })
      neoWriter.writeToFile(KillChainPhase.`type`, kp.mkString("\n"))
      // write the kill_chain_phase relationships with the given ids
      val krel = for (k <- kill_chain_phases_ids.split(";")) yield idString + "," + k + ",HAS_KILL_CHAIN_PHASE"
      neoWriter.writeToRelFile(KillChainPhase.`type`, krel.mkString("\n"))
    }
  }

  // write the external_references
  def writeExternRefs(idString: String, external_references: Option[List[ExternalReference]], external_references_ids: String) = {
    val externRefs = for (s <- external_references.getOrElse(List.empty))
      yield clean(s.source_name) + "," + clean(s.description.getOrElse("")) + "," +
        clean(s.url.getOrElse("")) + "," + clean(s.external_id.getOrElse("")) + "," + asCleanLabel(s.`type`)
    if (externRefs.nonEmpty) {
      val kp = (external_references_ids.split(";") zip externRefs).map({ case (a, b) => a + "," + b })
      neoWriter.writeToFile(ExternalReference.`type`, kp.mkString("\n"))
      // write the external_reference relationships with the given ids
      val krel = for (k <- external_references_ids.split(";")) yield idString + "," + k + ",HAS_EXTERNAL_REF"
      neoWriter.writeToRelFile(ExternalReference.`type`, krel.mkString("\n"))
    }
  }

  // write the granular_markings
  def writeGranulars(idString: String, granular_markings: Option[List[GranularMarking]], granular_markings_ids: String) = {
    val granulars = for (s <- granular_markings.getOrElse(List.empty))
      yield toStringArray(Option(s.selectors)) + "," + clean(s.marking_ref.getOrElse("")) + "," +
        clean(s.lang.getOrElse("")) + "," + asCleanLabel(s.`type`)
    if (granulars.nonEmpty) {
      val kp = (granular_markings_ids.split(";") zip granulars).map({ case (a, b) => a + "," + b })
      neoWriter.writeToFile(GranularMarking.`type`, kp.mkString("\n"))
      // write the granular_markings relationships with the given ids
      val krel = for (k <- granular_markings_ids.split(";")) yield idString + "," + k + ",HAS_GRANULAR_MARKING"
      neoWriter.writeToRelFile(GranularMarking.`type`, krel.mkString("\n"))
    }
  }

  // write the object_refs
  def writeObjRefs(idString: String, object_refs: Option[List[Identifier]], object_refs_ids: String, typeName: String) = {
    val objRefs = for (s <- object_refs.getOrElse(List.empty)) yield clean(s.toString()) + "," + typeName
    if (objRefs.nonEmpty) {
      val kp = (object_refs_ids.split(";") zip objRefs).map({ case (a, b) => a + "," + b })
      neoWriter.writeToFile(typeName, kp.mkString("\n"))
      // write the object_refs relationships with the given ids
      val krel = for (k <- object_refs_ids.split(";")) yield idString + "," + k + ",HAS_" + typeName.toUpperCase
      neoWriter.writeToRelFile(typeName, krel.mkString("\n"))
    }
  }

  // write the object_refs
  def writeObjRefRel(idString: String, object_refs: Option[List[Identifier]], typeName: String) = {
    if (object_refs.isDefined) {
      // write the object_refs relationships with the given ids
      for (s <- object_refs.getOrElse(List.empty)) {
        val rel = idString + "," + clean(s.toString()) + ",HAS_" + typeName.toUpperCase
        neoWriter.writeToRelFile(typeName, rel)
      }
    }
  }

  // write the created-by relation between idString and the Identifier
  def writeCreatedBy(idString: String, tgtOpt: Option[Identifier]) = {
    tgtOpt.map(tgt => neoWriter.writeToRelFile(NeoWriter.createdByRefs, idString + "," + tgt.toString() + ",CREATED_BY"))
  }

  // clean the string, i.e. replace all unwanted char
  private def clean(s: String) = s.replace(",", " ").replace(";", " ").replace("\"", "").replace("\\", "").replace("\n", "").replace("\r", "")

  // make an array of id values from the input list
  private def toIdArray(dataList: Option[List[Any]]) = {
    val t = for (s <- dataList.getOrElse(List.empty)) yield newId + ";"
    if (t.nonEmpty) t.mkString.reverse.substring(1).reverse else ""
  }

  // make an array of cleaned string values from the input list
  private def toStringArray(dataList: Option[List[String]]) = {
    val t = for (s <- dataList.getOrElse(List.empty)) yield clean(s) + ";"
    if (t.nonEmpty) t.mkString.reverse.substring(1).reverse else ""
  }

  // make an array of id strings --> no cleaning done here
  private def toStringIds(dataList: Option[List[Identifier]]) = {
    val t = for (s <- dataList.getOrElse(List.empty)) yield s.toString() + ";"
    if (t.nonEmpty) t.mkString.reverse.substring(1).reverse else ""
  }

  // the Neo4j :LABEL and :TYPE cannot deal with "-", so clean and replace with "_"
  private def asCleanLabel(s: String) = clean(s).replace("-", "_")

}
