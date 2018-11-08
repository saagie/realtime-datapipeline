package io.saagie.academy.datapipeline.kafka

import cakesolutions.kafka
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import org.apache.kafka.clients.consumer._
import org.apache.kafka.common.serialization.Deserializer
import org.scalatest.{Matchers, WordSpec}

class KafkaConsumerTest extends WordSpec with Matchers {
  "Kafka consumer configuration" when {
    val configuration = Consumer.createConfiguration()
    "created" should {
      "be defined" in {
        configuration should not be Option.empty[Conf[String, String]]
      }
      "have key deserializer as string serializer" in {
        assert(configuration.fold(false)(conf => conf.keyDeserializer.isInstanceOf[Deserializer[String]]))
      }
      "have value deserializer as string serializer" in {
        assert(configuration.fold(false)(conf => conf.valueDeserializer.isInstanceOf[Deserializer[String]]))
      }
      s"bootstrap server should be ${Consumer.broker}" in {
        assert(configuration.fold(false)(conf => conf.props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG).contains(Consumer.broker)))
      }
      s"group id should be ${Consumer.groupId}" in {
        assert(configuration.fold(false)(conf => conf.props.get(ConsumerConfig.GROUP_ID_CONFIG).contains(Consumer.groupId)))
      }
    }
  }

  "Kafka consumer" when {
    val consumer = Consumer.createConsumer()
    "created" should {
      "be defined" in {
        consumer should not be Option.empty[KafkaConsumer[String, String]]
      }
      "have the string serializer for key and value" in {
        assert(consumer.fold(false)(cons => cons.isInstanceOf[KafkaConsumer[String, String]]))
      }
    }

    "consuming" should {
      val mockConsumer = new MockConsumer[String, String](OffsetResetStrategy.EARLIEST)
      val result = Consumer.consume(mockConsumer)
      "not be empty" in {
        assert(result.isDefined)
      }
      s"has subscribed to ${Consumer.topic}" in {
        import scala.collection.JavaConverters._
        assert(mockConsumer.subscription().asScala.contains(Consumer.topic))
      }
    }
    consumer.foreach(_.close())
  }
}
