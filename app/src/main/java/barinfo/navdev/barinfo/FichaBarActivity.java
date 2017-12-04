package barinfo.navdev.barinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Utils.Constants;

public class FichaBarActivity extends AppCompatActivity implements OnMapReadyCallback {

    Bar mBar;

    MapView mMapview;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_bar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mBar = (Bar) bundle.getSerializable(Constants.EXTRA_BAR);

        setTitle(mBar.getNombre());
        final TextView barNombre = (TextView) findViewById(R.id.barNombre);
        barNombre.setText(mBar.getNombre());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                double percentage = (double) Math.abs(verticalOffset) / toolbar.getHeight();
                if (percentage > 0.8) {
                    barNombre.setVisibility(View.GONE);
                }else{
                    //EXPANDED
                    barNombre.setVisibility(View.VISIBLE);
                }
            }
        });

        cargarImagen();

        cargarInfo(savedInstanceState);
    }


    private void cargarInfo(Bundle savedInstanceState){
        TextView especialidad  = (TextView) findViewById(R.id.especialidad);

        if (mBar.getEspecialidad() != null && mBar.getEspecialidad() != ""){
            especialidad.setText(mBar.getEspecialidad());
        }else{
            especialidad.setVisibility(View.GONE);
        }


        LinearLayout infoubicacion = (LinearLayout) findViewById(R.id.infoubicacion);

        if (mBar.getLatitud() != 0){
            TextView zona = (TextView) findViewById(R.id.zona);
            zona.setText(mBar.getDescripZona());
            mMapview = (MapView) findViewById(R.id.map);
            mMapview.onCreate(savedInstanceState);
            mMapview.getMapAsync(this);
        }else{
            infoubicacion.setVisibility(View.GONE);
        }
    }

    private void cargarImagen(){
        if (mBar.getImgFicheroGN() != null && !mBar.getImgFicheroGN().equalsIgnoreCase("")){

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            final ImageView photo = (ImageView) findViewById(R.id.photo);
            imageLoader.loadImage(mBar.getImgFicheroGN(), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    photo.setImageBitmap(loadedImage);
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mBar.getLatitud(), mBar.getLongitud()))
                .title(mBar.getNombre()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mBar.getLatitud(), mBar.getLongitud()), 17));
        mMapview.onResume();
    }
}
