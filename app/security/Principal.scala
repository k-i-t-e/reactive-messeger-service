package security

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.api.util.PasswordInfo

case class Principal(loginInfo: LoginInfo,
                     userName: String,
                     userId: Long,
                     password: Option[PasswordInfo]) extends Identity

