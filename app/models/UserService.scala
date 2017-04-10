package models

import org.joda.time.DateTime
import play.api.{Logger, Application}
import play.api.cache.Cache
import securesocial.core.{AuthenticationMethod, Identity, IdentityId, UserServicePlugin}
import securesocial.core.providers.Token
import play.api.Play.current

case class UserService (application :Application) extends UserServicePlugin(application) {

  def find(id: IdentityId):Option[Identity] = {
    getUserDataByEmailAndProvider (id.userId , id.providerId)
  }

  def findByEmailAndProvider(email: String, providerId: String):Option[Identity] =
  {
    getUserDataByEmailAndProvider (email, providerId)
  }

  def save(user: Identity): Identity = {
    val userEmail = user.email.get
    val currentUser = getUserDataByEmailAndProvider(userEmail, user.identityId.providerId)
    val passwordInfo = user.passwordInfo.get
    if (currentUser != None) {
    if (user.authMethod == AuthenticationMethod.UserPassword) {
      val userDBInfo = currentUser.get
      if (userDBInfo.passwordInfo.get != user.passwordInfo.get) {
        models.User.resetPassword(List(userEmail, passwordInfo.password, passwordInfo.salt, passwordInfo.hasher))
        val updated = models.User.getUserDataByEmail(userEmail).get
        Cache.remove(updated.emailAddress + updated.providerId)
        updated
      } else {
        userDBInfo
      }
    } else {
      Logger.error("AuthMethod is not UserPassword")
      throw new RuntimeException("Internal error")
    }
  } else {
        models.User.completeEmpSignUp(List(user.firstName,user.lastName,userEmail,passwordInfo.password, passwordInfo.salt, passwordInfo.hasher))
    }
    user
  }

  def save(token:Token) = {
      models.User.addToken(
        List(token.uuid,
        token.email,
        token.isSignUp,
        token.creationTime.toLocalDateTime,
        token.expirationTime.toLocalDateTime,
        token.isExpired)
      )
  }

  def findToken(token: String): Option[Token] = {
    val tokenInfo = models.User.getTokenById(List(token))
    if(tokenInfo != null) {
      Some(Token(
        tokenInfo.get("id").get.asInstanceOf[String],
        tokenInfo.get("email") match {
          case None => ""
          case Some(value) => value.asInstanceOf[String]
        },
        tokenInfo.get("creationTime") match {
          case Some(Some(value)) => new DateTime(value.asInstanceOf[java.sql.Timestamp])
          case _ => null
        },
        tokenInfo.get("expirationTime") match {
          case Some(Some(value)) => new DateTime(value.asInstanceOf[java.sql.Timestamp])
          case _ => null
        },
        tokenInfo.get("isSignUp") match {
          case Some(Some(value)) => value.asInstanceOf[String].equals("true")
          case _ => false
        }
      ))
    } else {
      None
    }
  }

  def deleteToken(uuid: String) {
    models.User.deleteToken(List(uuid))
  }

  def deleteExpiredTokens() = {
    models.User.deleteUsersWithExpiredTokens()
    models.User.deleteExpiredTokens()
  }

  def getUserDataByEmailAndProvider (id:String, providerId:String):Option[Identity] = {
    val user = Cache.getAs[User](id + providerId)
    if(user.isDefined) user else User.getUserDataByEmail(id)
  }
}
