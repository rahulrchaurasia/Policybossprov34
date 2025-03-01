package com.policyboss.policybosspro.view.salesMaterial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.salesMaterial.CompanyEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.core.viewModel.salesMaterialVM.SalesMaterialViewNodel
import com.policyboss.policybosspro.databinding.ActivitySalesMaterialBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.salesMaterial.adapter.SalesMaterialAdapter
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SalesMaterialActivity : BaseActivity() {

    private lateinit var binding: ActivitySalesMaterialBinding
    private lateinit var salesMaterialAdapter: SalesMaterialAdapter

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    lateinit var companyLst: ArrayList<CompanyEntity>

    private val viewModel by viewModels<SalesMaterialViewNodel>()

    private var deeplinkProductID = ""


    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("SalesMaterial Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(Constant.SalesTitle)
        }

        initialize()

        intent.getStringExtra(Constant.Deeplink_PRODUCT_ID)?.let { data ->

            deeplinkProductID = data

        }

        //Mark :-- call Api for Sales Material Main Page
        viewModel.getSalesProducts()

        //Mark :- Observing Api, get Api Response
       observeResponse()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@SalesMaterialActivity.finish()
            }
        })
    }

   private fun initialize(){

       companyLst = ArrayList()


   }

    private fun setupSalesMaterialAdapter(salesProductList : List<SalesMateriaProdEntity>) {


        salesMaterialAdapter = SalesMaterialAdapter(
            context = this,
            salesProductList =  salesProductList,
            prefsManager = prefsManager,
            onItemClick = ::onSalesProductListener,

        )
        binding.rvSalesMaterial.apply {
            layoutManager = LinearLayoutManager(this@SalesMaterialActivity)
            adapter = salesMaterialAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    fun onSalesProductListener(entity: SalesMateriaProdEntity, pos : Int){
        val intent = Intent(this@SalesMaterialActivity, SalesDetailActivity::class.java)
            .apply {
            putExtra(Constant.PRODUCT_ID, entity)
        }
        startActivity(intent)

    }

    private fun deeplink(salesLst: List<SalesMateriaProdEntity>){

        val productId = deeplinkProductID.toIntOrNull() ?: 0  // Convert safely

        if(productId == 0){
            return
        }

        salesLst.find { it.Product_Id == productId }?.let { entity ->
            val intent = Intent(this, SalesDetailActivity::class.java).apply {
                putExtra(Constant.PRODUCT_ID, entity) // Ensure entity is Parcelable/Serializable if passing full object
            }
            startActivity(intent)
        }

    }

    private fun observeResponse() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                   viewModel.SalesMaterialResponse.collect{ event->

                       event.contentIfNotHandled?.let {

                           when(it){
                               is APIState.Empty -> {
                                   hideLoading()
                               }
                               is APIState.Failure -> {
                                   hideLoading()


                                   Log.d(Constant.TAG,it.errorMessage.toString())
                               }
                               is APIState.Loading -> {
                                   displayLoadingWithText()
                               }
                               is APIState.Success -> {

                                   hideLoading()

                                   it.data?.MasterData?.let { lstSalesProdEntity ->

                                       setupSalesMaterialAdapter(lstSalesProdEntity)

                                       if(deeplinkProductID.isNotEmpty()  && lstSalesProdEntity.isNotEmpty() ){
                                           deeplink(salesLst = lstSalesProdEntity )
                                       }


                                   }
                               }
                           }


                       }


                   }
                }
            }
        }
    }


      override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@SalesMaterialActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}