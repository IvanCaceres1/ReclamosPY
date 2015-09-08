package py.com.reclamospy;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import model.Reclamo;

/**
 * Created by ivan on 9/8/15.
 */
public class MapView extends ActionBarActivity implements GoogleMap.OnMarkerDragListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener{
    GoogleMap googleMap;
    Toolbar toolbar;
    Reclamo reclamo;
    LocationManager locationManager;
    double lat;
    double lng;
    @Override
    protected void onCreate(Bundle saveInstanceState){
         super.onCreate(saveInstanceState);
        setContentView(R.layout.map_view);
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        lat = -25.516666700000000000;
        lng = -54.616666699999996000;
        reclamo.setLng(lat+"");
        reclamo.setLng(lng+"");
        System.out.println("Reclamo: "+reclamo.toString());
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //reclamo.setLat(lat+"");
        //reclamo.setLng(lng+"");
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .draggable(true)
                .title("Ubicacion del relcamo"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
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
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        reclamo.setLat(dragLat+"");
        reclamo.setLng(dragLong+"");
        System.out.println("REclamo: "+reclamo.toString());
    }

    @Override
    public void onMapClick(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
