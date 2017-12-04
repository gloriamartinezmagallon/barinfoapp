package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;

public class BarDetailsFragment extends Fragment {

    public static final String TAG = "BarDetailsFragment";

    private Bar mBar;

    private OnAddOpinion mListener;

    public BarDetailsFragment() {
        // Required empty public constructor
    }

    public static BarDetailsFragment newInstance(Bar bar) {
        BarDetailsFragment fragment = new BarDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BAR, bar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBar =(Bar) getArguments().getSerializable(Constants.EXTRA_BAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bar_details, container, false);

        getActivity().setTitle(mBar.getNombre());
        final TextView barNombre = (TextView) v.findViewById(R.id.barNombre);
        barNombre.setText(mBar.getNombre());
        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        AppBarLayout app_bar = (AppBarLayout) v.findViewById(R.id.app_bar);
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

        cargarImagen(v);

        cargarInfo(v,savedInstanceState);


        Button addOpinion = (Button) v.findViewById(R.id.addOpinion);
        addOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOpinion();
            }
        });

        return v;
    }

    private void cargarInfo(View v, Bundle savedInstanceState){
        TextView especialidad  = (TextView) v.findViewById(R.id.especialidad);

        if (mBar.getEspecialidad() != null && mBar.getEspecialidad() != ""){
            especialidad.setText(mBar.getEspecialidad());
        }else{
            especialidad.setVisibility(View.GONE);
        }


        LinearLayout infoubicacion = (LinearLayout) v.findViewById(R.id.infoubicacion);

        if (mBar.getLatitud() != 0){
            TextView zona = (TextView) v.findViewById(R.id.zona);
            TextView direccion = (TextView) v.findViewById(R.id.direccion);
            TextView distancia = (TextView) v.findViewById(R.id.distancia);

            if (mBar.getDireccion() == null || mBar.getDireccion().equalsIgnoreCase("")){
                direccion.setText(mBar.getNombreLocalidad());
            }else{
                direccion.setText(mBar.getDireccion());
            }

            if (mBar.getDistance() > 0){
                distancia.setText(mBar.getDistanceWithFormat());
                distancia.setVisibility(View.VISIBLE);
            }else{
                distancia.setVisibility(View.GONE);
            }

            zona.setText(mBar.getDescripZona());
            final MapView mapview = (MapView) v.findViewById(R.id.map);
            mapview.onCreate(savedInstanceState);
            mapview.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    GoogleMap map = googleMap;

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(mBar.getLatitud(), mBar.getLongitud()))
                            .title(mBar.getNombre()));

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mBar.getLatitud(), mBar.getLongitud()), 17));
                    mapview.onResume();
                }
            });
            mapview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:"+mBar.getLatitud()+","+mBar.getLongitud());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
        }else{
            infoubicacion.setVisibility(View.GONE);
        }
    }

    private void cargarImagen(View v){
        if (mBar.getImgFicheroGN() != null && !mBar.getImgFicheroGN().equalsIgnoreCase("")){

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            final ImageView photo = (ImageView) v.findViewById(R.id.photo);
            imageLoader.loadImage(mBar.getImgFicheroGN(), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    photo.setImageBitmap(loadedImage);
                }
            });
        }
    }


    public void addOpinion() {
        if (mListener != null) {
            mListener.onAddOpinion();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddOpinion) {
            mListener = (OnAddOpinion) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddOpinion {
        void onAddOpinion();
    }
}
