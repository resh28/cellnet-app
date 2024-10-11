package com.example.cellnet.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


object Util {
    private var job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val snackBarNotificationFlow = MutableStateFlow(Pair(SnackbarInfoLevel.INFO, ""))


    @RequiresApi(Build.VERSION_CODES.O)
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @SuppressLint("HardwareIds")
    fun getDeviceInfo(context: Context): DeviceInfo {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val productName = Build.PRODUCT
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER
        val osVersion = Build.VERSION.RELEASE
        val appVersion = getPackageInfo(context).versionName
        val phoneType = NetworkUtil.getPhoneType(context)
        return DeviceInfo(
            androidId, productName, model, manufacturer, osVersion, appVersion, "", phoneType
        )
    }

    private fun getPackageInfo(context: Context, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            context.packageManager.getPackageInfo(
                context.packageName,
                flags
            )
        }

    fun showSnackbar(infoLevel: SnackbarInfoLevel, message: String) {
        scope.launch {
            snackBarNotificationFlow.tryEmit(Pair(infoLevel, message))
            delay(5000)
            snackBarNotificationFlow.tryEmit(Pair(SnackbarInfoLevel.INFO, ""))
        }
    }

    fun showSnackbarIndefinite(infoLevel: SnackbarInfoLevel, message: String) {
        scope.launch {
            snackBarNotificationFlow.tryEmit(Pair(infoLevel, message))
        }
    }

    fun getSnackbarFlow(): MutableStateFlow<Pair<SnackbarInfoLevel, String>> {
        return snackBarNotificationFlow
    }

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int,
        @DrawableRes bgVectorDrawableResourceId: Int,
        markerWidth: Int = 100, // Desired width for the marker
        markerHeight: Int = 100, // Desired height for the marker
        bgColor: Color,
        iconColor: Color
    ): BitmapDescriptor {
        // Get the background drawable
        val background = ContextCompat.getDrawable(context, bgVectorDrawableResourceId)
        background!!.setBounds(0, 0, markerWidth, markerHeight)
        background.setColorFilter(bgColor.toArgb(), PorterDuff.Mode.SRC_IN)

        // Create a bitmap with the desired marker size
        val bitmap = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Change background color
//        canvas.drawColor(bgColor.toArgb()) // Convert to ARGB
        background.draw(canvas)

        // Get the vector drawable
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)

        // Change icon color
        vectorDrawable!!.setColorFilter(iconColor.toArgb(), PorterDuff.Mode.SRC_IN) // Convert to ARGB

        // Calculate scale factor to fit the vector drawable within the background
        val scaleX = markerWidth.toFloat() / vectorDrawable.intrinsicWidth
        val scaleY = markerHeight.toFloat() / vectorDrawable.intrinsicHeight
        val scale = minOf(scaleX, scaleY) // Use the smaller scale to maintain aspect ratio

        // Create a scaled bitmap for the vector drawable
        val scaledWidth = (vectorDrawable.intrinsicWidth * scale).toInt()
        val scaledHeight = (vectorDrawable.intrinsicHeight * scale).toInt()

        // Set bounds for the scaled vector drawable (center it)
        vectorDrawable.setBounds(
            ((markerWidth - scaledWidth) / 2) + 10,
            ((markerHeight - scaledHeight) / 2) + 5,
            ((markerWidth + scaledWidth) / 2) - 10,
            ((markerHeight + scaledHeight) / 2) - 10
        )

        // Draw the scaled vector drawable onto the canvas
        vectorDrawable.draw(canvas)

        // Return the bitmap as a BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun <T> splitIntoBatches(list: List<T>, batchSize: Int): List<List<T>> {
        val batches = mutableListOf<List<T>>()
        var index = 0
        while (index < list.size) {
            val end = (index + batchSize).coerceAtMost(list.size)
            batches.add(list.subList(index, end))
            index += batchSize
        }
        return batches
    }
}