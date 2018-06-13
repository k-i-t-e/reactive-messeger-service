package security

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.Future

class NoopUserDao extends DelegableAuthInfoDAO[PasswordInfo] {
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future.successful(Option.empty)

  override def add(loginInfo: LoginInfo,
                   authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful(null)

  override def update(loginInfo: LoginInfo,
                      authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful(null)

  override def save(loginInfo: LoginInfo,
                    authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful(null)

  override def remove(loginInfo: LoginInfo): Future[Unit] = Future.successful()
}
