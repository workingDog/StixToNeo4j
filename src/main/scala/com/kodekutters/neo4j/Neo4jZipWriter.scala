package com.kodekutters.neo4j

import java.io.{File, IOException}
import java.nio.file.{Files, Path, Paths}
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import scala.collection.mutable

/**
  * writes the results into zip files
  * @param outDir the output directory to write to
  */
class Neo4jZipWriter(outDir: String) extends NeoWriter {

  // a map containing a ZipOutputStream for for each type in nodeTypes using the type as the file name
  private val nodeWriters = mutable.Map[String, ZipOutputStream]()

  // a map containing a ZipOutputStream for for each type in relationTypes using the type as the file name
  private val relWriters = mutable.Map[String, ZipOutputStream]()

  /**
    * create all required zip csv files
    */
  def init(): Unit = {
    // create a zip file for each type in nodeTypes using the type as the file name
    nodeWriters ++= (for (p <- NeoWriter.nodeTypes)
      yield {
        val outputZip = new ZipOutputStream(Files.newOutputStream(Paths.get(outDir + p + ".zip")))
        outputZip.putNextEntry(new ZipEntry(p + ".csv"))
        p -> outputZip
      }).toMap[String, ZipOutputStream]

    // create a zip file for each relations in relationTypes using the type_rel as the file name
    relWriters ++= (for (p <- NeoWriter.relationTypes)
      yield {
        val outputZip = new ZipOutputStream(Files.newOutputStream(Paths.get(outDir + p + "_rel.zip")))
        outputZip.putNextEntry(new ZipEntry(p + "_rel.csv"))
        p -> outputZip
      }).toMap[String, ZipOutputStream]

  }

  /**
    * write the required headers into all csv files
    */
  def writeHeaders(): Unit = {
    try {
      Neo4jHeaders.nodeHeaders.foreach(f => nodeWriters(f._1).write((f._2 + "\n").getBytes))
      Neo4jHeaders.relHeaders.foreach(f => relWriters(f._1).write((f._2 + "\n").getBytes))
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToFile(outFile: String, line: String): Unit = {
    try {
      nodeWriters(outFile).write((line + "\n").getBytes)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToRelFile(outFile: String, line: String): Unit = {
    try {
      relWriters(outFile).write((line + "\n").getBytes)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def closeAll = {
    // this closes the entries as well
    nodeWriters.foreach(p => p._2.close())
    relWriters.foreach(p => p._2.close())
  }

}