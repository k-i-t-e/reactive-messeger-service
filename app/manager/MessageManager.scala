package manager

import javax.inject.{Inject, Singleton}

import model.Message
import model.dto.MessageDto
import repository.MessageRepository

import scala.collection.mutable
import scala.concurrent.Future

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
@Singleton
class MessageManager @Inject() (repository: MessageRepository) {
  def getMessages(): Future[List[Message]] = repository.loadGlobalMessages()

  def getPrivateMessages(sender: String, addres: String): Future[List[Message]] =
    repository.loadPrivateMessages(sender, addres)

  /*def getLastMessages(user: String): mutable.Buffer[Message] =
    repository.loadLastMessages(user)
      .map(dto => Message(dto.sender, dto.text, dto.address))*/

  def saveMessage(message: Message): Message =  {
    repository.saveMessage(message)
    message
  }
}
