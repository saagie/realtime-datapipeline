package io.saagie.academy.datapipeline

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import org.apache.kafka.clients.producer.{MockProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import org.scalatest.{Matchers, WordSpec}

class KafkaProducerTest extends WordSpec with Matchers {

  "Kafka producer configuration" when {
    val configuration = Producer.createConfiguration()
    "created" should {
      "be defined" in {
        configuration should not be Option.empty[Conf[String, String]]
      }
      "have key serializer as string serializer" in {
        assert(configuration.fold(false)(conf => conf.keySerializer.isInstanceOf[Serializer[String]]))
      }
      "have value serializer as string serializer" in {
        assert(configuration.fold(false)(conf => conf.valueSerializer.isInstanceOf[Serializer[String]]))
      }
      s"bootstrap server should be ${Producer.broker}" in {
        configuration.fold(false)(conf => conf.props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).contains(Producer.broker))
      }
    }
  }

  "Kafka producer" when {
    val producer = Producer.createProducer()
    "created" should {
      "be defined" in {
        producer should not be Option.empty[KafkaProducer[String, String]]
      }

      "have the string serializer for key and value" in {
        assert(producer.fold(false)(prod => prod.isInstanceOf[KafkaProducer[String, String]]))
      }
    }
    producer.foreach(p => p.close())
  }

  "Kafka producer record" when {
    val key = "key"
    val value = "value"
    val record = Producer.createRecord(key, value)
    "created" should {
      "be defined" in {
        record should not be Option.empty[ProducerRecord[String, String]]
      }

      s"have topic set to ${Producer.topic}" in {
        assert(record.fold(false)(rec => rec.topic() == Producer.topic))
      }

      s"has correct key" in {
        assert(record.fold(false)(rec => rec.key() == key))
      }

      s"has correct value" in {
        assert(record.fold(false)(rec => rec.value() == value))
      }
    }

    "sent" should {
      val mockProducer = new MockProducer[String, String](true, new StringSerializer(), new StringSerializer())
      val producer = KafkaProducer(mockProducer)
      val metadata = Producer.sendRecord(producer)
      "be defined" in {
        metadata should not be Option.empty[ProducerRecord[String, String]]
      }

      s"received by broker" in {
        import scala.collection.JavaConverters._
        mockProducer.history().size() should be > 0
        mockProducer.history().asScala.map(record => {
          record.key() shouldEqual "key"
          record.value() shouldEqual "value"
          record.topic() shouldEqual Producer.topic
        })
      }
    }
  }
}
