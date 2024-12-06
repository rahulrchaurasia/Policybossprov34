package com.policyboss.policybosspro.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    private const val BUFFER_SIZE = 4 * 1024 // 4kb buffer
    private const val TAG = "FileUtils"

    @JvmStatic
    fun getFilePath(context: Context, uri: Uri): String? {
        try {
            val fileName = getFileName(context, uri) ?: run {
                Log.e(TAG, "Couldn't get file name")
                return null
            }

            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                    return file.absolutePath
                }
            } ?: run {
                Log.e(TAG, "Failed to open input stream")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file path: ${e.message}", e)
            return null
        }
    }

    @JvmStatic
    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            result = cursor.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting file name from cursor: ${e.message}", e)
            }
        }

        if (result == null) {
            result = uri.path?.let { path ->
                path.lastIndexOf('/').let { cut ->
                    if (cut != -1) path.substring(cut + 1) else path
                }
            }
        }

        return result
    }

    @JvmStatic
    fun isValidPdfFile(context: Context, uri: Uri): Boolean {
        return try {
            Log.d(TAG, "Uri: $uri, Scheme: ${uri.scheme}")
            context.contentResolver.getType(uri)?.lowercase()?.contains("pdf") == true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking PDF file type: ${e.message}", e)
            false
        }
    }

    @JvmStatic
    fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024 // 5 MB in bytes
        return file.length() <= maxFileSize
    }
}