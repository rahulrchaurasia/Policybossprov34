package com.policyboss.policybosspro.view.home

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.PolicyBossProApplication
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.analytics.WebEngageAnalytics.Companion.getInstance
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.model.notification.NotifyEntity
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity
import com.policyboss.policybosspro.core.response.home.UserCallingEntity
import com.policyboss.policybosspro.core.viewModel.NotificationVM.FCMNotificationViewModel
import com.policyboss.policybosspro.core.viewModel.NotificationVM.NotifyViewModel
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.databinding.CallingUserDetailDialogBinding
import com.policyboss.policybosspro.databinding.DrawerHeaderBinding
import com.policyboss.policybosspro.databinding.LayoutFailurePopupBinding
import com.policyboss.policybosspro.databinding.LayoutMenuDashboard3Binding
import com.policyboss.policybosspro.databinding.LayoutMysyncPopupBinding
import com.policyboss.policybosspro.databinding.LayoutSharePopupBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utility.UtilityNew
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.CoroutineHelper
import com.policyboss.policybosspro.utils.FeedbackHelper
import com.policyboss.policybosspro.utils.FirebasePushNotification.AccessToken
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.utils.getLocalIpAddress
import com.policyboss.policybosspro.utils.hideKeyboard
import com.policyboss.policybosspro.utils.showSnackbar
import com.policyboss.policybosspro.utils.showToast
import com.policyboss.policybosspro.view.appCode.AppCodeActivity
import com.policyboss.policybosspro.view.changePwd.ChangePaswordActivity
import com.policyboss.policybosspro.view.home.adapter.CallingDetailAdapter
import com.policyboss.policybosspro.view.home.adapter.DashboardRowAdapter
import com.policyboss.policybosspro.view.knowledgeGuru.KnowledgeGuruActivity
import com.policyboss.policybosspro.view.login.LoginActivity
import com.policyboss.policybosspro.view.myAccount.MyAccountActivity
import com.policyboss.policybosspro.view.notification.NotificationActivity
import com.policyboss.policybosspro.view.others.feedback.HelpFeedBackActivity
import com.policyboss.policybosspro.view.others.incomePotential.IncomePotentialActivity
import com.policyboss.policybosspro.view.salesMaterial.SalesMaterialActivity
import com.policyboss.policybosspro.view.splashscreen.SplashScreenActivity
import com.policyboss.policybosspro.view.syncContact.ui.SyncContactActivity
import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import com.webengage.sdk.android.Channel
import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, OnClickListener {


    //region Declare variables
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dashboardAdapter: DashboardRowAdapter
    private lateinit var shareProdDialog: AlertDialog
    private var mySyncPopUpAlert: AlertDialog? = null
    private var myUtilitiesDialog: AlertDialog? = null
    private var callingDetailDialog : AlertDialog? = null
    private lateinit var callingDetailAdapter: CallingDetailAdapter


    private var isSwipeRefresh = false

    private val viewModel by viewModels<HomeViewModel>()


    private val viewModelFCMNotify: FCMNotificationViewModel by viewModels()

    private val viewModelNotify : NotifyViewModel by viewModels()
    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager
    lateinit var weUser: User

    @Inject
   lateinit var myApplication: PolicyBossProApplication


    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var navigationView : NavigationView     //layout navigation_view
    private lateinit var headerBinding: DrawerHeaderBinding // layout name drawer_header in activity_xml

    var shortcutManager: ShortcutManager? = null

    var Title = ""


    private lateinit var permissionHandler: PermissionHandler
    //for Notification Counter

    private  var  textNotifyItemCount : TextView? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var currentSelectedItemId: Int = R.id.nav_home

    //endregion

    private val notificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result here
            val data = result.data

            textNotifyItemCount?.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        // region Set the toolbar as ActionBar
        setSupportActionBar(binding.toolbar)

        // Initialize ActionBarDrawerToggle
        toggle = object : ActionBarDrawerToggle(
            this, binding.drawer, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                // Handle the action when the drawer is closed
                // You can leave this blank if no action is needed
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // Handle the action when the drawer is opened
                try {
                    hideNavigationDrawerItem() // Custom logic to hide navigation items
                } catch (ex: Exception) {
                    ex.printStackTrace() // Handle exceptions gracefully
                }
            }
        }

       // myApplication = PolicyBossProApplication.instance!!
        // Attach the toggle to the drawer
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()


        //endregion

        weUser = WebEngage.get().user()

        // Initialize the navigation view
        navigationView = binding.navigationView

        //Initialize headerView
        headerBinding = DrawerHeaderBinding.bind(navigationView.getHeaderView(0))


        initHeaderLayout()

        setupHeaderLayoutListeners()

        setupNavigationViewTheme()

        permissionHandler = PermissionHandler(this)
        requestNotificationPermission()


        //region Handle OnBackPressed()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Check if the drawer is open
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    binding.drawer.closeDrawer(GravityCompat.START)
                } else {
                    // If drawer is not open, allow the system to handle the back press
                   // isEnabled = false // Disable this callback
                   // onBackPressedDispatcher.onBackPressed() // Call the default back press

                   showAlert(msg="Do you really want to exit?",title = "Exit App",positiveBtn = resources.getString(R.string.yes) , showNegativeButton = true,
                       onPositiveClick = {
                           this@HomeActivity.finish()
                       })
                }
            }
        })
        //endregion

        if (prefsManager.getSSID().toInt() != 0) {
            if (prefsManager.getSSID().toInt() == 5) {
                verifyPospNo()
                return
            }
            CoroutineHelper.saveDeviceDetails(this@HomeActivity, prefsManager.getSSID(), "Active")

        }



        observeMasterState()

        //Called Master Data ie UserConstant and Dynamic Dashb oard Parallel
        viewModel.getMasterData()



        setonClickListner()

        // Initialize the launcher
        //Mark : Reset Counter when come home after notification Activity
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here, you can get the data as:
                val data: Intent? = result.data
                // Process data if needed
            }
        }




       // binding.navigationView.menu.

//        val navMenu = binding.navigationView.menu
//        val contactMenuItem = navMenu.findItem(R.id.nav_MYUtilities)
//
//        // Check if the item is available, then set it as checked
//        contactMenuItem?.isChecked = true


       // setSelectedNavigationItem(R.id.nav_myaccount)


    }

    override fun onResume() {
        super.onResume()


    }

    private fun setupNavigationViewTheme() {
        // Get current night mode
        val isDarkMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            navigationView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.header_light_text))

            navigationView.itemTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            navigationView.itemIconTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

        } else {
            navigationView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

            navigationView.itemTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.header_dark_text))
            navigationView.itemIconTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.header_dark_text))
        }

    }


    //region Permission POST_NOTIFICATIONS
    private fun requestNotificationPermission() {
        permissionHandler.checkAndRequestPermissions(
            AppPermissionManager.PermissionType.POST_NOTIFICATIONS,
            onResult = { granted ->
                if (granted) {
                    // Permission granted, proceed with notifications
                } else {
                    // Handle permission denial
                   // we can also show here snackbar before asking message again...
                }
            },
            onPermanentlyDenied = { permanentlyDeniedPermissions ->
                // Show an explanation or direct the user to the settings to grant permissions
                showPermissionDeniedDialog(permanentlyDeniedPermissions)
            }
        )


    }

    private fun showPermissionDeniedDialog(permanentlyDeniedPermissions: List<String>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Denied")
            .setMessage("Please enable notification permissions in Settings to receive notifications.")
            .setPositiveButton("Open Settings") { _, _ ->
                permissionHandler.openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //endregion

//*************** DashBoard List Adapter Home/Dashboard Menu Action *****************************************

    // Action :  Move To diff page Using Home - DashBoard List
    // region Dashboard Menu
    private fun dashBoardMenusList(dashboardEntity: DashboardMultiLangEntity?) {

        dashboardEntity?.let { entity ->
            try {

                var ipaddress = getLocalIpAddress()?: ""
                val parent_ssid = ""
                val subSSID = prefsManager.getSUBUserSSId()
                val subFBAID = prefsManager.getSUBUserFBAID()
                val deviceId = Utility.getDeviceID(this@HomeActivity)
                val appVersion = "policyboss-${BuildConfig.VERSION_NAME}"

                when (entity.productId) {
                    1 -> {
                        // Car
                        var motorUrl = prefsManager.getFourWheelerUrl()



                        motorUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid,subSSID,subFBAID)

                        openCommonWebView(
                            motorUrl,
                            "Motor Insurance",
                            "Motor Insurance",
                            Constant.INSURANCE_TYPE
                        )



                        trackMainMenuEvent("Motor Insurance")

                    }

                    23 -> {

                        // Kotak
                        var kotakUrl = prefsManager.getUserConstantResponse()?.MasterData?.EliteKotakUrl ?: ""
                        kotakUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid ,subSSID,subFBAID)

                        openCommonWebView(
                            kotakUrl,
                            "Kotak Group health Care",
                            "Kotak Group health Care",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Kotak Group health Care")


                    }

                    2 ->  {

                        // health
                        var healthUrl = prefsManager.getHealthurl()
                        healthUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid ,subSSID,subFBAID)

                        openCommonWebView(
                            healthUrl,
                            "Health Insurance",
                            "Health Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Health Insurance")


                    }

                    10 -> {

                        //bike
                        var bikeUrl = prefsManager.getTwoWheelerUrl()
                        bikeUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid ,subSSID,subFBAID)

                        openCommonWebView(
                            bikeUrl,
                            "Two Wheeler Insurance",
                            "Two Wheeler Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Two Wheeler Insurance")


                    }

                    12 -> {

                        //bike
                        var cvUrl = prefsManager.getCVUrl()
                        cvUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid ,subSSID,subFBAID)

                        openCommonWebView(
                            cvUrl,
                            "Commercial Vehicle Insurance",
                            "Commercial Vehicle Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Commercial Vehicle Insurance")

                    }

                    5 -> {

                        //INVESTMENT PLANS
                        if(prefsManager.getInvestmentEnabled() == "1" ){

                            var  invUrl = prefsManager.getInvestmentUrl()

                            val append = "&ip_address=$ipaddress" +
                                    appVersion +
                                    "&device_id=${Utility.getDeviceID(this@HomeActivity)}" +
                                    "&login_ssid=$parent_ssid"
                            invUrl = invUrl + append

                            openCommonWebView(
                                invUrl,
                                "INVESTMENT PLANS",
                                "INVESTMENT PLANS",
                                Constant.INSURANCE_TYPE
                            )

                            trackMainMenuEvent("INVESTMENT PLANS")


                        }


                    }

                    41 -> {
                        //Synch Contact
                        startActivity(
                            Intent(
                                this@HomeActivity,
                                WelcomeSyncContactActivityKotlin::class.java
                            )
                        )
                        trackMainMenuEvent("Sync Contact")

                    }


                    else ->{

                        if (entity.productId < 100 && entity.productId != 41) {
                            entity.isNewPrdClickable?.let { clickable ->
                                if (clickable == "Y") {
                                    // Fetch dynamic product URL
                                    var dynamicUrl = ""
                                    val userConstantsData = prefsManager.getDashboardarray()
                                    for (dashboardItem in userConstantsData) {
                                        if (dashboardItem.ProdId?.toInt() == entity.productId) {
                                            //dynamicUrl = dashboardItem.url ?: ""
                                            //005 temp
                                            dynamicUrl = dashboardItem.url.replace("&sub_fba_id=0", "") ?: ""

                                            break
                                        }
                                    }

                                    if (dynamicUrl.isNotEmpty()) {
                                        ipaddress = try {
                                            getLocalIpAddress()?: ""// Add logic for fetching ip address here
                                        } catch (e: Exception) {
                                            "0.0.0.0"
                                        }

                                       //005 temp
                                        val append = "&sub_fba_id=$subFBAID" +
                                                "&ip_address=$ipaddress&mac_address=$ipaddress" +
                                                "&app_version=$appVersion&device_id=$deviceId&product_id=${entity.productId}&login_ssid=$parent_ssid" +
                                                "&sub_ss_id=$subSSID"
                                        dynamicUrl += append


                                        openCommonWebView(
                                            dynamicUrl,
                                            entity.productName,
                                            entity.productName,
                                            Constant.INSURANCE_TYPE
                                        )
                                    }
                                }
                            }
                        }
                        else if (entity.productId >= 100) {
                            trackMainMenuEvent(entity.productName)

                            openCommonWebView(
                                entity.link ?: "",
                                entity.productName,
                                entity.productName,
                                Constant.INSURANCE_TYPE
                            )
                        }
                    }


                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun buildUrlAppend(ipaddress: String, deviceId: String, appVersion: String, productId: Int, parentSsid: String = "",subSSID : String  = "0",subFBAID : String = "0" ): String {
        return "&sub_fba_id=$subFBAID" +
               "&ip_address=$ipaddress&mac_address=$ipaddress" +
                "&app_version=$appVersion" +
                "&device_id=$deviceId" +
                "&product_id=$productId&login_ssid=$parentSsid" +
                "&sub_ss_id=$subSSID"

    }

    //005 temp
    private fun buildUrlAppend1(ipaddress: String, deviceId: String, appVersion: String, productId: Int, parentSsid: String): String {
        return "&ip_address=$ipaddress&mac_address=$ipaddress" +
                "&app_version=$appVersion" +
                "&device_id=$deviceId" +
                "&product_id=$productId&login_ssid=$parentSsid"
    }

    // Helper method to open CommonWebViewActivity
    private fun openCommonWebView(url: String, name: String, title: String, dashboardType: String) {
        startActivity(
            Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("dashBoardtype", dashboardType)
                putExtra("NAME", name)
                putExtra("TITLE", title)
            }
        )
    }


    //endregion
 // ******************************End ************************************************************


    //region Drawer Menu handling
     //Mark : Drawer Menu onNavigationItemSelected Event
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        Log.d(Constant.TAG,"Product ${ menuItem.itemId}")

        if (!NetworkUtils.isNetworkAvailable(this@HomeActivity)) {
            showSnackbar(binding.root, getString(R.string.noInternet))
            return false
        }



        // Toggle checked state of the menu item
       toggleMenuItemChecked(menuItem)

        // Hide the keyboard
        hideKeyboard(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({



            currentSelectedItemId = menuItem.itemId
            // Add dynamic drawer menu items
            if (handleDynamicMenu(menuItem)) {

                return@postDelayed
            }

            // Handle specific menu item clicks
            handleMenuItemClick(menuItem)

        // Close the drawer after item selection
        binding.drawer.closeDrawer(GravityCompat.START)

            // Close drawer
            binding.drawer.closeDrawer(GravityCompat.START)
        }, 150)




        return true
    }
    //Mark: Drawer -Menu Click Navigate to Specific Activity / WebView Handling

    //region Drawer -Menu Methods

    private fun toggleMenuItemChecked(menuItem: MenuItem) {
       // menuItem.isChecked = !menuItem.isChecked

        val menu = binding.navigationView.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menuItem.isChecked = true
    }

    private fun handleDynamicMenu(menuItem: MenuItem): Boolean {
        prefsManager.getMenuDashBoard()?.MasterData?.Menu?.forEach { menuItemEntity ->
            var sequence = menuItemEntity.sequence?.toInt()
            if (sequence != null) {
                sequence = (sequence * 100) + 1
            }
            if (menuItem.itemId == sequence) {
                val appendMenu = "&ss_id=${prefsManager.getSSID()}" +
                        "&fba_id=${prefsManager.getFBAID()}" +
                        "&sub_fba_id=${prefsManager.getSUBUserFBAID()}" +
                        "ip_address=&mac_address=" +
                        "&app_version=policyboss-${BuildConfig.VERSION_NAME}" +
                        "&device_id=${Utility.getDeviceID(this@HomeActivity)}" +
                        "&login_ssid=&sub_ss_id=${prefsManager.getSUBUserSSId()}"

                val menuDetail = "${menuItemEntity.link}$appendMenu"

                startActivity(
                    Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                        putExtra("URL", menuDetail)
                        putExtra("NAME", menuItemEntity.menuname)
                        putExtra("TITLE", menuItemEntity.menuname)
                    }
                )
                return true
            }
        }
        return false
    }

    private fun clearNavigationSelection() {
        val menu = binding.navigationView.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
    }

    fun setSelectedNavigationItem(itemId: Int) {
        currentSelectedItemId = itemId
        binding.navigationView.setCheckedItem(itemId)
    }


    private fun handleMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> viewModel.getMasterData()

            R.id.nav_finbox -> {
                startCommonWebViewActivity(prefsManager.getFinboxurl(), "MY FINBOX")
            }

            R.id.nav_finperk -> {
                startCommonWebViewActivity(prefsManager.getFinperkurl(), "FINPERKS")
            }

            R.id.nav_festivelink -> {
                startCommonWebViewActivity(prefsManager.getFinperkurl(), "FESTIVE LINKS")
            }

            R.id.nav_AppointmentLetter -> {
                // Handle nav item 2 click
            }

            R.id.nav_contact -> {

                // startActivity(new Intent(HomeActivity.this, ContactLeadActivity.class));
                trackSyncContactEvent("Sync Contacts on Side Menu")
                startActivity(
                    Intent(
                        this@HomeActivity,
                        WelcomeSyncContactActivityKotlin::class.java
                    )
                )
            }



            R.id.nav_REQUEST -> {
                startActivity(Intent(this@HomeActivity, AppCodeActivity::class.java))
            }

            R.id.nav_changepassword -> {
                startActivity(Intent(this@HomeActivity, ChangePaswordActivity::class.java))
            }

            R.id.nav_myaccount -> {

                startActivity(Intent(this@HomeActivity, MyAccountActivity::class.java))
            }

            R.id.nav_pospenrollment -> {
                startPospEnrollment()
            }

            R.id.nav_leaddetail -> {
                startLeadDetailActivity()
            }

            R.id.nav_raiseTicket -> {
                startRaiseTicketActivity()
            }
            R.id.nav_MYUtilities -> {
                trackUtilityContactEvent()
                showMyUtilitiesDialog()
            }


            R.id.nav_disclosure -> {
                openCommonWebView(
                    "file:///android_asset/Disclosure.html",
                    "DISCLOSURE",
                    "DISCLOSURE",
                    "" // No dashboard type for disclosure
                )
            }

            R.id.nav_policy -> {
                openCommonWebView(
                    "https://www.policyboss.com/privacy-policy-policyboss-pro?app_version=${prefsManager.getAppVersion()}&device_code=${prefsManager.getDeviceID()}&ssid=${prefsManager.getSSID()}&fbaid=${prefsManager.getFBAID()}",
                    "PRIVACY POLICY",
                    "PRIVACY POLICY",
                    "" // No dashboard type for policy
                )
            }

            R.id.nav_delete -> {
                openCommonWebView(
                    "https://www.policyboss.com/initiate-account-deletion-elite?ss_id=${prefsManager.getSSID()}&app_version=${prefsManager.getAppVersion()}&device_code=${prefsManager.getDeviceID()}&fbaid=${prefsManager.getFBAID()}",
                    "ACCOUNT-DELETE",
                    "ACCOUNT-DELETE",
                    "" // No dashboard type for account deletion
                )
            }

            R.id.nav_logout -> {
                dialogLogout()
            }
        }
    }


    fun hideNavigationDrawerItem() {
        val navMenu = navigationView.menu


        prefsManager.getUserConstantEntity()?.enableenrolasposp?.let {
            if (it.isNotEmpty()) {
                if (it.toInt() == 1) {
                    navMenu.findItem(R.id.nav_pospenrollment)?.isVisible = true
                } else {
                    navMenu.findItem(R.id.nav_pospenrollment)?.isVisible = false
                }
            }
        }

        prefsManager.getUserConstantEntity()?.androidproouathEnabled?.let {
            if (it.isNotEmpty()) {
                if (it == "0") {
                    navMenu.findItem(R.id.nav_REQUEST)?.isVisible = false
                } else {
                    navMenu.findItem(R.id.nav_REQUEST)?.isVisible = true
                }
            }

        }


    }

    //endregion

    //Mark Open Specfic Activity/WebView Using below Helper Methods
    //region  Drawer -Menu Action open Specfic Activity/WebView
    private fun startCommonWebViewActivity(url: String, title: String) {
        startActivity(
            Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                putExtra(Constant.URL, url)
                putExtra("NAME", title)
                putExtra("TITLE", title)
            }
        )
    }


    private fun startPospEnrollment() {
        val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", prefsManager.getEnableProPOSPurl() +
                    "&app_version=" + prefsManager.getAppVersion() +
                    "&device_code=" + Utility.getDeviceID(this@HomeActivity) +
                    "&ssid=" + prefsManager.getSSID() +
                    "&fbaid=" + prefsManager.getFBAID())
            putExtra("NAME", "PospEnrollment")
            putExtra("TITLE", "Posp Enrollment")
        }
        startActivity(intent)
    }


    private fun startLeadDetailActivity() {
        val append_lead = "&ip_address=&mac_address=&app_version=policyboss-" +
                BuildConfig.VERSION_NAME +
                "&device_id=" + Utility.getDeviceID(this@HomeActivity) +
                "&login_ssid="
        val leaddetail = prefsManager.getLeadDashUrl() + append_lead

        val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", leaddetail)
            putExtra("NAME", "Sync Contact DashBoard")
            putExtra("TITLE", "Sync Contact DashBoard")
        }
        startActivity(intent)
    }


    private fun startRaiseTicketActivity() {
        val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", prefsManager.getRaiseTickitUrl() +
                    "&mobile_no=" + prefsManager.getMobileNo() +
                    "&UDID=" + prefsManager.getUserId() +
                    "&app_version=" + prefsManager.getAppVersion() +
                    "&device_code=" + Utility.getDeviceID(this@HomeActivity) +
                    "&ssid=" + prefsManager.getSSID() +
                    "&fbaid=" + prefsManager.getFBAID())
            putExtra("NAME", "RAISE_TICKET")
            putExtra("TITLE", "RAISE TICKET")
        }
        startActivity(intent)
    }

    //endregion

    //endregion

    //region  headerView Menu Handling
    private fun initHeaderLayout() {


        with(headerBinding) {

            txtEntityName.text = "Ver.${Utility.getVersionName(this@HomeActivity)}"

            if (prefsManager.getEmpData() != null) {
                txtDetails.text = prefsManager.getEmpData()?.Emp_Name ?: ""
                txtFbaID.text = "Fba Id - ${prefsManager.getFBAID()}"
                txtReferalCode.text = "Referral Code -"

                weUser.login(prefsManager.getEmpData()?.Email_Id ?: "")
                weUser.setOptIn(Channel.WHATSAPP, true)

                weUser.setAttribute("Is Agent",
                    when (prefsManager.getUserType()) {
                    "POSP", "FOS" -> true
                    else -> false
                   }
                )


            } else {
                txtDetails.text = ""
                txtFbaID.text = "Fba Id - "
                txtReferalCode.text = "Referral Code - "
            }

            prefsManager.getUserConstantEntity()?.let {
                try {
                    txtPospNo.text = "Posp No - ${prefsManager.getSSID()}"
                    txtErpID.text = "Erp Id - ${prefsManager.getERPID()}"

                    val fullname = prefsManager.getName().split("\\s+".toRegex())
                    weUser.setFirstName(fullname[0])
                    weUser.setLastName(fullname.getOrNull(1) ?: "")

                    weUser.setAttribute("POSP No.", prefsManager.getSSID().toIntOrNull() ?: 0)
                    weUser.setPhoneNumber(prefsManager.getEmpData()?.Mobile_Number ?: "" )
                    weUser.setEmail(prefsManager.getEmpData()?.Email_Id ?: "")

                    Glide.with(this@HomeActivity)
                        .load(prefsManager.getUserConstantEntity()?.loansendphoto)
                        .placeholder(R.drawable.circle_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(64, 64)
                        .transform(CircleCrop()) // applying image transformer
                        .into(ivProfile)

                } catch (e: Exception) {
                    // Handle exception
                }

            } ?: run {
                try {
                    txtPospNo.text = ""
                    txtErpID.text = ""

                    Glide.with(this@HomeActivity)
                        .load(R.drawable.finmart_user_icon)
                        .placeholder(R.drawable.circle_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(64, 64)
                       // .transform(CircleTransform(this@HomeActivity))
                        .into(ivProfile)

                } catch (e: Exception) {
                    // Handle exception
                }
            }

        }
    }

    private fun setupHeaderLayoutListeners() {
        with(headerBinding) {
            txtknwyour.setOnClickListener {

                binding.drawer.closeDrawer(GravityCompat.START)
                val url = "${prefsManager.getNotif_popupurl_elite()}&app_version=${prefsManager.getAppVersion()}" +
                        "&device_code=${prefsManager.getDeviceID()}&ssid=${prefsManager.getDeviceID()}&fbaid=${prefsManager.getFBAID()}"
                openWebViewPopUp(txtFbaID, url, true, "")
            }

            ivProfile.setOnClickListener {
                binding.drawer.closeDrawer(GravityCompat.START)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val shareIntent = Intent(this@HomeActivity, MyAccountActivity::class.java)
                    val pairs = arrayOf(android.util.Pair(ivProfile as View, "profileTransition"))
                    val options = ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, *pairs)
                    startActivity(shareIntent, options.toBundle())
                } else {
                    startActivity(Intent(this@HomeActivity, MyAccountActivity::class.java))
                }
            }

        }
    }



    //endregion

    //region Deeplik Handling



    //endregion


    //region Handle DeepLink and Notification
    private fun deeplinkHandle() {
        val deeplinkValue = prefsManager.getDeepLink()

        val subSSID = prefsManager.getSUBUserSSId()
        val subFBAID = prefsManager.getSUBUserFBAID()

        if (deeplinkValue != null && deeplinkValue.isNotEmpty()) {

            try {
                val myUri = Uri.parse(deeplinkValue)

                val prdID = myUri.getQueryParameter("product_id")
                val titleValue = myUri.getQueryParameter("title") ?: ""

                Title = titleValue

                when (prdID) {
                    "41" -> startActivity(Intent(this, WelcomeSyncContactActivityKotlin::class.java))
                    "501" -> startActivity(Intent(this, MyAccountActivity::class.java))
                    "502" -> {
                        val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                            putExtra("URL", prefsManager.getEnableProPOSPurl() +
                                    "&app_version=" + prefsManager.getAppVersion() +
                                    "&device_code=" + Utility.getDeviceID(this@HomeActivity) +
                                    "&ssid=" + prefsManager.getSSID() +
                                    "&fbaid=" + prefsManager.getFBAID() +
                                     "&sub_fba_id=${subFBAID}" +
                                     "&sub_ss_id=${subSSID}" )
                            putExtra("NAME", "Posp Enrollment")
                            putExtra("TITLE", "Posp Enrollment")
                        }
                        startActivity(intent)
                    }
                    "503" -> startActivity(Intent(this, NotificationActivity::class.java))
                    "504" -> startActivity(Intent(this, SalesMaterialActivity::class.java))
                    "505" -> FeedbackHelper.showFeedbackDialog(this)
                    else -> {
                        val ipAddress = try {
                            getLocalIpAddress()?: ""  // Replace with actual logic to get IP address
                        } catch (e: Exception) {
                            "0.0.0.0"
                        }

                        val append = "&ss_id=${prefsManager.getSSID()}&fba_id=${prefsManager.getFBAID()}" +
                                "&sub_fba_id=${subFBAID}&ip_address=$ipAddress&mac_address=$ipAddress" +
                                "&app_version=${prefsManager.getAppVersion()}&device_id=${Utility.getDeviceID(this)}" +
                                "&login_ssid=&sub_ss_id=${subSSID}"

                        // Update deeplinkValue with appended parameters
                        val updatedDeeplinkValue = deeplinkValue + append

                        // Delayed execution using Coroutine
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this, CommonWebViewActivity::class.java).apply {
                                putExtra("URL", updatedDeeplinkValue)
                                putExtra("NAME", Title)
                                putExtra("TITLE", Title)
                            })
                        }, 100)
                    }
                }
            } catch (ex: Exception) {
                Log.d("Deeplink", ex.toString())
            } finally {
                prefsManager.clearDeeplink() // Clear deeplink at the end
            }
        }
    }

    private fun getNotificationAction() {
        // region Activity Open Using Notification
        intent.extras?.let { extras ->

            // For getting User Click Action

            val notifyEntity: NotifyEntity? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(Constant.PUSH_NOTIFY, NotifyEntity::class.java)
            } else {
                @Suppress("DEPRECATION")
                extras.getParcelable(Constant.PUSH_NOTIFY)
            }

            notifyEntity?.let {
                val messageId = it.message_id?:"0"
               // RegisterController(this@HomeActivity).getUserClickActionOnNotification(messageId, null)
                viewModel.userClickActionOnNotification(
                    notifyReqID = messageId,
                    appVersion = prefsManager.getAppVersion(),
                    deviceCode = prefsManager.getDeviceID())
            }

            // Step 1: Check if the user is logged in
            if (prefsManager == null) {
                notifyEntity?.let { entity ->
                    prefsManager.setPushNotifyPreference(entity)
                    prefsManager.setSharePushType(entity.notifyFlag?:"")

                    val intent = Intent(this, SplashScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            // Step 2: Handle login notification action
            else if (extras.getString(Constant.PUSH_LOGIN_PAGE) != null) {
                val pushLogin = extras.getString(Constant.PUSH_LOGIN_PAGE)
                if (pushLogin == "555") {
                    var type = ""
                    var title = ""
                    var body = ""
                    var webUrl = ""
                    var webTitle = ""

                    prefsManager.getPushNotifyPreference()?.let { entity ->
                        type = entity.notifyFlag ?:""
                        title = entity.title ?: ""
                        body = entity.body ?: ""
                        webUrl = entity.web_url ?: ""
                        webTitle = entity.web_title ?: ""
                    }

                    prefsManager.clearNotification()

                } else {

                }
            }
            // region User already logged in and app is in foreground/background
            else if (notifyEntity != null) {
                when (notifyEntity.notifyFlag?.trim()) {
                    "NL" -> {
                        val intent = Intent(this, NotificationActivity::class.java)
                        startActivity(intent)
                    }
                    "PF" -> {
                        val intent = Intent(this, MyAccountActivity::class.java)
                        startActivity(intent)
                    }
                    "SL" -> {
                        val intent = Intent(this, SalesMaterialActivity::class.java)
                        startActivity(intent)
                    }
                    "SY" -> {
                        val intent = Intent(this, WelcomeSyncContactActivityKotlin::class.java)
                        startActivity(intent)
                    }
                    "SYC" -> {
                        val intent = Intent(this, SyncContactActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        notifyEntity.web_url?.let { webUrl ->
                            navigateViaNotification(notifyEntity.notifyFlag?:"", webUrl, notifyEntity.web_title ?: "")
                        }
                    }
                }
            } else {

            }
        }
        // endregion
    }

    private fun navigateViaNotification(prdID: String, webURL: String, title: String) {

        val subSSID = prefsManager.getSUBUserSSId()
        val subFBAID = prefsManager.getSUBUserFBAID()

        when (prdID) {
            "WB" -> {
                startActivity(
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra("URL", webURL)
                        .putExtra("NAME", title)
                        .putExtra("TITLE", title)
                )
            }
            "CB" -> {
                Utility.loadWebViewUrlInBrowser(this, webURL)
            }
            else -> {
                if (webURL.trim().isEmpty() || title.trim().isEmpty()) {
                    return
                }

                val ipAddress: String = try {
                    // You can replace the following line with the actual way to retrieve the IP address if needed
                    getLocalIpAddress() ?: ""// Assuming you want to leave it empty as per your Java code
                } catch (io: Exception) {
                    "0.0.0.0"
                }

                // Construct the URL with additional parameters
                val append = "&ss_id=${prefsManager.getSSID()}&fba_id=${prefsManager.getFBAID()}" +
                        "&sub_fba_id=${subFBAID}&ip_address=$ipAddress&mac_address=$ipAddress&app_version=${prefsManager.getAppVersion()}" +
                        "&device_id=${prefsManager.getDeviceID()}&product_id=$prdID&login_ssid=&sub_ss_id=${subSSID}"

                val updatedWebURL = webURL + append

                startActivity(
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra("URL", updatedWebURL)
                        .putExtra("NAME", title)
                        .putExtra("TITLE", title)
                )
            }
        }
    }



    //endregion
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)

        val menuItem = menu.findItem(R.id.action_push_notification)
        val menuNewItem = menu.findItem(R.id.action_new)

        val actionView = menuItem.actionView
        val actionViewNew = menuNewItem.actionView

        // Use viewBinding to reference views instead of findViewById
         textNotifyItemCount = actionView?.findViewById<TextView>(R.id.notify_badge)?.apply {
            visibility = View.GONE
        }

        val imgNew = actionViewNew?.findViewById<ImageView>(R.id.imgNew)

        if (imgNew != null) {
            Glide.with(this)
                .asGif()
                .load(R.drawable.newicon)
                .into(imgNew)
        }

        val pushCount = prefsManager.getNotificationCounter()

        if (pushCount == 0) {
            textNotifyItemCount?.visibility = View.GONE
        } else {
            textNotifyItemCount?.visibility = View.VISIBLE
            textNotifyItemCount?.text = pushCount.toString()
        }

        actionViewNew?.setOnClickListener {
            openWebViewPopUp(
                binding.root,
                "${prefsManager.getUserConstantEntity()?.notif_popupurl_elite}&app_version=${prefsManager.getAppVersion()}" +
                        "&device_code=${prefsManager.getDeviceID()}&ssid=${prefsManager.getSSID()}&fbaid=${prefsManager.getFBAID()}",
                true,
                ""
            )
        }

        actionView?.setOnClickListener {
            try {
                onOptionsItemSelected(menuItem)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_call -> {
                if (!NetworkUtils.isNetworkAvailable(this)) {
                    this.showSnackbar(binding.root,getString(R.string.noInternet))
                    return false
                }
                prefsManager.getUserConstantEntity()?.let { user ->
                    if (user.MangMobile != null && user.ManagName != null) {
                        if (callingDetailDialog?.isShowing == true) {
                            return false
                        } else {
                            //************* call User Details Api //*************
                              viewModel.getUserCallingDetail()
                        }
                    }
                }
            }

            R.id.action_push_notification -> {
                if (!NetworkUtils.isNetworkAvailable(this)) {

                    this.showSnackbar(binding.root,getString(R.string.noInternet))
                    return false
                }

//                val intent = Intent(this, NotificationActivity::class.java)
//                startActivity(intent)

                val intent = Intent(this, NotificationActivity::class.java)
                notificationLauncher.launch(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    fun shareCallingData(userCallingEntity: UserCallingEntity) {
        val intentCalling = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${userCallingEntity.MobileNo}")
        }
        startActivity(intentCalling)
    }

    fun shareEmailData(userCallingEntity: UserCallingEntity) {
        UtilityNew.shareMailSmsList(
            context = this@HomeActivity,
            prdSubject = "",
            prdDetail = "Dear Sir/Madam,",
            mailTo = userCallingEntity.EmailId,
            mobileNo = userCallingEntity.MobileNo
        )
    }


    fun setonClickListner(){

        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.tvKnowledge.setOnClickListener(this)
        binding.tvSalesMat.setOnClickListener(this)
    }

    //region SetUpDashboard
    private fun setupDashBoard_Adapter() {


        dashboardAdapter = DashboardRowAdapter(
            mContext = this,
            insurancePosition = 0,
            disclosurePosition = 1,
            listIns =  viewModel.getInsurProductLangList(),
            prefsManager = prefsManager,
            onShareClick = ::onShareListener,
            onInfoClick = ::onInfoListener,
            onDashBoardClick = ::onDashBoardListener

            )
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = dashboardAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    //endregion

    //region Adapter Callback
    fun onShareListener(entity: DashboardMultiLangEntity){
        viewModel.setCurrentDashboardShareEntity(entity)
        shareProductPopUp(shareEntity = entity)

    }
    fun onInfoListener(entity: DashboardMultiLangEntity){

        openWebViewPopUp(binding.root, entity.info, true, "")
    }
    fun onDashBoardListener(entity: DashboardMultiLangEntity){

        dashBoardMenusList(dashboardEntity = entity)
    }

    //endregion

    //region Mark : All Alert Dialog

    // region Share Product and Forece Sync Dialog

    fun shareProductPopUp(shareEntity: DashboardMultiLangEntity) {
        if (this::shareProdDialog.isInitialized && shareProdDialog.isShowing) {
            return
        }

        // Initialize the binding for the custom layout
        val binding = LayoutSharePopupBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setView(binding.root)

        shareProdDialog = builder.create()

        // Set values using view binding
        binding.txtTitle.text = shareEntity.title
        binding.txtMessage.text = shareEntity.popupmsg

        // Set up share button click listener
        binding.btnShare.setOnClickListener {

           viewModel.getProductShareURL(shareEntity.productId.toString(),"0")
            shareProdDialog.dismiss()
        }

        // Set up close button (cross icon) click listener
        binding.ivCross.setOnClickListener {
            shareProdDialog.dismiss()
        }

        // Show the dialog
        shareProdDialog.setCancelable(true)
        shareProdDialog.show()
    }

    fun showMySyncPopUpAlert(syncContactEntity: SyncContactEntity) {
        try {
            if (mySyncPopUpAlert != null && mySyncPopUpAlert?.isShowing == true) {
                return
            }

            val builder = AlertDialog.Builder(this, R.style.CustomDialog)

            // ViewBinding usage
            val binding = LayoutMysyncPopupBinding.inflate(layoutInflater)
            builder.setView(binding.root)
            mySyncPopUpAlert = builder.create()

            // Extract data from syncContactEntity
            val actionNeeded = syncContactEntity.ACTION_NEEDED
            val firstSyncCampaignCreative = syncContactEntity.FIRST_SYNC_CAMPAIGN_CREATIVE
            val reSyncCampaignCreative = syncContactEntity.RE_SYNC_CAMPAIGN_CREATIVE

            var url = ""
            var titleText = ""

            // Set data based on actionNeeded
            if (actionNeeded == "RE_SYNC") {
                titleText = "Update Resync Contacts!!"
                url = "$reSyncCampaignCreative?${(Math.random() * 1000).roundToInt()}"
                binding.btnAllow.text = "Go To Resync Contacts"
            } else {
                titleText = "Update Sync Contacts!!"
                url = "$firstSyncCampaignCreative?${(Math.random() * 1000).roundToInt()}"
                binding.btnAllow.text = "Go To Sync Contacts"
            }

            // Load image using Glide
            Glide.with(this)
                .load(url)
                .into(binding.ivMessage)

            // Set title
            binding.txtTile.text = titleText

            // Handle cross (close) button click
            binding.ivCross.setOnClickListener {
                mySyncPopUpAlert?.dismiss()
            }

            // Handle allow button click
            binding.btnAllow.setOnClickListener {
                mySyncPopUpAlert?.dismiss()
                if (actionNeeded == "RE_SYNC") {
                   // startActivity(Intent(this, SyncContactActivity::class.java))
                } else {
                    startActivity(Intent(this, WelcomeSyncContactActivityKotlin::class.java))
                }
            }

            mySyncPopUpAlert?.apply {
                setCancelable(true)
                setCanceledOnTouchOutside(true)
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //endregion

    //region Utilities Alert
    private fun showMyUtilitiesDialog() {
        if (myUtilitiesDialog?.isShowing == true) return

        val binding = LayoutMenuDashboard3Binding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
            .setView(binding.root)
            .setCancelable(false)

        myUtilitiesDialog = builder.create()

        with(binding) {

            cvIncomeCalculator.setOnClickListener {
                myUtilitiesDialog?.dismiss()
                startActivity(Intent(this@HomeActivity, IncomePotentialActivity::class.java))
                trackUtilityIncomeEvent()
            }

            cvMyTrainingCalender.setOnClickListener {

            }

            cvHelpFeedback.setOnClickListener {
                myUtilitiesDialog?.dismiss()
                startActivity(Intent(this@HomeActivity, HelpFeedBackActivity::class.java))
                //trackFeedbackEvent()
            }

            ivCross.setOnClickListener {
                myUtilitiesDialog?.dismiss()
            }
        }

        myUtilitiesDialog?.show()
    }

    //endregion

    //region Posp Alert
    private fun verifyPospNo() {
        val builder = AlertDialog.Builder(this@HomeActivity, R.style.CustomDialog)

        // Using ViewBinding for the dialog view
        val dialogBinding = LayoutFailurePopupBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val verifyDialog = builder.create()

        // set the custom dialog components using ViewBinding
        dialogBinding.txtTitle.text = "Authorization"
        dialogBinding.txtMessage.text = getString(R.string.verify_SSID)

        dialogBinding.btnClose.setOnClickListener {
            verifyDialog.dismiss()
            val intent = Intent(this@HomeActivity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
        }

        dialogBinding.ivCross.setOnClickListener {
            verifyDialog.dismiss()
            val intent = Intent(this@HomeActivity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
        }

        verifyDialog.setCancelable(false)
        verifyDialog.show()
    }

    //endregion

    //region Market PopUp Alert
    fun marketPopUpAlert(){

        prefsManager.getUserConstantEntity()?.let {  userConstant->
            userConstant.androidpromarketEnable?.let {
                if (prefsManager.getUserId() == "0") {
                    if (!userConstant.androidpromarkefbaurl.isNullOrEmpty()) {
                        openWebViewPopUp_marketing(binding.root, userConstant.androidpromarkefbaurl, true, "")
                    }
                } else {
                    if (!userConstant.androidpromarketuidurl.isNullOrEmpty()) {
                        openWebViewPopUp_marketing(binding.root, userConstant.androidpromarketuidurl, true, "")
                    }
                }
            }
        }

    }

    //endregion



    fun showCallingDetailsPopUp(lstCallingDetail: List<UserCallingEntity>) {
        if (callingDetailDialog?.isShowing == true) return

        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        val binding = CallingUserDetailDialogBinding.inflate(layoutInflater)

        builder.setView(binding.root)
        callingDetailDialog = builder.create()

        // Set up RecyclerView
            with(binding.rvCalling) {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = CallingDetailAdapter(lstCallingDetail,
                onMobileClick = { userEntity ->

                    callingDetailDialog?.dismiss()
                    shareCallingData(userEntity) },
                onEmailClick = { userEntity ->

                    callingDetailDialog?.dismiss()
                    shareEmailData(userEntity)
                }
            )
        }

        binding.txtMessage.text = getString(R.string.RM_Calling)

        // Set up cross button listener
        binding.ivCross.setOnClickListener {
            callingDetailDialog?.dismiss()
        }

        callingDetailDialog?.setCancelable(false)
        callingDetailDialog?.show()
    }

    //region Logout Alert
    fun dialogLogout() {

        showAlert(msg="Do you really want to logout?",title = "PolicyBossPro",
            positiveBtn = resources.getString(R.string.logout) ,
            negativeBtn = resources.getString(R.string.cancel),
            showNegativeButton = true,
            onPositiveClick = {


                //region Clear All data
                prefsManager.clearAll()

                removeShortcuts()
                weUser.logout()

                //endregion

                // Navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

                this@HomeActivity.finish()
            })


        // Use MaterialAlertDialogBuilder instead of AlertDialog.Builder
//        val builder = MaterialAlertDialogBuilder(this@HomeActivity)
//
//        builder.setTitle("")
//            .setMessage("Do you really want to logout?")
//            .setCancelable(false)
//            .setPositiveButton("LOGOUT") { dialog, _ ->
//                dialog.dismiss()
//
//                // Use the singleton instance of PolicyBossPrefsManager
//                prefsManager.clearAll()
//
//                removeShortcuts()
//                weUser.logout()
//                // Navigate to LoginActivity
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                finish()
//            }
//            .setNegativeButton("CANCEL") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//        // Create and show the dialog
//        builder.show()
    }

    //endregion



    //endregion

    // region webEnagage Event


    private fun trackMainMenuEvent(optionClicked: String) {
        val eventAttributes = mapOf("Option Clicked" to optionClicked)
        WebEngageAnalytics.getInstance().trackEvent("Main Menu Clicked", eventAttributes)
    }


    private fun trackSyncContactEvent(strEvent: String) {
        // Create event attributes
        val eventAttributes = HashMap<String, Any>()
        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent(strEvent, eventAttributes)
    }


    private fun trackUtilityContactEvent() {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        // Track the event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Clicked My Utilities on Options Menu", eventAttributes)
    }

    private fun trackUtilityIncomeEvent() {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        // Track the event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Clicked on Income Calculator in My Utilities", eventAttributes)
    }

    private fun trackTopMenuEvent(strMenu: String) {
        // Create event attributes
        val eventAttributes: MutableMap<String, Any> = HashMap()
        eventAttributes["Menu Clicked"] = strMenu

        // Track the login event using WebEngageHelper
        getInstance().trackEvent("Top Menu Viewed", eventAttributes)
    }


    //endregion

    // region ShortcutMenu
    fun shortcutAppMenu() {
        try {
            if (prefsManager.getEmpData() != null && prefsManager.getUserConstantEntity() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    shortcutManager = getSystemService(ShortcutManager::class.java)

                    shortcutManager?.let {


                        val motorUrl = prefsManager.getFourWheelerUrl()
                        val bikeUrl =prefsManager.getTwoWheelerUrl()
                        val healthUrl = prefsManager.getHealthurl()

                        val expressUrl = prefsManager.getDashboardarray().firstOrNull {
                            it.ProdId.toInt() == 35
                        }?.url.orEmpty()

                        var ipAddress = try { "" } catch (e: Exception) { "0.0.0.0" }

                        val parentSsid = ""

                        val appendParams = "&ip_address=$ipAddress&mac_address=$ipAddress&app_version=policyboss-${BuildConfig.VERSION_NAME}&device_id=${Utility.getDeviceID(this@HomeActivity)}&login_ssid=$parentSsid"

                        val motorUrlWithParams = motorUrl + appendParams + "&product_id=1"
                        val bikeUrlWithParams = bikeUrl + appendParams + "&product_id=10"
                        val healthUrlWithParams = healthUrl + appendParams.replace("&mac_address", "") // Remove unused params
                        val expressUrlWithParams = expressUrl + appendParams + "&product_id=35"

                        val intentPrivateCar = createWebIntent(motorUrlWithParams, "Motor Insurance")
                        val intentBike = createWebIntent(bikeUrlWithParams, "Two Wheeler Insurance")
                        val intentHealthIns = createWebIntent(healthUrlWithParams, "Health Insurance")
                        val intentExpressUrl = createExpressIntent(expressUrlWithParams)

                        val shortcuts = listOf(
                            createShortcut("ID1", "Sync Contacts", R.drawable.sync_contact, intentExpressUrl, 0),
                            createShortcut("ID2", "Health Insurance", R.drawable.health_insurance_sm, intentHealthIns, 1),
                            createShortcut("ID3", "Private Car", R.drawable.private_car_sm, intentPrivateCar, 2),
                            createShortcut("ID4", "Two Wheeler", R.drawable.two_wheeler_sm, intentBike, 3)
                        )

                        shortcutManager?.dynamicShortcuts = shortcuts
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d("SHORTCUTMENU", ex.toString())
            Log.d(Constant.TAG, ex.toString())
        }
    }

    fun removeShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val shortcutManager = getSystemService(ShortcutManager::class.java)

                shortcutManager?.let {
                    it.disableShortcuts(listOf("ID1"))
                    it.disableShortcuts(listOf("ID2"))
                    it.disableShortcuts(listOf("ID3"))
                    it.disableShortcuts(listOf("ID4"))
                    it.removeAllDynamicShortcuts()
                }
            } catch (ex: Exception) {
                Log.d("SHORTCUTMENU", ex.toString())
            }
        }
    }



    private fun createWebIntent(url: String, title: String): Intent {
        return Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", url)
            putExtra("dashBoardtype", "INSURANCE")
            putExtra("NAME", title)
            putExtra("TITLE", title)
            putExtra("APPMENU", "Y")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            action = Intent.ACTION_VIEW
        }
    }

    private fun createExpressIntent(url: String): Intent {
        return Intent(this@HomeActivity, WelcomeSyncContactActivityKotlin::class.java).apply {
            // putExtra("URL", url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            action = Intent.ACTION_VIEW
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun createShortcut(id: String, label: String, iconRes: Int, intent: Intent, rank: Int): ShortcutInfo {
        return ShortcutInfo.Builder(this, id)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(Icon.createWithResource(this, iconRes))
            .setIntent(intent)
            .setRank(rank)
            .build()
    }
    //endregion



    //region Open App in Play Store
    private fun openAppMarketPlace() {
        val appPackageName = packageName // getPackageName() from Context or Activity
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }

        // Tracking logic (if needed)
        // TrackingController(this).sendData(TrackingRequestEntity(TrackingData("Update: User opened marketplace"), "Update"), null)
    }

    //endregion

    override fun onClick(view: View?) {

        when (view?.id) {

            binding.tvKnowledge.id ->{

                if (!NetworkUtils.isNetworkAvailable(this@HomeActivity)) {

                    showSnackbar(view,getString(R.string.noInternet))
                    return
                }

                // Redirect to SalesMaterialActivity
                startActivity(Intent(this@HomeActivity, KnowledgeGuruActivity::class.java))

                // Tracking event
                trackTopMenuEvent("CUSTOMER COMM")


            }
            binding.tvSalesMat.id ->{

                if (!NetworkUtils.isNetworkAvailable(this@HomeActivity)) {

                    showSnackbar(view,getString(R.string.noInternet))
                    return
                }

                // Redirect to SalesMaterialActivity
                startActivity(Intent(this@HomeActivity, SalesMaterialActivity::class.java))

                // Tracking event
                trackTopMenuEvent("CUSTOMER COMM")


            }
        }
    }

    //region Observer
    private fun observeMasterState() {





        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModelFCMNotify.notificationFlow
                    .onEach { notifyEntity ->

                        notifyEntity?.let {

                            val notifyCount: Int = prefsManager.getNotificationCounter()

                            if (notifyCount == 0) {
                                textNotifyItemCount!!.visibility = View.GONE
                            } else {
                                textNotifyItemCount!!.visibility = View.VISIBLE
                                textNotifyItemCount!!.text = "" + notifyCount.toString()
                            }
                        }

                    }.catch { e->

                        Log.e("NOTIFICATION FCM ERROE", "Error:", e)
                    }
                    .collect()
            }
        }



        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                // Collecting masterState
                launch {
                    viewModel.masterState.collectLatest { state ->
                        when (state) {

                            is APIState.Loading -> {

                                displayLoadingWithText()

                            }
                            is APIState.Success -> {

                                hideLoading()


                                setupDashBoard_Adapter()
                                initHeaderLayout()
                                shortcutAppMenu()

                                if (!prefsManager.getDeepLink().isNullOrEmpty()) {
                                    // Handle deep link here
                                    deeplinkHandle()

                                    return@collectLatest

                                }

                                getNotificationAction()


                                //region ForceUpdate
                                //Mark: get  current version from App
                                val currentVersion = Utility.getCurrentVersion(this@HomeActivity) // Get the current version of your app

                                //Mark: check for new version from Server
                                val serverVersionCode =
                                    prefsManager.getAndroidProVersion()

                                //Mark : Force Update Via playStore
                                if (currentVersion < serverVersionCode) {

                                    UtilityNew.openPopUp(
                                        context = this@HomeActivity,
                                        title = "UPDATE",
                                        desc = "New version available on play store, Please update",
                                        positiveButtonName = "OK",
                                        isCancelable = false,
                                        onPositiveButtonClick = { dialog, view ->
                                            // Handle positive action
                                            dialog.dismiss()
                                            openAppMarketPlace()
                                        }
                                    )

                                    return@collectLatest

                                }

                                //endregion


                                // region market PopUp and sync PopUp Alert
                                //Mark :Call Market Pop Up Alert
                                marketPopUpAlert()

                                state.data?.horizonDetail?.SYNC_CONTACT?.takeIf { it.ACTION_NEEDED == "NO_ACTION"}
                                    ?.let { syncContactEntity->

                                        showMySyncPopUpAlert(syncContactEntity)

                                    }

                                //endregion



                            }
                            is APIState.Empty -> {
                                hideLoading()
                            }
                            is APIState.Failure -> {

                                hideLoading()
                                showToast(msg = state.errorMessage.toString())
                            }
                        }
                    }
                }

                launch {
                    viewModel.productShareResponse.collect{  event->

                        event.contentIfNotHandled?.let {

                            when (it) {
                                is APIState.Empty -> {

                                    hideLoading()
                                }

                                is APIState.Failure -> {
                                    hideLoading()
                                    this@HomeActivity.showSnackbar(binding.root, it.errorMessage)
                                }

                                is APIState.Loading -> {

                                    displayLoadingWithText()
                                }

                                is APIState.Success -> {
                                    hideLoading()

                                    if (it.data != null) {
                                        it.data?.let { shareEntity ->
                                            datashareList(
                                                this@HomeActivity,

                                                viewModel.ShareTitle(),
                                                shareEntity.msg,
                                                shareEntity.url
                                            )
                                        }

                                    }

                                }


                            }
                        }

                    }
                }

                launch {
                    viewModel.userCallingDtlResponse.collect{  event->

                        event.contentIfNotHandled?.let {

                            when (it) {
                                is APIState.Empty -> {

                                    hideLoading()
                                }

                                is APIState.Failure -> {
                                    hideLoading()
                                    this@HomeActivity.showSnackbar(binding.root, it.errorMessage)
                                }

                                is APIState.Loading -> {

                                    displayLoadingWithText()
                                }

                                is APIState.Success -> {
                                    hideLoading()

                                        it.data?.MasterData
                                            ?.filterNotNull() // Remove null values from the list
                                            ?.takeIf { it.isNotEmpty() } // Proceed only if the list is not empty
                                            ?.let { nonNullCallingDetails ->
                                                showCallingDetailsPopUp(nonNullCallingDetails)
                                            }



                                }


                            }
                        }

                    }
                }

            }
        }


    }




    //endregion



}