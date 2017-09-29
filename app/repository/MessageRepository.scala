package repository

import javax.inject.{Inject, Singleton}

import model.Message
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONNull

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Singleton
class MessageRepository @Inject() (val reactiveMongoApi: ReactiveMongoApi) {
  val db = reactiveMongoApi.database
  def messages = db.map(_.collection[BSONCollection]("messages"))

  implicit def personWriter: BSONDocumentWriter[Message] = Macros.writer[Message]
  implicit def personReader: BSONDocumentReader[Message] = Macros.reader[Message]

  def saveMessage(message: Message) = {
    messages.flatMap(_.insert(message).map(_ => {}))
  }

  def loadGlobalMessages() = {
      messages.flatMap(_.find(document("to" -> BSONNull)).cursor[Message]().collect[List](-1, false))
  }

  def loadPrivateMessages(from: String, to: String) = {
    messages.flatMap(_.find(document("to" -> to, "from" -> from)).cursor[Message]().collect[List]())
  }

  /*def loadLastMessages(user: String) = {
    messages.flatMap(_.find(document()))
  }*/
}
