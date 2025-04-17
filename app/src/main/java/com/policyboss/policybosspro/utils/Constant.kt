package com.policyboss.policybosspro.utils


var TAG = "ATMGO"


object Constant {



    const val base_url = "https://horizon.policyboss.com:5443"
    const val SHARED_PREF = "policybosspro_preference"
    const val token = "1234567890"
    const val TAG = "POLICYBOSS"

    const val enable_pro_signupurl	=	"https://www.policyboss.com/posp/registration?v=240709"
    const val enable_pro_pospurl	=	"https://www.policyboss.com/posp-form?product_id=1&ClientID=2&v=20231102"

    // production url
    //public static String URL = "https://horizon.policyboss.com:5443";
    // Test Environment url
    //   public static String URL = "https://qa.mgfm.in";
    //UAT
    //public static String URL = "https://uat.mgfm.in";


    val URL : String = "URL"
    const val DEFAULT = "-"

    val ErrorMessage : String = "Error occurred,Please try Again!"
    val Fail : String = "Failed"

    const val   NOData = "No Data Found"
    const val   MasterData = "Failed to fetch data! Please retry again"
    const val   SeverUnavaiable = "Server temporarily unavailable! Please retry again"
    const val   InValidPass = "Invalid UserId and Password"
    const val   ServerError = "Service Unavailable : No Data Found"
    const val   ServerNotFound = "Service Not Found "
    const val   InValidOTP = "Invalid OTP"


    const val KEY_result = "op_result"
    const val KEY_Max_Progress_result = "op_Max_Progress_result"
    const val KEY_error_result = "op_error_result"
    const val KEY_fbaid : String = "fbaid"

    const val KEY_sub_fba_id : String = "sub_fba_id"

    const val KEY_ssid = "ssid"

    const val KEY_parentid = "parentid"

    const val KEY_deviceid = "device_id"
    const val KEY_appversion = "appversion"

    const val CALL_LOG_Progress = "CALL_LOGProgress"
    const val CALL_LOG_MAXProgress = "CALL_LOGMAXProgress"

    const val CONTACT_LOG_Data_SIZE = "CONTACT_LOG_Data_SIZE"
    const val CONTACT_LOG_Progress = "CONTACT_LOGGProgress"
    const val CONTACT_LOG_MAXProgress = "CONTACT_LOGMAXProgress"
    const val REQUEST_CODE = 22

    const val APP_DATABASE = "atmmgo_database"
    const val USER_TABLE = "user_table"
    const val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    const val INITIAL_PIN = "register_initial_pin"
    const val LoginData = "atmgo_User_data"

    const val PIN_DATA = "pin_data"

    const val MERCHANT_DATA = "merchant_data"

    const val EmptyResponse = "Empty response body"
    const val StatusMessage =  "Network request failed with status"
    var SuccessMessage = "Success"

    const val today = "Today"
    const val yesterday = "Yesterday"
    const val thisWeek = "This Week"
    const val lastWeek = "Last Week"
    const val thisMonth = "This Month"
    const val lastMonth = "Last Month"
    const val SHARE_WHATSAPP = "sharewhatsapp"

    const val  PRODUCT_ID = "salesProductID"

    const val  Deeplink_PRODUCT_ID = "deeplinksalesProductID"
    const val DOC_DATA = "docData"
    const val POSP_IMAGE = "POSPIMAGE"
    const val FBA_IMAGE = "FBAIMAGE"

    const val SalesLangEnglish = "English"
    const val SalesLangHindi = "Hindi"
    const val  SalesTitle = "CUSTOMER COMM."
    const val  NotifyTitle = "NOTIFICATION"

    const val  ContactUsTitle = "CONTACT US"

    const val PUSH_NOTIFY = "notifyFlag"
    const val   NOTIFICATION_EXTRA = "NOTIFICATION_EXTRA"
    const val   NOTIFICATION_PROGRESS = "NOTIFICATION_PROGRESS"
    const val   NOTIFICATION_MAX = "NOTIFICATION_MAX"
    const val   NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE"
    const val SHARED_KEY_PUSH_NOTIFY = "SHARED_KEY_PUSH_NOTIFY"

    const val   PUSH_LOGIN_PAGE = "pushloginPage"

    const val PUSH_NOTITIFICATION = "demoAndroidApp_notification"

    const val   NOTIFICATION_RECEIVERNAME = "receiverName"

    const val CONTACT_LOG_DataFetching = "Data is Fetching Please Wait..."
    const val CONTACT_LOG_DataSending = "Sending Data to Server..."







    const val PERMISSION_CAMERA_STORAGE_CONSTANT = 103
    const val PERMISSION_STORAGE_CONSTANT = 104

    const val REQUEST_PERMISSION_SETTING = 101
    const val CROP_IMAGE_REQUEST_CODE = 105


    const val   TAG_SAVING_CALL_LOG = "SAVING_CALL_LOG"
    const val   TAG_SAVING_CONTACT_LOG = "SAVING_CONTACT_LOG"
    const val   TAG_SAVING_CONTACT_PHOTO_LOG = "SAVING_CONTACT_PHOTO_LOG"

    const val  ProfileSaving = "SAVING"
    const val  ProfileCurrent = "CURRENT"

    const val ULTRA_LAKSHA = "ULTRA_LAKSHA"


    const val INSURANCE_TYPE = "INSURANCE"

    const val PRIVATE_CAR = "MOTOR INSURANCE"
    const val PRIVATE_CAR_REQUEST = "MOTOR REQUEST"
    const val PRIVATE_CAR_RESPONSE = "MOTOR RESPONSE"
    const val PRIVATE_CAR_FASTLANE_RESPONSE = "MOTOR FASTLANE RESPONSE"
    const val TWO_WHEELER = "TWO WHEELER INSURANCE"
    const val TWO_WHEELER_REQUEST = "TWO WHEELER REQUEST"
    const val TWO_WHEELER_RESPONSE = "TWO WHEELER RESPONSE"
    const val TWO_WHEELER_FASTLANE_RESPONSE = "TWO WHEELER FASTLANE RESPONSE"
    const val FASTLANE = "FASTLANE"
    const val HEALTH_INS = "HEALTH INSURANCE"
    const val SyncContacts = "Sync Contacts"
    const val HEALTH_INS_OFF = "HEALTH INSURANCE_OFFLINE"
    const val LIFE_INS = "TERM INSURANCE"
    const val ULTRALAKSHA_INS = "ULTRA LAKSHA INSURANCE"
    const val HOME_LOAN = "HOME LOAN"
    const val PERSONA_LOAN = "PERSONAL LOAN"
    const val BUSINESS_LOAN = "BUSINESS LOAN"
    const val CAR_TOP_LOAN = "CAR LOAN TOP UP LOAN"
    const val LAP = "LAP"
    const val CREDIT_CARD = "CREDIT CARD"
    const val BALANCE_TRANSFER = "BALANCE TRANSFER"
    const val QUICK_LEAD = "QUICK LEAD"
    const val FIN_PEACE = "FIN PEACE"
    const val HEALTH_CHECKUP = "HEALTH CHECKUP PLANS"
    const val CV = "COMMERCIAL VEHICLE"
    const val ULTRALAKSHA_COMBO = "ULTRA LAKSHYA COMBO"
    const val SYNC_CONTACTS = "Sync Contacts"

    const val POSP = "POSP"

    const val GST = 0.18
}