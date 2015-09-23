package py.com.reclamospy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Date;

import model.Reclamo;

/**
 * Created by ivan on 9/6/15.
 */
public class ReportTypeSelection extends ActionBarActivity implements View.OnClickListener {
    Toolbar toolbar;
    Reclamo reclamo;
    ImageButton agua;
    ImageButton energia;
    ImageButton viaPublica;
    ImageButton emergencia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_type_selection);

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        reclamo = new Reclamo();
        reclamo.setFecha(new Date());
        //GET IMEI
        TelephonyManager mngr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        reclamo.setImei(mngr.getDeviceId());

        agua = (ImageButton)findViewById(R.id.aguaButton);
        agua.setOnClickListener(this);

        energia = (ImageButton)findViewById(R.id.energiaButton);
        energia.setOnClickListener(this);

        viaPublica = (ImageButton)findViewById(R.id.viapublicaButton);
        viaPublica.setOnClickListener(this);

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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aguaButton:
                reclamo.setCategoria("AGUA");
                Bundle bundle = new Bundle();
                bundle.putSerializable("reclamo",reclamo);
                Intent intent = new Intent(this, ReportSubTypeSelection.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.energiaButton:
                reclamo.setCategoria("ENERGIA");
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("reclamo",reclamo);
                Intent intent2 = new Intent(this, ReportSubTypeSelection.class);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.viapublicaButton:
                reclamo.setCategoria("VIA PUBLICA");
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("reclamo",reclamo);
                Intent intent3 = new Intent(this, ReportSubTypeSelection.class);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;
        }

    }
}
