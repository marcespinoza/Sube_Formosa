package com.sube.movil.Views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.github.rahatarmanahmed.cpv.CircularProgressView
import khangtran.preferenceshelper.PreferencesHelper
import com.afollestad.materialdialogs.MaterialDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.list.listItems
import kotlin.jvm.Synchronized
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import com.android.volley.VolleyError
import com.brouding.simpledialog.SimpleDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.sube.movil.Marker
import com.sube.movil.NetworkUtils
import com.sube.movil.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

/**
 * Created by Marcelo on 10/04/2015.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mAdView: AdView
    private var ubicacionActual: TextView? = null
    private lateinit var punto1: TextView
    private lateinit var punto2: TextView
    private lateinit var punto3: TextView
    var mGoogleMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var jArray: JSONArray? = null
    var loc1 = Location("")
    var locationManager: LocationManager? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var loc: LatLng? = null
    var mLocationRequest: LocationRequest? = null
    var snackbar: Snackbar? = null
    var progressView: CircularProgressView? = null
    lateinit var view1: View
    lateinit var view2: View
    lateinit var prefs: SharedPreferences
    var shared_provincia: String? = null
    var shared_ciudad: String? = null
    var coordinatorLayoutView: View? = null
    var mapView: View? = null
    lateinit var editor: SharedPreferences.Editor
    var _context: Context? = null
    private lateinit var info: PackageInfo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment1, container, false)
        PreferencesHelper.initHelper(activity)
        aviso()
        mAdView = v.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        coordinatorLayoutView = v.findViewById(R.id.snackbarPosition)
        progressView = v.findViewById(R.id.progress_view)
        view1 = v.findViewById(R.id.view1)
        view2 = v.findViewById(R.id.view3)
        view1.visibility = View.GONE
        view2.visibility = View.GONE
        ubicacionActual = v.findViewById(R.id.ubicacionactual)
        punto1 = v.findViewById(R.id.punto1)
        punto2 = v.findViewById(R.id.punto2)
        punto3 = v.findViewById(R.id.punto3)
        punto1.visibility = View.GONE
        punto2.visibility = View.GONE
        punto3.visibility = View.GONE
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment?
        prefs = requireActivity().getSharedPreferences("ubicacion", Context.MODE_PRIVATE)
        shared_provincia = prefs.getString("provincia", null)
        shared_ciudad = prefs.getString("ciudad", null)
        try {
            info =
                requireContext().packageManager.getPackageInfoCompat("com.sube.movil", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (info.versionName < "15" || shared_provincia == null) {
            MaterialDialog(requireContext()).show {
                title(text = "Selecciona tu provincia")
                positiveButton (text = "Seleccionar")
                listItems(R.array.provincia){ _, index, provincia ->
                    editor = prefs.edit()
                    when (provincia) {
                        "Buenos Aires" -> {
                            editor.putString("provincia", "BA")
                            cargarCiudades(R.array.buenos_aires)
                        }
                        "Capital Federal" -> {
                            editor.putString("provincia", "CF")
                            cargarCiudades(R.array.capital_federal)
                        }
                        "Corrientes" -> {
                            editor.putString("provincia", "CT")
                            editor.putString("ciudad", "")
                        }
                        "Chaco" -> {
                            editor.putString("provincia", "CH")
                            editor.putString("ciudad", "")
                        }
                        "Chubut" -> {
                            editor.putString("provincia", "Chubut")
                            editor.putString("ciudad", "")
                        }
                        "Corrientes" -> {
                            editor.putString("provincia", "CR")
                            editor.putString("ciudad", "")
                        }
                        "Entre Rios" -> {
                            editor.putString("provincia", "ER")
                            editor.putString("ciudad", "")
                        }
                        "Formosa" -> {
                            editor.putString("provincia", "FO")
                            editor.putString("ciudad", "")
                        }
                        "Jujuy" -> {
                            editor.putString("provincia", "JU")
                            editor.putString("ciudad", "")
                        }
                        "Mendoza" -> {
                            editor.putString("provincia", "MZ")
                            editor.putString("ciudad", "")
                        }
                        "Neuquen" -> {
                            editor.putString("provincia", "NQ")
                            editor.putString("ciudad", "")
                        }
                        "Rio Negro" -> {
                            editor.putString("provincia", "RN")
                            editor.putString("ciudad", "")
                        }
                        "San Juan" -> {
                            editor.putString("provincia", "SJ")
                            editor.putString("ciudad", "")
                        }
                        "San Luis" -> {
                            editor.putString("provincia", "SL")
                            editor.putString("ciudad", "")
                        }
                        "Santa Fe" -> {
                            editor.putString("provincia", "SF")
                            cargarCiudades(R.array.santa_fe)
                        }
                        "Tierra del Fuego" -> {
                            editor.putString("provincia", "Tierra del Fuego")
                            editor.putString("ciudad", "")
                        }
                    }
                    editor.commit()
                    reiniciarApp()
                    }
            }
        } else {
            showMap()
        }
        return v
    }

    private fun aviso() {
        val intValue = PreferencesHelper.getInstance().getIntValue("aviso", 0)
        if (intValue == 0) {
            SimpleDialog.Builder(requireActivity())
                .setTitle("Importante")
                .setContent(
                    "Sube Móvil es una aplicación independiente del organismo oficial " +
                            "que maneja la sube, por lo tanto la demora en la actualización del saldo no depende de la misma. " +
                            "Si desea enviar alguna queja o reclamo debe hacerlo a facebook/tarjetasube o al 0800-777-7823.",
                    3
                )
                .setBtnConfirmText("Entiendo")
                .onConfirm { _, _ -> PreferencesHelper.getInstance().setValue("aviso", 1) }
                .setBtnCancelTextColor("#555555")
                .show()
        }
    }

    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
        }


    private fun reiniciarApp() {
        val i = requireContext().packageManager
            .getLaunchIntentForPackage(requireContext().packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        System.exit(0)
    }

    private fun cargarCiudades(provincia: Int) {
        MaterialDialog(requireContext()).show {
            title(text = "Ciudad/Localidad")
            listItems(provincia) { dialog, index, text ->
                    editor = prefs!!.edit()
                    editor.putString("ciudad", text.toString())
                    editor.commit()
                    reiniciarApp()
                }
            positiveButton (text = "Seleccionar")

        }
    }

    fun showMap() {
        if (NetworkUtils.isConnected(requireContext())) {
            mapFragment!!.getMapAsync(this)
            mapView = mapFragment!!.view
            checkLocationPermission()
        } else {
            snackbar = Snackbar.make(
                coordinatorLayoutView!!,
                "Revisa tu conexión a internet",
                Snackbar.LENGTH_LONG
            )
            val snackBarView = snackbar!!.view
            val version = Build.VERSION.SDK_INT
            if (version >= 23) {
                snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.material_red_600
                    )
                )
            } else {
                snackBarView.setBackgroundColor(resources.getColor(R.color.material_red_600))
            }
            snackbar!!.show()
        }
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //Comprueba si el usuario negó el permiso anteriormente y si tildó la opcion "no preguntar de nuevo". False en caso afirmativo, true caso negativo.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double, flag: Boolean) {
        var strAdd = ""
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null && addresses.size > 0) {
                val address = addresses[0]
                val sb = StringBuilder()
                strAdd = address.thoroughfare + " " + address.subThoroughfare
                if (flag) {
                    ubicacionActual!!.text = strAdd
                } else {
                    punto1!!.text = strAdd
                }
                Log.w("My Current address", "" + sb.toString())
            } else {
                Log.w("My Current address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current address", "Canont get Address!")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            buildGoogleApiClient()
            mGoogleMap!!.isMyLocationEnabled = true
        }
        if (mapView != null && mapView!!.findViewById<View?>("1".toInt()) != null) {
            // Get the button view
            val locationButton =
                (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, 10)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(40, 10, 30, 30)
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 30000
        mLocationRequest!!.priority =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient!!,
                mLocationRequest!!,
                this
            )
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onLocationChanged(location: Location) {
        if (isAdded && context != null) {
            if (location != null) {
                loc = LatLng(location.latitude, location.longitude)
                getCompleteAddressString(location.latitude, location.longitude, true)
                loc1.latitude = location.latitude
                loc1.longitude = location.longitude
                //new Markers().execute();
                progressView!!.startAnimation()
                obtenerMarkers(_context)
                mGoogleMap!!.addMarker(MarkerOptions().position(loc!!))
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(loc!!, 15f))
            } else {
                Toast.makeText(
                    activity,
                    "Sin ubicación", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    protected fun onPost(result: Boolean?) {
        val markers = ArrayList<Marker>()
        if (!result!!) {
            MaterialDialog(requireContext()).show {
                icon(R.drawable.ic_drawer)
                message (text = "No se pudo obtener puntos de venta. Revisa tu conexión")
                positiveButton (text = "Listo")
            }
        } else {
            try {
                var posicioncercana = 999999.589f
                for (i in 0 until jArray!!.length()) {
                    val marker = Marker()
                    val location = Location("Sin ubicación")
                    val json_data = jArray!!.getJSONObject(i)
                    /*if (json_data.getInt("Type") == 1) {
                        mGoogleMap.addMarker((new MarkerOptions().position(
                                new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ptocompra)));
                    } else if (json_data.getInt("tas") == 1) {
                        mGoogleMap.addMarker((new MarkerOptions().position(
                                new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ptotas)));
                    } else {*/mGoogleMap!!.addMarker(
                        MarkerOptions().position(
                            LatLng(json_data.getDouble("lat"), json_data.getDouble("lgn"))
                        ).title(json_data.getString("Location"))
                            .snippet(json_data.getString("time"))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ptocarga))
                    )
                    location.longitude = json_data.getDouble("lgn")
                    location.latitude = json_data.getDouble("lat")
                    marker.setDireccion(json_data.getString("Location"))
                    marker.setHorario(json_data.getString("time"))
                    marker.setUbicacion(location)
                    markers.add(marker)
                    if (java.lang.Float.compare(loc1.distanceTo(location), posicioncercana) < 0) {
                        posicioncercana = loc1.distanceTo(location)
                    }
                }
                Collections.sort(markers) { marker1, marker2 ->
                    java.lang.Float.compare(
                        loc1.distanceTo(
                            marker1.getUbicacion()
                        ), loc1.distanceTo(marker2.getUbicacion())
                    )
                }
                progressView!!.stopAnimation()
                progressView!!.visibility = View.GONE
                if (markers.size >= 1) {
                    punto1!!.visibility = View.VISIBLE
                    punto1!!.text = markers[0].getDireccion() + " " + markers[0].getHorario()
                }
                if (markers.size >= 2) {
                    view1!!.visibility = View.VISIBLE
                    punto2!!.visibility = View.VISIBLE
                    punto2!!.text = markers[1].getDireccion() + " " + markers[1].getHorario()
                }
                if (markers.size >= 3) {
                    view2!!.visibility = View.VISIBLE
                    punto3!!.visibility = View.VISIBLE
                    punto3!!.text = markers[2].getDireccion() + " " + markers[2].getHorario()
                }
            } catch (e: JSONException) {
                Log.e("log_tag", "Error parsing data $e")
            }
        }
    }

    fun obtenerMarkers(context: Context?) {
        val url = "https://api-subemovil.onrender.com/api/recargas/"
        val jsonBody = JSONObject()
        try {
            jsonBody.put("provincia", shared_provincia)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val queue = Volley.newRequestQueue(context)
        val sr: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { respons: String? ->
                try {
                    if (respons != null) {
                        jArray = JSONArray(respons)
                        onPost(true)
                    } else {
                        onPost(false)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                if (error.message == null && getContext() != null) {
                    Toast.makeText(
                        getContext(),
                        "Falló la conexión, intente de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("error", error.message!!)
                }
            }) {
                override fun getParams(): Map<String, String>? {
                    val MyData: MutableMap<String, String> = HashMap()
                    MyData["provincia"] = shared_provincia!!
                    return MyData
                }
            }
        queue.add(sr)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) { // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient()
                    }
                    mGoogleMap!!.isMyLocationEnabled = true
                }
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(activity, "Debe otorgar permisos", Toast.LENGTH_LONG).show()
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this._context = context
    }

    override fun onDetach() {
        super.onDetach()
        this._context = null
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}