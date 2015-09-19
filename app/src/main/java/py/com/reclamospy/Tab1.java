package py.com.reclamospy;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab1 extends ListFragment {

    // URL to get contacts JSO
    private static String url = "http://yoreporto.org/SelectAll_v2.php";
    private ProgressDialog pd = null;
    // JSON Node names
    private static final String TAG_LATLONGS = "reclamo";

    private static final String TAG_ID = "reporte_id";
    private static final String TAG_IMEI = "IMEI";
    private static final String TAG_LAT = "latitud";
    private static final String TAG_LONG = "longitud";
    private static final String TAG_CATEGORIA= "RP_Category";
    private static final String TAG_SUBCATEGORIA = "RP_Group";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_DATE = "Created";
    private static final String TAG_FOTO = "Img";

    // contacts JSONArray
    JSONArray latlngs = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> latLngList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than
        // Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
                : android.R.layout.simple_list_item_1;

        // Create an array adapter for the list view, using the Ipsum headlines
        // array
        latLngList = new ArrayList<HashMap<String, String>>();

        setRetainInstance(true);
        if (!checkNetwork()) {
            Toast.makeText(getActivity().getBaseContext(), "Sin conexión a internet !!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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
        boolean isVisible = isVisible();
        boolean isMenuVisible = isMenuVisible();

        if (isVisibleToUser && isResumed() && latLngList.size() == 0 ) {
            new GetContacts().execute();
        }
    }

    /*
     *  Check network availability and connected
     */
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
    /**
     * Async task class to get json by making HTTP call
     * */
    public class GetContacts extends AsyncTask<Void, Void, Void> {

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
                        JSONObject c = latlngs.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String imei = c.getString(TAG_IMEI);
                        String categoria = c.getString(TAG_CATEGORIA);
                        String subcategoria = c.getString(TAG_SUBCATEGORIA);
                        String fecha = c.getString(TAG_FECHA);
                        String lat= c.getString(TAG_LAT);
                        String lng = c.getString(TAG_LONG);

                        // tmp hashmap for single contact
                        HashMap<String, String> reclamo = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        reclamo.put(TAG_ID, ""+id+" - ");
                        reclamo.put(TAG_IMEI,imei);
                        reclamo.put(TAG_CATEGORIA,categoria);
                        reclamo.put(TAG_SUBCATEGORIA,subcategoria);
                        DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = newDateFormat.parse(fecha);
                        reclamo.put(TAG_FECHA,calculateElapsedTime(d));
                        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        reclamo.put(TAG_DATE,formatter.format(d));
                        reclamo.put(TAG_LAT, lat);
                        reclamo.put(TAG_LONG, lng);

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
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (latLngList != null ) {
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), latLngList,
                        R.layout.list_item, new String[]{TAG_ID, TAG_CATEGORIA, TAG_SUBCATEGORIA, TAG_FECHA, TAG_DATE}, new int[]{R.id.id, R.id.categoria, R.id.subcategoria, R.id.elapsedTime, R.id.fecha});

                setListAdapter(adapter);
            }
            pd.dismiss();
        }

    }

}
