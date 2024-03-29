package com.himanshu.android.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.os.SystemClock
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.vmadalin.easypermissions.EasyPermissions
import com.himanshu.android.R
import com.himanshu.android.domain.db.user.UserLocationEntity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    class SafeClickListener(
        private var defaultInterval: Int = 3000,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
        Intent(this, activity).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    fun NavController.safeNavigate(direction: Any) {
        val action = if (direction is NavDirections) direction.actionId else direction
        currentDestination?.getAction(action as Int)?.run {
            navigate(action)
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun Fragment.showSoftKeyboard(searchView: SearchView) {
        val imm = (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.toggleSoftInputFromWindow(
            searchView.windowToken, 0, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun clearFocus(view: View, editText: TextInputEditText, activity: Activity) {
        if (view !is TextInputEditText) {
            view.setOnTouchListener { _, _ ->
                activity.hideSoftKeyboard()
                editText.clearFocus()
                editText.isCursorVisible = false
                false
            }
        }
        if (view is TextInputEditText) {
            view.setOnTouchListener { _, _ ->
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                clearFocus(innerView, editText, activity)
            }
        }
    }

    fun hasLocationPermission(context: Context) = EasyPermissions.hasPermissions(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    fun requestLocationPermission(hostFragment: Fragment) {
        EasyPermissions.requestPermissions(
            hostFragment,
            "You need to accept location permissions for this app to properly work.",
            Constants.PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun createLocationRequest(activity: Activity, hostFragment: Fragment) {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val taskVerifyLocationSetting: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        taskVerifyLocationSetting.addOnSuccessListener { locationSettingsResponse ->
            if (locationSettingsResponse.locationSettingsStates?.isLocationUsable == true) {
                requestLocationPermission(hostFragment)
            }
        }

        taskVerifyLocationSetting.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(activity,
                        Constants.REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun userLocationBuilder(
        id: Int? = null,
        address: Address?,
        latLng: LatLng,
    ): UserLocationEntity {
        return UserLocationEntity(
            id = id,
            address = address?.thoroughfare,
            city = address?.locality,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            currentUse = true
        )
    }

    fun List<String?>.stringBuilder(): StringBuilder {
        val categoryList = StringBuilder()
        this.forEach { category ->
            categoryList.append("$category • ")
        }
        return categoryList
    }

    fun timeFormatter(time: Int): String {
        val ft = DateUtils.formatElapsedTime(time.toLong())
        val timeSplit = ft.split(":")
        val hour = timeSplit[0].toInt()
        if (hour > 12) {
            return (hour - 12).toString() + ":${timeSplit[1]}pm"
        }
        return ft.slice(0..3) + "am"
    }



    fun GoogleMap?.setMap(app: Context, coordinates: LatLng) {
        this?.mapType = GoogleMap.MAP_TYPE_NORMAL
        this?.uiSettings?.setAllGesturesEnabled(false)
        this?.addMarker(MarkerOptions().position(coordinates).icon(bitmapDescriptorFromVector(app, R.drawable.ic_location)))
        this?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15.8f))
    }

    fun GoogleMap?.setMapWithTwoPoint(app: Context, position1: LatLng, position2: LatLng) {
        this?.mapType = GoogleMap.MAP_TYPE_NORMAL
        this?.uiSettings?.setAllGesturesEnabled(false)
        this?.addMarker(MarkerOptions().position(position1).icon(bitmapDescriptorFromVector(app, R.drawable.ic_location)))
        this?.addMarker(MarkerOptions().position(position2).icon(bitmapDescriptorFromVector(app, R.drawable.ic_location)))
        this?.moveCamera(CameraUpdateFactory.newLatLngZoom(position1, 15.8f))
    }

    fun RecyclerView.onReady(isReady: () -> Unit) {
        val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                isReady()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    fun bitmapDescriptorFromVector(app: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(app, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun parseTimeToString(time: Long, pattern: String): String {
        try {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(time))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}