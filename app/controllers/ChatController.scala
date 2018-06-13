package controllers

import javax.inject.Inject

import actor.UserActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import manager.MessageManager
import model.{IncomingMessage, Message}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import util.CustomActorFlow

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class ChatController @Inject()(cc: ControllerComponents, manager: MessageManager)
                              (implicit system: ActorSystem, materializer: Materializer) extends AbstractController(cc) {
  implicit val inEventFormat = Json.format[IncomingMessage]
  implicit val outEventFormat = Json.format[Message]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, Message]

  def join(name: String) = WebSocket.accept[JsValue, Message] { request =>
    CustomActorFlow.actorRefByName(out => UserActor.props(out, name, manager), Some(name))
  }
  def leave(name: String) = TODO
}
