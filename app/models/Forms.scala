package models

/**
 * Created by bhargav on 3/29/15.
 */
object Forms {

  def addCustomerDetails(customerDetails:List[Any]):Int = {
    DataBase.updateDB(DataBase.generateCall(customerDetails,"sp_insertCustomerDetails"))
  }
}
