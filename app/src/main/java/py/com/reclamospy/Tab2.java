package py.com.reclamospy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import model.Reclamo;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab2 extends Fragment {
    private static String url = "http://yoreporto.org/SelectAll_v2.php";

    // JSON Node names
    private static final String TAG_LATLONGS = "reclamo";

    private static final String TAG_LAT = "latitud";
    private static final String TAG_LONG = "longitud";
    private static final String TAG_CATEGORIA= "RP_Category";
    // contacts JSONArray
    JSONArray latlngs = null;
    // Hashmap for ListView
    ArrayList<Reclamo> latLngList;

    MapView mMapView;
    private GoogleMap googleMap;
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.tab_2, container,
                false);
        toolbar = (Toolbar) v.findViewById(R.id.tool_bar);

        latLngList = new ArrayList<Reclamo>();

        mMapView = (MapView) v.findViewById(R.id.map2);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // latitude and longitude

        // Perform any camera updates here
        //setSupportActionBar(toolbar);
        if (checkNetwork()) {
                new GetContacts().execute();
        }else{
            Toast.makeText(getActivity().getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
        return v;
    }
    /*
    *  Check network availability and connected
    */
    public boolean checkNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node

                    latlngs= jsonObj.getJSONArray(TAG_LATLONGS);

                    // looping through All Contacts
                    for (int i = 0; i < latlngs.length(); i++) {
                        Reclamo reclamo = new Reclamo();
                        JSONObject c = latlngs.getJSONObject(i);
                        reclamo.setCategoria(c.getString(TAG_CATEGORIA));
                        reclamo.setLat(c.getString(TAG_LAT));
                        reclamo.setLng(c.getString(TAG_LONG));
                        // adding contact to contact list
                        latLngList.add(reclamo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            double latDefault = -25.516666700000000000;
            double longDefault = -54.616666699999996000;

            // create marker
            if (latLngList != null) {
                for (Reclamo reclamo : latLngList) {
                    double latitude = Double.parseDouble(reclamo.getLat());
                    double longitude = Double.parseDouble(reclamo.getLng());
                    if (reclamo.getCategoria().equals("AGUA")) {
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("Agua");
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        googleMap.addMarker(marker);
                    } else if (reclamo.getCategoria().equals("ENERGIA")) {
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("Energia");
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        googleMap.addMarker(marker);
                    } else if (reclamo.getCategoria().equals("VIA PUBLICA")) {
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("Via publica");
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        googleMap.addMarker(marker);
                    }
                }
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latDefault, longDefault)).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }
}