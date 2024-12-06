package com.policyboss.policybosspro.utils.CameraGalleryManager

import android.content.Context
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CameraGalleryManager  constructor(

    @ActivityContext private  val context : Context,

    private val permissionHandler: PermissionHandler
) {

    fun handleGalleryCameraPopup(
        ivUser: ImageView,
        crn: String,
        documentId: String,
        documentType: String,
        insurerId: String,
        onShowPopup: (() -> Unit)? = null, // Nullable Kotlin lambda
        popupCallback: PopupCallback? = null // Nullable Java interface
    ) {
        // Generate file name
     //   val photoFileName = FileUtil.generatePhotoFileName(documentId)
        // Log file name
      //  println("All Uploading: $photoFileName")

        // Check permissions
        permissionHandler.checkAndRequestPermissions(
            type = AppPermissionManager.PermissionType.CAMERA_AND_STORAGE,
            onResult = { granted ->
                if (granted) {
                    // Show pop-up if permissions are granted
                    onShowPopup?.invoke()
                    popupCallback?.onShowPopup()
                }
            },
            onPermanentlyDenied = { permanentlyDeniedPermissions ->
                // Handle permanently denied permissions by directing to Settings
                permissionHandler.showPermissionDeniedDialog(
                    permanentlyDeniedPermissions = permanentlyDeniedPermissions,
                    txtMessage = "Please Grant Camera And Storage Permission")
            }
        )
    }


    private fun showPermissionDeniedDialog(permanentlyDeniedPermissions: List<String>) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Permission Denied")
            .setMessage("Please enable notification permissions in Settings to receive notifications.")
            .setPositiveButton("Open Settings") { _, _ ->
                permissionHandler.openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}


interface PopupCallback {
    fun onShowPopup()
}