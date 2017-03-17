package com.sube.movil;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment1, null, false);
        final View coordinatorLayoutView = v.findViewById(R.id.snackbarPosition);
        ubicacionActual =(TextView) v.findViewById(R.id.ubicacionactual);
        punto1=(TextView) v.findViewById(R.id.punto1);
        punto2=(TextView) v.findViewById(R.id.punto2);
        punto3=(TextView) v.findViewById(R.id.punto3);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if(NetworkUtils.isConnected(getContext())){
            mapFragment.getMapAsync(this);
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
        return v;
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
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)+" ");
                }
                strAdd = strReturnedAddress.toString();
                if (flag){
                    ubicacionActual.setText(strAdd);}
                else{
                    punto1.setText(strAdd);
                }
                Log.w("My Current address", "" + strReturnedAddress.toString());
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
        }
        else
        {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
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
        new Markers().execute();
        mGoogleMap.addMarker(new MarkerOptions().position(loc));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class Markers extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = obtenerMarkers();
            return result;
        }

        protected void onPostExecute(Boolean result){
            ArrayList<Marker> markers = new ArrayList<Marker>();
            super.onPostExecute(result);
            if (!result){
                new MaterialDialog.Builder(getActivity())
                        .iconRes(R.drawable.ic_drawer)
                        .content("No se pudo obtener puntos de venta. Revisa tu conexión")
                        .positiveText("Listo")
                        .show();
            }else{
                try {
                    jArray = new JSONArray(consulta);
                    String local="Sin ubicación";
                    Float posicioncercana= 999999.589F;
                    for (int i = 0; i < jArray.length(); i++) {
                        Marker marker = new Marker();
                        Location location= new Location("Sin ubicación");
                        JSONObject json_data = jArray.getJSONObject(i);
                        if(json_data.getInt("puntoObtencion")==1){
                        mGoogleMap.addMarker((new MarkerOptions().position(
                                new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ptocompra)));}
                        else{
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
                        if(Float.compare(loc1.distanceTo(location),posicioncercana)<0){
                            posicioncercana=loc1.distanceTo(location);
                        }
                    }

                    Collections.sort(markers, new Comparator<Marker>() {
                        @Override
                        public int compare(Marker marker1, Marker marker2) {
                            Log.i("comparacion",""+ marker1.getDireccion()+" "+ marker2.getDireccion());
                            return Float.compare(loc1.distanceTo(marker1.getUbicacion()), loc1.distanceTo(marker2.getUbicacion()));
                            //return Float.compare(loc1.distanceTo(marker1.getUbicacion()),loc1.distanceTo(marker2.getUbicacion()));
                        }
                    });

                    punto1.setText(markers.get(1).getDireccion()+" "+markers.get(1).getHorario());
                    punto2.setText(markers.get(2).getDireccion()+" "+markers.get(2).getHorario());
                    punto3.setText(markers.get(3).getDireccion()+" "+markers.get(3).getHorario());
                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }
            }
        }    }


    public Boolean obtenerMarkers(){
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://aquehorasale.webatu.com/private/subemovil.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
//convert response to string
        if(is!=null) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                is.close();
                consulta = total.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            return true;
        }else
            return false;

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