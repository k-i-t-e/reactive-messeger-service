package controllers

import javax.inject.Inject

import manager.MessageManager
import model.{Dialog, Message}
import play.api.mvc.{Action, BodyParsers, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsPath, Json, Reads, Writes}

import scala.concurrent.{Await, ExecutionContext, Future}
import play.api.libs.functional.syntax._

import scala.concurrent.duration.Duration


/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class Messages @Inject() (messageManager: MessageManager) extends Controller {

  implicit val messageReads: Reads[Message] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "text").read[String] and
      (JsPath \ "to").readNullable[String]
    )(Message.apply _)

  implicit val messageWrites: Writes[Message] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "text").write[String] and
      (JsPath \ "to").writeNullable[String]
    )(unlift(Message.unapply))

  implicit val dialogWrites: Writes[Dialog] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "to").write[String] and
      (JsPath \ "text").writeNullable[String]
    )(unlift(Dialog.unapply))

  def submit = Action.async(BodyParsers.parse.json) { request =>
    val res = request.body.validate[Message]
    Future(res.fold(
      errors => BadRequest("Invalid message format"),
      message => Ok(Json.toJson(messageManager.saveMessage(message)))
    ))
  }

  def get() = Action.async {
    for {
      messages <- messageManager.getMessages()
    } yield Ok(Json.toJson(messages))
  }

  def getPrivate(sender: String, address: String) = Action.async {
    implicit request => messageManager.getPrivateMessages(sender, address).flatMap(list => Future(Ok(Json.toJson(list))));
  }

  def getLast(user: String) = Action.async {
    implicit request => {
      Future(Ok(Json.toJson(List[Dialog](Dialog("", "", Option(""))))))
    };
  }
}
