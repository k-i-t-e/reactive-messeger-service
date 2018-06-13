package security

import com.mohiva.play.silhouette.api.services.IdentityService

trait UserDetailsService extends IdentityService[Principal] {

}
