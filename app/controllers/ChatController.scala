package controllers

import javax.inject.Inject

import actor.{ChatRoom, UserActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import model.Message
import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{Controller, WebSocket}
import play.api.libs.functional.syntax._


/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class ChatController @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {
  /*implicit val messageReads: Reads[Message] = (
    (JsPath \ "sender").read[String] and
      (JsPath \ "text").read[String]
    )(Message.apply _)

  implicit val messageWrites: Writes[Message] = (
    (JsPath \ "sender").write[String] and
      (JsPath \ "text").write[String]
    )(unlift(Message.unapply))

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[Message, Message]*/

  val chatRoom = system.actorOf(Props(new ChatRoom))

  def join(name: String) = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => {
      UserActor.props(out, chatRoom)
    })
  }
  def leave(name: String) = TODO
}
