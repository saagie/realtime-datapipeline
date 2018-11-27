package io.saagie.academy.datapipeline.spark.streaming

import io.saagie.academy.datapipeline.Message
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{ConsumerStrategy, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext, kafka010}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read

case object Streaming {
  def appName = "Streaming"

  def master = "local[*]"

  def interval = Seconds(5)

  def broker = "localhost:9092"

  def groupId = "group-1"

  def topic = "tweet"

  def createConfiguration: Option[SparkConf] = {
    val conf = Some(new SparkConf()
      .setAppName(appName)
      .setMaster(master)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.driver.allowMultipleContexts", "true"))
    conf
  }

  def createKafkaConfiguration: Option[Map[String, Object]] = {
    val conf = Some(Map[String, Object](
      "bootstrap.servers" -> broker,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    ))
    conf
  }

  def createStreamingContext: Option[StreamingContext] = {
    val ssc = new StreamingContext(createConfiguration.get, interval)
    Some(ssc)
  }

  def createKafkaDirectStream: Option[InputDStream[ConsumerRecord[String, String]]] = {
    val subscription = createKafkaConfiguration.fold(Option.empty[kafka010.ConsumerStrategy[String, String]])(conf => Some(Subscribe[String, String](Array(topic), conf)))
    val stream = Some(KafkaUtils.createDirectStream[String, String](
      createStreamingContext.get,
      PreferConsistent,
      subscription.get
    ))
    stream
  }

  def stringToMessage(input: String): Message = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    read[Message](input)
  }

  def transformToMessage(stream: DStream[String]): DStream[Int] = {
    stream
      .map(record => {
        stringToMessage(record)
      })
      .map(_.text.split(" ").length)
  }
}
