package com.policyboss.policybosspro.core.response.master.userConstant

import com.webengage.sdk.android.utils.http.Response

data class UserConstantResponse(
    val MasterData: UserConstantEntity?,
    val Message: String,
    val Status: String,
    val StatusNo: Int
)

data class UserConstantEntity(
    val AddPospVisible: String,
    val CVUrl: String,
    val ERPID: String,
    val EliteKotakUrl: String,
    val FBAId: String,
    val FinboxEnabled: String,
    val FinperksEnabled: String,
    val FourWheelerEnabled: String,
    val FourWheelerUrl: String,
    val FullName: String,
    val GenerateMotorLeadsEnabled: String,
    val HealthDemoUrl: String,
    val HealthPopup: String,
    val InvestmentEnabled: String,
    val InvestmentUrl: String,
    val IsDynamicDashEnabled: String,
    val KotakEliteEnabled: String,
    val LeadDashUrl: String,
    val LoginID: String,
    val ManagName: String,
    val MangEmail: String,
    val MangMobile: String,
    val MyMessagesEnabled: String,
    val MyTransactionsEnabled: String,
    val PBByCrnSearch: String,
    val POSPNo: String,
    val POSP_STATUS: String,
    val PospappformEnabled: String,
    val PospletterEnabled: String,
    val RaiseTickitEnabled: String,
    val RaiseTickitUrl: String,
    val SmsTemplatesEnabled: String,
    val Status: String,
    val SuppEmail: String,
    val SuppMobile: String,
    val TermPopup: String,
    val TermPopupurl: String,
    val TwoWheelerEnabled: String,
    val TwoWheelerUrl: String,
    val addposplimit: String,
    val android_Posp_web_Enable: String,
    val androidproattendanceEnable: String,
    val androidproeliteversion: String,
    val androidpromarkefbaurl: String,
    val androidpromarketEnable: String,
    val androidpromarketuidurl: String,
    val androidproouathEnabled: String,
    val androidproversion: String,
    val boempuid: String,
    val cobrowserisactive: String,
    val cobrowserlicensecode: String,
    val crnmobileno: String,
    val dashboardarray: List<Dashboardarray>,
    val emplat: String,
    val emplng: String,
    val enableInsuranceBusiness: String,
    val enableenrolasposp: String,
    val enablemyaccountupdate: String,
    val enablencd: String,
    val enablesynccontact: String,
    val enablesyncprofileupdate: String,
    val fba_campaign_id: String,
    val fba_campaign_name: String,
    val fba_uid: String,
    val finboxurl: String,
    val finmartwhatsappno: String,
    val finperkurl: String,
    val hdfc_code: String,
    val healthurl: String,
    val healthurltemp: String,
    val insurancerepositorylink: String,
    val iosuid: String,
    val iosversion: String,
    val isEmployee: String,
    val loanparentdesignation: String,
    val loanparentemail: String,
    val loanparentid: String,
    val loanparentmobile: String,
    val loanparentname: String,
    val loanparentphoto: String,
    val loanselfdesignation: String,
    val loanselfemail: String,
    val loanselfid: String,
    val loanselfmobile: String,
    val loanselfname: String,
    val loanselfphoto: String,
    val loansenddesignation: String,
    val loansendemail: String,
    val loansendid: String,
    val loansendmobile: String,
    val loansendname: String,
    val loansendphoto: String,
    val marketinghomedesciption: String,
    val marketinghomeenabled: String,
    val marketinghomemaxcount: String,
    val marketinghomepopupid: String,
    val marketinghometitle: String,
    val marketinghometransfertype: String,
    val marketinghomeurl: String,
    val messagesender: String,
    val myaccountupdateurl: String,
    val notif_popupurl_elite: String,
    val notificationpopupurl: String,
    val notificationpopupurltype: String,
    val paenable: String,
    val parentid: String,
    val plactive: String,
    val plbanner: String,
    val policyByCRNEnabled: String,
    val pospparentdesignation: String,
    val pospparentemail: String,
    val pospparentid: String,
    val pospparentmobile: String,
    val pospparentname: String,
    val pospparentphoto: String,
    val pospselfdesignation: String,
    val pospselfemail: String,
    val pospselfid: String,
    val pospselfmobile: String,
    val pospselfname: String,
    val pospselfphoto: String,
    val pospsenddesignation: String,
    val pospsendemail: String,
    val pospsendid: String,
    val pospsendmobile: String,
    val pospsendname: String,
    val pospsendphoto: String,
    val serviceurl: String,
    val showmyinsurancebusiness: String,
    val uid: String,
    val ultralakshyaenabled: Int,
    val userid: String,
    val enable_pro_Addsubuser_url : String? = null
)