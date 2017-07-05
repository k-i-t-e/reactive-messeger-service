package model

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
case class Message(sender: String, text: String, address: String = null)
case class IncomingMessage(sender: String, text: String, address: String = null)
