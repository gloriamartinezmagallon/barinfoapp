package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import amagi82.flexibleratingbar.FlexibleRatingBar;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.CampoOpinion;
import barinfo.navdev.barinfo.Clases.Opinion;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.PreferencesManager;

public class BarDetailsFragment extends BaseFragment {

    public static final String TAG = "BarDetailsFragment";

    //API GOOGLE STREET VIEW IMAGE
    //AIzaSyBXauccdW03Fn485kfvRj0qpcJzWyODUQc

    private Bar mBar;

    private Bundle mBundle;

    private OnAddOpinion mListener;

    View mView;

    long mUUID;
    Opinion miOpinion = null;

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

        mUUID = PreferencesManager.getInstance().getValue(Constants.PREF_UUID);

        mBundle = savedInstanceState;
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

        mView = v;
        cargarInfo();


        FloatingActionButton addOpinion = (FloatingActionButton) v.findViewById(R.id.fab);
        addOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOpinion();
            }
        });


        FloatingActionButton addOpinion2 = (FloatingActionButton) v.findViewById(R.id.fab2);
        addOpinion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOpinion();
            }
        });

        return v;
    }

    private void cargarInfo(){
        TextView especialidad  = (TextView) mView.findViewById(R.id.especialidad);

        if (mBar.getEspecialidad() != null && mBar.getEspecialidad() != ""){
            especialidad.setText(mBar.getEspecialidad());
        }else{
            especialidad.setVisibility(View.GONE);
        }


        LinearLayout infoubicacion = (LinearLayout) mView.findViewById(R.id.infoubicacion);

        if (mBar.getLatitud() != 0){
            TextView zona = (TextView) mView.findViewById(R.id.zona);
            TextView direccion = (TextView) mView.findViewById(R.id.direccion);
            TextView distancia = (TextView) mView.findViewById(R.id.distancia);

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
            final MapView mapview = (MapView) mView.findViewById(R.id.map);
            mapview.onCreate(mBundle);
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

        //CARGAR OPINIONES
        LinearLayout infoopiniones = mView.findViewById(R.id.infoopiniones);
        if (mBar.getOpiniones().size() == 0) {
            infoopiniones.setVisibility(View.GONE);
            return;
        }
        infoopiniones.setVisibility(View.VISIBLE);

        float calidadmedia = 0;
        HashMap<String, Integer> tipos = new HashMap<>();
        for(Opinion o: mBar .getOpiniones()){

            if (o.getDeviceid() != null && o.getDeviceid().equalsIgnoreCase(mUUID+"")){
                miOpinion = o;
            }
            calidadmedia += o.getCalidad();

            if(o.getTipo() != null) {
                if (!tipos.containsKey(o.getTipo().getNombre())) {
                    tipos.put(o.getTipo().getNombre(), 0);
                }
                tipos.put(o.getTipo().getNombre(), tipos.get(o.getTipo().getNombre()) + 1);
            }
        }
        calidadmedia = calidadmedia/mBar.getOpiniones().size();

        FlexibleRatingBar flexibleRatingBar = mView.findViewById(R.id.flexibleRatingBar);
        flexibleRatingBar.setRating(calidadmedia);
        TextView numopiniones =  mView.findViewById(R.id.numopiniones);
        if (mBar.getOpiniones().size() == 1)
            numopiniones.setText(getString(R.string.bardetails_unaopinion));
        else
            numopiniones.setText(getString(R.string.bardetails_opiniones,mBar.getOpiniones().size()));

        LinearLayout containerTipos = mView.findViewById(R.id.containerTipos);

        Object[] a = tipos.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });


        for(Object e : a){
            View child = getActivity().getLayoutInflater().inflate(R.layout.item_tipoopinion, null);
            TextView nombre = (TextView) child.findViewById(R.id.nombre);
            nombre.setText(((Map.Entry<String, Integer>) e).getKey());
            TextView numoks = (TextView) child.findViewById(R.id.numoks);
            numoks.setText(((Map.Entry<String, Integer>) e).getValue()+"");
            containerTipos.addView(child);
        }

        //CARGAR OPINIONES POR CAMPOS

        LinearLayout containerCampos = (LinearLayout) mView.findViewById(R.id.containerTiene);
        HashMap<String, HashMap<String,Integer>> campos = new HashMap<>();
        for (Opinion o: mBar.getOpiniones()) {
            for (CampoOpinion campoOpinion : o.getCamposopiniones()) {
                if (!campos.containsKey(campoOpinion.getCampo().getNombre())) {
                    campos.put(campoOpinion.getCampo().getNombre(), new HashMap<String, Integer>());
                }
                String key = "";
                if (campoOpinion.getMarcas().size() > 0) {
                    key = campoOpinion.getMarcas().get(0).getNombre();
                }
                key += "_";
                if (campoOpinion.getTamanios().size() > 0) {
                    key += campoOpinion.getTamanios().get(0).getTamanio();
                }
                if (!campos.get(campoOpinion.getCampo().getNombre()).containsKey(key)) {
                    campos.get(campoOpinion.getCampo().getNombre()).put(key, 0);
                }
                campos.get(campoOpinion.getCampo().getNombre()).put(key, campos.get(campoOpinion.getCampo().getNombre()).get(key) + 1);
            }
        }


        for(String nombreCampo: campos.keySet()){
            boolean primera = true;

            Object[] b = campos.get(nombreCampo).entrySet().toArray();
            Arrays.sort(b, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<String, Integer>) o2).getValue()
                            .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                }
            });


            for(Object e : b){
                View child = getActivity().getLayoutInflater().inflate(R.layout.item_campoopinion, null);

                TextView nombre = (TextView) child.findViewById(R.id.nombre);
                ImageView sizesmall = (ImageView) child.findViewById(R.id.sizesmall);
                ImageView sizenormal = (ImageView) child.findViewById(R.id.sizenormal);
                ImageView sizelarge = (ImageView) child.findViewById(R.id.sizelarge);
                TextView marca = (TextView) child.findViewById(R.id.marca);
                TextView numoks = (TextView) child.findViewById(R.id.numoks);

                if (primera){
                    nombre.setText(nombreCampo);
                    primera = false;
                }else{
                    nombre.setVisibility(View.GONE);
                }

                String[] keys = ((Map.Entry<String, Integer>) e).getKey().split("_");
                if (keys.length < 1){
                    continue;
                }
                //MARCA
                marca.setText(keys[0]);

                //TAMANIO
                if (keys.length > 1) {
                    switch (keys[1]) {
                        case "1":
                            sizesmall.setAlpha(1f);
                            break;
                        case "2":
                            sizenormal.setAlpha(1f);
                            break;
                        case "3":
                            sizelarge.setAlpha(1f);
                            break;
                    }
                }

                numoks.setText(((Map.Entry<String, Integer>) e).getValue()+"");
                containerCampos.addView(child);
            }

        }
    }

    private void cargarImagen(View v){

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = new SupportStreetViewPanoramaFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.streetviewpanorama_container, streetViewPanoramaFragment)
                .commit();

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        if (mBundle == null)
                         panorama.setPosition(new LatLng(mBar.getLatitud(),mBar.getLongitud()));
                    }
                });

        if (mBar.getImgFicheroGN() != null && !mBar.getImgFicheroGN().equalsIgnoreCase("")){

            /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
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
            });*/
        }
    }


    public void addOpinion() {
        if (mListener != null) {
            mListener.onAddOpinion(miOpinion);
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

    public void setBar(Bar bar){
        mBar = bar;
        cargarInfo();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddOpinion {
        void onAddOpinion(Opinion opinion);
    }
}
