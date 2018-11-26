package io.saagie.academy.datapipeline.spark.structured

import io.saagie.academy.datapipeline.Message
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions._

case object StructuredStreaming {
  def appName = "StructuredStreaming"

  def master = "local[*]"

  def broker = "localhost:9092"

  def createSession: Option[SparkSession] = {
    val spark = None
    spark
  }

  def readDataSet(sparkSession: SparkSession): Option[Dataset[Message]] = {
    import sparkSession.implicits._
    val ds = None
    ds
  }

  def countWords(df: Dataset[Message]): Option[Dataset[Long]] = {
    import df.sparkSession.implicits._
    None
  }
}
