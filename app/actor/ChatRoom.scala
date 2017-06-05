package actor

import akka.actor.{Actor, ActorRef}
import model.Message

case class Join(name: String)
case class Leave(name: String)

/**
  * Created by Mikhail_Miroliubov on 6/5/2017.
  */
class ChatRoom extends Actor{
  private var subscribers: Set[ActorRef] = Set.empty

  override def receive: Receive = {
    case msg: Join =>
      subscribers += sender()
      broadcast(Message(msg.name, msg.name + " has joined the chat"))
    case msg: Leave =>
      subscribers -= sender()
      broadcast(Message(msg.name, msg.name + " has left the chat"))
    case msg: Message => subscribers.filter(_ != sender()).foreach(_ ! msg)
  }

  private def broadcast(message: Message) = subscribers.foreach(_ ! message)
}
