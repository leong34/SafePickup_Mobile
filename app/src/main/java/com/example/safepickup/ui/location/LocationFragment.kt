package com.example.safepickup.ui.location


import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchOrganizationAddressRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import com.mapbox.navigation.ui.route.NavigationMapRoute
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class LocationFragment : Fragment(), OnMapReadyCallback{
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var requestQueue: RequestQueue? = null
    private var destLat: Double = 0.0
    private var destLng: Double = 0.0
    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation


    var organizationFullAddress: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        val root = inflater.inflate(R.layout.fragment_location, container, false)

        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)


        requestQueue = Volley.newRequestQueue(requireContext())

        getOrganizationGeocode(Utilities.getSafePref(requireContext(), "user_id"), Utilities.getSafePref(requireContext(), "credential"))
        return root
    }
    

    private fun getOrganizationGeocode(user_id: String, credential: String) {
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.7")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        val service = retrofit.create(API::class.java)
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Retrieving address. Please wait...", true)
        val call: Call<FetchOrganizationAddressRespond?>? = service.fetchOrganizationAddress(user_id, credential)

        call?.enqueue(object : Callback<FetchOrganizationAddressRespond?> {
            override fun onResponse(call: Call<FetchOrganizationAddressRespond?>, response: Response<FetchOrganizationAddressRespond?>) {
                progressDialog.dismiss()
                val organizationAddressRespond: FetchOrganizationAddressRespond? = response.body()
                organizationFullAddress = organizationAddressRespond?.fullAddress.toString()
                Log.d("Retrofit", organizationFullAddress)

                val geoUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + organizationFullAddress + "&key=" + getString(R.string.google_map_access_token)
                val geoRequest = JsonObjectRequest(Request.Method.GET, geoUrl, null, object : com.android.volley.Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        val data = response!!.getJSONArray("results")
                        val info = data.getJSONObject(0)
                        val geometry = info.getJSONObject("geometry")
                        val location = geometry.getJSONObject("location")
                        destLat = location.getDouble("lat")
                        destLng = location.getDouble("lng")

                        val options = MarkerOptions()
                        options.position(LatLng(destLat, destLng))
                        mapboxMap?.addMarker(options)
                        val position: CameraPosition = CameraPosition.Builder()
                                .target(LatLng(destLat, destLng))
                                .zoom(13.0) //13 14 15
                                .build()
                        mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))

                        Log.i("Volley", mapboxMap.toString())
                        Log.i("Volley", "$destLat - $destLng")
                    }
                }, object : com.android.volley.Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        error.printStackTrace()
                        Log.i("Volley", error.printStackTrace().toString())
                    }
                })
                requestQueue?.add(geoRequest)
                Log.i("Volley", requestQueue.toString())
            }

            override fun onFailure(call: Call<FetchOrganizationAddressRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(requireActivity(), "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun getRoute(origin: Point, destination: Point){
        TODO()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS)
    }


}