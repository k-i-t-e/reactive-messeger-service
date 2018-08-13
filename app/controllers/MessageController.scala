package controllers

import java.util.Date

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import manager.MessageManager
import model.{Acknowledgement, Dialog, Message, RestResult}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.mvc._
import security.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class MessageController @Inject()(cc: ControllerComponents,
                                  messageManager: MessageManager,
                                  silhouette: Silhouette[DefaultEnv])(implicit ec: ExecutionContext) extends AbstractController(cc) {
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
      (JsPath \ "text").writeNullable[String] and
        (JsPath \ "date").write[Date]
    )(unlift(Dialog.unapply))

  implicit val acknowledgementReads: Reads[Acknowledgement] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "to").read[String] and
      (JsPath \ "timestamp").read[Date] and
      (JsPath \ "received").readNullable[Boolean] and
      (JsPath \ "viewed").readNullable[Boolean]
    )((from, to, timestamp, received, viewed) => Acknowledgement(from, to, timestamp, received, viewed))

  implicit val resultDialogReads: Writes[RestResult[Seq[Dialog]]] = Json.writes[RestResult[Seq[Dialog]]]

  def getPrivate(address: String) = silhouette.SecuredAction.async {
    request => messageManager.getPrivateMessages(request.identity.userName, address).map(list => Ok(Json.toJson(list)));
  }

  def getDialogs() = silhouette.SecuredAction.async {
    request => {
      messageManager.getDialogs(request.identity.userName).map(dialogs => Ok(Json.toJson(RestResult(dialogs))))
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
