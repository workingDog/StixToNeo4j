package com.kodekutters.neo4j

import java.io.{File, InputStream}

import com.kodekutters.stix.Bundle
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.io.Source
import scala.language.implicitConversions
import scala.language.postfixOps


object Neo4jZipReader {

  /**
    * get a map of file names and bundles from the input zip file
    *
    * @param inFile the input zip file name
    * @return a map of (zip_file_name_entry and bundle options)
    */
  def readStixBundleZip(inFile: String): Map[String, Option[Bundle]] = {
    import scala.collection.JavaConverters._
    val rootZip = new java.util.zip.ZipFile(new File(inFile))
    rootZip.entries.asScala.
      filter(_.getName.toLowerCase.endsWith(".json")).
      collect { case stixFile => (stixFile.getName, loadBundle(rootZip.getInputStream(stixFile)))
      } toMap
  }

  /**
    * get a Bundle from the input source
    *
    * @param source the input InputStream
    * @return a Bundle option
    */
  def loadBundle(source: InputStream): Option[Bundle] = {
    // read a STIX bundle from the InputStream
    val jsondoc = Source.fromInputStream(source).mkString
    // create a bundle object from it
    decode[Bundle](jsondoc) match {
      case Left(failure) => println("-----> ERROR invalid bundle JSON in zip file: \n"); None
      case Right(bundle) => Option(bundle)
    }
  }

}
