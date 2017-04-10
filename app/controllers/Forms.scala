package controllers

import java.sql.Date
import java.text.SimpleDateFormat

import controllers.Application._
import play.Logger
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import net.liftweb.json.{NoTypeHints, Serialization}
import net.liftweb.json.Serialization.write
import net.liftweb.json
import net.liftweb.json._

object Forms extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)
  def forms = Action.async { implicit request =>
    Assets.at(path = "/public", "templates/Index.html").apply(request)
  }

  case class customerDetails(
  firstNameInput:Option[String],
  lastNameInput : Option[String],
  middleNameInput : Option[String],
  addressInput : Option[String],
  cityInput : Option[String],
  stateInput : Option[String],
  zipCodeInput : Option[String],
  phoneNumInput : Option[String],
  emailInput : Option[String],
  dobInput : Option[String],
  ssnInput : Option[String],
  licenseNumInput : Option[String])


  def customerDetailsPost(custDetails:String,dob:String)= SecuredAction { implicit request =>
    val customerJson = net.liftweb.json.parse(custDetails)
    val customerJsonToClass= customerJson.extract[customerDetails]
    var parsedDob = new Date(new SimpleDateFormat("mm-dd-yyyy").parse("1-1-9999").getTime)
    if(dob.length>2)
      parsedDob = new Date(new SimpleDateFormat("mm-dd-yyyy").parse(dob).getTime)

    val customerList:List[Any]=List(
      customerJsonToClass.firstNameInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.lastNameInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.middleNameInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.addressInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.cityInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.stateInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.zipCodeInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.phoneNumInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.emailInput match{case Some(s) => s case None=>"Null"},
      parsedDob,
      customerJsonToClass.ssnInput match{case Some(s) => s case None=>"Null"},
      customerJsonToClass.licenseNumInput match{case Some(s) => s case None=>"Null"}
    )
    var returnString=""
    if(models.Forms.addCustomerDetails(customerList)>0)
      returnString="Success"
    else
      returnString="Fail"

    Ok(write(returnString))
  }

  /* def userInfo() = Action { implicit request =>
     val userEmail = request.user.email.get
     models.User.getUserDataByEmail(userEmail,false) match {
       case Some(data) =>
         Ok(write(data))
       case None =>
         InternalServerError
     }
   }*/


}
