package py.com.reclamospy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import model.Reclamo;

/**
 * Created by ivan on 9/6/15.
 */
public class ReportSubTypeSelection extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Reclamo reclamo;
    //water image button
    private ImageButton sinServicio,aguaSucia,pocaPresion;
    //energy imageButton
    private ImageButton sinServicioEnergy,bajaTensionEnergy,averiaEnergy;
    //via publica imageButton
    private ImageButton transitoCerradoBtn,basurasBtn,malEstadoBtn,malEstacionadoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        /*
         * Check category and inflate relative subCategory
         */
        openSubType();
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        /*
         * Check internet access
         */
        if (!checkNetwork()) {
            Toast.makeText(getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(reclamo.getCategoria());
        toolbar.setNavigationIcon(R.mipmap.ic_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void openSubType(){
        if  (reclamo.getCategoria().equals("Agua")) {
            setContentView(R.layout.water_selection);
            sinServicio = (ImageButton)findViewById(R.id.aguasinservicio);
            sinServicio.setOnClickListener(this);

            aguaSucia = (ImageButton)findViewById(R.id.aguasucia);
            aguaSucia.setOnClickListener(this);

            pocaPresion = (ImageButton)findViewById(R.id.aguapocapresion);
            pocaPresion.setOnClickListener(this);

        }else if (reclamo.getCategoria().equals("Energia")){
            setContentView(R.layout.energy_selection);
            sinServicioEnergy = (ImageButton)findViewById(R.id.energiasinservicio);
            sinServicioEnergy.setOnClickListener(this);

            bajaTensionEnergy = (ImageButton)findViewById(R.id.energiabajatension);
            bajaTensionEnergy.setOnClickListener(this);

            averiaEnergy = (ImageButton)findViewById(R.id.energiaaveria);
            averiaEnergy.setOnClickListener(this);
        }else if (reclamo.getCategoria().equals("Via publica")){
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
    }

    /**
     * Check internet connection
     * @return
     */
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
    public void onClick(View v) {
        Intent intent = new Intent(this, MapView.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("reclamo",reclamo);
        intent.putExtras(bundle);
        switch(v.getId()){
            case R.id.aguasucia:
                reclamo.setSubcategoria("AGUA SUCIA");
                startActivity(intent);
                break;
            case R.id.aguasinservicio:
                reclamo.setSubcategoria("SIN SERVICIO");
                startActivity(intent);
                break;
            case R.id.aguapocapresion:
                reclamo.setSubcategoria("POCA PRESION");
                startActivity(intent);
                break;
            case R.id.energiaaveria:
                reclamo.setSubcategoria("AVERIA");
                startActivity(intent);
                break;
            case R.id.energiabajatension:
                reclamo.setSubcategoria("BAJA TENSION");
                startActivity(intent);
                break;
            case R.id.energiasinservicio:
                reclamo.setSubcategoria("SIN SERVICIO");
                startActivity(intent);
                break;
            case R.id.viapublicabasura:
                reclamo.setSubcategoria("BASURAS Y DESPERDICIOS");
                startActivity(intent);
                break;
            case R.id.viapublicamalestacionado:
                reclamo.setSubcategoria("MAL ESTACIONADO");
                startActivity(intent);
                break;
            case R.id.viapublicamalestado:
                reclamo.setSubcategoria("VIA EN MAL ESTADO");
                startActivity(intent);
                break;
            case R.id.viapublicatransitocerrado:
                reclamo.setSubcategoria("TRANSITO CERRADO");
                startActivity(intent);
                break;
        }

    }
}
