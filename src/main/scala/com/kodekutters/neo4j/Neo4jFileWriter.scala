package com.kodekutters.neo4j

import java.io.{File, IOException, PrintWriter}
import scala.collection.mutable


/**
  * writing to plain csv files
  * @param outDir the output directory to write to
  */
class Neo4jFileWriter(outDir: String) extends NeoWriter {

  // a map containing a writer for for each type in nodeTypes using the type as the file name
  private val nodeWriters = mutable.Map[String, PrintWriter]()

  // a map containing a writer for for each type in relationTypes using the type as the file name
  private val relWriters = mutable.Map[String, PrintWriter]()

  /**
    * create all required csv files
    */
  def init(): Unit = {
    // create a file and a writer for it, for each type in nodeTypes and use the type as the file name
    nodeWriters ++= (for (p <- NeoWriter.nodeTypes)
      yield p -> new PrintWriter(new File(outDir + p + ".csv"))).toMap[String, PrintWriter]

    // create a file and a writer for it, for each relations in relationTypes and use the type_rel as the file name
    relWriters ++= (for (p <- NeoWriter.relationTypes)
      yield p -> new PrintWriter(new File(outDir + p + "_rel.csv"))).toMap[String, PrintWriter]

  }

  /**
    * write the required headers into all csv files
    */
  def writeHeaders(): Unit = {
    try {
      Neo4jHeaders.nodeHeaders.foreach(f => nodeWriters(f._1).write(f._2 + "\n"))
      Neo4jHeaders.relHeaders.foreach(f => relWriters(f._1).write(f._2 + "\n"))
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToFile(outFile: String, line: String): Unit = {
    try {
      nodeWriters(outFile).write(line + "\n")
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def writeToRelFile(outFile: String, line: String): Unit = {
    try {
      relWriters(outFile).write(line + "\n")
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def closeAll = {
    nodeWriters.foreach(p => p._2.close())
    relWriters.foreach(p => p._2.close())
  }

}
