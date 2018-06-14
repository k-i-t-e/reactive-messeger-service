package model

import java.util.Date

import model.MessageType.MessageType

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
case class Message(from: String,
                   text: String,
                   to: Option[String],
                   date: Date,
                   received: Boolean,
                   viewed: Boolean)

case class Acknowledgement(from: String,
                           to: String,
                           timestamp: Date,
                           received: Option[Boolean],
                           viewed: Option[Boolean])

object MessageType extends Enumeration {
  type MessageType = Value
  val MESSAGE, ACKNOWLEDGEMENT = Value
}

case class IncomingMessage(from: String,
                           text: String,
                           to: String = null,
                           messageType: MessageType)

case class Dialog(from: String,
                  to: String,
                  lastMessage: Option[String])
