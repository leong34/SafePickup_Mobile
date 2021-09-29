package com.example.safepickup.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchOrganizationAddressRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.gson.GsonBuilder
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.mapbox.navigation.base.internal.extensions.applyDefaultParams
import com.mapbox.navigation.base.internal.extensions.coordinates
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.ui.NavigationViewOptions
import kotlinx.android.synthetic.main.activity_navigation.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class LocationFragment : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var requestQueue: RequestQueue? = null

    private var mapboxNavigation: MapboxNavigation? = null
    private var locationComponent: LocationComponent? = null
    private var origin: Point? = null
    private var destination: Point? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationOptions: NavigationViewOptions? = null

    private var destLat: Double = 0.0
    private var destLng: Double = 0.0
    private var oriLat: Double = 0.0
    private var oriLng: Double = 0.0

    var organizationFullAddress: String = ""

    private var client: FusedLocationProviderClient? = null
    private var callback: LocationCallback? = null

    private var btn: Button? = null

    private val ORIGIN_COLOR = "#32a852" // Green
    private val DESTINATION_COLOR = "#F84D4D" // Red


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        val root = inflater.inflate(R.layout.fragment_location, container, false)
        val mapboxNavigationOptions = MapboxNavigation
                .defaultNavigationOptionsBuilder(requireContext(), getString(R.string.mapbox_access_token))
                .build()
        mapboxNavigation = MapboxNavigation(mapboxNavigationOptions)

        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        requestQueue = Volley.newRequestQueue(requireContext())

        btn = root.findViewById(R.id.button)
        btn?.setOnClickListener {
            oriLng = 100.4748699
            oriLat = 5.2751108
            destLng = 100.2860238
            destLat = 5.338936299999999

            val intent = Utilities.intent_navigation(this.requireContext())
            intent.putExtra("oriLat", oriLat)
            intent.putExtra("oriLng", oriLng)
            intent.putExtra("destLat", destLat)
            intent.putExtra("destLng", destLng)
            startActivity(intent)
        }

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

                        val ori = LatLng(mapboxMap?.locationComponent?.lastKnownLocation?.latitude!!, mapboxMap?.locationComponent?.lastKnownLocation?.longitude!!)
                        val end = LatLng(destLat, destLng)

                        oriLat = mapboxMap?.locationComponent?.lastKnownLocation?.latitude!!
                        oriLng = mapboxMap?.locationComponent?.lastKnownLocation?.longitude!!

                        val options = MarkerOptions()
                        options.position(LatLng(destLat, destLng))
                        mapboxMap?.addMarker(options)
                        val latLngBounds = LatLngBounds.Builder()
                                .include(ori)
                                .include(end)
                                .build()
                        mapboxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000)

                        mapboxMap?.locationComponent?.lastKnownLocation?.let { originLocation ->
                            val ori: Point = Point.fromLngLat(originLocation.longitude, originLocation.latitude)
                            val dest: Point = Point.fromLngLat(destLng, destLat)

                            mapboxNavigation?.requestRoutes(
                                    RouteOptions.builder().applyDefaultParams()
                                            .accessToken(getString(R.string.mapbox_access_token))
                                            .coordinates(ori, null, dest)
                                            .alternatives(true)
                                            .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                                            .build(),
                                    routesReqCallback
                            )
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    Log.i("Volley", error.printStackTrace().toString())
                })
                requestQueue?.add(geoRequest)
                Log.i("Volley", requestQueue.toString())

                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<FetchOrganizationAddressRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(requireActivity(), "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val routesReqCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (routes.isNotEmpty()) {
                // Update a gradient route LineLayer's source with the Maps SDK. This will
                // visually add/update the line on the map. All of this is being done
                // directly with Maps SDK code and NOT the Navigation UI SDK.
                mapboxMap?.getStyle {
                    val clickPointSource = it.getSourceAs<GeoJsonSource>("ROUTE_LINE_SOURCE_ID")
                    val routeLineString = LineString.fromPolyline(
                            routes[0].geometry()!!,
                            6
                    )
                    clickPointSource?.setGeoJson(routeLineString)
                }
                Toast.makeText(requireContext(), "Route found", Toast.LENGTH_LONG).show()
//                Log.i("route call back ", "route request  %s$routes")
            } else {
                Toast.makeText(requireContext(), "No routes found", Toast.LENGTH_LONG).show()
            }
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            Log.i("route call back ", "route request failure %s$throwable")
            Toast.makeText(requireContext(), "Request failed", Toast.LENGTH_LONG).show()
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            Log.i("route call back ", "route request canceled")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                    requireActivity()?.onBackPressed()
                }
                return
            }
        }
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
        client?.removeLocationUpdates(callback);
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        client?.removeLocationUpdates(callback);
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
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            this.mapboxMap = mapboxMap

            enableLocationComponent()

            // Add the click and route sources
            it.addSource(GeoJsonSource("CLICK_SOURCE"))
            it.addSource(
                    GeoJsonSource(
                            "ROUTE_LINE_SOURCE_ID",
                            GeoJsonOptions().withLineMetrics(true)
                    )
            )

            // Add the destination marker image
            it.addImage(
                    "ICON_ID",
                    BitmapUtils.getBitmapFromDrawable(
                            ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.mapbox_marker_icon_default
                            )
                    )!!
            )

            // Add the LineLayer below the LocationComponent's bottom layer, which is the
            // circular accuracy layer. The LineLayer will display the directions route.
            it.addLayerBelow(
                    LineLayer("ROUTE_LAYER_ID", "ROUTE_LINE_SOURCE_ID")
                            .withProperties(
                                    lineCap(LINE_CAP_ROUND),
                                    lineJoin(LINE_JOIN_ROUND),
                                    lineWidth(6f),
                                    lineGradient(
                                            interpolate(
                                                    linear(),
                                                    lineProgress(),
                                                    stop(0f, color(parseColor(ORIGIN_COLOR))),
//                                                    stop(1f, color(parseColor(DESTINATION_COLOR)))
                                            )
                                    )
                            ),
                    "mapbox-location-shadow-layer"
            )

            // Add the SymbolLayer to show the destination marker
            it.addLayerAbove(
                    SymbolLayer("CLICK_LAYER", "CLICK_SOURCE")
                            .withProperties(
                                    iconImage("ICON_ID")
                            ),
                    "ROUTE_LAYER_ID"
            )
        }

//        getOrganizationGeocode(Utilities.getSafePref(requireContext(), "user_id"), Utilities.getSafePref(requireContext(), "credential"))
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            mapboxMap?.getStyle {
                mapboxMap?.locationComponent?.apply {
                    activateLocationComponent(
                            LocationComponentActivationOptions.builder(
                                    requireContext(),
                                    it
                            )
                                    .build()
                    )
                    isLocationComponentEnabled = true
                    cameraMode = CameraMode.TRACKING
                    renderMode = RenderMode.COMPASS
                }
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }
}