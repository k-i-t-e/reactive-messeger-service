package manager

import javax.inject.{Inject, Singleton}

import model.{Acknowledgement, Dialog, Message}
import repository.MessageRepository

import scala.concurrent.Future

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
@Singleton
class MessageManager @Inject() (repository: MessageRepository) {
  def getMessages(): Future[List[Message]] = repository.loadGlobalMessages()

  def getPrivateMessages(sender: String, addres: String): Future[List[Message]] =
    repository.loadPrivateMessages(sender, addres)

  def getLastMessages(user: String): Future[List[Dialog]] = {
    repository.loadLastMessages(user)
  }

  def saveMessage(message: Message): Message =  {
    repository.saveMessage(message)
    message
  }

  def updateMessageStatus(ack: Acknowledgement) = {
    repository.updateMessageStatus(ack)
  }

  def updateMessagesStatusBatch (ack: Acknowledgement) = {
    repository.updateMessagesStatusBatch(ack)
  }
}
