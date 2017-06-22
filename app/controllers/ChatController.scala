package controllers

import javax.inject.Inject

import actor.{ChatRoom, PersistActor, UserActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import manager.MessageManager
import model.{IncomingMessage, Message}
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, WebSocket}
import play.api.mvc.WebSocket.{FrameFormatter, MessageFlowTransformer}

/**
  * Created by Mikhail_Miroliubov on 5/29/2017.
  */
class ChatController @Inject()(implicit system: ActorSystem, materializer: Materializer, manager: MessageManager) extends Controller {
  private val chatRoom = system.actorOf(Props(new ChatRoom))
  private val persistActor = system.actorOf(Props(new PersistActor(manager)))

  implicit val inEventFormat = Json.format[IncomingMessage]
  implicit val outEventFormat = Json.format[Message]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[IncomingMessage, Message]

  def join(name: String) = WebSocket.accept[IncomingMessage, Message] { request =>
    ActorFlow.actorRef(out => UserActor.props(out, chatRoom, persistActor, name))
  }
  def leave(name: String) = TODO
}
