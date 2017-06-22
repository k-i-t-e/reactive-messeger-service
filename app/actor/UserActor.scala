package actor

import akka.actor.{Actor, ActorRef, Props}
import model.{IncomingMessage, Message}

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
class UserActor(out: ActorRef, chatRoom: ActorRef, persistActor: ActorRef, name: String) extends Actor {
  chatRoom ! Join(name)

  override def receive: Receive = {
    case msg: IncomingMessage =>
      chatRoom ! Message(name, msg.text)
      persistActor ! Message(name, msg.text)
    case msg: Message => out ! msg
  }

  override def postStop() = chatRoom ! Leave(name)

}

object UserActor {
  def props(out: ActorRef, chatRoom: ActorRef, persistActor: ActorRef, name: String) =
    Props(new UserActor(out, chatRoom, persistActor, name))
}
