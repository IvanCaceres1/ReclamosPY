package py.com.reclamospy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import java.util.Date;

import model.Reclamo;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab3 extends Fragment implements View.OnClickListener{
    private Toolbar toolbar;
    private Reclamo reclamo;
    private ImageButton agua;
    private ImageButton energia;
    private ImageButton viaPublica;
    private ImageButton emergencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.report_type_selection, container,
                false);

        toolbar = (Toolbar) v.findViewById(R.id.tool_bar);
        reclamo = new Reclamo();
        reclamo.setFecha(new Date());
        //GET IMEI
        TelephonyManager mngr = (TelephonyManager) this.getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        reclamo.setImei(mngr.getDeviceId());
        agua = (ImageButton)v.findViewById(R.id.aguaButton);
        agua.setOnClickListener(this);
        energia = (ImageButton)v.findViewById(R.id.energiaButton);
        energia.setOnClickListener(this);
        viaPublica = (ImageButton)v.findViewById(R.id.viapublicaButton);
        viaPublica.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("reclamo",reclamo);
        Intent intent = new Intent(getActivity(), ReportSubTypeSelection.class);
        intent.putExtras(bundle);
        switch(v.getId()){
            case R.id.aguaButton:
                reclamo.setCategoria("Agua");
                startActivity(intent);
                break;
            case R.id.energiaButton:
                reclamo.setCategoria("Energia");
                startActivity(intent);
                break;
            case R.id.viapublicaButton:
                reclamo.setCategoria("Via publica");
                startActivity(intent);
                break;
        }
    }
}