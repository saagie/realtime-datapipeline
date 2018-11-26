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
    val conf = None
    conf
  }

  def createKafkaConfiguration: Option[Map[String, Object]] = {
    val conf = None
    conf
  }

  def createStreamingContext: Option[StreamingContext] = {
    val ssc = None
    ssc
  }

  def createKafkaDirectStream: Option[InputDStream[ConsumerRecord[String, String]]] = {
    val subscription: Option[ConsumerStrategy[String, String]] = None
    val stream = None
    stream
  }

  def stringToMessage(input: String): Message = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    read[Message](input)
  }

  def transformToMessage(stream: DStream[String]): DStream[Int] = {
    stream.map(_ => 1)
  }
}
