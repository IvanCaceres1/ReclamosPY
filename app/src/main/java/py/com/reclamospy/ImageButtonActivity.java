package py.com.reclamospy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import adapter.ViewPagerAdapter;
import model.Reclamo;
import util.SlidingTabLayout;
/**
 * Created by Edwin on 15/02/2015.
 */
public class ImageButtonActivity extends ActionBarActivity{
    Reclamo reclamo;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_button_layout);
        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        image = (ImageView)findViewById(R.id.imageView);
        if (reclamo.getFoto() != null) {
            Bitmap bMap = BitmapFactory.decodeByteArray(reclamo.getFoto(), 0, reclamo.getFoto().length);
            image.setImageBitmap(bMap);
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
}