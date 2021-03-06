package security

import javax.inject.Named

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.EnumerationReader._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Creepy stuff to make Silhouette working. Declares all the required dependencies and binds them together
  */
class SilhouetteModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {
  override def configure() = {
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[NoopUserDao]
    bind[UserDetailsService].to[PrincipalIdentityService]

    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /** HTTP layer implementation.
    */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /** Silhouette environment.
    */
  @Provides
  def provideEnvironment(
                          userService: UserDetailsService,
                          authenticatorService: AuthenticatorService[JWTAuthenticator],
                          eventBus: EventBus): Environment[DefaultEnv] = {

    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /** Crypter for the authenticator.
    */
  @Provides @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {

    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.jwt.authenticator.crypter")
    new JcaCrypter(config)
  }

  /** Auth info repository.
    */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /** Authenticator service.
    */
  @Provides
  def provideJwtAuthenticatorService(
                                      @Named("authenticator-crypter") crypter: Crypter,
                                      idGenerator: IDGenerator,
                                      configuration: Configuration,
                                      clock: Clock): AuthenticatorService[JWTAuthenticator] = {

    val config = configuration.underlying.as[JWTAuthenticatorSettings]("silhouette.jwt.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)
    new JWTAuthenticatorService(config, None, encoder, idGenerator, clock)
  }

  /** Password hasher registry.
    */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }

  /** Credentials provider.
    */
  @Provides
  def provideCredentialsProvider(
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }
}
