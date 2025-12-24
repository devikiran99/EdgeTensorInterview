package com.devikiran.edgetesorinterview.data.source

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
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class GpsCollector(private val context: Context) {

    suspend fun collect(): String =
        withContext(Dispatchers.IO) {

            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val fineGranted =
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (!fineGranted) return@withContext "Location permission not granted"

            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            ).filter { lm.isProviderEnabled(it) }

            if (providers.isEmpty()) return@withContext "No location providers enabled"


            providers.forEach { provider ->
                lm.getLastKnownLocation(provider)?.let {
                    return@withContext format(it, "last_known_$provider")
                }
            }

            return@withContext withTimeoutOrNull(20_000) {
                suspendCancellableCoroutine { cont ->

                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            providers.forEach { _ -> lm.removeUpdates(this) }
                            cont.resume(format(location, "fresh_${location.provider}"))
                        }
                    }

                    providers.forEach { provider ->
                        lm.requestLocationUpdates(
                            provider,
                            0L,
                            0f,
                            listener,
                            Looper.getMainLooper()
                        )
                    }

                    cont.invokeOnCancellation {
                        providers.forEach { _ -> lm.removeUpdates(listener) }
                    }
                }
            } ?: "Location timeout"
        }

    private fun format(location: Location, source: String): String =
        "lat=${location.latitude}, lon=${location.longitude}, " +
                "acc=${location.accuracy}, provider=${location.provider}, source=$source"
}