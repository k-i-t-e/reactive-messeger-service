package controllers

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import security.DefaultEnv

import scala.concurrent.Future

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) {
  def check = silhouette.SecuredAction.async {
    implicit request => {
      Future.successful(Ok(Json.toJson(request.identity.userName)))
    }
  }
}
