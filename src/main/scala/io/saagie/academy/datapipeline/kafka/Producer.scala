package io.saagie.academy.datapipeline.kafka

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

case object Producer {

  def broker = "localhost:9092"

  def topic = "datapipeline-topic"

  def createConfiguration(): Option[Conf[String, String]] = {
    val configuration = Some(Conf(
      new StringSerializer,
      new StringSerializer,
      bootstrapServers = broker))
    configuration
  }

  def createProducer(): Option[KafkaProducer[String, String]] = {
    val kafkaProducer = Some(KafkaProducer(Producer.createConfiguration().get))
    kafkaProducer
  }

  def createRecord(key: String, value: String): Option[ProducerRecord[String, String]] = {
    val producerRecord = Some(new ProducerRecord(topic, key, value))
    producerRecord
  }

  def sendRecord(producer: KafkaProducer[String, String]): Option[Future[RecordMetadata]] = {
    val record = Producer.createRecord("key", "value")
    val metadata = record.fold(Option.empty[Future[RecordMetadata]])(rec => Some(producer.send(rec)))
    metadata
  }
}
