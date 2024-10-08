package com.policyboss.policybosspro.core.viewModel.homeVM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.repository.homeRepository.HomeRepository
import com.policyboss.policybosspro.core.response.home.ProductURLShareEntity
import com.policyboss.policybosspro.core.response.home.ProductURLShareResponse
import com.policyboss.policybosspro.core.response.master.MasterDataCombine
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val homeRepository: HomeRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {


    // StateFlow to manage loader state

    private val _masterState = MutableStateFlow<APIState<MasterDataCombine>>(APIState.Empty())
    val masterState: StateFlow<APIState<MasterDataCombine>> = _masterState


    //region Share DashBoard Product
    private val productShareMutableFlow: MutableStateFlow<Event<APIState<ProductURLShareEntity>>> =
        MutableStateFlow(Event(APIState.Empty()))

    val productShareResponse: StateFlow<Event<APIState<ProductURLShareEntity>>>
        get() = productShareMutableFlow

    //endregion


    private var _currentDashboardEntity: DashboardMultiLangEntity? = null

    // Setter method
    fun setCurrentDashboardShareEntity(shareEntity: DashboardMultiLangEntity) {
        _currentDashboardEntity = shareEntity
    }

    // Getter method
    fun getCurrentDashboardSharedEntity(): DashboardMultiLangEntity? {
        return _currentDashboardEntity
    }

    fun ShareTitle() = getCurrentDashboardSharedEntity()?.title?:""

    fun getMasterData() = viewModelScope.launch {



        val body = hashMapOf(
            "app_version" to prefManager.getAppVersion(),
            "device_code" to prefManager.getDeviceID(),
            "ssid" to prefManager.getSSID(),
            "fbaid" to prefManager.getFBAID()
        )


        _masterState.value = APIState.Loading()
        delay(3000)
        try {
            coroutineScope {
                // Run both API calls concurrently
                val userConstantDeferred = async { homeRepository.getUserConstant(body) }
                val dynamicDashboardDeferred = async { homeRepository.getDynamicDashboardMenu(body) }
                val horizonDetailDeferred = async { homeRepository.getSyncDetails(prefManager.getSSID().toInt()) }


                val userConstantResponse = userConstantDeferred.await()
                val dynamicDashboardResponse = dynamicDashboardDeferred.await()
                 val horizonDetailResponse = horizonDetailDeferred.await()

                // Check if both responses are successful
                if (userConstantResponse?.isSuccessful == true &&
                    dynamicDashboardResponse?.isSuccessful == true &&


                    userConstantResponse.body() != null  &&
                    dynamicDashboardResponse.body() != null &&
                    horizonDetailResponse.body() != null
                    ) {

                    //for Success stae hold both data // no need actually
                    _masterState.value = APIState.Success(
                        MasterDataCombine(
                            userConstant = userConstantResponse.body(),
                            menuMaster = dynamicDashboardResponse.body(),
                            horizonDetail = horizonDetailResponse?.body()
                        )

                    )
                  // Mark :- storeData in Prference
                    userConstantResponse.body()?.let { prefManager.saveUserConstantResponse(it) }

                    dynamicDashboardResponse.body()?.let {
                        prefManager.storeMenuDashboard(it)
                    }




                } else {
                    _masterState.value = APIState.Failure(errorMessage = Constant.MasterData)
                }
            }
        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
            _masterState.value = APIState.Failure("Error occurred: ${e.message}")

        }

    }

   fun getInsurProductLangList(): List<DashboardMultiLangEntity> {
        val dashboardEntities = mutableListOf<DashboardMultiLangEntity>()

        // Retrieve the dashboard data from prefManager
        val dashBoardItemEntities = prefManager.getMenuDashBoard()?.MasterData?.Dashboard

        dashBoardItemEntities?.let { items ->
            items.filter { it.dashboard_type.equals("1")  && it.isActive == 1 }
                .forEach { dashBoardItemEntity ->
                    val dashboardEntity = DashboardMultiLangEntity(
                        category = "INSURANCE",
                        sequence = dashBoardItemEntity.sequence.toInt(),
                        productId = dashBoardItemEntity.ProdId.toInt(),
                        menuName = dashBoardItemEntity.menuname,
                        description = dashBoardItemEntity.description,
                        iconResId = -1,  // Assuming no local resource, replace if needed
                        titleKey = "Insurance",
                        descriptionKey = ""
                    ).apply {
                        serverIcon = dashBoardItemEntity.iconimage
                        link = dashBoardItemEntity.link
                        productNameFontColor = dashBoardItemEntity.ProductNameFontColor
                        productDetailsFontColor = dashBoardItemEntity.ProductDetailsFontColor
                        productBackgroundColor = dashBoardItemEntity.ProductBackgroundColor
                        isExclusive = dashBoardItemEntity.IsExclusive
                        isNewPrdClickable = dashBoardItemEntity.IsNewprdClickable
                        isSharable = dashBoardItemEntity.IsSharable
                        title = dashBoardItemEntity.title
                        info = dashBoardItemEntity.info
                        popupmsg = dashBoardItemEntity.popupmsg
                    }
                    dashboardEntities.add(dashboardEntity)
                }
        }

        return dashboardEntities
    }



    fun getUserConstant(appVersion: String, deviceCode: String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", appVersion)
        body.put("device_code", deviceCode)
        body.put("ssid", prefManager.getSSID())
        body.put("fbaid", prefManager.getFBAID())



        try {
            // Concurrent API calls
            val UserConstatnDeferred = async { homeRepository.getUserConstant(body) }

            val UserConstatnResponse = UserConstatnDeferred.await()

            if (UserConstatnResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, "User Constant Success: ${UserConstatnResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred at User Constant : ${UserConstatnResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }

    fun getDynamicDashboardMenu(appVersion: String, deviceCode: String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", appVersion)
        body.put("device_code", deviceCode)
        body.put("ssid",prefManager.getSSID())
        body.put("fbaid", prefManager.getFBAID())



        try {
            // Concurrent API calls
            val DynamicDashboardDeferred = async { homeRepository.getDynamicDashboardMenu(body) }

            val DynamicDashboardResponse = DynamicDashboardDeferred.await()

            if (DynamicDashboardResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, "Dynamic DashboardSuccess: ${DynamicDashboardResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred Dynamic Dashboard: ${DynamicDashboardResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }



    //region  Share DashBoard Product
    fun getProductShareURL(product_id: String, sub_fba_id: String) = viewModelScope.launch {


        val body = hashMapOf(

            "fba_id" to prefManager.getFBAID(),
            "ss_id" to prefManager.getSSID(),
            "product_id" to product_id,
            "sub_fba_id" to sub_fba_id,


        )

        productShareMutableFlow.value = Event(APIState.Loading())

        homeRepository.getProductShareURL(body)
            .catch {
                productShareMutableFlow.value =  Event(APIState.Failure(it.message ?: Constant.Fail))
            }.collect{
                if (it.isSuccessful) {
                    if (it.body() != null && it.body()?.statusNo == 0) {

                        productShareMutableFlow.value =  Event(APIState.Success(it.body()?.MasterData))
                    } else {
                        productShareMutableFlow.value =
                            Event(APIState.Failure(it.body()?.message?: Constant.ErrorMessage ))
                    }
                } else {
                    productShareMutableFlow.value =
                        Event(APIState.Failure(it.message()))
                }
            }


    }

    //endregion

}