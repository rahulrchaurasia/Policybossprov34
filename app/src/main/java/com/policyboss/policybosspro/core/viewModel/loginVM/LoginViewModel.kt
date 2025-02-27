package com.policyboss.policybosspro.core.viewModel.loginVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.repository.loginRepository.LoginRepository
import com.policyboss.policybosspro.core.response.forgotPwd.ForgotResponse
import com.policyboss.policybosspro.core.response.login.AuthLoginResponse
import com.policyboss.policybosspro.core.response.login.DevicetokenResponse
import com.policyboss.policybosspro.core.response.login.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.core.response.login.OtpLoginResponse
import com.policyboss.policybosspro.core.response.login.OtpLoginResult
import com.policyboss.policybosspro.core.response.login.OtpVerifyResponse
import com.policyboss.policybosspro.core.response.login.UserNewSignUpResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager

import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginNewRepository: LoginRepository,
    private val prefManager: PolicyBossPrefsManager,

    ): ViewModel() {



    // region Declaration of Variable
    var remaingTime: Long = 0L

    //region Handele SSID
    private var ssid: String = ""
    private var OTP_mobNo: String = ""

    private var otpLoginResult: OtpLoginResult ? = null

    private fun setSsid(newSsid: String) {
        ssid = newSsid
    }

    fun getSsid(): String {
        return ssid
    }

    /////handle OTP response

    private fun setOTPReqLoginResult(_otpLoginResult: OtpLoginResult) {
        otpLoginResult = _otpLoginResult
    }

    fun getOTPReqLoginResult(): OtpLoginResult? = otpLoginResult
    ////

    fun getOtpMobileNo(): String {
        return OTP_mobNo
    }
    //endregion


    //region get isUserSignUp or Not ?
    private  val getsignUpMutuableStateFlow : MutableStateFlow<APIState<UserNewSignUpResponse>> = MutableStateFlow(APIState.Empty())

    val getsignUpStateFlow : StateFlow<APIState<UserNewSignUpResponse>>
        get() = getsignUpMutuableStateFlow

    //endregion

    private val insertTokenMutuableStateFlow : MutableStateFlow<APIState<DevicetokenResponse>> = MutableStateFlow(APIState.Empty())

    val insertTokenStateFlow : StateFlow<APIState<DevicetokenResponse>>
        get() = insertTokenMutuableStateFlow

    //.........
    //region DSAS Login
    private  val loginMutuableStateFlow : MutableStateFlow<APIState<LoginNewResponse_DSAS_Horizon>> = MutableStateFlow(APIState.Empty())

    val LoginStateFlow : StateFlow<APIState<LoginNewResponse_DSAS_Horizon>>
        get() = loginMutuableStateFlow

  //endregion

    //region OTP via Login

    //region otp  Initialization
    private  val otpLoginMutuableStateFlow : MutableStateFlow<APIState<OtpLoginResponse>> = MutableStateFlow(APIState.Empty())

    val otpLoginStateFlow : StateFlow<APIState<OtpLoginResponse>>
        get() = otpLoginMutuableStateFlow
    //endregion


    //endregion



    //region OTP Verification via Login
    private  val otpVerificationMutuableStateFlow : MutableStateFlow<APIState<OtpVerifyResponse>> = MutableStateFlow(APIState.Empty())

    val otpVerificationStateFlow : StateFlow<APIState<OtpVerifyResponse>>
        get() = otpVerificationMutuableStateFlow

    //endregion


    //region OTP  Resend
    private  val otpResendMutuableStateFlow : MutableStateFlow<APIState<OtpVerifyResponse>> = MutableStateFlow(APIState.Empty())

    val otpResendStateFlow : StateFlow<APIState<OtpVerifyResponse>>
        get() = otpVerificationMutuableStateFlow

    //endregion

    //region Password via Login
    private  val authLoginMutuableStateFlow : MutableStateFlow<APIState<AuthLoginResponse>> = MutableStateFlow(APIState.Empty())

    val authLoginStateFlow : StateFlow<APIState<AuthLoginResponse>>
        get() = authLoginMutuableStateFlow

    //endregion

    //region forgotPassword ?
    private  val forgotPasswordMutuableStateFlow : MutableStateFlow<APIState<ForgotResponse>> = MutableStateFlow(APIState.Empty())

    val forgotPasswordStateFlow : StateFlow<APIState<ForgotResponse>>
        get() = forgotPasswordMutuableStateFlow

    //endregion

    //endregion


    fun getusersignup(appVersion: String, deviceCode : String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", appVersion)
        body.put("ssid", "")
        body.put("fbaid", "")
        body.put("device_code", deviceCode)


        getsignUpMutuableStateFlow.value = APIState.Loading()

        setSsid("")
        loginNewRepository.getusersignup(body)
            .catch {
                getsignUpMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
            }
            .collect{ data ->
                if (data?.isSuccessful == true){
                    if(data.body()?.StatusNo?:1 == 0)
                    {

                            getsignUpMutuableStateFlow.value = APIState.Success(data = data.body())

                    }
                    else{

                        otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
                    }
                }
                else
                {
                    otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
                }

            }


    }
    fun insert_notification_token(Ss_Id: String) = viewModelScope.launch {
        var body = HashMap<String,String>()
        body.put("Ss_Id",Ss_Id)
        body.put("Device_Id",prefManager.getDeviceID())
        body.put("Device_Name",prefManager.getDEVICE_NAME())
        body.put("Token",prefManager.getToken())

        insertTokenMutuableStateFlow.value = APIState.Loading()

        loginNewRepository.insert_notification_token(body)
            .catch {
                insertTokenMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
            }
            .collect{data ->
                if(data?.isSuccessful == true) {
                    if (data.body()?.Status == "SUCCESS") {

                        insertTokenMutuableStateFlow.value = APIState.Success(data= data.body())

                    } else
                    {
                        insertTokenMutuableStateFlow.value   = APIState.Failure(errorMessage = Constant.NOData)
                    }
                }
                else
                {
                    insertTokenMutuableStateFlow.value  = APIState.Failure(errorMessage = Constant.NOData)
                }

            }
    }

    fun  getLoginDetailHorizon(ss_id : String) = viewModelScope.launch {

      //  loginMutuableStateFlow.value = APIState.Loading()

        try {

            loginNewRepository.getLoginHorizonDetails(ss_id)
                .catch {
                    loginMutuableStateFlow.value = APIState.Failure(errorMessage = it.message.toString())
                }.collect{  data ->
                    if (data.isSuccessful){
                        if(data.body()?.status?.uppercase().equals("SUCCESS"))
                        {
                            prefManager.saveLoginHorizonResponse(data.body())
                            loginMutuableStateFlow.value = APIState.Success(data = data.body())

                            insert_notification_token(ss_id)
                        }
                        else{
                            loginMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                        }
                    }
                    else
                    {
                        loginMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                    }
                }


        }catch (ex : Exception){

            loginMutuableStateFlow.value = APIState.Failure(errorMessage = Utility.ErrorMessage)
        }

    }


    fun getotpLoginHorizon(login_id: String,deviceID : String, ipAddress : String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("login_id", login_id)
        body.put("device_id", deviceID)
        body.put("ip_address", ipAddress)

        otpLoginMutuableStateFlow.value = APIState.Loading()

        setSsid("")
        loginNewRepository.otpLoginHorizon(body)
            .catch {
                otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.ServerError)
            }
            .collect{ data ->
                if (data.isSuccessful){
                    val responseBody= data.body()

                    responseBody?.let { response ->


                        //Set SSID and After Verify mob no. Call dsasLogin Horizon Details using SsID
//                        setSsid(data.body()?.Msg?.Ss_Id?.toString() ?:"")
//                        OTP_mobNo = data.body()?.Msg?.Mobile_No?.toString() ?:""

                       val otpLoginResult  =    when (val msg = response.Msg) {
                            is Map<*, *> -> {
                                val mobileNo = msg["Mobile_No"] as? String ?: ""
                                val ssid = (msg["Ss_Id"] as? Double)?.toLong()?.toString() ?: ""
                                setSsid(ssid.toString().trim())
                                OTP_mobNo = mobileNo.toString().trim()
                                val status = response.Status ?: "FAIL"
                                OtpLoginResult(
                                    status = status,
                                    mobileNumber = mobileNo,
                                    message = ""
                                )
                            }
                            is String -> {
                                //Note : in Api when "Status" is string come than status is byDefault "fail"  we can also write below static FAIL
                                //so mobileNumber on fail case is empty
                                OtpLoginResult(status = response.Status ?: "FAIL",mobileNumber ="", message = msg)
                            }
                            else -> {
                                OtpLoginResult(status = "FAIL",mobileNumber ="", message = Constant.ErrorMessage)
                            }
                        }
                          //set here the data which we get in Activity for handling both success and failure
                          setOTPReqLoginResult(_otpLoginResult = otpLoginResult)
                          otpLoginMutuableStateFlow.value = APIState.Success(data = data.body())
                          // regionNote : No need to filter on the base of Success and fail  status we need to take action on both case
//                    if(data.body()?.Status?.uppercase().equals("SUCCESS"))
//                    { otpLoginMutuableStateFlow.value = APIState.Success(data = data.body()) }
//                    else{
//                        otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.ErrorMessage)
//                    }
                          //endregion
                    } ?: run{

                        otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.SeverUnavaiable)

                      }

                }
                else
                {
                    otpLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.SeverUnavaiable)
                }

            }


    }

    fun  otpVerifyHorizon(otp : String, mobileno : String) = viewModelScope.launch {


        // Loading is start from Verify
        otpVerificationMutuableStateFlow.value = APIState.Loading()
        //loginMutuableStateFlow.value = APIState.Loading()

        try {

            loginNewRepository.otpVerifyHorizon(otp, mobileno)
                .catch {
                    otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.InValidOTP)
                }.collect{  data ->
                    if (data.isSuccessful){
                        if(data.body()?.Msg?.uppercase().equals("SUCCESS"))
                        {

                            // Success is trigger after DSS Horizon Api if successfully called
                            // otpVerificationMutuableStateFlow.value = APIState.Success(data = data.body())

                            if(getSsid().isNotEmpty()){
                                getLoginDetailHorizon(getSsid())

                            }else{

                                otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = "SSID not generated.Please contact admin")

                            }

                        }
                        else{
                            otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.InValidOTP)
                        }
                    }
                    else
                    {
                        otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.InValidOTP)
                    }
                }


        }catch (ex : Exception){

            otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = Utility.ErrorMessage)
        }

    }

    fun  otpResendHorizon(mobNo : String) = viewModelScope.launch {


      // otpResendMutuableStateFlow.value = APIState.Loading()

        try {

            loginNewRepository.otpResendHorizon(mobNo)
                .catch {
                   // otpResendMutuableStateFlow.value = APIState.Failure(errorMessage = it.message.toString())
                }.collect{  data ->
                    if (data.isSuccessful){
                        if(data.body()?.Msg?.uppercase().equals("SUCCESS"))
                        {

                          //  otpResendMutuableStateFlow.value = APIState.Success(data = data.body())
                        }
                        else{
                           // otpResendMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                        }
                    }
                    else
                    {
                       // otpResendMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                    }
                }


        }catch (ex : Exception){

            otpResendMutuableStateFlow.value = APIState.Failure(errorMessage = Utility.ErrorMessage)
        }

    }

    //region commented
    fun getAuthLoginHorizonOld(username: String, password : String) = viewModelScope.launch {

        //region Commment
//        var body = HashMap<String, String>()
//        body.put("username", username)
//        body.put("password", password)
//
//        ssid = ""
//        authLoginMutuableStateFlow.value = APIState.Loading()
//
//        loginNewRepository.authLoginHorizon(body)
//            .catch {
//                authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
//            }
//            .collect{ data ->
//                if (data.isSuccessful){
//                    if(data.body()?.Status?.uppercase().equals("SUCCESS"))
//                    {
//
//                        authLoginMutuableStateFlow.value = APIState.Success(data = data.body())
//
//                        setSsid(data.body()?.SS_ID?:"")
//
//                        data.body()?.SS_ID?.let {
//
//                            loginNewRepository.getLoginHorizonDetails(it)
//                        }
//                    }
//                    else{
//
//                        authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.InValidUser)
//                    }
//                }
//                else
//                {
//                    authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
//                }
//
//            }
//
        //endregion

    }

    fun  otpVerifyHorizonOld(otp : String) = viewModelScope.launch {


        otpVerificationMutuableStateFlow.value = APIState.Loading()

        try {

            loginNewRepository.otpVerifyHorizon(otp,"")
                .catch {
                    otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = it.message.toString())
                }.collect{  data ->
                    if (data.isSuccessful){
                        if(data.body()?.Msg?.uppercase().equals("SUCCESS"))
                        {

                            // otpVerificationMutuableStateFlow.value = APIState.Success(data = data.body())

                            getLoginDetailHorizon(getSsid())
                        }
                        else{
                            otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                        }
                    }
                    else
                    {
                        otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage ="No Data Found")
                    }
                }


        }catch (ex : Exception){

            otpVerificationMutuableStateFlow.value = APIState.Failure(errorMessage = Utility.ErrorMessage)
        }

    }

    //endregion
    fun getAuthLoginHorizon(username: String, password : String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("username", username)
        body.put("password", password)

        setSsid("")

        authLoginMutuableStateFlow.value = APIState.Loading()

        loginNewRepository.authLoginHorizon(body)
            .catch {
                authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.ServerError)
            }
            .collect{ data ->
                if (data.isSuccessful){
                    if(data.body()?.Status?.uppercase().equals("SUCCESS"))
                    {

                       // authLoginMutuableStateFlow.value = APIState.Success(data = data.body())

                        // Success is trigger after DSS Horizon Api if successfully called

                        data.body()?.SS_ID?.let {

                            getLoginDetailHorizon(it)
                        }?: also {
                            loginMutuableStateFlow.value = APIState.Failure(errorMessage = "SSID not generated.Please contact admin")
                        }



                    }
                    else{

//                        authLoginMutuableStateFlow.value =
//                            APIState.Failure(errorMessage = data.body()?.Msg?.ExceptionMessage ?: Constant.InValidPass)

                        authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.InValidPass)
                    }
                }
                else
                {
                    authLoginMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.ServerError)
                }

            }


    }


    fun forgotPassword(emailID: String ,appVersion: String, deviceCode : String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("EmailID", emailID)
        body.put("app_version", appVersion)
        body.put("ssid", "")
        body.put("fbaid", "")
        body.put("device_code", deviceCode)


        getsignUpMutuableStateFlow.value = APIState.Loading()

        setSsid("")
        loginNewRepository.forgotPassword(body)
            .catch {
                forgotPasswordMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
            }
            .collect{ data ->
                if (data?.isSuccessful == true){
                    if(data.body()?.StatusNo?:1 == 0)
                    {

                        forgotPasswordMutuableStateFlow.value = APIState.Success(data = data.body())

                    }
                    else{

                        forgotPasswordMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
                    }
                }
                else
                {
                    forgotPasswordMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.NOData)
                }

            }


    }



}

