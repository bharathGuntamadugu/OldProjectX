package controllers
import net.liftweb.json.{NoTypeHints, Serialization}
import net.liftweb.json.Serialization.write
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import securesocial.controllers.Registration
import securesocial.core.PasswordInfo
import securesocial.core.providers.Token
import scala.util.{Failure, Success}
import java.util.UUID


object Application extends Controller with securesocial.core.SecureSocial {
  implicit val formats = Serialization.formats(NoTypeHints)

  def index = SecuredAction.async { implicit request =>
    Assets.at(path = "/public", "templates/Index.html").apply(request)
  }


  def getUserInfo = SecuredAction { implicit request =>
    val userEmail = request.user.email.get
    models.User.getUserDataByEmailWp(userEmail) match {
      case Success(data) => {
        Ok(write(data))
      }
      case Failure(ex) =>
        InternalServerError
    }
  }

  def getActiveUsers = SecuredAction {implicit request =>
    models.User.getActiveUsers match {
      case Success(data) =>
        Ok(write(data))
      case Failure(ex) =>
        InternalServerError
    }
  }

  def inviteNewUser(userEmail:String,userRole:String) = SecuredAction {implicit request =>
    val adminEmail = request.user.email.get
    models.User.getUserDataByEmail(userEmail) match {
      case Some(user) =>
        Ok(write(Map("status" -> "Exists")))
      case None => {
        models.User.startEmpSignUp(List(adminEmail, userEmail, userRole))
        val (uuid,token) = createToken(userEmail, isSignUp = true)
        securesocial.core.providers.utils.Mailer.sendSignUpEmail(userEmail, uuid)
        models.User.addToken(
          List(token.uuid,
            token.email,
            token.isSignUp,
            token.creationTime.toLocalDateTime,
            token.expirationTime.toLocalDateTime,
            token.isExpired)
        )
        Ok(write(Map("status" -> "Success")))
      }
    }
  }

  def changePassword(cp:String, np:String) = SecuredAction { implicit request =>
    val id = "bcrypt"
    if(securesocial.controllers.PasswordChange.checkCurrentPassword(cp)) {
      val newPasswordInfo = PasswordInfo(id, BCrypt.hashpw(np, BCrypt.gensalt(10)))
      val ret = models.User.resetPassword(List(request.user.email.get, newPasswordInfo.password, newPasswordInfo.salt, newPasswordInfo.hasher))
      if(ret >0 ) Ok(write(Map("status" -> "Success"))) else Ok(write(Map("status" -> "Error")))
    }else{
      Ok(write(Map("status" -> "Failure")))
    }
  }

  def fetchPdfForm() = Action(parse.tolerantText) {implicit request =>
    val dataMap:Map[String,String] = parseFDF(request.body)
    println(dataMap)
    Ok("")
  }

  private def createToken(email: String, isSignUp: Boolean): (String, Token) = {
    val uuid = UUID.randomUUID().toString
    val now = DateTime.now
    val token = Token(
      uuid, email,
      now,
      now.plusMinutes(Registration.TokenDuration),
      isSignUp = isSignUp)
      (uuid, token)
  }

  private def parseFDF(textBody:String):Map[String,String] ={
    val regexValues = "/V\\((\\w+)\\)".r
    val regexKeys = "/T\\((\\w+)\\)".r
    val keys = regexKeys.findAllIn(textBody).toList.map(x => {val y = x.replaceAll("/T[(]",""); y.replaceAll("[)]","")})
    val values = regexValues.findAllIn(textBody).toList.map(x => {val y = x.replaceAll("/V[(]",""); y.replaceAll("[)]","")})
    keys zip values toMap
  }
}

