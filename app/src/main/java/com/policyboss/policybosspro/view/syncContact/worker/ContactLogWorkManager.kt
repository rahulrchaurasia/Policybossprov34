package com.policyboss.policybosspro.view.syncContact.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.policyboss.policybosspro.core.RetroHelper


import com.policyboss.policybosspro.core.requestbuilder.ContactLeadRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.ContactlistEntity
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.syncContact.helper.ContactHelper
import com.policyboss.policybosspro.view.syncContact.ui.SyncContactActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Created by Rahul on 10/06/2022.
 *
 *
 */
// logic For Sending data to Sever from contactlist size :

//      contactlist.size = 320
//       ProgressStep = 100

//for (i in 0 until contactlist.size step ProgressStep) {

//        Here's how the loop iterations will work:
//        Iteration 1:
//
//        i = 0
//        endIndex = min(0 + 100, 320) = 100
//        contactChunk = contactlist.subList(0, 100)
//
//        This gives you elements at indices 0 to 99 (100 elements)
//
//        Iteration 2:
//
//        i = 100
//        endIndex = min(100 + 100, 320) = 200
//        contactChunk = contactlist.subList(100, 200)
//
//        This gives you elements at indices 100 to 199 (100 elements)
//
//        Iteration 3:
//
//        i = 200
//        endIndex = min(200 + 100, 320) = 300
//        contactChunk = contactlist.subList(200, 300)
//
//        This gives you elements at indices 200 to 299 (100 elements)
//
//
//
//        Iteration 4:
//
//        i = 300
//        endIndex = min(300 + 100, 320) = 320
//        contactChunk = contactlist.subList(300, 320)
//
//        This gives you elements at indices 300 to 319 (20 elements)
//




class ContactLogWorkManager(
    val context: Context,
    workerParameters: WorkerParameters,

    ) : CoroutineWorker(context, workerParameters) {


    private val TAG = "CALL_LOG_CONTACT"
    private val ProgressStep = 100
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    override suspend fun doWork(): Result {
        return try {
            Log.d("CallLogWorker", "Run work manager")
            //Do Your task here

            var ContactCount = callContactTask()


            // Log.d("CallLogWorker", callLogList.toString())
            val outPutData: Data = Data.Builder()
                .putString(Constant.KEY_result, "${ContactCount}")
                .build()
            Result.success(outPutData)
        }
        catch (e: Exception) {
            Log.d(TAG, "exception in doWork1 ${e.message}")
            val errorData: Data = Data.Builder()
                .putString(Constant.KEY_error_result, "Data Not Uploaded.Please Try Again.")
                .build()
            Result.failure(errorData)

        }

    }

    private suspend fun callContactTask() : Int {

        var ContactCount = 0



        //region Comment : Not In Used
        var strbody = Constant.CONTACT_LOG_DataFetching
        var strResultbody = "Successfully fetch the data.."
        setForeground(createForegroundInfo(0, 0, strbody))

        //endregion


        // region getting Input Data
        val fbaid = inputData.getInt(Constant.KEY_fbaid, 0)
        val ssid = inputData.getString(Constant.KEY_ssid)
        val parentid = inputData.getString(Constant.KEY_parentid)
        val deviceID = inputData.getString(Constant.KEY_deviceid) ?: ""
        val appversion = inputData.getString(Constant.KEY_appversion) ?: ""

        var tfbaid = ""
        var tsub_fba_id = ""
        var getAllContactDetails :  MutableList<ContactHelper.ModelContact> = mutableListOf()

        if (parentid.isNullOrEmpty() || parentid.equals("0")) {

            tfbaid = fbaid.toString()
            tsub_fba_id = parentid.toString()

        } else {
            tfbaid = parentid.toString()
            tsub_fba_id = fbaid.toString()
        }

        //endregion
        //  delay(2000)

        withContext(Dispatchers.IO) {

            var url =  "https://horizon.policyboss.com:5443/sync_contacts" + "/contact_entry"


            var contactlist = getContactList()

            // region  05 temp commented for Testing for Increase the Contact List
//            val contactlist: MutableList<ContactlistEntity> = mutableListOf()
//
//            for (i in 1..8) {
//
//             //Assuming getContactList() returns a ContactlistEntity
//                contactlist.addAll(getContactList())
//            }
            //endregion

            ContactCount = contactlist.size
            Log.d(TAG, "Total Contact Size ${contactlist.size}")

            try{

                getAllContactDetails =  ContactHelper.getContact(context.applicationContext)

                Log.d(TAG, "1> Total contactlist Count: ${contactlist.count()}  2>Total  Row contact Count:${getAllContactDetails.count()}")

//                    var data = Gson().toJson(getAllContactDetails)
//                    Log.d(Constant.TAG_SAVING_CONTACT_LOG,data )
//

            }catch (ex :Exception ){
                Log.d(Constant.TAG_SAVING_CONTACT_LOG,ex.toString() )
            }

            //region ForegroundService and Background

            var remainderContactProgress = 0
            var remainderRawProgress = 0

            //var remainderProgress = 0
            var maxProgress = 0
            var currentProgress = 0
            var defaultProgress = 1

            ////////

            // Calculate total chunks for contactlist
            val contactChunks = contactlist.size / ProgressStep
            remainderContactProgress = contactlist.size % ProgressStep

            // Calculate total chunks for raw data
            val rawChunks = getAllContactDetails.size / ProgressStep
            remainderRawProgress = getAllContactDetails.size % ProgressStep

            // Calculate maxProgress
            maxProgress = contactChunks + rawChunks + defaultProgress

            // Add 1 for each remainder
            if (remainderContactProgress > 0) {
                maxProgress += 1
            }
            if (remainderRawProgress > 0) {
                maxProgress += 1
            }

            // Initialize currentProgress
            currentProgress = defaultProgress


            //////

//            maxProgress = contactlist!!.size / ProgressStep
//
//            remainderProgress = contactlist!!.size % ProgressStep
//            maxProgress = maxProgress + defaultProgress
//            currentProgress = defaultProgress
//            if (remainderProgress > 0) {
//                maxProgress = maxProgress + 1
//            }

            // Log.d(TAG, "maxProgress ${maxProgress}")
            setForeground(createForegroundInfo(maxProgress, currentProgress, strbody))
            val workProgessDefault = workDataOf(
                Constant.CONTACT_LOG_Progress to currentProgress,
                Constant.CONTACT_LOG_MAXProgress to maxProgress,
                // Constant.CONTACT_LOG_Data_SIZE to contactlist.size
            )
            setProgress(workProgessDefault)


            //endregion

            var subcontactlist: List<ContactlistEntity>
            var subAllContactDetails :   List<ContactHelper.ModelContact>

            if (contactlist != null && contactlist!!.size > 0) {



                //Mark For Sending contactlist Only
                for (i in 0 until  contactlist.size step ProgressStep) {
                    delay(1000)

                    // Calculate end index for contactlist chunk
                    val contactEndIndex = minOf(i + ProgressStep, contactlist.size)
                    val contactChunk = if (i < contactlist.size) {
                        contactlist.subList(i, contactEndIndex)
                    } else {
                        emptyList() // If contactlist is exhausted, send empty list
                    }

                    // Calculate end index for raw contacts chunk

                    Log.d(TAG, "contact Processing chunk: ${i}-${contactEndIndex}, contacts=${contactChunk.size} ")

                    // Prepare the request entity
                    val contactRequestEntity = ContactLeadRequestEntity(
                        fbaid = tfbaid,
                        ssid = ssid!!,
                        sub_fba_id = tsub_fba_id,
                        contactlist = contactChunk,
                        raw_data = "",
                        device_id = deviceID,
                        app_version = appversion
                    )

                  //  Log.d(Constant.TAG_SAVING_CONTACT_LOG, Gson().toJson(rawContactChunk))

                    // Make the API call
                    val resultResp = RetroHelper.api.saveContactLead(url, contactRequestEntity)

                    if (resultResp?.isSuccessful == true) {
                        Log.d(TAG, "Response Done Contact: ${i}")

                        // Update progress
                        currentProgress += 1
                        val workProgress = workDataOf(
                            Constant.CONTACT_LOG_Progress to currentProgress,
                            Constant.CONTACT_LOG_MAXProgress to maxProgress
                        )
                        setProgress(workProgress)

                        if (currentProgress < maxProgress) {
                            setForeground(
                                createForegroundInfo(
                                    maxProgress = maxProgress,
                                    progress = currentProgress,
                                    strbody = strbody
                                )
                            )
                        }

                    } else {
                        Log.d(TAG, resultResp.toString())
                    }
                }


                //Mark For Sending Raw Data Only
                for (i in 0 until  getAllContactDetails.size step ProgressStep) {

                    delay(1000)



                    // Calculate end index for raw contacts chunk
                    val rawEndIndex = minOf(i + ProgressStep, getAllContactDetails.size)
                    val rawContactChunk = if (i < getAllContactDetails.size) {
                        getAllContactDetails.subList(i, rawEndIndex)
                    } else {
                        emptyList() // If raw contacts are exhausted, send empty list
                    }

                    Log.d(TAG, "Raw Data Processing chunk: ${i}-${rawEndIndex}, rawData=${rawContactChunk.size}")

                    // Prepare the request entity
                    val contactRequestEntity = ContactLeadRequestEntity(
                        fbaid = tfbaid,
                        ssid = ssid!!,
                        sub_fba_id = tsub_fba_id,
                        contactlist = null,
                        raw_data = Gson().toJson(rawContactChunk),
                        device_id = deviceID,
                        app_version = appversion
                    )

                    Log.d(Constant.TAG_SAVING_CONTACT_LOG, Gson().toJson(rawContactChunk))

                    // Make the API call
                    val resultResp = RetroHelper.api.saveContactLead(url, contactRequestEntity)

                    if (resultResp?.isSuccessful == true) {
                        Log.d(TAG, "Response Done Contact: ${i}")

                        // Update progress
                        currentProgress += 1
                        val workProgress = workDataOf(
                            Constant.CONTACT_LOG_Progress to currentProgress,
                            Constant.CONTACT_LOG_MAXProgress to maxProgress
                        )
                        setProgress(workProgress)

                        if (currentProgress < maxProgress) {
                            // Update foreground with progress
                            setForeground(
                                createForegroundInfo(
                                    maxProgress = maxProgress,
                                    progress = currentProgress,
                                    strbody = strbody
                                )
                            )
                        } else {

                            // Final success message after both loops complete
                            setForeground(
                                createForegroundInfo(
                                    maxProgress = maxProgress,
                                    progress = currentProgress,
                                    strbody = strResultbody
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, resultResp.toString())
                    }
                }



            }

        }

        return ContactCount
    }
    private  fun getContactList(): MutableList<ContactlistEntity> {

        var contactlist: MutableList<ContactlistEntity> = ArrayList<ContactlistEntity>()
        var templist: MutableList<String> = ArrayList<String>()
        var phones: Cursor? = null


        val PROJECTION = arrayOf(
            ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.CONTACT_ID,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1
        )

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val filter =
            "" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        val order =
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"// LIMIT " + limit + " offset " + lastId + "";

        phones = applicationContext.contentResolver.query(uri, PROJECTION, filter, null, order)




        ///////////////////////////


        val regex = Regex("[^.0-9]")

        phones.let {

            if (phones != null && phones!!.getCount() > 0) {
                try {
                    var i = 1
                    while (phones.moveToNext()) {


                        var name =
                            "" + phones.getString(
                                phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) ?: 0
                            )
                        var phoneNumber =
                            "" + phones.getString(
                                phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    ?: 0
                            )

                        //  phoneNumber = phoneNumber.trim().replace("[^0-9\\s+]", "");   // remove Special character and Space

                        phoneNumber = regex.replace(phoneNumber, "") // works
                        //.replace("\\s".toRegex(), "")

                        if (phoneNumber.length >= 10) {

                            phoneNumber = phoneNumber.takeLast(10)
                            // check whether the number alreday added to list or not

                            if (!templist!!.contains(phoneNumber)) {
                                templist?.add(phoneNumber)

                                val selectUser = ContactlistEntity(
                                    name = name,
                                    mobileno = phoneNumber,
                                    id = i
                                )
                                // Log.i(TAG, "Key ID: " + i + " Name: " + name + " Mobile: " + phoneNumber + "\n");
                                contactlist.add(selectUser)

                            }


                        }


                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }



        return contactlist


    }


    //region Creates notifications for service
    private fun createForegroundInfo(
        maxProgress: Int,
        progress: Int,
        strbody: String
    ): ForegroundInfo {


        val id = "com.utility.PolicyBossPro.notifications556"
        val channelName = "SynContact channel"
        val title = "Sync Contact"
        val cancel = "Cancel"

        val body = strbody
//            .setProgress(0, 0, true)    // for indeterminate progress

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id, channelName)
        }


        // region Commented :--> For Handling  cancel
//        val intent = WorkManager.getInstance(applicationContext)
//            .createCancelPendingIntent(getId())

        // .setSummaryText(NotifyData.get("body"));

        //  new createBitmapFromURL(NotifyData.get("img_url")).execute();

        //endregion
        /////////////////////////////////////

        val notifyIntent = Intent(applicationContext, SyncContactActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notifyIntent.putExtra(Constant.NOTIFICATION_EXTRA, true)
        notifyIntent.putExtra(Constant.NOTIFICATION_PROGRESS, progress)
        notifyIntent.putExtra(Constant.NOTIFICATION_MAX, maxProgress)
        notifyIntent.putExtra(Constant.NOTIFICATION_MESSAGE, strbody)

        //region Commented : Adding Pending Intent For Handling Notification Click Action
//        val notifyPendingIntent = PendingIntent.getActivities(
//            applicationContext, 0, arrayOf(notifyIntent), PendingIntent.FLAG_UPDATE_CURRENT
//        )

        //or

//        val notifyPendingIntent: PendingIntent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notifyPendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//            )
//        } else {
//            notifyPendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }
//
        //endregion


        /////////////////////////

        val notificationBuilder: NotificationCompat.Builder

        notificationBuilder = NotificationCompat.Builder(applicationContext, id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setSmallIcon(com.policyboss.policybosspro.R.drawable.pb_pro_logo)
            notificationBuilder.color = applicationContext.getColor(com.policyboss.policybosspro.R.color.colorPrimary)
        } else {
            notificationBuilder.setSmallIcon(com.policyboss.policybosspro.R.drawable.pb_pro_logo)
        }

        // .addAction(android.R.drawable.ic_delete, cancel, intent)

        notificationBuilder
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(body)
            .setOngoing(false)
            .setProgress(maxProgress, progress, false)

            // .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)

            .build()



        return ForegroundInfo(1,
            notificationBuilder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel1(id: String, channelName: String) {
        notificationManager.createNotificationChannel(
            NotificationChannel(id, channelName, NotificationManager.IMPORTANCE_DEFAULT)

        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(id: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                id,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.BLUE
            channel.description = "PoliyBoss Pro"
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel.lockscreenVisibility =
                Notification.VISIBILITY_PUBLIC // Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }
    }

    //endregion
}