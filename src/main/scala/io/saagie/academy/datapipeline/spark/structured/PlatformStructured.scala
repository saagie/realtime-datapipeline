package io.saagie.academy.datapipeline.spark.structured

import io.saagie.academy.datapipeline.Message
import org.apache.spark.sql.{Encoders, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.StringType

object PlatformStructured extends App {
  def appName = "StructuredStreaming"

  def broker = "dq1:31200"

  val spark = SparkSession.builder()
    .appName(appName)
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val ds = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", broker)
    .option("subscribe", "tweet")
    .load()
    .select(from_json($"value" cast StringType, Encoders.product[Message].schema) as "value")
    .select($"value.*")
    .as[Message]
    .select($"text", split($"text", " ") as "words")
    .select($"text", size($"words"))

  val query = ds
    .writeStream
    .format("console")
    .outputMode(OutputMode.Append())
    .start()

  query.awaitTermination()
}
