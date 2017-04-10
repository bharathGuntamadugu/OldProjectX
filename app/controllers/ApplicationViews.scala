package controllers

import securesocial.controllers.TemplatesPlugin
import play.api.mvc.{Flash, RequestHeader, Request}
import play.api.templates.{Txt, Html}
import play.api.data.Form
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.core.{Identity, SecuredRequest}
import securesocial.controllers.PasswordChange.ChangeInfo

 class ApplicationViews(application:play.api.Application) extends TemplatesPlugin{

  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                               msg: Option[String] = None): Html =
  {
    implicit val flashing: Flash = request.flash
    views.html.custom.login(form, msg)
  }

   override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
     views.html.custom.registration.signUp(form, token)
   }

   override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
     views.html.custom.registration.startSignUp(form)
   }

   override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
     views.html.custom.registration.startResetPassword(form)
   }

   def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
     views.html.custom.registration.resetPasswordPage(form, token)
   }

   def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]):Html = {
     views.html.custom.passwordChange(form)
   }

   def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
     views.html.custom.notAuthorized()
   }

   def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.signUpEmail(token).body)))
   }

   def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.alreadyRegisteredEmail(user).body)))
   }

   def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.welcomeEmail(user).body)))
   }

   def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.unknownEmailNotice(request).body)))
   }

   def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.passwordResetEmail(user, token).body)))
   }

   def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
     (None, Some(Html(views.html.custom.mails.passwordChangedNotice(user).body)))
   }

}
