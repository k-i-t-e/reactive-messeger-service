package repository

import javax.inject.{Inject, Singleton}

import model.{Acknowledgement, Dialog, Message}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONNull, BSONString, Macros, document}
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      val from = bson.getAs[BSONDocument]("_id").get.getAsTry[String]("big").get
      val to = bson.getAs[BSONDocument]("_id").get.getAsTry[String]("small").get
      val lastMsg = bson.getAs[String]("lastMsg")
      Dialog(from, to, lastMsg)
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
    messages.flatMap(_.find(document("$or" -> List(document("to" -> to, "from" -> from), document("to" -> from, "from" -> to)))).cursor[Message]().collect[List]())
  }

  def updateMessageStatus(ack: Acknowledgement) = {
    val update =
      (if (ack.received.isDefined) document("recieved" -> ack.received.get) else document()) ++
      (if (ack.viewed.isDefined) document("viewed" -> ack.viewed.get) else document())

    messages.flatMap(
      _.update(document("from" -> ack.from, "to" -> ack.to, "date" -> ack.timestamp), update).map(_ => {}))
  }

  def updateMessagesStatusBatch(ack: Acknowledgement) = {
    val update =
      (if (ack.received.isDefined) document("recieved" -> ack.received.get) else document()) ++
        (if (ack.viewed.isDefined) document("viewed" -> ack.viewed.get) else document())

    messages.flatMap(
      _.update(document("from" -> ack.from, "to" -> ack.to, "date" -> document("$lte" -> ack.timestamp)), update, multi = true))
  }

  // db.messages.aggregate([
  //   {
  //     $match:{
  //       $or: [{from: "kite"}, {to:"kite"}],
  //       to: {$not: {$eq: null}}
  //     }
  //   },
  //   {
  //     $group:{
  //       _id:{from:"$from", to:"$to"},
  //       last_msg:{$last:"$text"}
  //     }
  //   }
  // ])
  /*
  db.messages.aggregate([
     {
       $match:{
         $or: [{from: "kite"}, {to:"kite"}],
         to: {$not: {$eq: null}}
       }
     },
	 {
		$project : {
			from: 1,
			to: 1,
			text:1,
			"groupId" : {"$cond" : [{"$gt" : ['$from', '$to']}, {big : "$from", small : "$to"}, {big : "$to", small : "$from"}]}
		}
	},
     {
       $group:{
         _id:"$groupId",
         lastMsg:{$last:"$text"}
       }
     }
   ])
   */
  def loadLastMessages(user: String): Future[List[Dialog]] = {
    def perform(col: BSONCollection, userName: String) = {
      import col.BatchCommands.AggregationFramework.{
        Group, Match, LastField, Project
      }

      col.aggregate(
        Match(document(
          "$or" -> List(document("from" -> userName), document("to" -> userName)),
          "to" -> document(
            "$not" -> document(
              "$eq" -> BSONNull
            )
          ))
        ),
        List(
          Project(document(
            "from" -> 1,
            "to" -> 1,
            "text" -> 1,
            "groupId" -> document(
              "$cond" -> List(
                document("$gt" -> List("$from", "$to")),
                document("big" -> "$from", "small" -> "$to"),
                document("big" -> "$to", "small" -> "$from")
              )
            )
          )),
          Group(BSONString("$groupId"))("lastMsg" -> LastField("text"))
        )
      ).map(_.result[Dialog])
    }

    messages.flatMap(perform(_, user))
  }
}
