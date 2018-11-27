package io.saagie.academy.datapipeline.kafka.streams

import java.time.Duration

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import org.apache.kafka.common.serialization.{IntegerDeserializer, LongDeserializer}

object StreamConsumer extends App {
  def broker = "dq1:31200"

  def topic = "realtime-streams-count-ok"

  def groupId = "group-1"

  val conf = Conf(new LongDeserializer, new IntegerDeserializer, bootstrapServers = broker, groupId)
  val consumer = KafkaConsumer(conf)

  import scala.collection.JavaConverters._

  consumer.subscribe(Seq(topic).asJava)
  while (true) {
    consumer
      .poll(Duration.ofSeconds(10))
      .iterator()
      .asScala
      .foreach(c => println(s"${c.key()}: ${c.value()}"))
  }
}
