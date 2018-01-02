package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;

import amagi82.flexibleratingbar.FlexibleRatingBar;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Campo;
import barinfo.navdev.barinfo.Clases.CampoOpinion;
import barinfo.navdev.barinfo.Clases.CampoOpinionMarca;
import barinfo.navdev.barinfo.Clases.CampoOpinionTamanio;
import barinfo.navdev.barinfo.Clases.Opinion;
import barinfo.navdev.barinfo.Clases.Tipo;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.PreferencesManager;


public class AddOpinionFragment extends BaseFragment {
    public static final String TAG = "AddOpinionFragment";

    private Bar mBar;
    private Buscador mBuscador;
    private Opinion mOpinion;

    FlexibleRatingBar mPuntuacion;
    MaterialBetterSpinner mTipoACTV;


    LinearLayout camposLL;
    HashMap<Integer,View> camposViews;

    private OnSaveOpinion mListener;

    public AddOpinionFragment() {
        // Required empty public constructor
    }

    public static AddOpinionFragment newInstance(Bar bar, Opinion opinion, Buscador buscador) {
        AddOpinionFragment fragment = new AddOpinionFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BAR, bar);
        args.putSerializable(Constants.EXTRA_OPINION, opinion);
        args.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBar =(Bar) getArguments().getSerializable(Constants.EXTRA_BAR);
            mOpinion =(Opinion) getArguments().getSerializable(Constants.EXTRA_OPINION);
            mBuscador =(Buscador) getArguments().getSerializable(Constants.EXTRA_BUSCADOR_INIT);
        }
        if (mOpinion == null){
            mOpinion = new Opinion();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_opinion, container, false);

        getActivity().setTitle(mBar.getNombre());
        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        mPuntuacion = (FlexibleRatingBar) v.findViewById(R.id.flexibleRatingBar);
        if (mOpinion.getCalidad() > 0){
            mPuntuacion.setRating(mOpinion.getCalidad());
        }
        mTipoACTV = (MaterialBetterSpinner) v.findViewById(R.id.tipoACView);

        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;

        ArrayList<Tipo> tipos = new ArrayList<>(mBuscador.getTipos());
        tipos.add(0, new Tipo());

        mTipoACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, tipos));
        if (mOpinion.getTipo() != null){
            mOpinion.setTipo_id(mOpinion.getTipo().getId());
            mTipoACTV.setText(mOpinion.getTipo().getNombre());
        }
        mTipoACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0){
                    mOpinion.setTipo(mBuscador.getTipos().get(i-1));
                    mOpinion.setTipo_id(mBuscador.getTipos().get(i-1).getId());
                }else{
                    mOpinion.setTipo(null);
                    mOpinion.setTipo_id(0);
                }

            }
        });


        camposLL = (LinearLayout) v.findViewById(R.id.containerCampos);
        camposViews = new HashMap<>();
        for(final Campo c: mBuscador.getCampos()){
            CampoOpinion opinionCampo  = null;
            for (int i = 0; i < mOpinion.getCamposopiniones().size(); i++){
                if (mOpinion.getCamposopiniones().get(i).getCampo_id() == c.getId()){
                    opinionCampo = mOpinion.getCamposopiniones().get(i);
                    break;
                }
            }

            View child = getActivity().getLayoutInflater().inflate(R.layout.item_formcampo, null);
            CheckBox nombre = (CheckBox) child.findViewById(R.id.nombre);
            nombre.setText(c.getNombre());
            if (opinionCampo != null){
                nombre.setChecked(true);
            }else{
                nombre.setChecked(false);
            }

            final AutoCompleteTextView marcaACTV = (AutoCompleteTextView) child.findViewById(R.id.marcaACV);
            if (c.getIndicarmarca()==0){
                marcaACTV.setVisibility(View.GONE);
            }else{
                marcaACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, c.getMarcas()));
                if (opinionCampo != null){
                    marcaACTV.setText(opinionCampo.getMarcas().get(0).getNombre());
                }
            }

            final LinearLayout tamanioLL = (LinearLayout) child.findViewById(R.id.tamanio);
            if (c.getIndicartamanio()==0){
                tamanioLL.setVisibility(View.GONE);
            }else{
                tamanioLL.setVisibility(View.VISIBLE);
            }

            nombre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked){
                        if (c.getIndicarmarca() == 1){
                            marcaACTV.setVisibility(View.VISIBLE);
                        }
                        if (c.getIndicartamanio() == 1){
                            tamanioLL.setVisibility(View.VISIBLE);
                        }
                    }else{
                        marcaACTV.setVisibility(View.GONE);
                        tamanioLL.setVisibility(View.GONE);
                    }
                }
            });

            if (!nombre.isChecked()) {
                marcaACTV.setVisibility(View.GONE);
                tamanioLL.setVisibility(View.GONE);
            }

            final ImageView smallSize = (ImageView)  child.findViewById(R.id.sizesmall);
            final ImageView  normalSize= (ImageView)  child.findViewById(R.id.sizenormal);
            final ImageView largeSize = (ImageView)  child.findViewById(R.id.sizelarge);
            smallSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    smallSize.setAlpha(1.0f);
                    normalSize.setAlpha(0.3f);
                    largeSize.setAlpha(0.3f);
                }
            });
            normalSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    normalSize.setAlpha(1.0f);
                    smallSize.setAlpha(0.3f);
                    largeSize.setAlpha(0.3f);
                }
            });
            largeSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    largeSize.setAlpha(1.0f);
                    normalSize.setAlpha(0.3f);
                    smallSize.setAlpha(0.3f);
                }
            });

            if (opinionCampo != null){
                for (int i = 0; i < opinionCampo.getTamanios().size(); i++){
                    if (opinionCampo.getTamanios().get(i).getTamanio().equalsIgnoreCase("1")){
                        smallSize.setAlpha(1f);
                    }else if(opinionCampo.getTamanios().get(i).getTamanio().equalsIgnoreCase("2")){
                        normalSize.setAlpha(1f);
                    }else if(opinionCampo.getTamanios().get(i).getTamanio().equalsIgnoreCase("3")){
                        largeSize.setAlpha(1f);
                    }
                }
            }

            camposLL.addView(child);
            camposViews.put(c.getId(),child);
        }

        Button button = (Button) v.findViewById(R.id.saveOpinion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOpinion();
            }
        });

        return v;
    }



    public void saveOpinion() {

        if (mPuntuacion.getRating() == 0){
            AlertUtils.errorDialog(getContext(), "Para poder añadir una opinión, hay que puntuar el bar", new AlertUtils.OnErrorDialog() {
                @Override
                public void run() {
                    mPuntuacion.requestFocus();
                }
            }).show();
        }else{
            mOpinion.setBar_id(mBar.getId());
            PreferencesManager.initializeInstance(getContext());
            mOpinion.setDeviceid(PreferencesManager.getInstance().getValue(Constants.PREF_UUID)+"");
            mOpinion.setCalidad((int) mPuntuacion.getRating());


            for (Integer campo_id: camposViews.keySet()){
                View v = camposViews.get(campo_id);
                CheckBox nombre = (CheckBox) v.findViewById(R.id.nombre);
                if (nombre.isChecked()){
                    CampoOpinion co = new CampoOpinion(mBar.getId(),campo_id,1);

                    final AutoCompleteTextView marcaACTV = (AutoCompleteTextView) v.findViewById(R.id.marcaACV);
                    if (marcaACTV.getVisibility() == View.VISIBLE){
                        ArrayList<CampoOpinionMarca> marcas = new ArrayList<>();
                        marcas.add(new CampoOpinionMarca(marcaACTV.getText().toString()));
                        co.setMarcas(marcas);
                    }

                    final LinearLayout tamanioLL = (LinearLayout) v.findViewById(R.id.tamanio);
                    final ImageView smallSize = (ImageView)  v.findViewById(R.id.sizesmall);
                    final ImageView  normalSize= (ImageView)  v.findViewById(R.id.sizenormal);
                    final ImageView largeSize = (ImageView)  v.findViewById(R.id.sizelarge);
                    if (tamanioLL.getVisibility() == View.VISIBLE){
                        ArrayList<CampoOpinionTamanio> tamanios = new ArrayList<>();
                        if (smallSize.getAlpha() == 1.0f){
                            tamanios.add(new CampoOpinionTamanio("Pequeño"));
                            co.setTamanios(tamanios);
                        }else if (normalSize.getAlpha() == 1.0f){
                            tamanios.add(new CampoOpinionTamanio("Normal"));
                            co.setTamanios(tamanios);
                        }else if (largeSize.getAlpha() == 1.0f){
                            tamanios.add(new CampoOpinionTamanio("Grande"));
                            co.setTamanios(tamanios);
                        }

                    }

                    mOpinion.addCampoOpinion(co);
                }
            }

            if (mListener != null) {
                mListener.onSave(mOpinion);
            }
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
        void onSave(Opinion opinion);
    }
}
