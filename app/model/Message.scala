package model

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
case class Message(from: String, text: String, to: Option[String])
case class IncomingMessage(from: String, text: String, to: String = null)
case class Dialog(from: String, to: String, lastMessage: Option[String])