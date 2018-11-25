package io.saagie.academy.datapipeline.generator

import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import io.saagie.academy.datapipeline.Message
import org.apache.kafka.common.serialization.{LongSerializer, StringSerializer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, LongType}
import org.json4s.DefaultFormats

case object Generator extends App {
  def broker = "localhost:9092"

  def topic = "tweet"

  val producer = KafkaProducer(Conf(new LongSerializer, new StringSerializer, broker))

  val spark = SparkSession
    .builder()
    .appName("Data Generator.")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val df = spark
    .read
    .option("header", "true")
    .csv("training.1600000.processed.noemoticon.utf8.csv")
    .drop($"_c0")
    .withColumn("sentiment", $"sentiment" cast IntegerType)
    .withColumn("ids", $"ids" cast LongType)
    .as[Message]

  import org.json4s.native.Serialization.write

  implicit val formats: DefaultFormats.type = DefaultFormats
  while (true) {
    spark
      .read
      .option("header", "true")
      .csv("training.1600000.processed.noemoticon.utf8.csv")
      .drop($"_c0")
      .withColumn("sentiment", $"sentiment" cast IntegerType)
      .withColumn("ids", $"ids" cast LongType)
      .as[Message]
      .foreach(r => {
        producer.send(KafkaProducerRecord(topic, r.ids: java.lang.Long, write(r)))
        Thread.sleep(1000)
      })
  }
}
