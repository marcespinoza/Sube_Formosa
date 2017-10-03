package com.sube.movil;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcelo on 10/04/2015.
 */
public class fragment1 extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    InputStream is;
    TextView ubicacionActual, punto1, punto2, punto3;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;
    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    String consulta;
    JSONArray jArray;
    Location loc1 = new Location("");
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    LatLng loc;
    LocationRequest mLocationRequest;
    Snackbar snackbar;
    CircularProgressView progressView;
    View view1, view2;
    SharedPreferences prefs;
    String shared_provincia, shared_ciudad;
    View coordinatorLayoutView;
    View mapView;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment1, null, false);
        coordinatorLayoutView = v.findViewById(R.id.snackbarPosition);
        progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        view1 = v.findViewById(R.id.view1);
        view2 = v.findViewById(R.id.view3);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        ubicacionActual =(TextView) v.findViewById(R.id.ubicacionactual);
        punto1=(TextView) v.findViewById(R.id.punto1);
        punto2=(TextView) v.findViewById(R.id.punto2);
        punto3=(TextView) v.findViewById(R.id.punto3);
        punto1.setVisibility(View.GONE);
        punto2.setVisibility(View.GONE);
        punto3.setVisibility(View.GONE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        prefs = getActivity().getSharedPreferences("ubicacion", MODE_PRIVATE);
        shared_provincia = prefs.getString("provincia", null);
        shared_ciudad = prefs.getString("ciudad", null);
        if (shared_provincia == null) {
            new MaterialDialog.Builder(getContext())
                    .title("Selecciona tu provincia")
                    .items(R.array.provincia)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            editor = prefs.edit();
                            switch (which){
                                case 0:editor.putString("provincia", "Buenos Aires"); cargarCiudades(R.array.buenos_aires);break;
                                case 1:editor.putString("provincia", "Capital Federal");cargarCiudades(R.array.capital_federal);break;
                                case 2:editor.putString("provincia", "Catamarca");editor.putString("ciudad", ""); reiniciarApp();
                                case 3:editor.putString("provincia", "Chaco");editor.putString("ciudad", ""); reiniciarApp();
                                case 4:editor.putString("provincia", "Corrientes");editor.putString("ciudad", ""); reiniciarApp();
                                case 5:editor.putString("provincia", "Entre Rios");editor.putString("ciudad", "");reiniciarApp();
                                case 6:editor.putString("provincia", "Formosa"); editor.putString("ciudad", "");reiniciarApp();
                                case 7:editor.putString("provincia", "Jujuy"); editor.putString("ciudad", "");reiniciarApp();
                                case 8:editor.putString("provincia", "San Luis"); reiniciarApp();
                                case 9:editor.putString("provincia", "Santa Fe"); cargarCiudades(R.array.santa_fe); break;
                            }
                            editor.commit();
                            return true;
                        }
                    })
                    .positiveText("Seleccionar")
                    .show();
        }else
        {
            showMap();
        }
        return v;
    }

    private void reiniciarApp(){
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        System.exit(0);
    }

    private void cargarCiudades(int provincia){
        new MaterialDialog.Builder(getContext())
                .title("Ciudad/Localidad")
                .items(provincia)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        editor = prefs.edit();
                        editor.putString("ciudad", String.valueOf(text));
                        editor.commit();
                        reiniciarApp();
                        return true;
                    }
                })
                .positiveText("Seleccionar")
                .show();
    }

   public void showMap(){
       if(NetworkUtils.isConnected(getContext())){
           mapFragment.getMapAsync(this);
           mapView = mapFragment.getView();
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
           {
               checkLocationPermission();
           }
            }else{
                snackbar=Snackbar.make(coordinatorLayoutView, "Revisa tu conexión a internet", Snackbar.LENGTH_LONG);
                 View snackBarView = snackbar.getView();
                 final int version = Build.VERSION.SDK_INT;
           if (version >= 23) {
               snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.material_red_600));
           } else {
               snackBarView.setBackgroundColor(getResources().getColor(R.color.material_red_600));
           }
           snackbar.show();
       }
   }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Comprueba si el usuario negó el permiso anteriormente y si tildó la opcion "no preguntar de nuevo". False en caso afirmativo, true caso negativo.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else
            {
                // No explanation needed, we can request the permission.
                requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }

        return true;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, boolean flag) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                strAdd =address.getThoroughfare()+" "+address.getSubThoroughfare();
                if (flag){
                    ubicacionActual.setText(strAdd);}
                else{
                    punto1.setText(strAdd);
                }
                Log.w("My Current address", "" + sb.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }



    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, 10);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(40, 10, 30, 30);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        loc = new LatLng(location.getLatitude(),location.getLongitude());
        getCompleteAddressString(location.getLatitude(), location.getLongitude(), true);
        loc1.setLatitude(location.getLatitude());
        loc1.setLongitude(location.getLongitude());
        //new Markers().execute();
        progressView.startAnimation();
        obtenerMarkers();
        mGoogleMap.addMarker(new MarkerOptions().position(loc));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


        protected void onPost(Boolean result){
            ArrayList<Marker> markers = new ArrayList<Marker>();

            if (!result){
                new MaterialDialog.Builder(getActivity())
                        .iconRes(R.drawable.ic_drawer)
                        .content("No se pudo obtener puntos de venta. Revisa tu conexión")
                        .positiveText("Listo")
                        .show();
            }else{
                try {
                    //jArray = new JSONArray(consulta);
                    String local = "Sin ubicación";
                    Float posicioncercana = 999999.589F;
                    for (int i = 0; i < jArray.length(); i++) {
                        Marker marker = new Marker();
                        Location location = new Location("Sin ubicación");
                        JSONObject json_data = jArray.getJSONObject(i);
                        if (json_data.getInt("puntoObtencion") == 1) {
                            mGoogleMap.addMarker((new MarkerOptions().position(
                                    new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ptocompra)));
                        } else if (json_data.getInt("tas") == 1) {
                            mGoogleMap.addMarker((new MarkerOptions().position(
                                    new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ptotas)));
                        } else {
                            mGoogleMap.addMarker((new MarkerOptions().position(
                                    new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ptocarga)));
                        }
                        location.setLongitude(json_data.getDouble("longitud"));
                        location.setLatitude(json_data.getDouble("latitud"));
                        marker.setDireccion(json_data.getString("direccion"));
                        marker.setHorario(json_data.getString("horario"));
                        marker.setUbicacion(location);
                        markers.add(marker);
                        if (Float.compare(loc1.distanceTo(location), posicioncercana) < 0) {
                            posicioncercana = loc1.distanceTo(location);
                        }
                    }

                    Collections.sort(markers, new Comparator<Marker>() {
                        @Override
                        public int compare(Marker marker1, Marker marker2) {
                            return Float.compare(loc1.distanceTo(marker1.getUbicacion()), loc1.distanceTo(marker2.getUbicacion()));
                            //return Float.compare(loc1.distanceTo(marker1.getUbicacion()),loc1.distanceTo(marker2.getUbicacion()));
                        }
                    });
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    punto1.setVisibility(View.VISIBLE);
                    punto2.setVisibility(View.VISIBLE);
                    punto3.setVisibility(View.VISIBLE);
                    if (markers.size() >= 1){
                        punto1.setText(markers.get(0).getDireccion() + " " + markers.get(0).getHorario());
                     }
                    if (markers.size() >=2){
                        punto2.setText(markers.get(1).getDireccion() + " " + markers.get(1).getHorario());
                    }
                    if (markers.size() >= 3){
                        punto3.setText(markers.get(2).getDireccion() + " " + markers.get(2).getHorario());
                    }

                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }
            }
        }


    public void obtenerMarkers(){
        String url = null;
        switch (shared_provincia){
            case "Buenos Aires": url = "http://subemovil.000webhostapp.com/private/buenos_aires.php"; break;
            case "Capital Federal": url = "http://subemovil.000webhostapp.com/private/capital_federal.php"; break;
            case "Catamarca": url = "http://subemovil.000webhostapp.com/private/catamarca.php"; break;
            case "Chaco": url = "http://subemovil.000webhostapp.com/private/chaco.php"; break;
            case "Corrientes": url = "http://subemovil.000webhostapp.com/private/corrientes.php"; break;
            case "Entre rios": url = "http://subemovil.000webhostapp.com/private/entre_rios.php"; break;
            case "Formosa": url = "http://subemovil.000webhostapp.com/private/formosa.php"; break;
            case "Jujuy": url = "http://subemovil.000webhostapp.com/private/jujuy.php"; break;
            case "San Luis": url = "http://subemovil.000webhostapp.com/private/san_luis.php"; break;
            case "Santa Fe": url = "http://subemovil.000webhostapp.com/private/santa_fe.php"; break;
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respons) {
                try {
                    jArray = new JSONArray(respons);
                    onPost(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ciudad",shared_ciudad);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
                    {

                        //   if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        // {
                        //      showGPSDisabledAlertToUser();
                        //  }
                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

}