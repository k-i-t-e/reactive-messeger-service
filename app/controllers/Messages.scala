package controllers

import java.util.Date
import javax.inject.Inject

import manager.MessageManager
import model.{Acknowledgement, Dialog, Message}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.Future

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class Messages @Inject() (messageManager: MessageManager) extends Controller {
  implicit val messageWrites: Writes[Message] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "text").write[String] and
      (JsPath \ "to").writeNullable[String] and
      (JsPath \ "date").write[Date] and
      (JsPath \ "received").write[Boolean] and
      (JsPath \ "viewed").write[Boolean]
    )(unlift(Message.unapply))

  implicit val dialogWrites: Writes[Dialog] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "to").write[String] and
      (JsPath \ "text").writeNullable[String]
    )(unlift(Dialog.unapply))

  implicit val acknowledgementReads: Reads[Acknowledgement] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "to").read[String] and
      (JsPath \ "timestamp").read[Date] and
      (JsPath \ "received").readNullable[Boolean] and
      (JsPath \ "viewed").readNullable[Boolean]
    )((from, to, timestamp, received, viewed) => Acknowledgement(from, to, timestamp, received, viewed))

  def get() = Action.async {
    for {
      messages <- messageManager.getMessages()
    } yield Ok(Json.toJson(messages))
  }

  def getPrivate(sender: String, address: String) = Action.async {
    implicit request => messageManager.getPrivateMessages(sender, address).map(list => Ok(Json.toJson(list)));
  }

  def getLast(user: String) = Action.async {
    implicit request => {
      messageManager.getLastMessages(user).map(list => Ok(Json.toJson(list)))
    };
  }

  def updateMessagesStatus = Action.async(BodyParsers.parse.json) { request =>
    val res = request.body.validate[Acknowledgement]
    res.fold(
      errors => Future(BadRequest("Invalid request format")),
      ack => {
        messageManager.updateMessagesStatusBatch(ack)
        Future(Ok(Json.obj("success" -> true)))
      }
    )
  }
}
