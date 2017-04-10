package models

import play.Logger
import securesocial.core.{AuthenticationMethod, PasswordInfo, IdentityId, Identity}
import scala.util.{Failure, Success, Try}

case class User ( data:Map[String,Any]) extends Identity {
  def email = Some(data.get("email").get.asInstanceOf[String])
  def providerId= data.get("dealerCode").get.asInstanceOf[String]
  def emailAddress = data.get("email").get.asInstanceOf[String]
  def identityId  = IdentityId(emailAddress,providerId)
  def firstName:String = data.get("firstName").get match {case Some(value) => value.asInstanceOf[String] ; case None => "NA"}
  def lastName:String = data.get("lastName").get match {case Some(value) => value.asInstanceOf[String] ; case None => "NA"}
  def fullName = firstName +" "+lastName
  val hashFun = data.get("hash").get match {case Some(value) => value.asInstanceOf[String] ; case None => "NA"}
  val password = data.get("password").get match {case Some(value) => value.asInstanceOf[String] ; case None => "NA"}
  val salt:Option[String] = Some(data.get("salt").get match {case Some(value) => value.asInstanceOf[String] ; case None => "NA"})
  def passwordInfo:Option[PasswordInfo] = Some(PasswordInfo (hashFun,password,salt))
  def avatarUrl: Option[String] = None
  def oAuth1Info = None
  def oAuth2Info = None
  def authMethod: AuthenticationMethod =  AuthenticationMethod.UserPassword
}

object User {

  def startEmpSignUp(data:List[Any]):Int = {
    DataBase.updateDB(DataBase.generateCall(data,"sp_StartEmpSignUp"))
  }
  def completeEmpSignUp(data:List[Any]):Int ={
    DataBase.updateDB(DataBase.generateCall(data,"sp_CompleteEmpSignUp"))
  }
  def employeeSignIn(data:List[Any]):Seq[Map[String,Any]] ={
    DataBase.readDB(DataBase.generateCall(data,"sp_EmployeeLogin"))
  }

  def getUserDataByEmail(userId:String):Option[User] ={
      val data = DataBase.readDB(DataBase.generateCall(List(userId), "sp_GetUserDataByEmail"))
      if (data.length > 0) Some(User(data(0))) else  None
  }

  def getUserDataByEmailWp(userId:String):Try[Map[String,Any]] ={
    try {
      Success(DataBase.readDB(DataBase.generateCall(List(userId), "sp_GetUserDataByEmail_wp"))(0))
    }catch{
      case e:Exception =>
        Failure(e)
    }
  }

  def resetPassword(data:List[Any]):Int = {
    DataBase.updateDB(DataBase.generateCall(data, "sp_ResetPassword"))
  }

  def addToken(data:List[Any]):Int = {
    Logger.info(data.toString)
    DataBase.updateDB(DataBase.generateCall(data, "sp_AddToken"))
  }

  def getTokenById(data:List[Any]):Map[String,Any] = {
     val ret = DataBase.readDB(DataBase.generateCall(data, "sp_GetTokenById"))
     if(ret.length == 0)
       null
    else
       ret(0)
  }

  def deleteToken(data:List[Any]):Int = {
    DataBase.updateDB(DataBase.generateCall(data, "sp_DeleteToken"))
  }

  def deleteExpiredTokens():Int = {
    DataBase.updateDB(DataBase.generateCall(List(), "sp_DeleteExpiredTokens"))
  }

  def getActiveUsers:Try[Seq[Map[String,Any]]]={
    try {
      Success(DataBase.readDB(DataBase.generateCall(List(),"sp_GetActiveUsers")))
    }catch {
      case e:Exception =>
        Failure(e)
    }
  }

  def deleteUsersWithExpiredTokens():Int= {
    DataBase.updateDB(DataBase.generateCall(List(), "sp_DeleteUsersWithExpiredTokens"))
  }

  def listCustomer:Try[Seq[Map[String,Any]]]={
    try {
      Success(DataBase.readDB("select * from USER"))
    }catch {
      case e:Exception =>
        Failure(e)
    }
  }
}
