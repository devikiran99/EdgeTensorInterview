package com.devikiran.edgetesorinterview.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class GpsCollector(private val context: Context) {

    suspend fun collect(): String =
        withContext(Dispatchers.IO) {

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return@withContext "GPS disabled"
            }

            suspendCancellableCoroutine { cont ->

                val fineGranted =
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (!fineGranted) {
                    cont.resume("GPS permission not granted")
                    return@suspendCancellableCoroutine
                }

                val listener = object : LocationListener {

                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)

                        val result = buildString {
                            append("lat=${location.latitude}, ")
                            append("lon=${location.longitude}, ")
                            append("alt=${location.altitude}, ")
                            append("acc=${location.accuracy}, ")
                            append("provider=${location.provider}")
                        }

                        cont.resume(result)
                    }

                    override fun onProviderDisabled(provider: String) {
                        locationManager.removeUpdates(this)
                        cont.resume("GPS provider disabled")
                    }
                }

                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                } catch (e: Exception) {
                    cont.resume("GPS error: ${e.message}")
                }

                cont.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }
            }
        }
}