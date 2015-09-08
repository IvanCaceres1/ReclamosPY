package py.com.reclamospy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import adapter.ViewPagerAdapter;
import model.Reclamo;
import util.SlidingTabLayout;

/**
 * Created by ivan on 9/6/15.
 */
public class WaterSelection extends ActionBarActivity implements View.OnClickListener {
    Toolbar toolbar;
    Reclamo reclamo;
    ImageButton sinServicio;
    ImageButton aguaSucia;
    ImageButton pocaPresion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_selection);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        System.out.println("RECLAMO FROM WATER SELECTION SERIALIZABLE" + reclamo.toString());
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        sinServicio = (ImageButton)findViewById(R.id.aguasinservicio);
        sinServicio.setOnClickListener(this);
        aguaSucia = (ImageButton)findViewById(R.id.aguasucia);
        aguaSucia.setOnClickListener(this);
        pocaPresion = (ImageButton)findViewById(R.id.aguapocapresion);
        pocaPresion.setOnClickListener(this);
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aguasucia:
                reclamo.setCategoria("AGUA");
                Intent intent = new Intent(this, MapView.class);
                startActivity(intent);
                System.out.println("AUGA PRESSED");
                break;
            case R.id.aguasinservicio:
                System.out.println("ENERGIA PRESSED");
                break;
            case R.id.aguapocapresion:
                System.out.println("EMERGENCIA PRESSED");
                break;
        }

    }
}
