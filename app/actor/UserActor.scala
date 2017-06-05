package actor

import akka.actor.{Actor, ActorRef, Props}
import model.Message

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
class UserActor(out: ActorRef, chatRoom: ActorRef, persistActor: ActorRef, name: String) extends Actor {
  chatRoom ! Join(name)

  override def receive: Receive = {
    case msg: String =>
      chatRoom ! Message(name, msg)
      persistActor ! Message(name, msg)
    case msg: Message => out ! (msg.sender + ": " + msg.text)
  }

  override def postStop() = chatRoom ! Leave(name)

}

object UserActor {
  def props(out: ActorRef, chatRoom: ActorRef, persistActor: ActorRef, name: String) =
    Props(new UserActor(out, chatRoom, persistActor, name))
}
