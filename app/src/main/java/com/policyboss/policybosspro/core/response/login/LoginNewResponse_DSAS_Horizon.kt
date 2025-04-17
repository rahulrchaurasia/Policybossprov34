package com.policyboss.policybosspro.core.response.login

data class LoginNewResponse_DSAS_Horizon(
    val EMP: EMP ?,
    val POSP: Any? = null,
    val POSP_USER: Any? = null,
    val DEVICE : DEVICE ?,
    val channel: String ?,
    val product: String ?,
    val status: String ?,
    val user_type: String ?,
    val Ss_Id: String ?,
    val SUB_USER : SUB_USER? = null
)

data class EMP(
    var AdharCardNumber:  String="",
    var Alternate_Email:  String="",
    var Alternate_Number: String="",
    var BloodGroup:  String="",
    var Branch: String="",
    var CreatedOn:String="",
    var DOB:  String="",
    var DOJ:  String="",
    var Dailer_Id:  String="",
    var DateOfBirth:  String="",
    var Department_ID:  String="",
    var Designation_ID:  String="",
    var Email_Id: String="",
    var Emp_Code:  String="",
    var Emp_Id:  String="",
    var Emp_Name: String="",
    var Emp_Password: String="",
    var FBA_ID:  String="",
    var IsActive:  String="",
    var IsPasswordChanged:  String="",
    var LastPasswordChangedDate:  String="",
    var Mobile_Number: String="",
    var Pan:  String="",
    var Reporting_Manager_Emp_Id:  String="",
    var Role_ID:  String="",
    var UID:  String="",
    var Updated_On:  String="",
    var VendorCode:  String="",
    var _id: String="",
)

data class POSP(
    var Agent_City: String = "",
    var App_Installed_On: String = "",
    var Channel: String = "",
    var Contact_Sync_On: String = "",
    var Created_On: String = "",
    var Cust_Id:  String="",

    var ERPID_CreatedDate: String =
        "",
    var Email_Id: String="",
    var Erp_Id: String="",
    var FOS_Code: String="",
    var Father_Name: String="",
    var Fba_Id: String?="",
    var First_Name: String="",
    var Gender: String="",
    var IsFOS:  String="",
    var Is_Active: Boolean?,
    var Is_App_Installed:  String="",
    var Is_Contact_Sync:  String="",
    var Last_Name: String="",
    var Middle_Name: String="",
    var Mobile_No: String="",
    var Posp_Id:  String="",
    var Posp_Onboarding_Photo: String="",
    var Recruitment_Status: String="",
    var RegAmount:  String="",
    var Sm_Posp_Id: String="",
    var Sm_Posp_Name: String="",
    var Ss_Id:  String="",
    var Telephone_No: String="",
    var _id: String
)

data class POSP_USER(
    var Name_On_PAN : String = "",

    var User_Id : String = "",

    var  Mobile_No: String = "",
    var Erp_Id: String="",
)

data class SUB_USER(

    val Ss_Id: Long? = null,
    val Sub_FBA_ID: Long? = null,
    val First_Name: String? = null,
    val Last_Name: String? = null,
    val Email_ID: String? = null,
    val Mobile: String? = null
)

data class DEVICE(
    var OS_Detail: String = "",
    var App_Version: String = "",
    var Device_Name: String = "",
    var Device_Identifier: String = "",
    var Created_On: String = "",
    var Activated_On:  String="",
)