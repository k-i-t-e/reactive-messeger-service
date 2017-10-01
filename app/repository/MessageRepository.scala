package repository

import javax.inject.{Inject, Singleton}

import model.{Dialog, Message}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONNull, BSONString, Macros, document}
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Singleton
class MessageRepository @Inject() (val reactiveMongoApi: ReactiveMongoApi) {
  val db = reactiveMongoApi.database
  def messages = db.map(_.collection[BSONCollection]("messages"))

  implicit def messageWriter: BSONDocumentWriter[Message] = Macros.writer[Message]
  implicit def messageReader: BSONDocumentReader[Message] = Macros.reader[Message]
  implicit object DialogReader extends BSONDocumentReader[Dialog] {
    override def read(bson: BSONDocument): Dialog = {
      val dialog: Option[Dialog] = for {
        from <- bson.getAs[BSONDocument]("_id").get.getAs[String]("from")
        to <- bson.getAs[BSONDocument]("_id").get.getAs[String]("to")
        lastMsg <- bson.getAs[String]("lastMsg").orElse(null)
      } yield Dialog(from, to, if (lastMsg != null) Option(lastMsg) else Option.empty)

      dialog.get
    }
  }

  implicit def dialogReader: BSONDocumentReader[Dialog] = DialogReader

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
  def loadLastMessages(user: String) = {
    messages.flatMap(perform(_, user))

    def perform(col: BSONCollection, from: String) = {
      import col.BatchCommands.AggregationFramework.{
        AggregationResult, Group, Match, LastField
      }

      col.aggregate(
        Match(document("from" -> from)),
        List(Group(document("from" -> "$from", "to" -> "$to"))("lastMsg" -> LastField("$text")))
      ).map(r => r.result[Dialog])
    }
  }
}
