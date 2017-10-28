package model

import java.util.Date

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
case class Message(from: String, text: String, to: Option[String], date: Date, received: Boolean, viewed: Boolean)
case class IncomingMessage(from: String, text: String, to: String = null)
case class Dialog(from: String, to: String, lastMessage: Option[String])
case class Acknowledgement(from: String, to: String, timestamp: Date, received: Option[Boolean], viewed: Option[Boolean])