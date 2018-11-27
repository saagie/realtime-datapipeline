package io.saagie.academy.datapipeline.kafka.streams

import java.util.Properties
import java.util.concurrent.TimeUnit

import io.saagie.academy.datapipeline.Message
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read

object StreamsCount extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  import org.apache.kafka.streams.scala.Serdes._

  val p = new Properties()
  p.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-streams")
  p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dq1:31200")

  val builder = new StreamsBuilder
  val lines: KStream[Long, String] = builder.stream[Long, String]("tweet")
  val textLine = lines.mapValues(read[Message](_).text)
  val textValue: KStream[String, Int] = textLine.map((_, value) => (value, value.split(" ").length))
  val linesWordCount = lines
    .mapValues(read[Message](_).text)
    .mapValues(text => text.split(" ").length)

  linesWordCount.to("realtime-streams-count-ok")

  val streams = new KafkaStreams(builder.build(), p)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }
}
