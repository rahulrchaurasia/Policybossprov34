package com.policyboss.policybosspro.view.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.broadcast.SMSReaderBroadCastReceiver
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.viewModel.loginVM.LoginViewModel
import com.policyboss.policybosspro.databinding.ActivityLoginBinding
import com.policyboss.policybosspro.databinding.LayoutLoginViaotpBinding
import com.policyboss.policybosspro.databinding.LayoutLoginViapasswordBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.AppSignatureHashHelper
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.NetworkUtils.Companion.isNetworkAvailable
import com.policyboss.policybosspro.utils.ValidationUtil
import com.policyboss.policybosspro.utils.getLocalIpAddress
import com.policyboss.policybosspro.utils.hideKeyboard
import com.policyboss.policybosspro.utils.showAlert
import com.policyboss.policybosspro.utils.showKeyboard
import com.policyboss.policybosspro.utils.showToast
import com.policyboss.policybosspro.view.WebView.PrivacyWebViewActivity
import com.policyboss.policybosspro.view.home.HomeActivity
import com.policyboss.policybosspro.view.raiseTicketDialog.RaiseTicketDialogActivity
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    //region Declaration

    private val loginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    var isClickable = true

//    var selectedLogin: LoginOption = LoginOption.NoData // Default value

    var  selectedLogin: LoginOption = LoginOption.OTP

    private var isPasswordObserving = false
    private var isDSASObserving = false
    private var isOTPVerifyObserving = false

    private lateinit var alertDialogPassword: AlertDialog

    private  var alertDialogOTP : AlertDialog? = null




    private var timer: CountDownTimer? = null

    private var resendTime = 60      // 60 sec after resend is Launched...

    private var isResendOTPUpdate = false

    //selected edittext position
    var selectedETPosition = 0    // For OTP EditText Dialog

    //   User weUser;
    var enable_pro_signupurl = ""

    var enable_otp_only = "N"

    var perms = arrayOf(
        "android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.POST_NOTIFICATIONS",
        "android.permission.READ_MEDIA_IMAGES"
    )

    private var intentFilter: IntentFilter? = null
    private var smsReceiver: SMSReaderBroadCastReceiver? = null


    //endregion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        Log.d(Constant.TAG, "HashKey: " + appSignatureHashHelper.appSignatures[0])

        init()

        setListener()

        radioButtonListner()

        //region displaying the response which we get from above API


        if (!checkPermission()) {
            requestPermission()
        }

        //region declaration
        observe()
        // Check For LoginVia
        loginViewModel.getusersignup(appVersion = prefManager.getAppVersion(),
            deviceCode = prefManager.getDeviceID())

        // Init Sms Retriever >>>>
        initSmsListener()
       // initBroadCast()

        //endregion

        if (!isNetworkAvailable(this)) {
            Snackbar.make(binding.root, getString(R.string.noInternet), Snackbar.LENGTH_SHORT).show()
            return
        }

        displayLoadingWithText()
        //endregion




    }




    // region method

    //region initialization
    private fun init(){


        //  setEnableNextButton(false)
        setEnableNextButton(bln = true)



        prefManager.setDeviceID(Utility.getDeviceID(this@LoginActivity))

        prefManager.setAppVersion("policyboss-" + BuildConfig.VERSION_NAME) 
        

        //   val deviceId: String =
        //     Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val deviceName = Build.MODEL

        prefManager.setDEVICE_NAME(Build.MODEL)

    }

    private fun setListener() {

        binding.includeLoginNew.btnNext.setOnClickListener(this)
        binding.includeLoginNew.tvSignUp.setOnClickListener(this)
       
        binding.includeLoginNew.tvForgotPass.setOnClickListener(this)
        binding.includeLoginNew.lyRaiseTicket.setOnClickListener(this)
        binding.includeLoginNew.txtterm.setOnClickListener(this)
        binding.includeLoginNew.txtprivacy.setOnClickListener(this)
    }

    //endregion

    //region Radio Button for Login Type
    fun setEnableNextButton(bln : Boolean){

        if(bln){
            binding.includeLoginNew.btnNext.apply {
                isEnabled = bln
                alpha = 1f   // Set alpha back to 1 (100% opacity)
            }
        }else{
            binding.includeLoginNew.btnNext.apply {
                isEnabled = bln
                alpha = 0.5f   ///Set alpha to 0.5 (50% transparency)
            }
        }

    }


    private fun radioButtonListner(){

        // Listen for changes in the RadioGroup
        binding.includeLoginNew.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Check if any radio button is selected
            val selectedRadioButton = findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {

                // Handle the selected radio button (e.g., display a toast with the selected text)

                setEnableNextButton(bln = true)
                selectedLogin = when(selectedRadioButton.id){
                    R.id.rbOtp -> LoginOption.OTP
                    R.id.rbPassword -> LoginOption.Password
                    // Add more cases as needed for other RadioButtons
                    else ->LoginOption.NoData
                }
            }
        }
    }

    //endregion

    //region auto read sms
    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = SMSReaderBroadCastReceiver()
        smsReceiver?.setOTPListener(object : SMSReaderBroadCastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.d(Constant.TAG, "OTP Received: $otp")
                // showAlert("OTP Received: $otp")

                otp?.let {
                    pasteOTP(strOTP =  it)
                }

                otp?.let {
                    // pasteOTP(otp)
                    // Re-initialize the SMS Retriever for subsequent attempts {ie for Next time handling of data}
                    initSmsListener()
                }

            }

            override fun onOTPReceiveError(error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initSmsListener() {
        val client = SmsRetriever.getClient(this)
        // client.startSmsRetriever()   //  temp
        client.startSmsRetriever().addOnSuccessListener {
           // registerReceiver(smsReceiver, intentFilter)
            registerSmsReceiver()
        }.addOnFailureListener {

           // showAlert("Failed to start SMS Retriever")
        }
    }

    private fun registerSmsReceiver() {
        smsReceiver = SMSReaderBroadCastReceiver().apply {
            setOTPListener(object : SMSReaderBroadCastReceiver.OTPReceiveListener {
                override fun onOTPReceived(otp: String?) {
                    Log.d(Constant.TAG, "OTP Received: $otp")
                    otp?.let { pasteOTP(strOTP = it) }
                    initSmsListener() // Re-initialize for subsequent attempts
                }

                override fun onOTPReceiveError(error: String) {
                    Log.e(Constant.TAG, "OTP Receive Error: $error")
                }
            })
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        //For Broadcast recevier Handling ver 34 and above
        if (Build.VERSION.SDK_INT >= 34 && getApplicationInfo().targetSdkVersion >= 34) {
            registerReceiver(smsReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(smsReceiver, intentFilter)
        }


//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//                // Android 14 (API 34) and above
//                registerReceiver(
//                    smsReceiver,
//                    intentFilter,
//                    Context.RECEIVER_NOT_EXPORTED  // Use NOT_EXPORTED since this is for SMS retrieval
//                )
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
//                // Android 13 (API 33)
//                registerReceiver(
//                    smsReceiver,
//                    intentFilter,
//                    Context.RECEIVER_EXPORTED
//                )
//            }
//            else -> {
//                // Below Android 13
//                registerReceiver(smsReceiver, intentFilter)
//            }
//        }
    }


    //endregion

    fun maskPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length < 10) {
            // Handle cases where the phone number is not long enough to mask
            return phoneNumber
        }

        val maskedDigits = "******"
        val lastDigits = phoneNumber.takeLast(4)
        val maskedPortion = maskedDigits + lastDigits

        return maskedPortion
    }

    //region Alert Dialog

    private fun showOTPDialog(  mobNo : String) {

        selectedETPosition = 0
        isResendOTPUpdate = false
        var bindingOTP = LayoutLoginViaotpBinding.inflate(layoutInflater)

        alertDialogOTP = AlertDialog.Builder(this,R.style.CustomDialog)
            .setView(bindingOTP.root)
            .create()

//        binding.txtResend.apply {
//            isEnabled = false
//            alpha = 0.4f   // Set alpha back to 1 (100% opacity)
//        }

        bindingOTP.lyPaste.visibility = View.INVISIBLE
        bindingOTP.txtResend.visibility = View.GONE

        bindingOTP.txtError.visibility = View.GONE
        bindingOTP.txtTextDtl.text = "We have sent you One-Time Password on ${maskPhoneNumber(mobNo)}"
        //bindingOTP.pinview.requestFocus()

        // showKeyboard( bindingOTP.btnSubmit)


        //region EditText TextWatcher

        val pinnedViewTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                bindingOTP.txtError.visibility = View.GONE


            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.length ?: 0 == 4) {

                    //region OTP Verification Handling


                    var inputOtp  = bindingOTP.pinview.getText().toString()

                    if (inputOtp.length == 4) {

                        // Called Api
                        cancelTimer()
                        hideKeyboard(bindingOTP.btnSubmit)
                        loginViewModel.otpVerifyHorizon(inputOtp,mobNo)

                    } else if (inputOtp.length == 0) {
                        bindingOTP.txtError.visibility = View.VISIBLE
                        bindingOTP.txtError.text = "Please Enter OTP"

                    } else {


                        bindingOTP.pinview.setLineColor( ContextCompat.getColor(this@LoginActivity,R.color.red))
                        bindingOTP.txtError.visibility = View.VISIBLE
                        bindingOTP.txtError.text = "Invalid OTP Entered."

                        //endregion

                    }

                    //endregion

                }
            }
        }
        //endregion


        bindingOTP.pinview.addTextChangedListener(pinnedViewTextWatcher)
        bindingOTP.btnSubmit.setOnClickListener {




            //region OTP Verification Handling


            var inputOtp  = bindingOTP.pinview.getText().toString()

            if (inputOtp.length == 4) {

                // Called Api
                cancelTimer()
                hideKeyboard(bindingOTP.btnSubmit)
                loginViewModel.otpVerifyHorizon(inputOtp,mobNo)

            } else if (inputOtp.length == 0) {
                bindingOTP.txtError.visibility = View.VISIBLE
                bindingOTP.txtError.text = "Please Enter OTP"

            } else {


                bindingOTP.pinview.setLineColor( ContextCompat.getColor(this@LoginActivity,R.color.red))
                bindingOTP.txtError.visibility = View.VISIBLE
                bindingOTP.txtError.text = "Invalid OTP Entered."

                //endregion

            }

            //endregion


        }

        bindingOTP.imgClose.setOnClickListener {
            alertDialogOTP?.dismiss()
            // Cancel the existing timer if it's running
            cancelTimer()

            bindingOTP.pinview.removeTextChangedListener(pinnedViewTextWatcher)


        }



        bindingOTP.txtResend.setOnClickListener {

            //region Resend OTP
            loginViewModel.otpResendHorizon(binding.includeLoginNew.etEmail.text?.trim().toString())

            bindingOTP.progessBar.visibility = View.VISIBLE
//            binding.txtResend.apply {
//                isEnabled = false
//                alpha = 0.4f   // Set alpha back to 1 (100% opacity)
//            }
            bindingOTP.txtResend.visibility = View.GONE
            bindingOTP.btnSubmit.apply {
                isEnabled = false
                alpha = 0.4f
            }
            cancelTimer()

            lifecycleScope.launch {

                //region delay for showng Loader
                delay(3000) // 3 seconds delay
                // Hide the progressBar
                bindingOTP.progessBar.visibility = View.GONE
//                binding.txtResend.apply {
//                    isEnabled = true
//                    alpha = 1f   // Set alpha back to 1 (100% opacity)
//                }
                bindingOTP.txtResend.visibility = View.VISIBLE
                bindingOTP.btnSubmit.apply {
                    isEnabled = true
                    alpha = 1f
                }

                //endregion


            }
            startTimerCountdown(bindingOTP.txtcountdownTimer, bindingOTP.txtResend)

            //endregion


        }

        startTimerCountdown(bindingOTP.txtcountdownTimer, bindingOTP.txtResend)

        alertDialogOTP?.setCancelable(false)



        alertDialogOTP?.getWindow()
            ?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alertDialogOTP?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        showKeyboard(bindingOTP.btnSubmit)


        alertDialogOTP?.show()
    }

    //region Alert when Error Message is Come ...Only for Wrong OTP handling. ie when Otp Open again
    private fun showOTPDialog(mobNo : String,errorMsg : String = "") {

        selectedETPosition = 0
        isResendOTPUpdate = false
        var bindingOTP = LayoutLoginViaotpBinding.inflate(layoutInflater)

        alertDialogOTP = AlertDialog.Builder(this@LoginActivity, R.style.CustomDialogWithoutAnim)
            .setView(bindingOTP.root)
            .create()

//            binding.txtResend.apply {
//                isEnabled = false
//                alpha = 0.4f   // Set alpha back to 1 (100% opacity)
//            }

        bindingOTP.lyPaste.visibility = View.INVISIBLE
        bindingOTP.txtResend.visibility = View.GONE
        bindingOTP.txtError.visibility = View.GONE
        bindingOTP.txtTextDtl.text = "We have sent you One-Time Password on ${maskPhoneNumber(mobNo)}"
        bindingOTP.pinview.requestFocus()

        if(errorMsg.isNotEmpty()){
            bindingOTP.txtError.visibility = View.VISIBLE
            bindingOTP.txtError.text = errorMsg
        }

        //region EditText TextWatcher


        val pinnedViewTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                bindingOTP.txtError.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.length ?: 0 == 4) {

                    //region OTP Verification Handling

                    var inputOtp  = bindingOTP.pinview.getText().toString()

                    if (inputOtp.length == 4) {

                        // Called Api
                        cancelTimer()
                        hideKeyboard(bindingOTP.btnSubmit)
                        loginViewModel.otpVerifyHorizon(inputOtp,mobNo)

                    } else if (inputOtp.length == 0) {
                        bindingOTP.txtError.visibility = View.VISIBLE
                        bindingOTP.txtError.text = "Please Enter OTP"
                    } else {

                        //region EditText error display
                        bindingOTP.pinview.setLineColor( ContextCompat.getColor(this@LoginActivity,R.color.red))

                        bindingOTP.txtError.visibility = View.VISIBLE
                        bindingOTP.txtError.text = "Invalid OTP Entered."

                        //endregion

                    }

                    //endregion

                }
            }
        }
        //endregion



        bindingOTP.pinview.addTextChangedListener(pinnedViewTextWatcher)



        bindingOTP.btnSubmit.setOnClickListener {



            //region OTP Verification Handling

            var inputOtp  = bindingOTP.pinview.getText().toString()

            if (inputOtp.length == 4) {

                // Called Api
                cancelTimer()
                hideKeyboard(bindingOTP.btnSubmit)
                loginViewModel.otpVerifyHorizon(inputOtp,mobNo)

            } else if (inputOtp.length == 0) {
                bindingOTP.txtError.visibility = View.VISIBLE
                bindingOTP.txtError.text = "Please Enter OTP"
            } else {

                //region EditText error display
                bindingOTP.pinview.setLineColor( ContextCompat.getColor(this@LoginActivity,R.color.red))

                bindingOTP.txtError.visibility = View.VISIBLE
                bindingOTP.txtError.text = "Invalid OTP Entered."

                //endregion

            }

            //endregion



        }
        bindingOTP.imgClose.setOnClickListener {
            alertDialogOTP?.dismiss()
            // Cancel the existing timer if it's running
            cancelTimer()
            bindingOTP.pinview.removeTextChangedListener(pinnedViewTextWatcher)
        }



        bindingOTP.txtResend.setOnClickListener{

            loginViewModel.otpResendHorizon(binding.includeLoginNew.etEmail.text?.trim().toString())

            bindingOTP.progessBar.visibility = View.VISIBLE
//                binding.txtResend.apply {
//                    isEnabled = false
//                    alpha = 0.4f   // Set alpha back to 1 (100% opacity)
//                }
            bindingOTP.txtResend.visibility = View.GONE
            bindingOTP.btnSubmit.apply {
                isEnabled = false
                alpha = 0.4f
            }
            cancelTimer()

            lifecycleScope.launch {

                //region delay for showng Loader
                delay(3000) // 3 seconds delay
                // Hide the progressBar
                bindingOTP.progessBar.visibility = View.GONE
//                    binding.txtResend.apply {
//                        isEnabled = true
//                        alpha = 1f   // Set alpha back to 1 (100% opacity)
//                    }
                bindingOTP.txtResend.visibility = View.VISIBLE
                bindingOTP.btnSubmit.apply {
                    isEnabled = true
                    alpha = 1f
                }

                //endregion


            }
            startTimerCountdown(bindingOTP.txtcountdownTimer, bindingOTP.txtResend)

        }

        startTimerCountdown(bindingOTP.txtcountdownTimer, bindingOTP.txtResend, remainingTime = loginViewModel.remaingTime)
        alertDialogOTP?.show()
        alertDialogOTP?.setCancelable(false)





    }


    private fun dialogForgotPassword() {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setCancelable(true)
        // builder.setTitle("FORGOT PASSWORD");

        val view = this.layoutInflater.inflate(R.layout.layout_forgot_password, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val etEmail = view.findViewById<View>(R.id.etEmail) as EditText
        val btnReset = view.findViewById<View>(R.id.btnReset) as Button

        btnReset.setOnClickListener {
            if (!ValidationUtil.isValidEmailID(etEmail)) {
                etEmail.error = "Invalid Email ID"
                etEmail.isFocusable = true
                //return;
            } else {
                dialog.dismiss()

                //05 temp pending
                displayLoadingWithText(text = "Retrieving password...")
                // LoginController(this@LoginActivity)
                //    .forgotPassword(etEmail.text.toString(), this@LoginActivity)
                loginViewModel.forgotPassword(emailID = etEmail.text.toString(),
                    appVersion = prefManager.getAppVersion(),
                    deviceCode = prefManager.getDeviceID() )
            }
        }
    }
    //endregion

    private fun showPasswordDialog(strUserID : String) {

        if (this.isFinishing || this.isDestroyed) {
            return // Exit the function if the activity is not in a valid state
        }


        //var binding: LayoutLoginViapasswordBinding? = null

        var bindingPassword = LayoutLoginViapasswordBinding.inflate(layoutInflater)

        alertDialogPassword = AlertDialog.Builder(this, R.style.CustomDialog)
            .setView(bindingPassword.root)
            .setCancelable(false)
            .create()

        this.showKeyboard(bindingPassword.etPassword)

        bindingPassword.etPassword.setText("")
        bindingPassword.imgClose.setOnClickListener {
            alertDialogPassword.dismiss()
        }
        bindingPassword.txtError.visibility = View.GONE
        bindingPassword.txtError.text = ""

        bindingPassword.tilPwd.hint = ""

        bindingPassword.etPassword.doOnTextChanged { text, start, before, count ->

            if(text!!.length >0){
                bindingPassword.txtError.visibility = View.GONE
                bindingPassword.txtError.text = ""
                bindingPassword.etPassword.setBackgroundResource(R.drawable.rect_lightgray_shape)
            }
        }
        bindingPassword.btnSubmit.setOnClickListener {

            if (bindingPassword.etPassword.text.isNullOrBlank()) {

                bindingPassword.txtError.visibility = View.VISIBLE
                bindingPassword.txtError.text = "Enter Password"

                bindingPassword.etPassword.setBackgroundResource(R.drawable.rect_error_shape)
            } else {

                alertDialogPassword.dismiss()
                isPasswordObserving = false
                loginViewModel.getAuthLoginHorizon(
                    binding.includeLoginNew.etEmail.text?.trim().toString(),
                    bindingPassword.etPassword.text.toString()
                )

            }


        }



        alertDialogPassword.show()
        alertDialogPassword.setCancelable(false)


    }


    fun loginFailVerifyAlert(
     strMessage : String
    ) {
        val builder = AlertDialog.Builder(this@LoginActivity, R.style.CustomDialog);
        val btnClose: Button
        val txtHdr: TextView
        val txtMessage : TextView
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_login_device_verify, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        btnClose = dialogView.findViewById(R.id.btnClose)
        txtMessage = dialogView.findViewById(R.id.txtMessage)

        txtHdr = dialogView.findViewById(R.id.txtHdr)
        // txtHdr.text = "" + strhdr
        //  txtMessage.text = "" + strBody


        txtMessage.setText(strMessage)

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun pasteOTP( strOTP : String) {

        if (alertDialogOTP != null ) {


            if (alertDialogOTP!!.isShowing) {


                if (strOTP.length > 0) {


                    if (strOTP.length == 4) {



                        // val btnSubmit = alertDialogOTP.findViewById<Button>(R.id.btnSubmit)


                        var pinview = alertDialogOTP?.findViewById<com.chaos.view.PinView>(R.id.pinview)

                        pinview?.setText(strOTP)
                        //********************************************************************************
                        //Note : Since we have written pinview textChange Listener : trigger will done at 4 th position

                        // btnSubmit.performClick()  no need again call btnSubmit
                        //********************************************************************************/

                    }


                }


            }
        }


    }


    //endregion

    // region CountDown Timmer
    private fun startTimerCountdown(txtTimer : TextView, txtResend : TextView, remainingTime: Long= 0L ) {

        var totalTimeInMillis : Long = 0L
        var totalMainTimeInMillis : Long =  2 * 60 * 1000L
        if(remainingTime == 0L){
            totalTimeInMillis =  2 * 60 * 1000
        }else{
            totalTimeInMillis =  remainingTime
        }
        // 2 minutes in milliseconds

        val totalResendTimes = resendTime.times(1000)  // same as  30 * 1000

        // Variable to keep track of elapsed time
        var elapsedTime = 0L
        var elapsedMainTime = 0L
        timer = object : CountDownTimer(totalTimeInMillis, 1000) {

            var blnResendOTP = false
            override fun onTick(millisUntilFinished: Long) {

                runOnUiThread {
                    val seconds = (millisUntilFinished / 1000) % 60
                    val minutes = (millisUntilFinished / (1000 * 60)) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    txtTimer.text = "" + formattedTime

                    // Calculate elapsed tim
                    elapsedTime = totalTimeInMillis - millisUntilFinished
                    // Update the ViewModel
                    loginViewModel.remaingTime = totalTimeInMillis - elapsedTime

                    elapsedMainTime = totalMainTimeInMillis - millisUntilFinished

                    if (!blnResendOTP) {
                        // we req elapse time form Original Start Time ie 2 min hence we store in totalMainTimeInMillis and totalMainTimeInMillis
                        if (elapsedMainTime >= totalResendTimes) {
//                            txtResend.apply {
//                                isEnabled = true
//                                alpha = 1f   // Set alpha back to 1 (100% opacity)
//                            }
                            txtResend.visibility = View.VISIBLE


                            blnResendOTP = true

                        }
                    }


                }

            }

            override fun onFinish() {

                txtTimer.text = "00:00"
                if (alertDialogOTP != null ){
                    if(alertDialogOTP?.isShowing == true){
                        alertDialogOTP?.dismiss()
                    }
                }

            }

        }
        timer?.start()
    }

    private fun cancelTimer() {
        timer?.cancel()

        timer = null // Set the timer to null to indicate that it's no longer active
    }

    //endregion

    //region Observation OF Api using Flow
    private fun observe() {

        //region  is UserSignUp
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                loginViewModel.getsignUpStateFlow.collect { event ->

                    event.contentIfNotHandled?.let {

                        when (it) {
                            is APIState.Loading -> {
                                // showAnimDialog()
                                displayLoadingWithText()

                            }

                            is APIState.Success -> {


                                hideLoading()
                                if (it != null) {

                                    //pospurl

                                    enable_pro_signupurl =
                                        it.data?.MasterData?.get(0)?.enable_pro_signupurl ?: ""


                                    prefManager.setEnableProPOSPurl(enable_pro_signupurl)



                                    enable_otp_only =
                                        it.data?.MasterData?.get(0)?.enable_otp_only ?: ""

                                    if (enable_otp_only != null) {
                                        if (enable_otp_only.isEmpty()) {
                                            binding.includeLoginNew.lyloginvia.visibility =
                                                View.VISIBLE
                                            binding.includeLoginNew.lblloginvia.visibility =
                                                View.VISIBLE
                                        } else {

                                            if (enable_otp_only.equals("Y")) {
                                                binding.includeLoginNew.lyloginvia.visibility =
                                                    View.GONE
                                                binding.includeLoginNew.lblloginvia.visibility =
                                                    View.GONE

                                                binding.includeLoginNew.etEmail.requestFocus()
                                            } else {
                                                binding.includeLoginNew.lyloginvia.visibility =
                                                    View.VISIBLE
                                                binding.includeLoginNew.lblloginvia.visibility =
                                                    View.VISIBLE
                                            }

                                        }
                                    } else {
                                        binding.includeLoginNew.lyloginvia.visibility = View.VISIBLE
                                        binding.includeLoginNew.lblloginvia.visibility =
                                            View.VISIBLE
                                    }

                                }
                            }

                            is APIState.Failure -> {
                                hideLoading()


                            }

                            is APIState.Empty -> {
                                hideLoading()
                            }
                        }
                    }

                }


            }


        }
        //endregion

        //region  Login Using OTP Alert
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                loginViewModel.otpLoginStateFlow.collect { event ->

                    event.contentIfNotHandled?.let {

                        when (it) {
                            is APIState.Loading -> {
                                // showAnimDialog()
                                displayLoadingWithText()

                            }

                            is APIState.Success -> {


                                hideLoading()

                                //var mobileNo = it.data?.Msg?.Mobile_No?:0
                                // showAlert(prefManager.getSSIDByOTP())
                                val otpResult = loginViewModel.getOTPReqLoginResult()

                                if (otpResult?.status.equals("SUCCESS", true)) {

                                    showOTPDialog(mobNo = loginViewModel.getOtpMobileNo())
                                } else {


                                    //005 temp
                                    // showAlert(msg = otpResult?.message?:"",title = "PolicyBoss Pro")

                                    loginFailVerifyAlert(strMessage = otpResult?.message ?: "")


                                }


                            }

                            is APIState.Failure -> {
                                hideLoading()
                                Log.d("Error", it.errorMessage.toString())
                                showToast(it.errorMessage.toString())


                            }

                            is APIState.Empty -> {
                                hideLoading()
                            }
                        }
                    }

                }


            }


        }
        //endregion

        // region otp Verification Horizon
        lifecycleScope.launch{

            repeatOnLifecycle(Lifecycle.State.STARTED){

                loginViewModel.otpVerificationStateFlow.collect{ event ->

                    event.contentIfNotHandled?.let {
                        when (it) {
                            is APIState.Loading -> {
                                // showAnimDialog()
                                displayLoadingWithText()

                            }

                            is APIState.Success -> {


                            }

                            is APIState.Failure -> {
                                hideLoading()
                                Log.d("Error", it.errorMessage.toString())

                                if (alertDialogOTP != null) {
                                    if (alertDialogOTP?.isShowing == true) {
                                        alertDialogOTP?.dismiss()
                                    }
                                }

                                showKeyboard(binding.root)
                                showOTPDialog(
                                    mobNo = loginViewModel.getOtpMobileNo(),
                                    errorMsg = "InValid OTP"
                                )


                            }

                            is APIState.Empty -> {


                            }
                        }
                    }
                }




            }


        }
        //endregion

        //region Login Using Id and Password Alert
        lifecycleScope.launch{

            repeatOnLifecycle(Lifecycle.State.STARTED){

                if(!isPasswordObserving) {
                    isPasswordObserving = true

                    loginViewModel.authLoginStateFlow.collect{ event ->

                        event.contentIfNotHandled?.let {
                            when (it) {
                                is APIState.Loading -> {
                                    // showAnimDialog()
                                    displayLoadingWithText()

                                }

                                is APIState.Success -> {

                                    // Call Horizon DSSS API

                                }

                                is APIState.Failure -> {
                                    hideLoading()
                                    Log.d("Error", it.errorMessage.toString())
                                    showToast(it.errorMessage.toString())


                                }

                                is APIState.Empty -> {
                                    hideLoading()
                                }
                            }
                        }
                    }
                }





            }


        }

        //endregion

        // region DSAS Horizon Last Api. IF we got success than go toHome Page

        lifecycleScope.launch{

            repeatOnLifecycle(Lifecycle.State.STARTED){

                loginViewModel.LoginStateFlow.collect{ event ->

                    event.contentIfNotHandled?.let {
                        when (it) {
                            is APIState.Loading -> {

                                //displayLoadingWithText()

                            }

                            is APIState.Success -> {


                                hideLoading()
                                if (it != null) {


                                    showToast("Login is Successfully...")

                                    this@LoginActivity.finish()
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            HomeActivity::class.java
                                        )
                                    )


                                }
                            }

                            is APIState.Failure -> {
                                hideLoading()
                                Log.d("LoginResp erro", it.errorMessage.toString())


                                showAlert(it.errorMessage.toString())
                            }

                            is APIState.Empty -> {

                            }
                        }
                    }
                }




            }


        }

        //endregion

        //region  Forgot Password
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                loginViewModel.forgotPasswordStateFlow.collect { event ->

                    event.contentIfNotHandled?.let {
                        when (it) {
                            is APIState.Loading -> {
                                // showAnimDialog()
                                displayLoadingWithText()

                            }

                            is APIState.Success -> {


                                hideLoading()
                                showAlert(
                                    it.data?.Message
                                        ?: "Email has been sent on your registered Email address"
                                )
                            }

                            is APIState.Failure -> {
                                hideLoading()


                            }

                            is APIState.Empty -> {
                                hideLoading()
                            }
                        }
                    }

                }


            }


        }
        //endregion
    }

    //endregion

    //region permission
    private fun checkPermission(): Boolean {
        val camera = ActivityCompat.checkSelfPermission(applicationContext, perms[0])
        val WRITE_EXTERNAL = ActivityCompat.checkSelfPermission(applicationContext, perms[1])
        val READ_EXTERNAL = ActivityCompat.checkSelfPermission(applicationContext, perms[2])
        val READ_CONTACTS = ActivityCompat.checkSelfPermission(applicationContext, perms[3])
        val READ_CALL_LOG = ActivityCompat.checkSelfPermission(applicationContext, perms[4])
        val POST_NOTIFICATION = ActivityCompat.checkSelfPermission(applicationContext, perms[5])
        val READ_MEDIA_IMAGE = ActivityCompat.checkSelfPermission(applicationContext, perms[6])
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            camera == PackageManager.PERMISSION_GRANTED && READ_MEDIA_IMAGE == PackageManager.PERMISSION_GRANTED && POST_NOTIFICATION == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            camera == PackageManager.PERMISSION_GRANTED && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED && POST_NOTIFICATION == PackageManager.PERMISSION_GRANTED
        } else {
            camera == PackageManager.PERMISSION_GRANTED && WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED && READ_CONTACTS == PackageManager.PERMISSION_GRANTED && READ_CALL_LOG == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkRationale() {
        if (checkRationalePermission()) {
            //Show Information about why you need the permission
            requestPermission()
        } else {

        }
    }

    private fun checkRationalePermission(): Boolean {
        val camera =
            ActivityCompat.shouldShowRequestPermissionRationale(this@LoginActivity, perms[0])
        val write_external = ActivityCompat.shouldShowRequestPermissionRationale(
            this@LoginActivity,
            perms[1]
        )
        val read_external = ActivityCompat.shouldShowRequestPermissionRationale(
            this@LoginActivity,
            perms[2]
        )
        val read_contacts = ActivityCompat.shouldShowRequestPermissionRationale(
            this@LoginActivity,
            perms[3]
        )
        val read_call_log = ActivityCompat.shouldShowRequestPermissionRationale(
            this@LoginActivity,
            perms[4]
        )
        val read_media_image = ActivityCompat.shouldShowRequestPermissionRationale(
            this@LoginActivity,
            perms[6]
        )

        // boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            camera || read_media_image || read_contacts || read_call_log
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            camera || read_external || read_contacts || read_call_log
        } else {
            camera || write_external || read_external || read_contacts || read_call_log
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            perms,
            Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
        )
    }




//    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
//        AlertDialog.Builder(this@LoginActivity, R.style.AlertDialog_Theme)
//            .setCancelable(false)
//            .setTitle("Retry")
//            .setMessage(message)
//            .setPositiveButton("OK", okListener) //.setNegativeButton("Cancel", null)
//            .create()
//            .show()
//    }

    //endregion

    //region Event
    override fun onClick(view: View?) {


        when(view?.id){

            binding.includeLoginNew.tvForgotPass.id ->{
                dialogForgotPassword()
            }

            binding.includeLoginNew.btnNext.id ->{
                hideKeyboard(binding.root)
                if (!isClickable) return

                isClickable = false
                // Perform your action here

                // Enable button after delay
                Handler(Looper.getMainLooper()).postDelayed({
                    isClickable = true
                }, 2000)

                if (!isNetworkAvailable(this)) {
                    Snackbar.make(view, getString(R.string.noInternet), Snackbar.LENGTH_SHORT).show()
                    return
                }else{

                    if(binding.includeLoginNew.etEmail.text!!.isNotBlank() && selectedLogin == LoginOption.OTP){
                        //have to added in 2025 project 005 temp
                        val  deviceID = Utility.getDeviceID(this@LoginActivity)
                        val ipAddress = getLocalIpAddress()
                        loginViewModel.getotpLoginHorizon(
                            login_id =  binding.includeLoginNew.etEmail.text!!.trim().toString(),
                            token = prefManager.getToken(),
                            deviceID = deviceID ,
                            ipAddress = getLocalIpAddress()?: "")

                    }
                    else if(binding.includeLoginNew.etEmail.text!!.isNotBlank() && selectedLogin == LoginOption.Password){
                        showPasswordDialog(strUserID = binding.includeLoginNew.etEmail.text.toString().trim())
                    }
                    else{
                        showAlert("Please Enter User ID")
                    }

                    //region Comented for testing purpose
                    //                if (!this::alertDialogOTP.isInitialized) {
                    //
                    //                    showOTPDialog(mobNo = "909099")
                    //
                    //                }else{
                    //
                    //                    if(!alertDialogOTP.isShowing){
                    //                        showOTPDialog(mobNo = "909099")
                    //                    }
                    //                }
                    //endregion
                }

            }

            binding.includeLoginNew.tvSignUp.id ->{

                if (!isNetworkAvailable(this)) {
                    Snackbar.make(view, getString(R.string.noInternet), Snackbar.LENGTH_SHORT)
                        .show()
                    return
                }

                if (enable_pro_signupurl != null) {
                    if (enable_pro_signupurl.isEmpty()) {
                       // startActivity(Intent(this, RegisterActivity::class.java))    //temp05
                    } else {
                        val signupurl: String =
                            enable_pro_signupurl + "&app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=&fbaid="
                        Utility.loadWebViewUrlInBrowser(this@LoginActivity, signupurl)
                    }
                } else {
                    //startActivity(Intent(this, RegisterActivity::class.java))   //temp05
                }


                trackEvent("")
            }

            binding.includeLoginNew.lyRaiseTicket.id ->{

                val url =
                    "https://origin-cdnh.policyboss.com/fmweb/Ticketing/ticket_login.html?landing_page=login_page&app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=&fbaid="
                Log.d("URL", "Raise Ticket URL: $url")


                startActivity(
                    Intent(this, RaiseTicketDialogActivity::class.java)
                        .putExtra("URL", url)
                )
            }

            binding.includeLoginNew.txtprivacy.id ->{

                    startActivity(Intent(this, PrivacyWebViewActivity::class.java).apply {
                        putExtra("URL", "https://www.policyboss.com/privacy-policy-policyboss-pro?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=&fbaid=")
                        putExtra("NAME", "privacy-policy")
                        putExtra("TITLE", "privacy-policy")
                    })


            }

            binding.includeLoginNew.txtterm.id ->{

                startActivity(
                    Intent(this, PrivacyWebViewActivity::class.java)
                        .putExtra(
                            "URL",
                            "https://www.policyboss.com/terms-condition?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=&fbaid="
                        )
                        .putExtra("NAME", "" + "Terms & Conditions")
                        .putExtra("TITLE", "" + "Terms & Conditions")
                )

            }


        }
    }

    private fun unregisterSmsReceiver() {
        smsReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                Log.e(Constant.TAG, "Receiver not registered", e)
            }
        }
        smsReceiver = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSmsReceiver()
        binding.includeLoginNew.radioGroup.setOnCheckedChangeListener(null)

        if (::alertDialogPassword.isInitialized && alertDialogPassword.isShowing) {
            alertDialogPassword.dismiss()
        }
        alertDialogOTP?.dismiss()
        alertDialogOTP = null


    }


    override fun onResume() {
        super.onResume()

    }

    //endregion

    //region Other
    private fun trackEvent(status: String) {
        // Create event attributes
        val eventAttributes: MutableMap<String, Any> = HashMap()
        eventAttributes["Sign"] = status // Add any relevant attributes

        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Sign Up Initiated", eventAttributes)
    }

    //region comment
//    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true
//        } else {
//            return super.onKeyUp(keyCode, event)
//        }
//    }

    //endregion
    enum class LoginOption {
        OTP,
        Password,
        NoData
    }

    //endregion

   

}