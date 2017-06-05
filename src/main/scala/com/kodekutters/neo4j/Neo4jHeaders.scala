package com.kodekutters.neo4j

import com.kodekutters.stix._

import scala.collection.mutable

/**
  * the neo4j csv files headers
  */
object Neo4jHeaders {

  val nodeHeaders = mutable.Map[String, String]()
  val relHeaders = mutable.Map[String, String]()

  val commonH = "created,modified,revoked:boolean,labels:string[],confidence:int,external_references:string[],lang,object_marking_refs:string[],granular_markings:string[],created_by_ref"
  val commonRelHeader = "id:string," + commonH
  val commonNodeHeader = "id:ID(StixObj),type," + commonH
  val endHeader = ",:LABEL"

  //---------------------------------------------------------------------
  //----------------nodes------------------------------------------------
  //---------------------------------------------------------------------

  val attackPatternHeader = commonNodeHeader + ",name,description,kill_chain_phases:string[]" + endHeader

  val identityHeader = commonNodeHeader + ",name,identity_class,sectors:string[],contact_information,description" + endHeader

  val campaignHeader = commonNodeHeader + ",name,objective,aliases:string[],first_seen,last_seen,description" + endHeader

  val courseOfActionHeader = commonNodeHeader + ",name,description" + endHeader

  val indicatorHeader = commonNodeHeader + ",name,description,pattern,valid_from,valid_until,kill_chain_phases:string[]" + endHeader

  val intrusionSetHeader = commonNodeHeader + ",name,description,aliases:string[],first_seen,last_seen,goals:string[],resource_level,primary_motivation,secondary_motivations:string[]" + endHeader

  val reportHeader = commonNodeHeader + ",name,published,object_refs:string[],description" + endHeader

  val threatActorHeader = commonNodeHeader + ",name,description,aliases:string[],roles:string[],goals:string[],sophistication,resource_level,primary_motivation,secondary_motivations:string[],personal_motivations:string[]" + endHeader

  val toolHeader = commonNodeHeader + ",name,description,kill_chain_phases:string[],tool_version" + endHeader

  val malwareHeader = commonNodeHeader + ",name,description,kill_chain_phases:string[]" + endHeader

  val vulnerabilityHeader = commonNodeHeader + ",name,description" + endHeader

  val killChainPhaseHeader = "kill_chain_phase_id:ID(kill_chain_phase_id),kill_chain_name,phase_name" + endHeader

  val externalReferenceHeader = "external_reference_id:ID(external_reference_id),source_name,description,url,external_id" + endHeader

  val granularMarkingHeader = "granular_marking_id:ID(granular_marking_id),selectors:string[],marking_ref,lang" + endHeader

  val whereSightedHeader = "where_sighted_ref_id:ID(where_sighted_ref_id),identifier" + endHeader

  val objRefsHeader = "object_ref_id:ID(object_ref_id),identifier" + endHeader

  val markingDef = "id:ID(StixObj),type,created,definition_type,definition,external_references:string[],object_marking_refs:string[],granular_markings:string[],created_by_ref" + endHeader

  val markingObjRefHeader = "marking_id:ID(marking_id),marking" + endHeader

  val langContent = "id:ID(StixObj),type,created,modified,object_modified,object_ref,labels:string[],revoked:boolean,external_references:string[],object_marking_refs:string[],granular_markings:string[],created_by_ref" + endHeader

  // todo
  val observedDataHeader = commonNodeHeader + ",first_observed,last_observed,number_observed:int,description" + endHeader

  // todo
  val obDataRefDef = "observe_id:ID(observe_id),observe,:LABEL"

  nodeHeaders ++= Map(AttackPattern.`type` -> attackPatternHeader,
    KillChainPhase.`type` -> killChainPhaseHeader,
    ExternalReference.`type` -> externalReferenceHeader,
    GranularMarking.`type` -> granularMarkingHeader,
    NeoWriter.whereSightedRefs -> whereSightedHeader,
    NeoWriter.objectRefs -> objRefsHeader,
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
    NeoWriter.observedDataRefs -> obDataRefDef,
    NeoWriter.markingObjRefs -> markingObjRefHeader
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
  val markingObjRelHeader = ":START_ID(StixObj),:END_ID(marking_id),mark_type,:TYPE"

  relHeaders ++= Map(
    KillChainPhase.`type` -> killChainPhaseRelHeader,
    ExternalReference.`type` -> externalReferenceRelHeader,
    NeoWriter.observedDataRefs -> objectRefRelHeader,
    NeoWriter.objectRefs -> objectRefRelHeader,
    NeoWriter.whereSightedRefs -> whereSightedRelHeader,
    GranularMarking.`type` -> granularMarkingRelHeader,
    Relationship.`type` -> relationshipsHeader,
    Sighting.`type` -> sightingsHeader,
    NeoWriter.markingObjRefs -> markingObjRelHeader
  )

}

