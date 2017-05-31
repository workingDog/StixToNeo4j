package com.kodekutters.neo4j

import com.kodekutters.stix._

import scala.collection.mutable

/**
  * the neo4j csv files headers
  */
object Neo4jHeaders {

  val nodeHeaders = mutable.Map[String, String]()
  val relHeaders = mutable.Map[String, String]()

  val commonRelHeader = "id:string,created,modified,revoked:boolean,labels:string[],confidence:int,external_references:string[],lang,object_marking_refs:string[],granular_markings:string[],created_by_ref"
  val commonNodeHeader = "id:ID(StixObj),type,created,modified,name,revoked:boolean,labels:string[],confidence:int,external_references:string[],lang,object_marking_refs:string[],granular_markings:string[],created_by_ref,"
  val endHeader = ",:LABEL"

  //---------------------------------------------------------------------
  //----------------nodes------------------------------------------------
  //---------------------------------------------------------------------

  val attackPatternHeader = commonNodeHeader + "description,kill_chain_phases:string[]" + endHeader

  val identityHeader = commonNodeHeader + "identity_class,sectors:string[],contact_information,description" + endHeader

  val campaignHeader = commonNodeHeader + "objective,aliases:string[],first_seen,last_seen,description" + endHeader

  val courseOfActionHeader = commonNodeHeader + "description" + endHeader

  val indicatorHeader = commonNodeHeader + "description,pattern,valid_from,valid_until,kill_chain_phases:string[]" + endHeader

  val intrusionSetHeader = commonNodeHeader + "description,aliases:string[],first_seen,last_seen,goals:string[],resource_level,primary_motivation,secondary_motivations:string[]" + endHeader

  val reportHeader = commonNodeHeader + "published,object_refs:string[],description" + endHeader

  val threatActorHeader = commonNodeHeader + "description,aliases:string[],roles:string[],goals:string[],sophistication,resource_level,primary_motivation,secondary_motivations:string[],personal_motivations:string[]" + endHeader

  val toolHeader = commonNodeHeader + "description,kill_chain_phases:string[],tool_version" + endHeader

  val malwareHeader = commonNodeHeader + "description,kill_chain_phases:string[]" + endHeader

  val vulnerabilityHeader = commonNodeHeader + "description" + endHeader

  val killChainPhaseHeader = "kill_chain_phase_id:ID(kill_chain_phase_id),kill_chain_name,phase_name" + endHeader

  val externalReferenceHeader = "external_reference_id:ID(external_reference_id),source_name,description,url,external_id" + endHeader

  val granularMarkingHeader = "granular_marking_id:ID(granular_marking_id),selectors:string[],marking_ref,lang" + endHeader

  // todo
  val observedDataHeader = commonNodeHeader + "first_observed,last_observed,number_observed:int,description" + endHeader

  val whereSightedHeader = "where_sighted_ref_id:ID(where_sighted_ref_id),identifier" + endHeader

  val objRefsHeader = "object_ref_id:ID(object_ref_id),identifier" + endHeader

  // todo
  val langContent = "lang_id:ID(lang_id),lang,:LABEL"
  // todo
  val markingDef = "marking_id:ID(marking_id),mark,:LABEL"
  // todo
  val obDataRefDef = "observe_id:ID(observe_id),observe,:LABEL"

  nodeHeaders ++= Map(AttackPattern.`type` -> attackPatternHeader,
    KillChainPhase.`type` -> killChainPhaseHeader,
    ExternalReference.`type` -> externalReferenceHeader,
    GranularMarking.`type` -> granularMarkingHeader,
    Neo4jWriter.whereSightedRefs -> whereSightedHeader,
    Neo4jWriter.objectRefs -> objRefsHeader,
    Identity.`type` -> identityHeader,
    Campaign.`type` -> campaignHeader,
    CourseOfAction.`type` -> courseOfActionHeader,
    Indicator.`type` -> indicatorHeader,
    IntrusionSet.`type` -> intrusionSetHeader,
    ThreatActor.`type` -> threatActorHeader,
    Tool.`type` -> toolHeader,
    Malware.`type` -> malwareHeader,
    Vulnerability.`type` -> vulnerabilityHeader,
    ObservedData.`type` -> observedDataHeader,
    Report.`type` -> reportHeader,
    LanguageContent.`type` -> langContent,
    MarkingDefinition.`type` -> markingDef,
    Neo4jWriter.observedDataRefs -> obDataRefDef
  )

  //---------------------------------------------------------------------
  //----------------relationships----------------------------------------
  //---------------------------------------------------------------------

  val killChainPhaseRelHeader = ":START_ID(StixObj),:END_ID(kill_chain_phase_id),:TYPE"
  val externalReferenceRelHeader = ":START_ID(StixObj),:END_ID(external_reference_id),:TYPE"
  val granularMarkingRelHeader = ":START_ID(StixObj),:END_ID(granular_marking_id),:TYPE"
  val objectRefRelHeader = ":START_ID(StixObj),:END_ID(object_ref_id),:TYPE"
  val whereSightedRelHeader = ":START_ID(StixObj),:END_ID(where_sighted_ref_id),:TYPE"
  val relationshipsHeader = ":START_ID(StixObj),:END_ID(StixObj),:TYPE,description," + commonRelHeader
  val sightingsHeader = ":START_ID(StixObj),:END_ID(StixObj),:TYPE,first_seen,last_seen,count,summary:boolean,observed_data_ids:string[],where_sighted_refs_id:string[],description," + commonRelHeader

  relHeaders ++= Map(
    KillChainPhase.`type` -> killChainPhaseRelHeader,
    ExternalReference.`type` -> externalReferenceRelHeader,
    Neo4jWriter.observedDataRefs -> objectRefRelHeader,
    Neo4jWriter.objectRefs -> objectRefRelHeader,
    Neo4jWriter.whereSightedRefs -> whereSightedRelHeader,
    GranularMarking.`type` -> granularMarkingRelHeader,
    Relationship.`type` -> relationshipsHeader,
    Sighting.`type` -> sightingsHeader
  )

}

