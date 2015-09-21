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
    //water image button
    ImageButton sinServicio,aguaSucia,pocaPresion;
    //energy imageButton
    ImageButton sinServicioEnergy,bajaTensionEnergy,averiaEnergy;
    //via publica imageButton
    ImageButton transitoCerradoBtn,basurasBtn,malEstadoBtn,malEstacionadoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        if  (reclamo.getCategoria().equals("AGUA")) {
            setContentView(R.layout.water_selection);
            sinServicio = (ImageButton)findViewById(R.id.aguasinservicio);
            sinServicio.setOnClickListener(this);

            aguaSucia = (ImageButton)findViewById(R.id.aguasucia);
            aguaSucia.setOnClickListener(this);

            pocaPresion = (ImageButton)findViewById(R.id.aguapocapresion);
            pocaPresion.setOnClickListener(this);

        }else if (reclamo.getCategoria().equals("ENERGIA")){
            setContentView(R.layout.energy_selection);
            sinServicioEnergy = (ImageButton)findViewById(R.id.energiasinservicio);
            sinServicioEnergy.setOnClickListener(this);

            bajaTensionEnergy = (ImageButton)findViewById(R.id.energiabajatension);
            bajaTensionEnergy.setOnClickListener(this);

            averiaEnergy = (ImageButton)findViewById(R.id.energiaaveria);
            averiaEnergy.setOnClickListener(this);
        }else if (reclamo.getCategoria().equals("VIA PUBLICA")){
            setContentView(R.layout.incident_selection);
            transitoCerradoBtn = (ImageButton)findViewById(R.id.viapublicatransitocerrado);
            transitoCerradoBtn.setOnClickListener(this);

            basurasBtn = (ImageButton)findViewById(R.id.viapublicabasura);
            basurasBtn.setOnClickListener(this);

            malEstadoBtn= (ImageButton)findViewById(R.id.viapublicamalestado);
            malEstadoBtn.setOnClickListener(this);

            malEstacionadoBtn= (ImageButton)findViewById(R.id.viapublicamalestacionado);
            malEstacionadoBtn.setOnClickListener(this);
        }
        toolbar = (Toolbar) findViewById(R.id.tool_bar);


        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
            case R.id.energiaaveria:
                reclamo.setSubcategoria("AVERIA");
                Intent intent4 = new Intent(this, MapView.class);
                Bundle bundle4 = new Bundle();
                bundle4.putSerializable("reclamo",reclamo);
                intent4.putExtras(bundle4);
                startActivity(intent4);
                break;
            case R.id.energiabajatension:
                reclamo.setSubcategoria("BAJA TENSION");
                Intent intent5 = new Intent(this, MapView.class);
                Bundle bundle5 = new Bundle();
                bundle5.putSerializable("reclamo", reclamo);
                intent5.putExtras(bundle5);
                startActivity(intent5);
                break;
            case R.id.energiasinservicio:
                reclamo.setSubcategoria("SIN SERVICIO");
                Intent intent6 = new Intent(this, MapView.class);
                Bundle bundle6 = new Bundle();
                bundle6.putSerializable("reclamo",reclamo);
                intent6.putExtras(bundle6);
                startActivity(intent6);
                break;
            case R.id.viapublicabasura:
                reclamo.setSubcategoria("BASURAS Y DESPERDICIOS");
                Intent intent7 = new Intent(this, MapView.class);
                Bundle bundle7 = new Bundle();
                bundle7.putSerializable("reclamo",reclamo);
                intent7.putExtras(bundle7);
                startActivity(intent7);
                break;
            case R.id.viapublicamalestacionado:
                reclamo.setSubcategoria("MAL ESTACIONADO");
                Intent intent8 = new Intent(this, MapView.class);
                Bundle bundle8 = new Bundle();
                bundle8.putSerializable("reclamo", reclamo);
                intent8.putExtras(bundle8);
                startActivity(intent8);
                break;
            case R.id.viapublicamalestado:
                reclamo.setSubcategoria("VIA EN MAL ESTADO");
                Intent intent9 = new Intent(this, MapView.class);
                Bundle bundle9 = new Bundle();
                bundle9.putSerializable("reclamo",reclamo);
                intent9.putExtras(bundle9);
                startActivity(intent9);
                break;
            case R.id.viapublicatransitocerrado:
                reclamo.setSubcategoria("TRANSITO CERRADO");
                Intent intent10 = new Intent(this, MapView.class);
                Bundle bundle10 = new Bundle();
                bundle10.putSerializable("reclamo",reclamo);
                intent10.putExtras(bundle10);
                startActivity(intent10);
                break;
        }

    }
}
