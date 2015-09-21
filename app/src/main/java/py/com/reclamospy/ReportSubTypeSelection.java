package py.com.reclamospy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import adapter.ViewPagerAdapter;
import model.Reclamo;
import util.SlidingTabLayout;

/**
 * Created by ivan on 9/6/15.
 */
public class ReportSubTypeSelection extends ActionBarActivity implements View.OnClickListener {
    Toolbar toolbar;
    Reclamo reclamo;
    ImageButton sinServicio;
    ImageButton aguaSucia;
    ImageButton pocaPresion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        if  (reclamo.getCategoria().equals("AGUA")) {
            setContentView(R.layout.water_selection);
        }else if (reclamo.getCategoria().equals("ENERGIA")){
            setContentView(R.layout.energy_selection);
        }else if (reclamo.getCategoria().equals("VIA PUBLICA")){
            setContentView(R.layout.incident_selection);
        }
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        sinServicio = (ImageButton)findViewById(R.id.aguasinservicio);
        sinServicio.setOnClickListener(this);

        aguaSucia = (ImageButton)findViewById(R.id.aguasucia);
        aguaSucia.setOnClickListener(this);

        pocaPresion = (ImageButton)findViewById(R.id.aguapocapresion);
        pocaPresion.setOnClickListener(this);

        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }

        setSupportActionBar(toolbar);
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aguasucia:
                reclamo.setSubcategoria("AGUA SUCIA");
                Intent intent = new Intent(this, MapView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reclamo",reclamo);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.aguasinservicio:
                reclamo.setSubcategoria("SIN SERVICIO");
                Intent intent2 = new Intent(this, MapView.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("reclamo", reclamo);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.aguapocapresion:
                reclamo.setSubcategoria("POCA PRESION");
                Intent intent3 = new Intent(this, MapView.class);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("reclamo",reclamo);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;
        }

    }
}
