package manager

import javax.inject.{Inject, Singleton}

import model.Message
import model.dto.MessageDto
import repository.MessageRepository

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
@Singleton
class MessageManager @Inject() (repository: MessageRepository) {
  def getMessages(name: String): Vector[Message] = repository.loadGlobalMessages(name).map(dto => Message(dto.sender, dto.text))

  def getPrivateMessages(sender: String, addres: String): Vector[Message] =
    repository.loadPrivateMessages(sender, addres)
      .map(dto => Message(dto.sender, dto.text, dto.address))

  def saveMessage(message: Message): Message =  {
    repository.saveMessage(new MessageDto(message.text, message.sender, message.address))
    message
  }
}
