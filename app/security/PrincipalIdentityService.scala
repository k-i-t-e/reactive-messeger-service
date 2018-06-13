package security

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordHasher

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrincipalIdentityService @Inject()(passwordHasher: PasswordHasher)
                                        (implicit ec: ExecutionContext) extends UserDetailsService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[Principal]] = {
    Future.successful(Option(Principal(loginInfo, loginInfo.providerKey, 0, None)))
  }
}
