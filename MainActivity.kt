package com.tracker.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import okhttp3.*
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val SUPABASE_URL = "https://akqbbsnangcxbdktmruk.supabase.co"
    private val API_KEY = "sb_publishable_0VC2d_CBQH1q1V9-9-enVg_Avfn2uGP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startTracking()
    }

    private fun startTracking() {
        val request = LocationRequest.create().apply {
            interval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                sendToServer(loc.latitude, loc.longitude, loc.speed * 3.6)
            }
        }, null)
    }

    private fun sendToServer(lat: Double, lng: Double, speed: Float) {
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("courier_id", "11111111-1111-1111-1111-111111111111")
        json.put("lat", lat)
        json.put("lng", lng)
        json.put("speed", speed)
        json.put("status", if (speed < 5) "STOP" else "MOVE")

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(SUPABASE_URL)
            .post(body)
            .addHeader("apikey", API_KEY)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).execute()
    }
}
