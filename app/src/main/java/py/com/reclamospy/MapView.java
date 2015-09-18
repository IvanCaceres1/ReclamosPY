package py.com.reclamospy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import model.Reclamo;

/**
 * Created by ivan on 9/8/15.
 */
public class MapView extends ActionBarActivity implements GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnMyLocationChangeListener{
    private GoogleMap googleMap;
    private byte[] bArray;
    private Toolbar toolbar;
    private float markerColor;
    private Reclamo reclamo;
    //Reverse geocoding addressess result
    private List<Address> addresses;
    Geocoder geocoder;
    static final double DEFAULT_LATITUDE = -25.516666700000000000;
    static final double DEFAULT_LONGITUDE= -54.616666699999996000;
    //Set marker at currrent position and set location change listener to null
    boolean isFirsChangeListen;
    private FloatingActionButton cameraBtn,sendBtn,uploadBtn;
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
        if (reclamo.getCategoria().equals("AGUA")){
            markerColor = BitmapDescriptorFactory.HUE_BLUE;
        }else if  (reclamo.getCategoria().equals("ENERGIA")){
            markerColor = BitmapDescriptorFactory.HUE_YELLOW;
        }else{
            markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        //Obtain address from lat,lng
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
              addresses = geocoder.getFromLocation(DEFAULT_LATITUDE,DEFAULT_LONGITUDE,1);
            if (addresses.size() > 0){
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String know = addresses.get(0).getFeatureName();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(DEFAULT_LATITUDE,DEFAULT_LONGITUDE))
                        .draggable(true)
                        .title(know+" , "+address+" , "+city+" , "+
                                 country)
                        .icon(BitmapDescriptorFactory
                        .defaultMarker(markerColor)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), 13));

                Toast.makeText(getBaseContext(), know+" , "+address+" , "+city+" , "+state+" , "+country,Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        googleMap.setOnMapClickListener(this);

        if (googleMap!= null) {
            googleMap.setOnMyLocationChangeListener(this);
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {

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
                Toast.makeText(getBaseContext(), know+" , "+address+" , "+city+" , "+state+" , "+country,Toast.LENGTH_LONG).show();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude,latLng.longitude))
                        .draggable(true)
                        .title(know+" , "+address+" , "+city+" , "+
                                country)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(markerColor)));
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
        switch(v.getId()){
            case R.id.add_camera_icon:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent,2);
                break;
            case R.id.add_send_icon:
                Bundle bundle = new Bundle();
                bundle.putSerializable("reclamo",reclamo);
                Intent intent2 = new Intent(this, ImageButtonActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
                //new HttpAsyncTask().execute("http://192.168.1.107/");
                break;
            case R.id.add_upload_icon:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
        }

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bArray = bos.toByteArray();
            reclamo.setFoto(bArray);
            Toast.makeText(getBaseContext(), "TAKE: "+reclamo.toString(), Toast.LENGTH_LONG).show();
        }else if (requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            Bitmap mBitmap = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            reclamo.setFoto(imageInByte);
            Toast.makeText(getBaseContext(), "UPLOAD: "+reclamo.toString(), Toast.LENGTH_LONG).show();
            cursor.close();
        }
    }

    public static String POST(String url, Reclamo reclamo){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("RP_Category", reclamo.getCategoria());
            jsonObject.accumulate("fecha", reclamo.getFecha());
            jsonObject.accumulate("latitud", reclamo.getLat());
            jsonObject.accumulate("longitud",reclamo.getLng());
            jsonObject.accumulate("imei",reclamo.getImei());
            jsonObject.accumulate("RP_Group",reclamo.getSubcategoria());
            jsonObject.accumulate("img",reclamo.getFoto());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            System.out.println("Json Object POST :"+json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
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
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String know = addresses.get(0).getFeatureName();
                    Toast.makeText(getBaseContext(), know + " , " + address + " , " + city + " , " + state + " , " + country, Toast.LENGTH_LONG).show();
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .draggable(true)
                            .title(know + " , " + address + " , " + city + " , " +
                                    country)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(markerColor)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isFirsChangeListen = false;
        }

    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],reclamo);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }
}
