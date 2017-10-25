package actor

import java.util.Date

import akka.actor.{Actor, ActorRef}
import model.Message

case class Join(name: String)
case class Leave(name: String)

/**
  * Created by Mikhail_Miroliubov on 6/5/2017.
  */
class ChatRoom extends Actor{ // TODO: remove - get rid of global chat
  private var subscribers: Set[ActorRef] = Set.empty

  override def receive: Receive = {
    case msg: Join =>
      subscribers += sender()
      broadcast(Message(msg.name, msg.name + " has joined the chat", Option.empty, new Date()))
    case msg: Leave =>
      subscribers -= sender()
      broadcast(Message(msg.name, msg.name + " has left the chat", Option.empty, new Date()))
    case msg: Message => subscribers.filter(_ != sender()).foreach(_ ! msg)
  }

  private def broadcast(message: Message) = subscribers.foreach(_ ! message)
}
