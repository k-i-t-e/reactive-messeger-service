package actor

import akka.actor.Actor
import manager.MessageManager
import model.Message

/**
  * Created by Mikhail_Miroliubov on 6/5/2017.
  */
class PersistActor (manager: MessageManager) extends Actor {
  override def receive: Receive = {
    case msg: Message => manager.saveMessage(msg)
  }
}
