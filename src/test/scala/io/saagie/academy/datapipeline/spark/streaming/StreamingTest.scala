package io.saagie.academy.datapipeline.spark.streaming

import com.holdenkarau.spark.testing.StreamingSuiteBase
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.scalatest.{Matchers, WordSpec}

class StreamingTest extends WordSpec with Matchers with StreamingSuiteBase {
  "Spark Streaming configuration" when {
    val configuration = Streaming.createConfiguration
    "created" should {
      "be defined" in {
        configuration should not be Option.empty[SparkConf]
      }
      s"have Application name set to ${Streaming.appName}" in {
        assert(configuration.fold(false)(conf => conf.get("spark.app.name") == Streaming.appName))
      }
      s"have its master set to ${Streaming.master}" in {
        assert(configuration.fold(false)(conf => conf.get("spark.master") == Streaming.master))
      }
      s"have its serializer set to Kryo" in {
        assert(configuration.fold(false)(conf => conf.get("spark.serializer") == "org.apache.spark.serializer.KryoSerializer"))
      }
    }
  }

  "Kafka configuration" when {
    val configuration = Streaming.createKafkaConfiguration
    "created" should {
      "be defined" in {
        configuration should not be Option.empty[SparkConf]
      }
      s"have bootstrap servers set to ${Streaming.broker}" in {
        assert(configuration.fold(false)(conf => {
          conf.contains("bootstrap.servers") && conf.get("bootstrap.servers").contains(Streaming.broker)
        }))
      }
      s"have key deserializer as String" in {
        assert(configuration.fold(false)(conf => conf.get("key.deserializer").contains(classOf[StringDeserializer])))
      }
      s"have value deserializer as String" in {
        assert(configuration.fold(false)(conf => conf.get("value.deserializer").contains(classOf[StringDeserializer])))
      }
      s"have group id set to ${Streaming.groupId}" in {
        assert(configuration.fold(false)(conf => conf.get("group.id").contains(Streaming.groupId)))
      }
      s"have offset reset set to earliest" in {
        assert(configuration.fold(false)(conf => conf.get("auto.offset.reset").contains("earliest")))
      }
      s"have auto commit set to false: java.lang.Boolean" in {
        assert(configuration.fold(false)(conf => conf.get("enable.auto.commit").contains(false: java.lang.Boolean)))
      }
    }
  }

  "Streaming context" when {
    val ssc = Streaming.createStreamingContext
    "created" should {
      "be defined" in {
        ssc should not be Option.empty[StreamingContext]
      }
    }
  }

  "Kafka direct stream" when {
    val stream = Streaming.createKafkaDirectStream
    "created" should {
      "be defined" in {
        stream should not be Option.empty[InputDStream[ConsumerRecord[String, String]]]
      }
      s"have a duration of ${Streaming.interval}" in {
        stream.get.slideDuration shouldEqual Streaming.interval
      }
    }
  }

  "Data processing" when {
    "launch" should {
      "have number of words" in {
        val input = List(
          List("{\"sentiment\":4,\"ids\":1551332580,\"date\":\"Sat Apr 18 08:46:33 PDT 2009\",\"flag\":\"NO_QUERY\",\"user\":\"The_Lake_Effect\",\"text\":\"weekend off! tickets are now available for our show @ Santa Fe in College Park next weekend \"}", "{\"sentiment\":0,\"ids\":1467810369,\"date\":\"Mon Apr 06 22:19:45 PDT 2009\",\"flag\":\"NO_QUERY\",\"user\":\"_TheSpecialOne_\",\"text\":\"@switchfoot http://twitpic.com/2y1zl - Awww, that's a bummer. You shoulda got David Carr of Third Day to do it. ;D\"}"),
          List(" {\"sentiment\":0,\"ids\":2059478713,\"date\":\"Sat Jun 06 17:01:00 PDT 2009\",\"flag\":\"NO_QUERY\",\"user\":\"ahashake\",\"text\":\"Still quite tired and not wanting to go to work and still undecided whether I should hit the town again tonight for Glitter...\"}")
        )
        val expected = List(
          List(17, 19),
          List(23)
        )
        testOperation(input, Streaming.transformToMessage, expected)
      }
    }
  }
}
