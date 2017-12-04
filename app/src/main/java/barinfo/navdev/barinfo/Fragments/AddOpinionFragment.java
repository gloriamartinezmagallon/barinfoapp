package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;

import amagi82.flexibleratingbar.FlexibleRatingBar;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Campo;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;


public class AddOpinionFragment extends Fragment {
    public static final String TAG = "AddOpinionFragment";

    private Bar mBar;
    private Buscador mBuscador;

    FlexibleRatingBar mPuntuacion;
    MaterialBetterSpinner tipoACTV;


    LinearLayout camposLL;
    HashMap<Integer,View> camposViews;

    private OnSaveOpinion mListener;

    public AddOpinionFragment() {
        // Required empty public constructor
    }

    public static AddOpinionFragment newInstance(Bar bar, Buscador buscador) {
        AddOpinionFragment fragment = new AddOpinionFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BAR, bar);
        args.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBar =(Bar) getArguments().getSerializable(Constants.EXTRA_BAR);
            mBuscador =(Buscador) getArguments().getSerializable(Constants.EXTRA_BUSCADOR_INIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_opinion, container, false);

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

         mPuntuacion = (FlexibleRatingBar) v.findViewById(R.id.flexibleRatingBar);
        tipoACTV = (MaterialBetterSpinner) v.findViewById(R.id.tipoACView);

        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;
        tipoACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, mBuscador.getTipos()));

        cargarImagen(v);

        camposLL = (LinearLayout) v.findViewById(R.id.containerCampos);
        camposViews = new HashMap<>();
        for(Campo c: mBuscador.getCampos()){

            View child = getActivity().getLayoutInflater().inflate(R.layout.item_formcampo, null);
            TextView nombre = (TextView) child.findViewById(R.id.nombre);
            nombre.setText(c.getNombre());
            TextInputLayout marca = (TextInputLayout) child.findViewById(R.id.marcaACV_id);
            AutoCompleteTextView marcaACTV = (AutoCompleteTextView) child.findViewById(R.id.marcaACV);
            if (c.getIndicarmarca()==0){
                marca.setVisibility(View.GONE);
            }else{
                marcaACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, c.getMarcas()));
            }

            TextInputLayout tamanio = (TextInputLayout) child.findViewById(R.id.tamanio_id);
            MaterialBetterSpinner tamanioACTV = (MaterialBetterSpinner) child.findViewById(R.id.tamanio);
            if (c.getIndicartamanio()==0){
                tamanio.setVisibility(View.GONE);
            }else{
                ArrayList<String> tamaniosString = Campo.getDefaultTamanios();
                tamanioACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, tamaniosString));
            }

            camposLL.addView(child);
            camposViews.put(c.getId(),child);
        }

        return v;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void saveOpinion(Uri uri) {
        if (mListener != null) {
            mListener.onSave(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveOpinion) {
            mListener = (OnSaveOpinion) context;
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

    public interface OnSaveOpinion {
        void onSave(Uri uri);
    }
}
