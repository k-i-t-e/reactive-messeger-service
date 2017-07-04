package controllers

import javax.inject.Inject

import actor.{ChatRoom, PersistActor, UserActor}
import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import manager.MessageManager
import model.{IncomingMessage, Message}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{Controller, WebSocket}
import util.CustomActorFlow

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class ChatController @Inject()(implicit system: ActorSystem, materializer: Materializer, manager: MessageManager) extends Controller {
  private val chatRoom = system.actorOf(Props(new ChatRoom))
  private val persistActor = system.actorOf(Props(new PersistActor(manager)))

  implicit val inEventFormat = Json.format[IncomingMessage]
  implicit val outEventFormat = Json.format[Message]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, Message]

  def join(name: String) = WebSocket.accept[JsValue, Message] { request =>
    CustomActorFlow.actorRefByName(out => UserActor.props(out, chatRoom, persistActor, name), Some(name))
  }
  def leave(name: String) = TODO
}
