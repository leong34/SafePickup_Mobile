package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.safepickup.Interface.API
import com.example.safepickup.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.ui.NavigationViewOptions
import com.mapbox.navigation.ui.OnNavigationReadyCallback
import com.mapbox.navigation.ui.listeners.NavigationListener
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import kotlinx.android.synthetic.main.activity_navigation.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener {
    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_navigation)
        supportActionBar?.hide()

        val position = CameraPosition.Builder()
                .target(LatLng(intent.getDoubleExtra("oriLat", 0.0), intent.getDoubleExtra("oriLng", 0.0)))
                .zoom(15.0)
                .build()

        navigationView.onCreate(savedInstanceState)
        navigationView.initialize(this, position)
    }

    private fun getDirection(){
        val ori: Point = Point.fromLngLat(intent.getDoubleExtra("oriLng", 0.0), intent.getDoubleExtra("oriLat", 0.0))
        val dest: Point = Point.fromLngLat(intent.getDoubleExtra("destLng", 0.0), intent.getDoubleExtra("destLat", 0.0))

        val client = MapboxDirections.builder()
                .origin(ori)
                .destination(dest)
                .steps(true)
                .bannerInstructions(true)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .accessToken(getString(R.string.mapbox_access_token))
                .voiceInstructions(true)
                .build()

        client?.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.body() == null) {
                    Log.i("Direction","No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.i("Direction","No routes found")
                    return
                }
                val response = response

                val currentRoute = response.body()!!.routes()[0]
//
                val optionsBuilder = NavigationViewOptions.builder(this@NavigationActivity)
                optionsBuilder.navigationListener(this@NavigationActivity)
                optionsBuilder.directionsRoute(currentRoute)
                optionsBuilder.shouldSimulateRoute(true)

                navigationView.startNavigation(optionsBuilder.build())

            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.i("Direction","Error: " + t.message)
            }
        })
    }

    override fun onNavigationReady(isRunning: Boolean) {
        if (!isRunning && !::navigationMapboxMap.isInitialized) {
            if (navigationView.retrieveNavigationMapboxMap() != null) {
                this.navigationMapboxMap = navigationView.retrieveNavigationMapboxMap()!!
                navigationView.retrieveMapboxNavigation()?.let { this.mapboxNavigation = it }
                getDirection()
            }
        }
    }

    override fun onCancelNavigation() {
        navigationView.stopNavigation()
        finish()
    }

    override fun onNavigationFinished() {
        finish()
    }

    override fun onNavigationRunning() {
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView.onResume()
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onPause() {
        super.onPause()
        navigationView.onPause()
    }

    override fun onDestroy() {
        navigationView.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!navigationView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView.onRestoreInstanceState(savedInstanceState)
    }
}