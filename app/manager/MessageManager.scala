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
  def getMessages(name: String): Vector[Message] = repository.loadMessagesFor(name).map(dto => Message(dto.text, dto.sender))

  def saveMessage(message: Message): Message =  {
    repository.saveMessage(new MessageDto(message.text, message.sender))
    message
  }
}
