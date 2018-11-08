package io.saagie.academy.datapipeline.kafka

import java.time.Duration

import cakesolutions.kafka
import cakesolutions.kafka.KafkaConsumer.Conf
import org.apache.kafka.clients.consumer.{Consumer, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

case object Consumer {
  def broker = "localhost:9092"

  def topic = "datapipeline-topic"

  def groupId = "group-1"

  def createConfiguration(): Option[Conf[String, String]] = {
    val configuration = Some(Conf(
      new StringDeserializer,
      new StringDeserializer,
      bootstrapServers = broker,
      groupId))
    configuration
  }

  def createConsumer(): Option[KafkaConsumer[String, String]] = {
    val kafkaConsumer = Some(kafka.KafkaConsumer(Consumer.createConfiguration().get))
    kafkaConsumer
  }

  def consume(kafkaConsumer: Consumer[String, String]): Option[ConsumerRecords[String, String]] = {
    import scala.collection.JavaConverters._
    kafkaConsumer.subscribe(Seq(Consumer.topic).asJava)
    Some(kafkaConsumer.poll(Duration.ofSeconds(10)))
  }
}
