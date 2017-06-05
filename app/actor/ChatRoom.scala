package actor

import akka.actor.{Actor, ActorRef}
import model.Message

case object Join
case object Leave
/**
  * Created by Mikhail_Miroliubov on 6/5/2017.
  */
class ChatRoom extends Actor{
  private var subscribers: Set[ActorRef] = Set.empty

  override def receive: Receive = {
    case Join => subscribers += sender()
    case Leave => subscribers -= sender()
    case msg: Message => subscribers.filter(_ != sender()).foreach(_ ! msg)
  }
}
