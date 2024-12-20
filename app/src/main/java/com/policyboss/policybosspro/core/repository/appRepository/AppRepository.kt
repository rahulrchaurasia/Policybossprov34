package com.policyboss.policybosspro.core.repository.appRepository

import com.policyboss.policybosspro.core.api.poilcyBossProAppApi
import com.policyboss.policybosspro.core.api.poilcyBossProHomeApi
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.core.response.notification.NotificationUpdateResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesClickResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository  @Inject constructor(
    private  val apiService : poilcyBossProAppApi
){

    //region Sales Material API
    /****************************************************************************************
     *   Sales Material
     *************************************************************************************/

    suspend fun getSalesProducts(  body : HashMap<String,String>) = flow {


        val response = apiService.getSalesProducts(body)
        emit(response)

    }.flowOn(Dispatchers.IO)


    //getSalesProducts

    suspend fun getSalesProductDetail(  body : HashMap<String,String>) = flow {


        val response = apiService.getSalesProductDetails(body)
        emit(response)

    }.flowOn(Dispatchers.IO)





    suspend fun getSalesProductClick(body : HashMap<String,String> ): Response<SalesClickResponse?>? {


        return apiService.getSalesProductClick(body)

    }


    //endregion


    //region Profile
    suspend fun getProfileDetail(  body : HashMap<String,String>) = flow {


        val response = apiService.getProfileDetail(body)
        emit(response)

    }.flowOn(Dispatchers.IO)


    suspend fun uploadDocument(document: MultipartBody.Part, body: HashMap<String, String>) = flow {
        val response = apiService.uploadDocument(document, body)
        emit(response)
    }.flowOn(Dispatchers.IO)

    //endregion





    suspend fun getNotificationData(body : HashMap<String,String>) = flow {
        val response = apiService.getNotificationData(body)
        emit(response)
    }.flowOn(Dispatchers.IO)


    suspend fun getContactUs(body : HashMap<String,String>) = flow {
        val response = apiService.getContactUs(body)
        emit(response)
    }.flowOn(Dispatchers.IO)





}