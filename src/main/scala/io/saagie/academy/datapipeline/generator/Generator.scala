package io.saagie.academy.datapipeline.generator

import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import io.saagie.academy.datapipeline.Message
import org.apache.kafka.common.serialization.{LongSerializer, StringSerializer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, LongType}
import org.json4s.DefaultFormats

case object Generator extends App {
  def broker = "dq1:31200"

  def topic = "tweet"

  val producer = KafkaProducer(Conf(new LongSerializer, new StringSerializer, broker))

  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Data Generator.")
    .getOrCreate()

  spark
    .sparkContext
    .setLogLevel("ERROR")

  import spark.implicits._

  def toJson(r: Message): String = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    import org.json4s.jackson.Serialization.write
    write(r)
  }

  while (true) {
    spark
      .read
      .option("header", "true")
      .csv("hdfs://cluster/user/academy/realtime-datapipeline/training.1600000.processed.noemoticon.utf8.csv")
      .drop($"_c0")
      .withColumn("sentiment", $"sentiment" cast IntegerType)
      .withColumn("ids", $"ids" cast LongType)
      .as[Message]
      .foreach(r => {
        producer.send(KafkaProducerRecord(topic, r.ids: java.lang.Long, toJson(r)))
        Thread.sleep(1000)
      })
  }
}
