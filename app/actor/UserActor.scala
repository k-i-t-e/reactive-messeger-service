package actor

import akka.actor.{Actor, ActorRef, Props}
import model.Message

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
class UserActor(out: ActorRef, chatRoom: ActorRef) extends Actor {
  chatRoom ! Join

  override def receive: Receive = {
    case msg: String => chatRoom ! Message("", msg)
    case msg: Message => out ! ("I message from " + msg.text)
  }
}

object UserActor {
  def props(out: ActorRef, chatRoom: ActorRef) = Props(new UserActor(out, chatRoom))
}
