package com.policyboss.policybosspro.utils.AppPermission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.provider.Settings
import javax.inject.Inject
import javax.inject.Singleton

//tempRahul

class PermissionHandler  constructor (private val activity: AppCompatActivity)  {

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private var permanentlyDeniedCallback: ((List<String>) -> Unit)? = null

    private val permissionLauncher: ActivityResultLauncher<Array<String>> = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResult(permissions)
    }

    fun checkAndRequestPermissions(
        type: AppPermissionManager.PermissionType,
        onResult: (Boolean) -> Unit,
        onPermanentlyDenied: (List<String>) -> Unit
    ) {
        permissionCallback = onResult
        permanentlyDeniedCallback = onPermanentlyDenied

        val permissions = AppPermissionManager.getPermissions(type)

        when {
            hasPermissions(permissions) -> onResult(true)
            shouldShowPermissionRationale(permissions) -> showPermissionRationaleDialog(permissions)
            else -> permissionLauncher.launch(permissions)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun shouldShowPermissionRationale(permissions: Array<String>): Boolean {
        return permissions.any { permission ->
            activity.shouldShowRequestPermissionRationale(permission)
        }
    }

    private fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val permanentlyDenied = mutableListOf<String>()

        val allGranted = permissions.entries.all { entry ->
            val isGranted = entry.value
            if (!isGranted && !activity.shouldShowRequestPermissionRationale(entry.key)) {
                permanentlyDenied.add(entry.key)
            }
            isGranted
        }

        when {
            allGranted -> permissionCallback?.invoke(true)
            permanentlyDenied.isNotEmpty() -> permanentlyDeniedCallback?.invoke(permanentlyDenied)
            else -> permissionCallback?.invoke(false)
        }
    }

    private fun showPermissionRationaleDialog(permissions: Array<String>) {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Permissions Required")
            .setMessage(getRationaleMessage(permissions))
            .setPositiveButton("Grant") { _, _ -> permissionLauncher.launch(permissions) }
            .setNegativeButton("Cancel") { _, _ -> permissionCallback?.invoke(false) }
            .show()
    }

    private fun getRationaleMessage(permissions: Array<String>): String {
        return when {
            permissions.contains(Manifest.permission.CAMERA) ->
                "Camera and storage permissions are required to take and save photos."

            permissions.contains(Manifest.permission.READ_MEDIA_IMAGES) ||
                    permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE) ->
                "Storage permission is required to access and share files."

            permissions.contains(Manifest.permission.READ_CONTACTS) ->
                "Contacts and call log permissions are required to sync your contacts."

            permissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> // Added rationale for POST_NOTIFICATIONS
                "Notification permission is required to send notifications to you."

            else -> "These permissions are required for the app to function properly."
        }
    }

    fun showPermissionDeniedDialog(permanentlyDeniedPermissions: List<String>,txtMessage : String? = null) {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Permission Denied")
            .setMessage(txtMessage ?: "Please enable necessary permissions in Settings to continue.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(this)
        }
    }
}