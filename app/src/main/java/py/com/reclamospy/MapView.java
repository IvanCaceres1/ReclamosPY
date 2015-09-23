package py.com.reclamospy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Reclamo;

/**
 * Created by ivan on 9/8/15.
 */
public class MapView extends ActionBarActivity implements GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMyLocationButtonClickListener{
    static final double DEFAULT_LATITUDE = -25.516666700000000000;
    static final double DEFAULT_LONGITUDE= -54.616666699999996000;
    private GoogleMap googleMap;
    private Toolbar toolbar;
    private BitmapDescriptor markerColor;
    private Reclamo reclamo;
    //Reverse geocoding result //
    private List<Address> addresses;
    Geocoder geocoder;
    //Set marker at currrent position and set location change listener to null
    boolean isFirsChangeListen;
    private FloatingActionButton cameraBtn,sendBtn,uploadBtn;
    ProgressDialog pd,pdLoadMap;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.map_view);
        isFirsChangeListen = true;

        cameraBtn = (FloatingActionButton)findViewById(R.id.add_camera_icon);
        cameraBtn.setOnClickListener(this);

        sendBtn = (FloatingActionButton)findViewById(R.id.add_send_icon);
        sendBtn.setOnClickListener(this);

        uploadBtn = (FloatingActionButton)findViewById(R.id.add_upload_icon);
        uploadBtn.setOnClickListener(this);

        //Obtain reclamo Object from SubCategory activity
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        reclamo.setLat(DEFAULT_LATITUDE + "");
        reclamo.setLng(DEFAULT_LONGITUDE + "");

        if (reclamo.getCategoria().equals("Agua")){
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue);
        }else if  (reclamo.getCategoria().equals("Energia")){
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_yellow);
        }else{
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_red);
        }
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);

        //Obtain address from lat,lng
        geocoder = new Geocoder(this, Locale.getDefault());
        if (checkNetwork()) {
            obtainAddressFromGPS();
            checkForLocationService();
        }else{
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        if (googleMap!= null) {
            googleMap.setOnMyLocationChangeListener(this);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Reportes PY");
        toolbar.setNavigationIcon(R.mipmap.ic_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void checkForLocationService(){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()){
            case R.id.action_normal:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.action_satellite :
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.action_hybrid:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.action_terrain :
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

        }

        return false;
    }



    @Override
    public void onMapClick(LatLng latLng) {
        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
        if (reclamo.getCategoria().equals("Agua")){
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue);
        }else if  (reclamo.getCategoria().equals("Energia")){
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_yellow);
        }else{
            markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_red);
        }
        googleMap.clear();
        reclamo.setLat(latLng.latitude + "");
        reclamo.setLng(latLng.longitude + "");
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addresses.size() > 0){
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String know = addresses.get(0).getFeatureName();
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude,latLng.longitude))
                        .draggable(true)
                        .title(reclamo.getCategoria()+" - "+reclamo.getSubcategoria())
                        .snippet(address+", "+city)
                        .icon(markerColor));
                marker.showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude), 13));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isFirsChangeListen = false;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onClick(View v) {
        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
        switch(v.getId()){
            case R.id.add_camera_icon:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent,2);
                break;
            case R.id.add_send_icon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        new ContextThemeWrapper(this,android.R.style.Theme_Dialog));
                // set dialog message
                alertDialogBuilder
                        .setMessage("Confirma el reclamo ?")
                        .setCancelable(false)
                        .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                new HttpAsyncTask().execute("http://civpy.com/reporteS.php");
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
            case R.id.add_upload_icon:
               // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
               // intent.setType("image/*");
               // startActivityForResult(intent,1);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
        }

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                pd = ProgressDialog.show(MapView.this, "Por favor espere !","Cargando imagen...", true);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                reclamo.setFoto(bos.toByteArray());
                Toast.makeText(getBaseContext(), "Imagen agregada con exito ! ", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }else{
                Toast.makeText(getBaseContext(), "Data is null from TAKE ", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == 1 && resultCode == RESULT_OK){
            if (data != null) {
                pd = ProgressDialog.show(MapView.this, "Por favor espere !","Cargando imagen...", true);
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                reclamo.setFoto(imageInByte);
                Toast.makeText(getBaseContext(), "Imagen agregada con exito ! ", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }else{
                Toast.makeText(getBaseContext(), "Data is null from UPLOAD !! ", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void obtainAddressFromGPS(){
        try {
            addresses = geocoder.getFromLocation(DEFAULT_LATITUDE,DEFAULT_LONGITUDE,1);
            if (reclamo.getCategoria().equals("Agua")){
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue);
            }else if  (reclamo.getCategoria().equals("Energia")){
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_yellow);
            }else{
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_red);
            }
            if (addresses.size() > 0){
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String know = addresses.get(0).getFeatureName();
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
                        .draggable(true)
                        .title(reclamo.getCategoria()+" - "+reclamo.getSubcategoria())
                        .snippet(address+", "+city)
                        .icon(markerColor));
                marker.showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), 13));
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Fallo en obtener dirección !", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static String POST(String url, Reclamo reclamo) {
        try {
            String responseServer = null;

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            String encodeBase64 = null;
            if (reclamo.getFoto() != null) {
                encodeBase64 = Base64.encodeToString(reclamo.getFoto(), Base64.DEFAULT);
            }
            jsonObject.put("foto", encodeBase64);
            jsonObject.put("RP_Group", reclamo.getCategoria());
            jsonObject.put("RP_Category", reclamo.getSubcategoria());
            jsonObject.put("latitud", reclamo.getLat());
            jsonObject.put("longitud", reclamo.getLng());
            jsonObject.put("imei", reclamo.getImei());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("datos", jsonObject.toString()));
            Log.e("mainToPost", "mainToPost" + nameValuePairs.toString());

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httpPost);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return "";
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    public void onMyLocationChange(Location location) {

        if (isFirsChangeListen) {
            googleMap.clear();
            reclamo.setLat(location.getLatitude() + "");
            reclamo.setLng(location.getLongitude() + "");
            if (reclamo.getCategoria().equals("Agua")){
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue);
            }else if  (reclamo.getCategoria().equals("Energia")){
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_yellow);
            }else{
                markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_red);
            }
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String know = addresses.get(0).getFeatureName();
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .draggable(true)
                            .title(reclamo.getCategoria()+" - "+reclamo.getSubcategoria())
                            .snippet(address+", "+city)
                             .icon(markerColor));
                    marker.showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isFirsChangeListen = false;
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
            //TODO: Any custom actions
            if (googleMap.getMyLocation() != null) {
                googleMap.clear();
                reclamo.setLat(googleMap.getMyLocation().getLatitude() + "");
                reclamo.setLng(googleMap.getMyLocation().getLongitude() + "");
                if (reclamo.getCategoria().equals("Agua")){
                    markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue);
                }else if  (reclamo.getCategoria().equals("Energia")){
                    markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_yellow);
                }else{
                    markerColor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_red);
                }
                try {
                    addresses = geocoder.getFromLocation(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude(), 1);
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()))
                                .draggable(true)
                                .title(reclamo.getCategoria()+" - "+reclamo.getSubcategoria())
                                .snippet(address+", "+city)
                                 .icon(markerColor));
                        marker.showInfoWindow();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()), 13));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        return false;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pd = ProgressDialog.show(MapView.this, "Por favor espere !","Enviando reporte...", true);

        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],reclamo);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Reporte enviado!" + result, Toast.LENGTH_LONG).show();
            pd.dismiss();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
