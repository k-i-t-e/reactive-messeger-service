package repository

import javax.inject.{Inject, Singleton}

import model.Message
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, BSONNull, BSONString, Macros, document}
import play.modules.reactivemongo.ReactiveMongoApi

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
   // db.messages.aggregate([{$match:{from:"kite"}}, {$group:{_id:{from:"$from", to:"$to"}, last_msg:{$last:"$text"}}}])
  /*def loadLastMessages(user: String) = {
    //messages.flatMap(_.aggregate(document("$group" -> document("_id" -> document(), "last_msg" -> document()))))
    messages.flatMap(_ => perform(_, user))
    def perform(col: BSONCollection, from: String) = {
      import col.BatchCommands.AggregationFramework.{
        AggregationResult, Group, Match, LastField
      }

      col.aggregate(
        Match(document("from" -> from)),
        List(Group(document("from" -> "$from", "to" -> "$to"))("last_msg" -> LastField("$text")))
      ).map(_.head[])
    }
  }*/
}
