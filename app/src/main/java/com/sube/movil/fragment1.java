package com.sube.movil;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by Marcelo on 10/04/2015.
 */
public class fragment1 extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    InputStream is;
    TextView ubicacionActual, ubicacionCercana;
    private GoogleMap map;
    SupportMapFragment mapFragment;
    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    String consulta;
    JSONArray jArray;
    Location loc1 = new Location("");
    ListInterface listInterface;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment1, container, false);

        ubicacionActual =(TextView) rootView.findViewById(R.id.ubicacionactual);
        ubicacionCercana=(TextView) rootView.findViewById(R.id.ubicacioncercana);
        mapFragment = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        return rootView;
    }



    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, boolean flag) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
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
                    Log.i("ubicacion cercana","cercana");
                    ubicacionCercana.setText(strAdd);
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
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude(), true);
            map.addMarker(new MarkerOptions().position(loc));
            loc1.setLatitude(mLastLocation.getLatitude());
            loc1.setLongitude(mLastLocation.getLongitude());
            new Markers().execute();
            if(map != null){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                map.setOnMyLocationChangeListener(null);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        getCompleteAddressString(location.getLatitude(), location.getLongitude(), true);
        map.addMarker(new MarkerOptions().position(loc));
        loc1.setLatitude(location.getLatitude());
        loc1.setLongitude(location.getLongitude());
        new Markers().execute();
        if(map != null){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            map.setOnMyLocationChangeListener(null);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        ubicacionActual.setText("Sin ubicación");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(getActivity(),"buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
                    Location location= new Location("");
                    String local="";
                    Float posicioncercana= 999999.589F;
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        map.addMarker((new MarkerOptions().position(
                                new LatLng(json_data.getDouble("latitud"), json_data.getDouble("longitud"))).title(json_data.getString("direccion")).snippet(json_data.getString("horario"))).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.puntosube)));
                                location.setLongitude(json_data.getDouble("longitud"));
                                location.setLatitude(json_data.getDouble("latitud"));

                                if(Float.compare(loc1.distanceTo(location),posicioncercana)<0){
                                    posicioncercana=loc1.distanceTo(location);
                                    local=(json_data.getString("direccion"))+"\n"+(json_data.getString("horario"));
                                 }
                    }
                    ubicacionCercana.setText(local);
                    listInterface.onCreateList(jArray);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listInterface = (ListInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement interface");


        }
    }
    }