package io.saagie.academy.datapipeline.spark.structured

import io.saagie.academy.datapipeline.Message
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StringType

case object StructuredStreaming extends App {
  def appName = "StructuredStreaming"

  def broker = "dq1:31200"

  def createSession: Option[SparkSession] = {
    val spark = Some(SparkSession.builder()
      .appName(appName)
      .getOrCreate())
    spark
  }

  def readDataSet(sparkSession: SparkSession): Option[Dataset[Message]] = {
    import sparkSession.implicits._
    val ds = Some(sparkSession
      .readStream
      .format("kafka")
      .option("bootstrap.servers", broker)
      .option("subscribe", "tweet")
      .load()
      .select($"value" cast StringType as "stringvalue")
      .select(from_json($"stringvalue", Encoders.product[Message].schema) as "v")
      .select($"v.*")
      .as[Message])
    ds
  }

  def countWords(df: Dataset[Message]): Option[Dataset[Long]] = {
    import df.sparkSession.implicits._
    Some(df.select(count(split($"text", " "))).as[Long])
  }

}
