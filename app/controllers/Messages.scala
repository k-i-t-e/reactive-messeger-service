package controllers

import javax.inject.Inject

import manager.MessageManager
import model.Message
import play.api.mvc.{Action, BodyParsers, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsPath, Json, Reads, Writes}

import scala.concurrent.Future
import play.api.libs.functional.syntax._


/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class Messages @Inject() (messageManager: MessageManager) extends Controller {

  implicit val messageReads: Reads[Message] = (
    (JsPath \ "sender").read[String] and
      (JsPath \ "text").read[String] and
      (JsPath \ "address").read[String]
    )(Message.apply _)

  implicit val messageWrites: Writes[Message] = (
    (JsPath \ "sender").write[String] and
      (JsPath \ "text").write[String] and
      (JsPath \ "address").write[String]
    )(unlift(Message.unapply))

  def submit = Action.async(BodyParsers.parse.json) { request =>
    val res = request.body.validate[Message]
    Future(res.fold(
      errors => BadRequest("Invalid message format"),
      message => Ok(Json.toJson(messageManager.saveMessage(message)))
    ))
  }

  def get() = Action.async {
    implicit request => Future(Ok(Json.toJson(messageManager.getMessages())))
  }

  def getPrivate(sender: String, address: String) = Action.async {
    implicit request => Future(Ok(Json.toJson(messageManager.getPrivateMessages(sender, address))))
  }

  def getLast(user: String) = Action.async {
    implicit request => Future(Ok(Json.toJson(messageManager.getLastMessages(user))))
  }
}
