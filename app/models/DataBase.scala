package models

import anorm._
import org.joda.time.DateTime
import play.Logger
import play.api.Play.current
import play.api.db.DB

object DataBase {

  def generateCall(data:List[Any],spc:String):String = {
    val call = "call " + spc + " " + data.map(x => {
      x match {
        case s:Int => s
        case s:Any => "'" + s + "'"
      }
    }).toString.substring(4)
    Logger.info(call)
    call
  }
  def readDB(spc:String):Seq[Map[String,Any]] = {
   try {
     DB.withConnection { implicit c =>
       val rows: Seq[Row] = SQL(spc).on().list()
       rows.map (x => x.asMap.toMap[String, Any] map { case (k, v) => (k.substring(k.indexOf(".") + 1), v)})
     }
   }catch {
     case e: Exception =>
       e.printStackTrace(Console.out)
       Seq[Map[String, Any]]()
   }
  }

  def updateDB(spc:String):Int= {
    var result:Int = 0
    DB.withConnection { implicit c =>
      result = SQL(spc).on().executeUpdate()
    }
    result
  }

}
