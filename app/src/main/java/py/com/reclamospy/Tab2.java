package py.com.reclamospy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import java.io.IOException;
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
    private static String url = "http://civpy.com/SelectAll_v2.php";

    // JSON Node names
    private static final String TAG_LATLONGS = "reclamo";

    private static final String TAG_LAT = "latitud";
    private static final String TAG_LONG = "longitud";
    private static final String TAG_CATEGORIA= "RP_Group";
    private static final String TAG_SUBCATEGORIA = "RP_Category";
    private static final String TAG_FECHA = "fecha";
    JSONArray latlngs = null;
    ArrayList<Reclamo> latLngList;
    private boolean isFirsChangeListen;


    MapView mMapView;
    private GoogleMap googleMap;
    Toolbar toolbar;
    private ProgressDialog pd = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.tab_2, container,
                false);
        toolbar = (Toolbar) v.findViewById(R.id.tool_bar);
        boolean isFirsChangeListen = true;
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

        if (checkNetwork()) {
            checkForLocationService();
        }else {
            Toast.makeText(getActivity().getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }


        return v;
    }
    /*
    *  Check network availability and connected
    */

    public void checkForLocationService(){
        LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Servicios de ubicacion no esta activado");
            builder.setMessage("Por favor habilita el sevicio de ubicacion y el GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

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
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden){
            new GetContacts().execute();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && latLngList.size() == 0) {
            new GetContacts().execute();
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
     * Calculate elapsed time.. Hace 1 hora, 30 minutos, 5 dias
     * @param item
     * @return
     */
    public String calculateElapsedTime(Date item){
        Date datePosted = item;
        Date current = new Date();
        String returnStr;
        String dayElapsed = " días",hourElapsed = " horas",minuteElapsed = " minutos";
        long diffHours = (current.getTime() - datePosted.getTime())/(60*60*1000);
        long diff = (current.getTime() - datePosted.getTime())/(24*60*60*1000);
        long diffMinutes = (current.getTime() - datePosted.getTime())/(60*1000);
        if (diff == 1 )
            dayElapsed = " día";
        if (diffHours == 1)
            hourElapsed = " hora";
        if (diffMinutes == 1){
            minuteElapsed = " minuto";
        }
        if (diff != 0){
            returnStr = "Hace "+diff+dayElapsed;
        }else if (diffHours != 0){
            returnStr = "Hace "+diffHours+ hourElapsed;
        }else{
            returnStr = "Hace "+diffMinutes+ minuteElapsed;
        }
        return returnStr;
    }
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= ProgressDialog.show(getActivity(), "Por favor espere !","Obteniendo datos...", true);
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
                        reclamo.setSubcategoria(c.getString(TAG_SUBCATEGORIA));
                        String fecha = c.getString(TAG_FECHA);
                        DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = newDateFormat.parse(fecha);

                        reclamo.setFecha(d);
                        // adding contact to contact list
                        latLngList.add(reclamo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
                    MarkerOptions marker = null;
                    MarkerOptions icon = null;
                    if (reclamo.getCategoria().equals("AGUA")) {
                        marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("AGUA")
                        .snippet(reclamo.getSubcategoria() +" - " + calculateElapsedTime(reclamo.getFecha()));
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        googleMap.addMarker(marker);
                    } else if (reclamo.getCategoria().equals("ENERGIA")) {
                        marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("ENERGIA - "+ calculateElapsedTime(reclamo.getFecha()))
                        .snippet(reclamo.getSubcategoria());
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        googleMap.addMarker(marker);
                    } else if (reclamo.getCategoria().equals("VIA PUBLICA")) {
                        marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title("VIA PUBLICA - "+calculateElapsedTime(reclamo.getFecha()))
                                .snippet(reclamo.getSubcategoria());

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
            pd.dismiss();
        }

    }
}