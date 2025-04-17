package com.policyboss.policybosspro.view.syncContact.helper

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.ContactsContract



// create by Rahul
//For handling all field in contacts

/*
 Note : we put all data in " myData MutableList "

  1> DisplayName is main key of  myData MutableList

  2> Other Field For eg contatcWebSiteMapList all has MutableMap<String, MutableList<String>> ie key & value pair
   where  we set DisplayName as key and List of Phone is Value

  3> Mainly we get the vale from map and again put the value on the map basis on key which is DisplayName { this give benefits that we
  can maintain a list of record again same key }


     var websiteList  = contatcWebSiteMapList.get(displayName) // using the key ie Display Name and get All Element of Phone
        if(websiteList == null){
            websiteList = ArrayList<String>()
        }
         val webSiteUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.URL))

        websiteList.add(webSiteUrl.toString().trim())
        contatcWebSiteMapList.put(displayName,websiteList)
        **************************************************
       //VVIMP : 1>  contatcWebSiteMapList  **First Time by default its give null value to websiteList. **
       bec first time key doesnot have any value { we later added it manually..}
       so our list is null. hence we first initialize it  websiteList = ArrayList<String>() .

       2> val webSiteUrl = cursor.getString(c.....)
        after getting webSiteUrl we add field webSiteUrl to our List ie websiteList now our List has one data.

        VVIMP
       3> Now This websiteList agin we put to contatcWebSiteMapList ie displayName is key and websiteList its value.

         contatcWebSiteMapList.put(displayName,websiteList)

        2> when we come second time ie device has two website at same Id details
            var websiteList = contatcWebSiteMapList.get(displayName)
         contatcWebSiteMapList Second Time return the websiteList. which has one recods and its a list.
         Basically we has a key which is "displayName" and give value which is List of wevite

         3> this process was repeated depends on count of website that particular contact Id has and our List Size is increased.

         4> Finally We have myData List which has Field 'DisplayName' and contatcWebSiteMapList which has key 'DisplayName'

        **************************************************


  4>      // Add WebSite
                    contactWebSiteMapList.forEach{ website ->

                        if(myData.displayName.equals(website.key)){

                            myData.websites = website.value
                        }
                    }

              so we can assign data to myData's  websites field  by using  website.key which has value as  'websiteList'
         **************************************************



 */
object ContactHelper {

    val maxSize = 150
    val maxStandardSize = 100
    private val TAG = "CALL_LOG_CONTACT"

    @JvmStatic
    fun getContact(context: Context)  :   MutableList<ModelContact>   {

        var deviceData : MutableList<ModelContact> = mutableListOf()

        val regex = Regex("[^.0-9]")

        val contactNameMapList: MutableMap<String, MutableList<NameData>> = mutableMapOf()

        val contactPhoneMapList: MutableMap<String, MutableList<PhoneData>> = mutableMapOf()

        val contactEmailMapList: MutableMap<String, MutableList<EmailData>> = mutableMapOf()

        val contactAddressMapList: MutableMap<String, MutableList<AddressData>> = mutableMapOf()

        val contactOrganizationMapList : MutableMap<String,  MutableList<CompanyData>> = mutableMapOf()

        val contactWebSiteMapList : MutableMap<String, MutableList<String>> = mutableMapOf()

        val contactRelationMapList : MutableMap<String, MutableList<RelationData>> = mutableMapOf()

        val contactEventMapList : MutableMap<String, MutableList<EventData>> = mutableMapOf()

        val contactNickNameMapList : MutableMap<String, String> = mutableMapOf()

        val contactNoteMapList : MutableMap<String, String> = mutableMapOf()

        // val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME

        val ORDER =
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"// LIMIT " + limit + " offset " + lastId + "";
        val cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI, arrayOf(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.RAW_CONTACT_ID,
                ContactsContract.Data.LOOKUP_KEY,
                ContactsContract.Data.DATA1,
                ContactsContract.Data.DATA2,
                ContactsContract.Data.DATA3,
                ContactsContract.Data.DATA4,
                ContactsContract.Data.DATA5,
                ContactsContract.Data.DATA6,
                ContactsContract.Data.DATA7,
                ContactsContract.Data.DATA8,
                ContactsContract.Data.DATA9,
                ContactsContract.Data.DATA10,

                ContactsContract.Data.DATA11,
                ContactsContract.Data.DATA12,
                ContactsContract.Data.DATA13,
                ContactsContract.Data.DATA14,
                ContactsContract.Data.DATA15,
                ContactsContract.Contacts.PHOTO_URI // Add the PHOTO_URI column

            ),
//                (ContactsContract.Data.HAS_PHONE_NUMBER + ">0" + " AND "
//                        + "(" + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')"),
//                null, ORDER


            (ContactsContract.Data.HAS_PHONE_NUMBER + ">0" + " AND "

                    + "(" + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "' OR "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' OR "

                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "')"),
            null, ORDER

        )

        // val contactMaps  =  HashMap<String,ArrayList<Model?>>()


        if (cursor != null && cursor.count > 0) {

            while (cursor.moveToNext()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME))



                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE))

                //Note : mimeType is Required to filter required type of Data
                if ((mimeType == ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {

                    val givenName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)) ?: ""

                    val middleName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)) ?: ""

                    val familyName =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)) ?: ""

                    setName(cursor = cursor, displayName = displayName , givenName = givenName , middleName = middleName ,familyName = familyName , contactNameMapList = contactNameMapList)

                }


                // Get Phone and Name Details
                else if ((mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    //arrayPhoneList.clear()
                    setPhoneList(mcontext = context, cursor = cursor, displayName = displayName , contactPhoneMapList = contactPhoneMapList )
                }

                else if (mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) {
                    setEmailList(cursor = cursor, displayName = displayName , contatcEmailMapList = contactEmailMapList)
                }else if(mimeType == ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE){
                    setAddress(cursor = cursor, displayName = displayName , contatcAddressMapList = contactAddressMapList)

                }else if ((mimeType == ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                    setCompany(cursor = cursor, displayName = displayName , contatcOrganizationMapList = contactOrganizationMapList)
                }
                else if ((mimeType == ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)) {
                    setWebSiteList(cursor = cursor, displayName = displayName , contatcWebSiteMapList = contactWebSiteMapList)
                }
                else if ((mimeType == ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE)) {

                    setRelationList(cursor = cursor, displayName = displayName , contactRelationMapList = contactRelationMapList)
                }
                else if ((mimeType == ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)) {

                    setEventList(cursor = cursor, displayName = displayName , contactEventMapList = contactEventMapList)
                }

                else if ((mimeType == ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)) {

                    setNickName(cursor = cursor, displayName = displayName , contactNickNameMapList = contactNickNameMapList)

                }

                else if ((mimeType == ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)) {

                    setNote(cursor = cursor, displayName = displayName , contactNoteMapList = contactNoteMapList)
                }


            }
            //               var count = 0

//                val entriesEmail: List<String> = contatcEmailMapList.entries.map { "(${it.key}, ${it.value})" }
//                entriesEmail.forEach {
//                    // println(it)
            //                  Log.d(Constant.TAG_SAVING_CONTACT_LOG,it)
//                }


            /******************************************************************************************************
            Once we get All field we have to add  maually on the main List
             ****************************************************************************************************** */


            //region Add Phone Number in Main List
            lateinit var modeContact : ModelContact

            contactNameMapList.forEach{

                modeContact =  ModelContact(displayName = it.key)  // Get Display name as key From Phone Number

                modeContact.givenName = it.value[0].givenName
                modeContact.middleName = it.value[0].middleName
                modeContact.familyName = it.value[0].familyName

                deviceData.add(modeContact)
            }


            deviceData.forEach{ myData ->


                // add PhoneNumber

                contactPhoneMapList.forEach{ phone ->

                    if(myData.displayName.equals(phone.key)){

                        myData.phoneNumbers = phone.value
                    }


                }

                // Add Email
                contactEmailMapList.forEach{ email ->

                    if(myData.displayName.equals(email.key)){

                        myData.emails = email.value
                    }

                }

                // Add Address
                contactAddressMapList.forEach{ address ->

                    if(myData.displayName.equals(address.key)){

                        myData.addresses = address.value

                    }
                }

                // Add Organization
                contactOrganizationMapList.forEach{ organization ->

                    if(myData.displayName.equals(organization.key)){

                        myData.companyName =  organization.value[0].companyName
                        myData.companyTitle = organization.value[0].companyTitle
                        myData.companyDepartment = organization.value[0].companyDepartment

                    }
                }

                // Add WebSite
                contactWebSiteMapList.forEach{ website ->

                    if(myData.displayName.equals(website.key)){

                        myData.websites = website.value
                    }
                }

                // Add Relation
                contactRelationMapList.forEach{ relation ->

                    if(myData.displayName.equals(relation.key)){

                        myData.relations = relation.value
                    }
                }

                // Add Events
                contactEventMapList.forEach{ event ->

                    if(myData.displayName.equals(event.key)){

                        myData.events = event.value
                    }
                }

                // Add NickName
                contactNickNameMapList.forEach{ nickName ->

                    if(myData.displayName.equals(nickName.key)){

                        myData.nickname = nickName.value
                    }
                }

                // Add Note
                contactNoteMapList.forEach{ note ->

                    if(myData.displayName.equals(note.key)){

                        myData.note = note.value
                    }
                }
            }

            // region commented below line for testing Purpose

            // val entries: List<String> = myData.entries.map { "(${it.key}, ${it.value})" }
//                myData.forEach {
//                    // println(it)
//                    Log.d("CALL_LOG_CONTACT",it.displayName +" " + it.phoneNo +" " + it.emiailId +" " + it.address  )
//                }


//                val entries: List<String> = contatcAddressMapList.entries.map { "(${it.key}, ${it.value})" }
//
//                entries.forEach {
//                    // println(it)
//                    Log.d(Constant.TAG_SAVING_CONTACT_LOG,it.toString()  )
//                }
            //endregion
            cursor.close()

            //endregion
        }

        return  deviceData
    }



    // region setContact Field seperately in a List

    private fun setName(cursor: Cursor, displayName : String, givenName : String,middleName : String,familyName : String ,  contactNameMapList: MutableMap<String, MutableList<NameData>>) {


        /********************** displayName work as KEY ************/

        var nameList  = contactNameMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if(nameList == null){


            nameList = ArrayList<NameData>()

        }
        // var nameList  = mutableListOf<NameData>()// using the key ie Display Name and get All Element of Phone



        nameList.add(NameData(displayName = displayName, givenName = givenName,middleName = middleName , familyName = familyName))


        contactNameMapList.put(displayName, nameList)


    }


    private fun setPhoneList(mcontext : Context, cursor: Cursor, displayName : String, contactPhoneMapList: MutableMap<String, MutableList<PhoneData>> ) {


        /********************** displayName work as KEY ************/
        var mutableList  = contactPhoneMapList.get(displayName) // using the key ie Display Name and get All Element of Phone
        //var photoUriList =  contactPhotoUriMapList.get(displayName)

        if(mutableList == null){


            mutableList = ArrayList<PhoneData>()


        }


        val phoneNo =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: ""


        val phoneType =
            cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE))

        var phoneLabel = ""
        if(phoneType == 0){

            phoneLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL)) ?: "CUSTOM"
        }else{
            phoneLabel = getPhoneLabel(phoneType)
        }





        mutableList.add(
            PhoneData(
                normalizedNumber = phoneNo.replace("\\s".toRegex(), "").takeLast(10),
                number = phoneNo ,
                type = phoneLabel
            )
        )


        contactPhoneMapList.put(displayName,mutableList.toSet().toMutableList())


    }




    private fun setEmailList(cursor: Cursor, displayName : String, contatcEmailMapList: MutableMap<String, MutableList<EmailData>> ) {

        //var contatcEmailMapList: MutableMap<String, MutableList<String>> = mutableMapOf()
        /********************** displayName work as KEY ************/
        var emailList  = contatcEmailMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if(emailList == null){


            emailList = ArrayList<EmailData>()

        }

        var emailAddress =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)) ?: ""

        emailAddress = emailAddress.toString().replace("\\s".toRegex(), "")

        val emailType =
            cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.TYPE))

        var emailLabel = ""
        if(emailType == 0){

            emailLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.LABEL)) ?: "CUSTOM"
        }else{
            emailLabel = getAddressOrEmailLabel(emailType)
        }

        emailList.add(  EmailData(address = emailAddress , type = emailLabel  ))


        contatcEmailMapList.put(displayName,emailList.toSet().toMutableList())


    }



    private fun setAddress(cursor: Cursor, displayName : String, contatcAddressMapList: MutableMap<String, MutableList<AddressData>> ) {


        /********************** displayName work as KEY ************/
        var addressList  = contatcAddressMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if(addressList == null){


            addressList = ArrayList<AddressData>()

        }

        val address = Address()     // For Address  05 Added Below for test large data


        var street = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.STREET)) ?: ""

        street.let {

            if(it.count() > maxSize){

                street = it.take(maxSize)
            }

            address.street = street
        }


        var city =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.CITY)) ?:""

        city.let {

            if(it.count() > maxStandardSize){

                city = it.take(maxStandardSize)
            }

            address.city = city
        }


        var region =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.CITY)) ?:""

        region.let {

            if(it.count() > maxStandardSize){

                region = it.take(maxStandardSize)
            }

            address.region = region
        }

        val addressType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.TYPE))

        if (addressType == 0) {
            address.label =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)) ?: "CUSTOM"


        }else {
            address.label = getAddressOrEmailLabel(addressType)
        }


        var formattedAddress = address.city + address.region + address.street


        addressList.add( AddressData(formattedAddress = formattedAddress , type = address.label))

        contatcAddressMapList.put(displayName,addressList)


    }


    private fun setCompany(cursor: Cursor, displayName : String, contatcOrganizationMapList: MutableMap<String,  MutableList<CompanyData>>) {

        /********************** displayName work as KEY ************/
        var organizationList  = contatcOrganizationMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if(organizationList == null){


            organizationList = ArrayList<CompanyData>()

        }

        //   var nickname = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Nickname.NAME)) ?: ""


        // contactNickNameMapList.put(displayName,nickname.trim())

        var companyName =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY)) ?: ""
        var  companyTitle =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TITLE)) ?: ""
        var companyDep =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DEPARTMENT)) ?: ""


        organizationList.add(CompanyData(companyName = companyName, companyTitle = companyTitle, companyDepartment = companyDep))


        contatcOrganizationMapList.put(displayName, organizationList)


    }

    private fun setWebSiteList(cursor: Cursor, displayName : String, contatcWebSiteMapList: MutableMap<String, MutableList<String>> ) {

        //var contatcEmailMapList: MutableMap<String, MutableList<String>> = mutableMapOf()
        /********************** displayName work as KEY ************/
        var websiteList  = contatcWebSiteMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if(websiteList == null){


            websiteList = ArrayList<String>()

        }

        val webSiteUrl =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.URL)) ?:""

        websiteList.add(webSiteUrl.toString().trim())


        contatcWebSiteMapList.put(displayName,websiteList)


    }

    private fun setRelationList(cursor: Cursor, displayName : String, contactRelationMapList: MutableMap<String, MutableList<RelationData>> ) {


        /********************** displayName work as KEY ************/
        var relationList =
            contactRelationMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if (relationList == null) {


            relationList = ArrayList<RelationData>()

        }

        val relationName =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Relation.NAME)) ?: ""

        val relationType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Relation.TYPE))

        var relationLabel = ""
        if(relationType == 0){

            relationLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Relation.LABEL)) ?:"CUSTOM"
        }else{
            relationLabel = getRelationLabel(relationType)
        }


        relationList.add(RelationData(relationName = relationName, relationLabel = relationLabel))


        contactRelationMapList.put(displayName, relationList)


    }

    private fun setEventList(cursor: Cursor, displayName : String, contactEventMapList: MutableMap<String, MutableList<EventData>> ) {


        /********************** displayName work as KEY ************/
        var eventList =
            contactEventMapList.get(displayName) // using the key ie Display Name and get All Element of Phone

        if (eventList == null) {


            eventList = ArrayList<EventData>()

        }

        val eventName =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.START_DATE)) ?:""

        val eventType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.TYPE))

        var eventLabel = ""
        if(eventType == 0){

            eventLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.LABEL)) ?: "CUSTOM"
        }else{
            eventLabel = getEventLabel(eventType)
        }


        eventList.add( EventData(startDate = eventName, type = eventLabel))
        contactEventMapList.put(displayName, eventList)


    }

    private fun setNickName(cursor: Cursor, displayName : String, contactNickNameMapList: MutableMap<String, String> ) {

        /********************** displayName work as KEY ************/


        var nickname =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Nickname.NAME)) ?: ""


        contactNickNameMapList.put(displayName,nickname.trim())

    }


    //region Comment
//    private fun setPhotoURI(cursor: Cursor, displayName : String,contactPhotoUriList : MutableList<PhotoUriData> ) {
//
//        val photoUri =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.Photo.PHOTO_URI)) ?: ""
//
//
//        // region handling PhotoUri
//
//        if(!photoUri.isNullOrEmpty()){
//
//            try {
//
//                contactPhotoUriList.add(
//
//                    PhotoUriData(displayName = displayName ,
//                        photoUri =photoUri ,
//                        number = ""
//                    )
//                )
//
//
//            }
//            catch (ex : Exception){
//
//                Log.d(Constant.TAG_SAVING_CONTACT_LOG, "Done" )
//            }
//
//        }
//
//        //endregion
//    }
    //endregion

    private fun setNote(cursor: Cursor, displayName : String, contactNoteMapList: MutableMap<String, String> ) {

        /********************** displayName work as KEY ************/


        var note =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Nickname.NAME)) ?: ""


        contactNoteMapList.put(displayName,note.trim())

    }

    //endregion

    //region other Methods
    fun getRelationLabel(relationType: Int): String {
        return when (relationType) {
            ContactsContract.CommonDataKinds.Relation.TYPE_FATHER -> "FATHER"
            ContactsContract.CommonDataKinds.Relation.TYPE_MOTHER -> "MOTHER"
            ContactsContract.CommonDataKinds.Relation.TYPE_BROTHER -> "BROTHER"
            ContactsContract.CommonDataKinds.Relation.TYPE_SISTER -> "SISTER"

            ContactsContract.CommonDataKinds.Relation.TYPE_ASSISTANT -> "ASSISTANT"
            ContactsContract.CommonDataKinds.Relation.TYPE_CHILD -> "CHILD"
            ContactsContract.CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER -> "DOMESTIC_PARTNER"
            ContactsContract.CommonDataKinds.Relation.TYPE_FRIEND -> "FRIEND"

            ContactsContract.CommonDataKinds.Relation.TYPE_MANAGER -> "MANAGER"
            ContactsContract.CommonDataKinds.Relation.TYPE_PARENT -> "PARENT"
            ContactsContract.CommonDataKinds.Relation.TYPE_PARTNER -> "PARTNER"
            ContactsContract.CommonDataKinds.Relation.TYPE_REFERRED_BY -> "REFERRED_BY"

            ContactsContract.CommonDataKinds.Relation.TYPE_RELATIVE -> "RELATIVE"

            ContactsContract.CommonDataKinds.Relation.TYPE_SPOUSE -> "SPOUSE"
            ContactsContract.CommonDataKinds.Relation.TYPE_CUSTOM -> "CUSTOM"


            // Add more cases for other relation types as needed
            else -> ""
        }
    }

    fun  getEventLabel(EventType : Int) : String {

        return  when(EventType){

            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY -> "BIRTHDAY"
            ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY -> "ANNIVERSARY"
            ContactsContract.CommonDataKinds.Event.TYPE_OTHER -> "OTHER"
            ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM -> "CUSTOM"
            else -> {""}
        }
    }

    fun  getAddressOrEmailLabel(EventType : Int) : String {

        return  when(EventType){

            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME -> "HOME"
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK -> "WORK"
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER -> "OTHER"
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM -> "CUSTOM"
            else -> {""}
        }
    }

    fun  getPhoneLabel(EventType : Int) : String {

        return  when(EventType){

            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "MOBILE"
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "WORK"
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "HOME"
            ContactsContract.CommonDataKinds.Phone.TYPE_MAIN -> "MAIN"

            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK -> "FAX_WORK"
            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME -> "FAX_HOME"
            ContactsContract.CommonDataKinds.Phone.TYPE_PAGER -> "PAGER"
            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> "OTHER"

            ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM -> "CUSTOM"
            ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK -> "CALLBACK"
            ContactsContract.CommonDataKinds.Phone.TYPE_CAR -> "CAR"
            ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN -> "COMPANY_MAIN"

            ContactsContract.CommonDataKinds.Phone.TYPE_ISDN -> "ISDN"
            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX -> "OTHER_FAX"
            ContactsContract.CommonDataKinds.Phone.TYPE_RADIO -> "RADIO"
            ContactsContract.CommonDataKinds.Phone.TYPE_TELEX -> "TELEX"

            ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD -> "TTY_TD"
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE -> "WORK_MOBILE"
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER -> "WORK_PAGER"
            ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT -> "ASSISTANT"
            ContactsContract.CommonDataKinds.Phone.TYPE_MMS -> "MMS"
            else -> {""}
        }
    }

    //endregion




    //********************************* Model  ******************************************//



    //region Molel Data
    data class ModelContact(
        var displayName: String? = "",
    ){

        var givenName: String = ""
        var middleName : String = ""
        var familyName : String = ""

        var phoneNumbers: MutableList<PhoneData> = mutableListOf()

        var nickname: String = ""
        var companyName : String = ""
        var companyTitle : String = ""
        var companyDepartment : String = ""

        var emails: MutableList<EmailData> = mutableListOf()
        var addresses: MutableList<AddressData> = mutableListOf()

        var websites: MutableList<String> = mutableListOf()
        var relations : MutableList<RelationData> = mutableListOf()

        var events: MutableList<EventData> = mutableListOf()

        var note: String? = null
    }

    class Address() {
        var street : String  = ""
        var city : String  = ""
        var region : String  = ""
        var country  : String  = ""
        var label: String = ""

    }


    data class PhoneData(

        var normalizedNumber : String  = "",
        var number : String  = "",
        var type : String = ""
        // var photouri : String  = "",
    )
    data class EmailData(

        var address : String  = "",
        var type : String = ""
    )

    data class AddressData(

        var formattedAddress : String  = "",
        var type : String = ""
    )

    data class NameData(
        var displayName: String = "",
        var givenName : String  = "",
        var middleName : String = "",
        var familyName : String = ""
    )
    data class CompanyData(

        var companyName : String  = "",
        var companyTitle : String = "",
        var companyDepartment : String = ""
    )

    data class RelationData(

        var relationName : String  = "",
        var relationLabel : String = ""
    )

    data class  EventData(

        var type : String  = "",
        var startDate : String  = ""
    )


    data class  PhotoUriData(

        var  displayName:  String  = "",
        var photoUri : String  = "",
        var number : String = ""

    )
    //endregion











}