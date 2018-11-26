package io.saagie.academy.datapipeline

case class Message(sentiment: Int, ids: Long, date: String, flag: String, user: String, text: String)

case object Message {
//  def apply(): Message = new Message()
}
