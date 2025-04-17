package com.policyboss.policybosspro.view.splashscreen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.policyboss.policybosspro.databinding.ActivitySplashScreenBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant

import com.policyboss.policybosspro.view.home.HomeActivity
import com.policyboss.policybosspro.view.introslider.WelcomeActivity
import com.policyboss.policybosspro.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)



        // Launch coroutine for initialization sequence
        lifecycleScope.launch {
            handleInitialization()
        }


        //region comment
//        getToken()
//
//        if (prefManager.isFirstTimeLaunch()) {
//            startActivity(
//                Intent(this, WelcomeActivity::class.java)
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            )
//        }else{
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                this.finish()
//
//                if (prefManager.getEmpData() != null) {
//                    startActivity(
//                        Intent(this@SplashScreenActivity, HomeActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    )
//
//                }else{
//
//                    startActivity(
//                        Intent(this@SplashScreenActivity, LoginActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    )
//
//                }
//            }, 3000)
//        }

        //endregion



    }


    //**********************************************************//

    private suspend fun handleInitialization() {
        try {
            // First fetch token

            // Fetch token
             getToken()

            // Get dynamic link from Firebase
            getDynamicLinkFromFirebase()

            // Then handle navigation
            if (prefManager.isFirstTimeLaunch()) {

                Log.d(Constant.TAG,"isFirstTimeLaunch: true, Move Welcome")
                navigateToWelcome()
            } else {
                delay(2000) // Optional delay if you still want it
                Log.d(Constant.TAG,"isFirstTimeLaunch: false, Move Home")
                navigateBasedOnLoginStatus()
            }
        } catch (e: Exception) {
            Log.e("Initialization", "Error during initialization", e)
            // Handle error case - maybe show error UI or retry
        }
    }

    private fun navigateToWelcome() {
        startActivity(
            Intent(this, WelcomeActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun navigateBasedOnLoginStatus() {
        val targetActivity = if (prefManager.getEmpData() != null) {
            HomeActivity::class.java
        } else {

            Log.d(Constant.TAG,"Login Data , Not Employee")
            LoginActivity::class.java
        }

        startActivity(
            Intent(this, targetActivity)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    //**********************************************************//





    private suspend fun getDynamicLinkFromFirebase() {
        try {
            val pendingDynamicLinkData = FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .await() // Use Kotlin Coroutines to await the result

            val deepLink: Uri? = pendingDynamicLinkData?.link ?: intent.data

            deepLink?.let {
                val deeplinkUrl = it.toString()
                Log.i("Dynamic Link", deeplinkUrl)
                prefManager.setDeeplink(deeplinkUrl)
                // Handle deep link navigation if needed
            } ?: run {
                prefManager.setDeeplink("")
                Log.d("Dynamic Link", "No dynamic link found")
            }
        } catch (e: Exception) {
            Log.d("Dynamic Link", "Failed to get dynamic link", e)
            // Handle the error case, e.g., show a toast or fallback to a default behavior
           // Toast.makeText(this, "Failed to fetch dynamic link", Toast.LENGTH_SHORT).show()
        }
    }



    private fun testDeeplink(){

        val deeplinkUrl =  "https://www.policyboss.com/UI22/sync?product_id=506"
        prefManager.setDeeplink(deeplinkUrl)
    }


    private suspend fun getToken() {
        try {
            // Fetch the FCM token in a coroutine
            val token = FirebaseMessaging.getInstance().token.await() // Use Kotlin Coroutines to await the result

            token?.let {
                // Save the token in SharedPreferences
                prefManager.setToken(it)
                Log.d("FCMToken", "Token fetched on app start: $it")
            } ?: Log.e("FCMToken", "Fetching FCM token failed: Token is null")
        } catch (e: Exception) {
            Log.e("FCMToken Error", "Error fetching FCM token", e)
        }
    }


    //region comment
    private suspend fun getTokenOLD(){

       // Fetch the FCM token in the background
      // lifecycleScope.launch(Dispatchers.IO) {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result

                    token?.let {
                        // Save the token in SharedPreferences
                        prefManager.setToken(it)
                        Log.d("FCMToken", "Token fetched on app start: $it")
                    }
                } else {
                    Log.e("FCMToken", "Fetching FCM token failed", task.exception)
                }
            }
        }
        catch (e: Exception) {
            Log.e("FCMToken Error", "Error fetching FCM token", e)
            Log.d("FCMToken Error", "Error fetching FCM token", e)
        }
      // }

   }

    private suspend  fun getDynamicLinkFromFirebaseOLD() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                Log.i("Sync", "We have link")
                var deepLink: Uri? = null


                //note: For Testing we have added below method :  testDeeplink()
                // testDeeplink()

                // Check if we received a dynamic link from Firebase
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                if (deepLink != null) {
                    // Dynamic link exists, process it
                    val deeplinkUrl = deepLink.toString()
                    Log.i("dynamic url", deeplinkUrl)

                    // Save to preferences or handle the deeplink
                    prefManager.setDeeplink(deeplinkUrl)

                    // Call method to handle deep link (navigation, etc.)

                } else {
                    // No dynamic link, check for regular intent URI
                    val uri = intent.data
                    if (uri != null) {
                        val deeplinkUrl = uri.toString()
                        Log.i("intent url", deeplinkUrl)

                        // Save and handle the normal intent
                        prefManager.setDeeplink(deeplinkUrl)

                    } else {
                        // No deep link or URI found, proceed with normal flow

                    }
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w("HomeActivity", "getDynamicLink:onFailure", e)
                // Proceed normally if dynamic link fetching failed
                // handleNoDeepLink()
            }
    }

    //endregion




}