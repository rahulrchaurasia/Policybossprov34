package com.policyboss.policybosspro.utility

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.policyboss.policybosspro.core.model.DeviceDetailEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utility {


      const val ErrorMessage : String = "Data Not Found.Please try Again!!"

        @JvmStatic
        fun getFilePath(context: Context, contentUri: Uri): String? {
            try {
                val filePathColumn = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                )

                val returnCursor = contentUri.let { context.contentResolver.query(it, filePathColumn, null, null, null) }

                if (returnCursor != null) {

                    returnCursor.moveToFirst()
                    val nameIndex = returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    val name = returnCursor.getString(nameIndex)
                    val file = File(context.cacheDir, name)
                    val inputStream = context.contentResolver.openInputStream(contentUri)
                    val outputStream = FileOutputStream(file)
                    var read: Int
                    val maxBufferSize = 1 * 1024 * 1024
                    val bytesAvailable = inputStream!!.available()

                    val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    val buffers = ByteArray(bufferSize)

                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.write(buffers, 0, read)
                    }

                    inputStream.close()
                    outputStream.close()
                    return file.absolutePath
                }
                else
                {
                    Log.d("","returnCursor is null")
                    return null
                }
            }
            catch (e: Exception) {
                Log.d("","exception caught at getFilePath(): $e")
                return null
            }
        }

       @JvmStatic
       fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }

    //Method to handle URI from content provider or local storage
    @JvmStatic
    fun handleUri(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                // For content URIs (e.g., cloud storage), get the file path using the content resolver
                try {
                    getFilePath(context, uri)
                } catch (e: Exception) {
                    Log.e("handleUri", "Error getting file path for content URI", e)
                    null
                }
            }
            "file" -> {
                // For file URIs (e.g., local files), directly get the path
                uri.path
            }
            else -> {
                // Handle any other URI types, such as cloud storage
                null
            }
        }
    }

    @JvmStatic
    fun getDeviceDetail(context: Context) : DeviceDetailEntity {

      return  DeviceDetailEntity(

            Model = Build.MODEL ?: "",
            ID = Build.ID?: "",

            SDK =  ""+ Build.VERSION.SDK_INT,
            Manufacture = Build.MANUFACTURER?: "",
            Brand = Build.BRAND?: "",
            VersionCode = Build.VERSION.RELEASE?: ""
        )
    }


    @JvmStatic
    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context): String {
        try {
            return  Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }catch (ex: Exception){
            return ""
        }

    }

    @JvmStatic
    @SuppressLint("HardwareIds")
    fun getDeviceName(): String {
        try {
            return  "${Build.BRAND}-${Build.MODEL}"
        }catch (ex: Exception){
            return ""
        }

    }
    @SuppressLint("HardwareIds")
    fun getOS(): String {
        try {
            return  "Android:${Build.VERSION.RELEASE}"
        }catch (ex: Exception){
            return ""
        }

    }


    @JvmStatic
    fun copyTextToClipboard(str : String,context: Context ) {
        val textToCopy = str

        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)


        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }


    @JvmStatic
    fun loadWebViewUrlInBrowser(context: Context, url: String) {
        Log.d("URL", url)
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(browserIntent)
    }


    @JvmStatic
    fun getVersionName(context: Context): String {
        return try {
            val pinfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pinfo.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun getCurrentVersion(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // For API level 28 and above (Android 9/Pie)
                packageInfo.longVersionCode
            } else {
                // For below API level 28
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0L
        }
    }

    @JvmStatic
    fun getVersionCode(context: Context): Long {
        return try {
            val pinfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pinfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pinfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0L
        }
    }

    @JvmStatic
    fun createDirIfNotExists(): File? {
        val file = File(Environment.getExternalStorageDirectory(), "/PolicyBossPro")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog", "Problem creating Image folder")
                return null
            }
        }
        return file
    }

    @JvmStatic
    fun createDirIfNotExistsNew(context: Context): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PolicyBossPro")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("File Log", "Directory not created")
            }
        }
        return file
    }

    @JvmStatic
    fun saveImageToStorage(bitmap: Bitmap, name: String, context: Context): File {
        val dir = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            createDirIfNotExists()
        } else {
            createDirIfNotExistsNew(context)
        }

        val fileName = "$name.jpg".replace("\\s+".toRegex(), "")
        val file = File(dir, fileName)

        FileOutputStream(file).use { outStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, outStream)
            outStream.flush()
        }

        return file
    }




    @JvmStatic
    fun createShareDirIfNotExists(): File? {
        val file = File(Environment.getExternalStorageDirectory(), "/PolicyBossPro/QUOTES")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog", "Problem creating Quotes folder")
                return null
            }
        }
        return file
    }

    @JvmStatic
    fun getNewFileName(name: String): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "$name$timeStamp.jpg"
    }

    @JvmStatic
    fun getPdfFileName(name: String): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "$name$timeStamp.pdf"
    }

    @JvmStatic
    fun getImageDirectoryPath(): String? {
        return Environment.DIRECTORY_PICTURES + File.separator + "PolicyBossPro"
    }





    @JvmStatic
    fun getCurrentMobileDateTime(): String {
        val sdf = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    @JvmStatic
    fun getMultipartImage(file: File): MultipartBody.Part {
//        val imgBody = RequestBody.create(MediaType.parse("image/*"), file)
//        return MultipartBody.Part.createFormData("DocFile", file.name, imgBody)

        val mediaType = "image/*".toMediaTypeOrNull() // Use the new `toMediaTypeOrNull()` method
        val imgBody = file.asRequestBody(mediaType)  // Use `asRequestBody()` extension function
        return MultipartBody.Part.createFormData("DocFile", file.name, imgBody)

    }

    @JvmStatic
    fun getMultipartImage(file: File, serverKey: String): MultipartBody.Part {
        val mediaType = "image/*".toMediaTypeOrNull()
        val imgBody = file.asRequestBody(mediaType)
        return MultipartBody.Part.createFormData(serverKey, file.name, imgBody)
    }

    @JvmStatic
    fun getBody( FbaID: String, DocType: String, DocName: String, ssid: String, appVersion: String, deviceCode: String): HashMap<String, String> {
        return hashMapOf(
            "FBAID" to FbaID,
            "DocType" to DocType,
            "DocName" to DocName,
            "app_version" to appVersion,
            "ssid" to ssid,
            "device_code" to deviceCode
        )
    }

    @JvmStatic
    fun getMultipartPdf(file: File, fileName: String, serverKey: String): MultipartBody.Part {
//        val pdfBody = RequestBody.create(MediaType.parse("file/*"), file)
//        return MultipartBody.Part.createFormData(serverKey, fileName, pdfBody)

        val mediaType = "application/pdf".toMediaType()  // Use correct media type for PDFs
        val pdfBody = file.asRequestBody(mediaType)      // Create RequestBody for PDF file
        return MultipartBody.Part.createFormData(serverKey, fileName, pdfBody)
    }

    @JvmStatic
    fun getBodyCommon(context: Context, id: String, crn: String, fileType: String, insurerId: String): HashMap<String, String> {
        return hashMapOf(
            "crn" to crn,
            "document_id" to id,
            "insurer_id" to insurerId,
            "document_type" to fileType
        )
    }



    //Mark : byteArray to Bitmap
    @JvmStatic
     fun decodeBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    suspend fun urlToBitmap(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                return@withContext BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }


    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return inputStream?.use {
            BitmapFactory.decodeStream(it)
        }
    }



    suspend fun downloadBitmapFromUrl(url: URL?): Bitmap? {
        return withContext(Dispatchers.IO) {
            if (url == null) return@withContext null // Early return for null URL

            try {
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()

                connection.getInputStream().use { inputStream ->
                    return@withContext BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()  // Log the exception if needed
                return@withContext null
            }
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }


    fun getDateFromAge(age: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -age)
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
    }

    fun getDateFromWeb(birthdate: String): String {
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val inputDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return try {
            // Parse the input date string to a Date object
            val date = inputDateFormat.parse(birthdate)

            // Format the Date object to the desired output format
            outputDateFormat.format(date ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the parse exception if the input date format is incorrect
            ""
        }
    }

    fun getAgeFromDate(birthdate: String): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val birthDate = Calendar.getInstance()
            birthDate.time = dateFormat.parse(birthdate) ?: Date()

            val today = Calendar.getInstance()
            val curYear = today.get(Calendar.YEAR)
            val dobYear = birthDate.get(Calendar.YEAR)

            curYear - dobYear
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }



    // Function for creating a unique image file path
    @JvmStatic
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    // Function to create an image URI
    @JvmStatic
    fun createImageUri(context: Context): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraApp")
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // For Android 9 and below, save in external storage
            val imageFile = createImageFile(context)
            FileProvider.getUriForFile(
                context,
                "com.policyboss.policybosspro.fileprovider",
                imageFile
            )
        }
    }



    @JvmStatic
    fun createImageUri1(context: Context) : Uri {

        // val image = File(context.filesDir,"camera_photo.png")
        val imageFile = createImageFile(context)

        return FileProvider.getUriForFile(context.applicationContext,
            "com.policyboss.policybosspro.fileprovider",
            imageFile
        )


    }



   //camera image Rotation
    fun handleImageOrientation(uri: Uri, context: Context): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exifInterface = inputStream?.let { ExifInterface(it) }
        val orientation = exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val bitmap = BitmapFactory.decodeStream(inputStream)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)

        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true

        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }








}

