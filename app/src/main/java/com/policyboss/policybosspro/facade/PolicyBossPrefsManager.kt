package com.policyboss.policybosspro.facade

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.policyboss.policybosspro.core.model.notification.NotifyEntity
import com.policyboss.policybosspro.core.response.login.EMP
import com.policyboss.policybosspro.core.response.login.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.core.response.login.OtpLoginMsg
import com.policyboss.policybosspro.core.response.login.POSP
import com.policyboss.policybosspro.core.response.login.SUB_USER
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.Dashboardarray
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantEntity
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PolicyBossPrefsManager @Inject constructor(@ApplicationContext context: Context) {

    private var pref = context.getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = pref.edit()



    private  val gson = Gson()

    companion object {
        const val PREF_NAME = "magic-finmart"
        private const val MOTOR_VERSION = "motor_master_version"
        private const val POPUP_COUNTER = "popup_counter_value"
        private const val POPUP_ID = "popup_id"
        private const val IS_LANGUAGE = "user_language"
        private const val IS_USER_PASSWORD = "user_password"
        private const val NOTIFICATION_TYPE_ENABLE = "NotificationType_Enable"
        private const val MSG_FIRST_CHECK = "msgfirst_check"
        private const val CONTACT_FIRST_CHECK = "Contactfirst_check"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val IS_BIKE_MASTER_UPDATE = "isBikeMasterUpdate"
        private const val IS_CAR_MASTER_UPDATE = "isCarMasterUpdate"
        private const val IS_RTO_MASTER_UPDATE = "isRtoMasterUpdate"
        private const val IS_INSURANCE_MASTER_UPDATE = "isRtoMasterUpdate"
        private const val IS_DEVICE_TOKEN = "devicetoken"
        private const val IS_RBL_CITY_MASTER = "isRblCityMaster"
        private const val IS_EMPLOYER_NAME_MASTER = "employernamemaster"
        private const val IS_ZOHO_MASTER = "iszohomaster"
        private const val POSP_INFO = "pospinfo"
        private const val IS_UPDATE_SHOWN = "updateshown"
        private const val CAR_VEHICLE_NUMBER_LOG = "vehicle_number_log"
        private const val CAR_VEHICLE_MOBILE_LOG = "vehicle_mobile_log"
        private const val FOS_USER_AUTHENTICATIONN = "fos_user_authenticationn"
        const val PUSH_VERIFY_LOGIN = "push_verify_login"
        const val NOTIFICATION_COUNTER = "Notification_Counter"
        const val IS_ENABLE_PRO_POSPURL = "IS_enable_pro_POSPurl"
        const val IS_ENABLE_PRO_ADDSUBUSER_URL = "IS_enable_pro_Addsubuser_url"
        const val SHARED_KEY_PUSH_NOTIFY = "shared_notifyFlag"
        const val SHARED_KEY_PUSH_WEB_URL = "shared_notify_webUrl"
        const val SHARED_KEY_PUSH_WEB_TITLE = "shared_notify_webTitle"
        const val SHARED_KEY_PUSH_BODY = "shared_notify_Title"
        const val SHARED_KEY_PUSH_TITLE = "shared_notify_Body"
        const val PUSH_NOTIFICATION = "push_notifyication_data"

        private const val DeepLink = "DeepLink"
        const val MPS_DATA = "mps_data"
        private const val MENU_DASHBOARD = "menu_dashboard"
        private const val CONTACT_COUNT = "contact_count"
        private const val DEVICE_ID = "policybossproDeviceID"
        private const val APP_VERSION = "policybossproAppVersion"


        private val LoginHorizonKey = "LOGIN_DSAS_Horizon"
        private val LoginOTPDataKey = "Login_OTP_Data_Key"

        private val IS_DEVICE_TOKEN_Login = "devicetokenLogin"

        private val IS_DEVICE_ID = "deviceid"
        private val IS_DEVICE_Name = "devicename"

        private const val device_ID = "policybossproDeviceID"
        private const val app_Version = "policybossproAppVersion"

        private const val USER_CONSTANT_RESPONSE_KEY = "UserConstantResponseKey"
    }





    fun setLanguage(language: String) {
        editor.putString(IS_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return pref.getString(IS_LANGUAGE, "")
    }

    fun setUserPassword(pwd: String) {
        editor.putString(IS_USER_PASSWORD, pwd).apply()
    }

    fun getUserPassword(): String? {
        return pref.getString(IS_USER_PASSWORD, "")
    }

    // Vehicle Detail Methods
    fun setVehicleCarVehicleLog(): Boolean {
        editor.putInt(CAR_VEHICLE_NUMBER_LOG, pref.getInt(CAR_VEHICLE_NUMBER_LOG, 0) + 1)
        return editor.commit()
    }

    fun getVehicleCarVehicleLog(): Int {
        return pref.getInt(CAR_VEHICLE_NUMBER_LOG, 0)
    }

    fun setVehicleCarMobileLog(): Boolean {
        editor.putInt(CAR_VEHICLE_MOBILE_LOG, pref.getInt(CAR_VEHICLE_MOBILE_LOG, 0) + 1)
        return editor.commit()
    }

    fun getVehicleCarMobileLog(): Int {
        return pref.getInt(CAR_VEHICLE_MOBILE_LOG, 0)
    }

    // MPS Data Methods

    // Update Status Methods
    fun setIsUpdateShown(isFirstTime: Boolean) {
        editor.putBoolean(IS_UPDATE_SHOWN, isFirstTime).apply()
    }

    fun getUpdateShown(): Boolean {
        return pref.getBoolean(IS_UPDATE_SHOWN, true)
    }

    fun setIsZohoMaster(isFirstTime: Boolean) {
        editor.putBoolean(IS_ZOHO_MASTER, isFirstTime).apply()
    }

    fun getIsZohoMaster(): Boolean {
        return pref.getBoolean(IS_ZOHO_MASTER, true)
    }

    fun setIsRblCityMaster(isFirstTime: Boolean) {
        editor.putBoolean(IS_RBL_CITY_MASTER, isFirstTime).apply()
    }

    fun getIsRblCityMaster(): Boolean {
        return pref.getBoolean(IS_RBL_CITY_MASTER, true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }

    fun setIsCarMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_CAR_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isCarMasterUpdate(): Boolean {
        return pref.getBoolean(IS_CAR_MASTER_UPDATE, true)
    }

    fun setIsBikeMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_BIKE_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isBikeMasterUpdate(): Boolean {
        return pref.getBoolean(IS_BIKE_MASTER_UPDATE, true)
    }

    fun setIsEmployerNameUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_EMPLOYER_NAME_MASTER, isFirstTime).apply()
    }

    fun isEmployerNameUpdate(): Boolean {
        return pref.getBoolean(IS_EMPLOYER_NAME_MASTER, true)
    }

    fun setIsRtoMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_RTO_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isRtoMasterUpdate(): Boolean {
        return pref.getBoolean(IS_RTO_MASTER_UPDATE, true)
    }

    fun setIsInsuranceMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_INSURANCE_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isInsuranceMasterUpdate(): Boolean {
        return pref.getBoolean(IS_INSURANCE_MASTER_UPDATE, true)
    }

    fun setFosUser(strData: String) {
        editor.putString(FOS_USER_AUTHENTICATIONN, strData).apply()
    }

    fun getFosUser(): String? {
        return pref.getString(FOS_USER_AUTHENTICATIONN, "")
    }



    fun getNotificationCounter(): Int {
        return pref.getInt(NOTIFICATION_COUNTER, 0)
    }

    fun setNotificationCounter(counter: Int) {
        editor.putInt(NOTIFICATION_COUNTER, counter).apply()
    }


    fun setPushNotifyPreference(notifyEntity: NotifyEntity) {
        try {
            editor.putString(Constant.PUSH_NOTITIFICATION, gson.toJson(notifyEntity))
            editor.apply() // Use apply for async saving
        } catch (e: Exception) {
            e.printStackTrace() // Consider logging or handling the exception
        }
    }

    fun setSharePushType(type: String) {
        editor.putString(Constant.SHARED_KEY_PUSH_NOTIFY, type)
        editor.apply() // Use apply for async saving
    }

    fun getPushNotifyPreference(): NotifyEntity? {
        val pushKey = pref.getString(Constant.PUSH_NOTITIFICATION, null) ?: return null
        return gson.fromJson(pushKey, NotifyEntity::class.java)
    }

    fun clearNotification() {
        editor.remove(Constant.SHARED_KEY_PUSH_NOTIFY)
            .remove(Constant.PUSH_NOTITIFICATION)
            .apply() // Use apply for async saving
    }


    fun setIsUserLogin(isUserLogin: Boolean) {
        editor.putBoolean(PUSH_VERIFY_LOGIN, isUserLogin).apply()
    }

    fun isUserLogin(): Boolean {
        return pref.getBoolean(PUSH_VERIFY_LOGIN, false)
    }


    fun setEnableProAddSubUserUrl(proSignupUrl: String) {
        editor.putString(IS_ENABLE_PRO_ADDSUBUSER_URL, proSignupUrl).apply()
    }

    fun getEnableProAddSubUserUrl(): String? {
        return pref.getString(IS_ENABLE_PRO_ADDSUBUSER_URL, "")
    }

    // Notification Methods

    fun setEnableProPOSPurl(proSignupUrl: String) {
        editor.putString(IS_ENABLE_PRO_POSPURL, proSignupUrl)
        editor.apply()  // Use apply() instead of commit() for asynchronous saving
    }

    // Getter for EnableProPOSPurl
    fun getEnableProPOSPurl(): String {
        return pref.getString(IS_ENABLE_PRO_POSPURL, "") ?: ""
    }


    //region Deeplink
    fun setDeeplink(strDeepLink: String): Boolean {
        editor.remove(DeepLink).apply()
        return editor.putString(DeepLink, strDeepLink).commit()
    }

    fun getDeepLink(): String? {
        return pref.getString(DeepLink, "")
    }

    fun clearDeeplink() {
        pref.edit().remove(DeepLink)
            .commit()
    }

    //endregion


    //region Important all API data  //05
    fun saveLoginHorizonResponse(  loginHorizon : LoginNewResponse_DSAS_Horizon?){

        loginHorizon?.let { response ->

            val json = gson.toJson(response)
            pref.edit().putString(LoginHorizonKey, json).apply()

            Log.d(Constant.TAG, " Horizon Respnse : LoginNewResponse_DSAS_Horizon response saved")

        }


    }

    fun getLoginHorizonResponse() : LoginNewResponse_DSAS_Horizon?  {

        val LoginResponse = pref.getString(LoginHorizonKey,null)

        return gson.fromJson(LoginResponse,LoginNewResponse_DSAS_Horizon::class.java )
    }


    fun getEmpData() : EMP? {

        val response = getLoginHorizonResponse()
        return response?.EMP
    }

  // 16333
    fun getSSID() : String {

        val response = getLoginHorizonResponse()

        return response?.Ss_Id?:"0"

    }



    fun getPOSPNo() : String {

        val response = getLoginHorizonResponse()

        return response?.Ss_Id?:"0"
    }

    //005 temp
    // region Mark : Added :SUB_USER object field to add in webView url
    private fun getSUBUser() : SUB_USER? {

        val response = getLoginHorizonResponse()

        return response?.SUB_USER
    }

     //SubUser SUB_SSID
    fun getSUBUserSSId(): String {

         val response = getLoginHorizonResponse()
         val userType = response?.user_type ?: ""


         return if (userType == "POSP") {
             getSUBUser()?.Ss_Id?.toString() ?: "0"
         } else {
             "0"
         }


    }

    //SubUser SUB_FBAID
    fun getSUBUserFBAID(): String {


        val response = getLoginHorizonResponse()
        val userType = response?.user_type ?: ""


        return if (userType == "POSP") {
            getSUBUser()?.Sub_FBA_ID?.toString() ?: "0"
        } else {
            "0"
        }

    }
    //endregion





    fun getFBAID(): String {
        val response = getLoginHorizonResponse()
        val userType = response?.user_type ?: ""

        return when (userType) {
            "POSP", "FOS" -> {
                // Assuming POSP can be a Map here
                val posp = response?.POSP
                if (posp is Map<*, *>) {
                    (posp["Fba_Id"] as? String)?.takeIf { it.isNotEmpty() } ?: "0"
                } else {
                    "0"
                }
            }
            "EMP", "MISP" -> response?.EMP?.FBA_ID?.toString() ?: "0"
            else -> "0"
        }
    }



    fun  getERPID() : String {

        val response = getLoginHorizonResponse()

        val erpIDID: String? = when (val obj = response?.POSP_USER) {
            is Map<*, *> -> {
                // Assume it's a Map, you can adjust this based on your actual JSON structure
                (obj["Erp_Id"] as? String) ?:"0"
            }
            else -> {

                ""
            }
        }

        Log.d("User Email ID.",erpIDID?:"0")
        return  erpIDID?:"0"

    }


    fun getName() : String {

        val response = getLoginHorizonResponse()

        val usertype= response?.user_type?:""

        when(usertype){


            "POSP" , "FOS" ->{

                val username: String? = when (val obj = response?.POSP_USER) {
                    is Map<*, *> -> {
                        // Assume it's a Map, you can adjust this based on your actual JSON structure
                        (obj["Name_On_PAN"] as? String)?.takeIf { it.isNotEmpty() } ?:
                        response?.EMP?.Emp_Name?:""
                    }
                    else -> {

                        ""
                    }
                }
                Log.d("User Name.",username?:"")
                return  username?:""

            }


            "EMP" ->{
                return response?.EMP?.Emp_Name?:""
            }
            "MISP" ->{
                return response?.EMP?.Emp_Name?:""
            }

        }

        return ""
    }




    fun getMobileNo() : String {

        val response = getLoginHorizonResponse()

        val usertype= response?.user_type?:""

        when(usertype){


            "POSP" , "FOS" ->{


                val mobileNo: String? = when (val obj = response?.POSP_USER) {
                    is Map<*, *> -> {
                        // Assume it's a Map, you can adjust this based on your actual JSON structure
                        (obj["Mobile_No"] as? String)?.takeIf { it.isNotEmpty() } ?:""
                    }
                    else -> {

                        ""
                    }
                }

                Log.d("MOBILE NO.",mobileNo?:"")
                return  mobileNo?:""

            }


            "EMP" ->{
                return response?.EMP?.Mobile_Number?:"0"
            }
            "MISP" ->{
                return response?.EMP?.Mobile_Number?:"0"
            }

        }

        return "0"
    }

    fun getEmailId() : String {

        val response = getLoginHorizonResponse()

        val usertype= response?.user_type?:""

        when(usertype){


            "POSP" , "FOS" ->{

                val emailID: String? = when (val obj = response?.POSP_USER) {
                    is Map<*, *> -> {
                        // Assume it's a Map, you can adjust this based on your actual JSON structure
                        (obj["Email_Id"] as? String)?.takeIf { it.isNotEmpty() } ?:""
                    }
                    else -> {

                        ""
                    }
                }

                Log.d("User Email ID.",emailID?:"")
                return  emailID?:""

            }


            "EMP" ->{
                return response?.EMP?.Email_Id?:"0"
            }
            "MISP" ->{
                return response?.EMP?.Email_Id?:"0"
            }

        }

        return ""
    }

    fun getUserType():String{


        val response = getLoginHorizonResponse()
        //  val usertype= response?.user_type?:""

        return response?.user_type?:""

    }
    fun getUserId() : String {

        val response = getLoginHorizonResponse()

        val usertype= response?.user_type?:""

        when(usertype){


            "POSP" ->{
                return response?.EMP?.UID?:"0"
            }
            "FOS" ->{
                return response?.EMP?.UID?:"0"
            }

            "EMP" ->{
                return response?.EMP?.UID?:"0"
            }
            "MISP" ->{
                return response?.EMP?.UID?:"0"
            }

        }

        return "0"
    }
    fun getappVersionHorizon() : String {

        val response = getLoginHorizonResponse()

        return response?.DEVICE?.App_Version?:""
    }

    fun getdeviceIDHorizon() : String {

        val response = getLoginHorizonResponse()

        return response?.DEVICE?.Device_Identifier?:""
    }



    fun saveLoginOTPResponse(  loginOTP : OtpLoginMsg?){

        loginOTP?.let { response ->

            val json = gson.toJson(response)
            pref.edit().putString(LoginOTPDataKey, json).apply()

        }


    }

    fun getLoginOTPResponse() : OtpLoginMsg?  {

        val loginOTP = pref.getString(LoginOTPDataKey,null)

        return gson.fromJson(loginOTP,OtpLoginMsg::class.java )
    }

    fun getSSIDByOTP() : String {

        val response = getLoginOTPResponse()

        return response?.Ss_Id?.toString() ?:"0"
    }

    fun setToken(token: String) {
        pref.edit()
            .putString(IS_DEVICE_TOKEN_Login, token)
            .apply()
    }

    fun getToken(): String {
        return pref.getString(IS_DEVICE_TOKEN_Login, "") ?: ""
    }

//    fun setDEVICE_ID(token: String) {
//        pref.edit()
//            .putString(IS_DEVICE_ID, token)
//            .apply()
//    }
//
//    fun getDEVICE_ID(): String {
//        return pref.getString(IS_DEVICE_ID, "") ?: ""
//    }

    fun setDEVICE_NAME(token: String) {
        pref.edit()
            .putString(IS_DEVICE_Name, token)
            .apply()
    }

    fun getDEVICE_NAME(): String {
        return pref.getString(IS_DEVICE_Name, "") ?: ""
    }

    // Device ID
    fun setDeviceID(deviceID: String) {
        editor.putString(DEVICE_ID, deviceID).apply()
    }

    fun getDeviceID(): String {
        return pref.getString(DEVICE_ID, "") ?: ""
    }

    // App Version
    fun setAppVersion(appVersion: String) {
        editor.putString(APP_VERSION, appVersion).apply()
    }

    fun getAppVersion(): String {
        return pref.getString(APP_VERSION, "") ?: ""
    }




    /////

    fun setEnablePro_ADDSUBUSERurl(ProSignupurl: String) {
        editor.putString(IS_ENABLE_PRO_ADDSUBUSER_URL, ProSignupurl)
        editor.apply()
    }

    fun getEnablePro_ADDSUBUSERurl(): String {
        return pref.getString(IS_ENABLE_PRO_ADDSUBUSER_URL, "") ?: ""
    }



    //endregion

    // region UserConstant -For DashUrl,private car, two Wheeler etc

    fun saveUserConstantResponse(userConstantResponse: UserConstantResponse) {
        val json = gson.toJson(userConstantResponse)
        editor.putString(USER_CONSTANT_RESPONSE_KEY, json)
        editor.apply() // Commit changes
    }

    // Retrieve UserConstantResponse from SharedPreferences
    fun getUserConstantResponse(): UserConstantResponse? {
        val json = pref.getString(USER_CONSTANT_RESPONSE_KEY, null)
        return if (json != null) {
            gson.fromJson(json, UserConstantResponse::class.java) // Convert JSON string back to UserConstantResponse
        } else {
            null // Return null if no data is found
        }
    }

    fun getUserConstantEntity(): UserConstantEntity? {
       return getUserConstantResponse()?.MasterData
    }
    fun getDashboardarray(): List<Dashboardarray> {

        return   getUserConstantResponse()?.MasterData?.dashboardarray?: emptyList()
    }

    fun getAndroidProVersion(): Long {

        return   (getUserConstantResponse()?.MasterData?.androidproversion?:"0").toLong()
    }
    fun getLeadDashUrl(): String {

     return   getUserConstantResponse()?.MasterData?.LeadDashUrl?:""
    }
    fun getRaiseTickitUrl(): String {

        return   getUserConstantResponse()?.MasterData?.RaiseTickitUrl?:""
    }

    //005 temp
    fun getFourWheelerUrl(): String {

        //return   getUserConstantResponse()?.MasterData?.FourWheelerUrl?:""

        val url = getUserConstantResponse()?.MasterData?.FourWheelerUrl ?: ""
        return url.replace("&sub_fba_id=0", "")
    }

    fun getTwoWheelerUrl(): String {

        //return   getUserConstantResponse()?.MasterData?.TwoWheelerUrl?:""

        val url = getUserConstantResponse()?.MasterData?.TwoWheelerUrl?:""
        return url.replace("&sub_fba_id=0", "")
    }

    fun getCVUrl(): String {

       // return   getUserConstantResponse()?.MasterData?.CVUrl?:""

        val url = getUserConstantResponse()?.MasterData?.CVUrl?:""
        return url.replace("&sub_fba_id=0", "")
    }

    fun getHealthurl(): String {

       // return   getUserConstantResponse()?.MasterData?.healthurl?:""

        val url = getUserConstantResponse()?.MasterData?.healthurl?:""
        return url.replace("&sub_fba_id=0", "")
    }

    fun getInvestmentEnabled(): String {

        return   getUserConstantResponse()?.MasterData?.InvestmentEnabled?:""
    }

    fun getInvestmentUrl(): String {

        return   getUserConstantResponse()?.MasterData?.InvestmentUrl?:""
    }

    fun getFinboxurl(): String {

        return   getUserConstantResponse()?.MasterData?.finboxurl?:""
    }

    fun getFinperkurl(): String {

        return   getUserConstantResponse()?.MasterData?.finperkurl?:""
    }

    fun getNotif_popupurl_elite(): String {

        return   getUserConstantResponse()?.MasterData?.notif_popupurl_elite?:""
    }



    //endregion


    //region Store Menu Dashboard in SharedPreferences
    fun storeMenuDashboard(menuMasterResponse: MenuMasterResponse): Boolean {
        return try {
            editor.apply {
                remove(MENU_DASHBOARD)
                putString(MENU_DASHBOARD, gson.toJson(menuMasterResponse))
            }.commit() // commit() writes changes synchronously
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Retrieve Menu Dashboard from SharedPreferences
    fun getMenuDashBoard(): MenuMasterResponse? {
        val json = pref.getString(MENU_DASHBOARD, "")
        return if (!json.isNullOrEmpty()) {
            try {
                gson.fromJson(json, MenuMasterResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    //endregion

    //Not in Used
    fun clear() {
        val strToken = getToken()
        pref.edit().clear().apply()
        setToken(strToken)
    }


    //Mark : get Token and getContactMsgFirst before clear data and set it again
    fun clearAll() {
        val strToken = getToken()
        val strContact = getContactMsgFirst()

        editor.clear().apply()

        setToken(strToken)
        updateContactMsgFirst(strContact)

    }

    // Example of getToken()


    // Example of setToken()


    // Example of getContactMsgFirst()
    fun getContactMsgFirst(): String? {
        return pref.getString("contact_msg_key", null)
    }

    // Example of updateContactMsgFirst()
    fun updateContactMsgFirst(contactMsg: String?) {
        editor.putString("contact_msg_key", contactMsg).apply()
    }

}