package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Campo;
import barinfo.navdev.barinfo.Clases.CampoOpinionTamanio;
import barinfo.navdev.barinfo.Clases.Tipo;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.PreferencesManager;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilterFragment extends BaseFragment {

    public static final String TAG = "FilterFragment";

    private Buscador mBuscador;

    MaterialBetterSpinner mTipoACTV;
    MaterialBetterSpinner mLocalidadACTV;

    Tipo tipoSeleccionado;
    String localidadSeleccionada;


    LinearLayout mProgressBarContainer;
    HashMap<Integer,View> camposViews;


    LinearLayout camposLL;

    private OnCancelButtonClick mCancelListener;
    private OnFilterButtonClick mFilterListener;

    public FilterFragment() { }

    public static FilterFragment newInstance(Buscador buscador) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuscador = (Buscador) getArguments().getSerializable(Constants.EXTRA_BUSCADOR_INIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_filter, container, false);

        mProgressBarContainer  = (LinearLayout) v.findViewById(R.id.progressBarContainer);

        mTipoACTV = (MaterialBetterSpinner) v.findViewById(R.id.tipoACView);

        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;
        ArrayList<Tipo> tipos = new ArrayList<>(mBuscador.getTipos());
        tipos.add(0, new Tipo());
        mTipoACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, tipos));
        if (mBuscador.getTipoSeleccionado() != null){
            for (int i = 0; i < mBuscador.getTipos().size(); i++){
                if (mBuscador.getTipos().get(i).getId() == mBuscador.getTipoSeleccionado().getId()){
                    mTipoACTV.setSelection(i);
                    break;
                }
            }
        }
        mTipoACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tipoSeleccionado = mBuscador.getTipos().get(i);
            }
        });

        mLocalidadACTV = (MaterialBetterSpinner) v.findViewById(R.id.localidadACView);

        mLocalidadACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, mBuscador.getLocalidades()));
        if (mBuscador.getLocalidadSeleccionada() != null){
            for (int i = 0; i < mBuscador.getLocalidades().size(); i++){
                if (mBuscador.getLocalidades().get(i).equalsIgnoreCase(mBuscador.getLocalidadSeleccionada())){
                    mLocalidadACTV.setSelection(i);
                    break;
                }
            }
        }
        mLocalidadACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                localidadSeleccionada = mBuscador.getLocalidades().get(i);
            }
        });

        camposViews = new HashMap<>();
        camposLL = (LinearLayout) v.findViewById(R.id.campos);
        for(final Campo c: mBuscador.getCampos()){
            if (c.getNumopiniones() == 0){
                camposViews.put(c.getId(),null);
                continue;
            }
            View child = getActivity().getLayoutInflater().inflate(R.layout.item_filtercampo, null);
            CheckBox nombre = (CheckBox) child.findViewById(R.id.nombre);
            nombre.setText(c.getNombre());
            final MaterialBetterSpinner marcaACTV = (MaterialBetterSpinner) child.findViewById(R.id.marcaACV);
            if (c.getIndicarmarca()==0){
                marcaACTV.setVisibility(View.GONE);
            }else{
                marcaACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, c.getMarcas()));
            }

            final LinearLayout tamanioLL = (LinearLayout) child.findViewById(R.id.tamanio);
            if (c.getIndicartamanio()==0){
                tamanioLL.setVisibility(View.GONE);
            }else{
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

            }
            tamanioLL.setVisibility(View.GONE);
            marcaACTV.setVisibility(View.GONE);
            nombre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        if (c.getIndicartamanio() == 1)
                            tamanioLL.setVisibility(View.VISIBLE);

                        if (c.getIndicarmarca() == 1)
                            marcaACTV.setVisibility(View.VISIBLE);
                    }else{
                        tamanioLL.setVisibility(View.GONE);
                        marcaACTV.setVisibility(View.GONE);
                    }
                }
            });
            camposLL.addView(child);
            camposViews.put(c.getId(),child);
        }

        setupActionBar(v);

        return v;
    }

    private void setupActionBar(View v) {
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu_main);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hola = 11;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_close_actionbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_filter:
                        filtrar();
                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterButtonClick) {
            mFilterListener = (OnFilterButtonClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterButtonClick");
        }

        if (context instanceof OnCancelButtonClick) {
            mCancelListener = (OnCancelButtonClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCancelButtonClick");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCancelListener = null;
        mFilterListener = null;
    }

    private void filtrar(){

        mProgressBarContainer.setVisibility(View.VISIBLE);
        mBuscador.setLocalidadSeleccionada(localidadSeleccionada);
        mBuscador.setTipoSeleccionado(tipoSeleccionado);


        for (Integer campo_id: camposViews.keySet()){

            int indiceCampo = -1;
            for (int i = 0; i < mBuscador.getCampos().size(); i++){
                if (mBuscador.getCampos().get(i).getId() == campo_id){
                    indiceCampo = i;
                    break;
                }
            }
            if (indiceCampo > -1){
                View v = camposViews.get(campo_id);
                CheckBox nombre = (CheckBox) v.findViewById(R.id.nombre);
                if (nombre.isChecked()){
                    mBuscador.getCampos().get(indiceCampo).setQuetenga(true);
                    final MaterialBetterSpinner marcaACTV = (MaterialBetterSpinner) v.findViewById(R.id.marcaACV);
                    if (marcaACTV.getVisibility() == View.VISIBLE){
                        mBuscador.getCampos().get(indiceCampo).setMarcaSeleccionada(marcaACTV.getText().toString());
                    }

                    final LinearLayout tamanioLL = (LinearLayout) v.findViewById(R.id.tamanio);
                    final ImageView smallSize = (ImageView)  v.findViewById(R.id.sizesmall);
                    final ImageView  normalSize= (ImageView)  v.findViewById(R.id.sizenormal);
                    final ImageView largeSize = (ImageView)  v.findViewById(R.id.sizelarge);
                    if (tamanioLL.getVisibility() == View.VISIBLE){
                        ArrayList<CampoOpinionTamanio> tamanios = new ArrayList<>();
                        if (smallSize.getAlpha() == 1.0f){
                            mBuscador.getCampos().get(indiceCampo).setTamanioSeleccionada(1);
                        }else if (normalSize.getAlpha() == 1.0f){
                            mBuscador.getCampos().get(indiceCampo).setTamanioSeleccionada(2);
                        }else if (largeSize.getAlpha() == 1.0f){
                            mBuscador.getCampos().get(indiceCampo).setTamanioSeleccionada(3);
                        }

                    }
                }else{
                    mBuscador.getCampos().get(indiceCampo).setQuetenga(false);
                }
            }
        }


        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);
        long mUUID = PreferencesManager.getInstance().getValue(Constants.PREF_UUID);
        try{
            Call<ArrayList<Bar>> call = restClient.getBares(mUUID,
                    mBuscador.isConOpiniones(),
                    mBuscador.getLocalidadSeleccionada(),
                    (mBuscador.getTipoSeleccionado() != null ? mBuscador.getTipoSeleccionado().getId(): 0),
                    "","", mBuscador.camposToJSON()
                    ,mBuscador.getLatitud(),mBuscador.getLongitud());

            call.enqueue(new Callback<ArrayList<Bar>>() {
                @Override
                public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {

                    mProgressBarContainer.setVisibility(View.GONE);

                    switch (response.code()) {
                        case 200:
                            ArrayList<Bar> bares = response.body();
                            if (bares.size() == 0){
                                AlertUtils.errorDialog(getContext(), getString(R.string.filtro_sinresultados), new AlertUtils.OnErrorDialog() {
                                    @Override
                                    public void run() {}
                                }).show();
                            }else{
                                mFilterListener.onClickFilterButton(mBuscador,bares);
                            }
                            break;

                        default:
                            AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                                @Override
                                public void run() {
                                    //TODO finish();
                                }
                            }).show();
                            break;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Bar>> call, Throwable t) {
                    Log.e("error", t.toString());
                    mProgressBarContainer.setVisibility(View.GONE);
                    AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                        @Override
                        public void run() {
                            //TODO finish();
                        }
                    }).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                @Override
                public void run() {
                    //TODO finish();
                }
            }).show();
        }

    }

    public interface OnFilterButtonClick {
        void onClickFilterButton(Buscador buscador, ArrayList<Bar> bares);
    }

    public interface OnCancelButtonClick {
        void onClickCancelButton();
    }
}
