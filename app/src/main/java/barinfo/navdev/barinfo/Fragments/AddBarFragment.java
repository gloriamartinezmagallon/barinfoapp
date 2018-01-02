package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddBarFragment extends BaseFragment{

    public static final String TAG = "AddBarFragment";

    private Buscador mBuscador;
    private ArrayList<Bar> mBares;
    Bar nuevoBar;

    private OnSaveBarListener mListener;

    LinearLayout mProgressBarContainer;
    ScrollView mFormulario;
    MaterialAutoCompleteTextView mNombreACTV, mLocalidadACTV;

    public AddBarFragment() { }


    public static AddBarFragment newInstance(Buscador buscador) {
        AddBarFragment fragment = new AddBarFragment();
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
        View v =  inflater.inflate(R.layout.fragment_add_bar, container, false);

        mProgressBarContainer  = (LinearLayout) v.findViewById(R.id.progressBarContainer);
        mFormulario  = (ScrollView) v.findViewById(R.id.formulario);
        mProgressBarContainer.setVisibility(View.VISIBLE);
        mFormulario.setVisibility(View.VISIBLE);

        mNombreACTV = (MaterialAutoCompleteTextView) v.findViewById(R.id.nombreACView);
        mLocalidadACTV = (MaterialAutoCompleteTextView) v.findViewById(R.id.localidadACView);

        mNombreACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nuevoBar = mBares.get(i);
                rellenarFormulario();
            }
        });
        mNombreACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nuevoBar = null;
                limpiarFormulario();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;
        mLocalidadACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, mBuscador.getLocalidades()));
        cargarListadoDeBares();

        final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                nuevoBar = new Bar();
                nuevoBar.setNombre(place.getName().toString());
                nuevoBar.setDireccion(place.getAddress().toString());
                nuevoBar.setLatitud(place.getLatLng().latitude);
                nuevoBar.setLongitud(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "onError: Status = " + status.toString());

                Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        final CardView containerFragment = (CardView) v.findViewById(R.id.containerFragment);
        containerFragment.setVisibility(View.GONE);

        Button buscar = (Button) v.findViewById(R.id.buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nuevoBar == null){
                    containerFragment.setVisibility(View.VISIBLE);
                    autocompleteFragment.setText(mNombreACTV.getText().toString()+" "+mLocalidadACTV.getText().toString());
                }else{
                    Log.d("LG","Nuevo bar"+nuevoBar.getNombre());
                }
            }
        });

        setupActionBar(v);

        return  v;
    }

    private  void  setupActionBar(View v){
        Toolbar toolbar= (Toolbar) v.findViewById(R.id.toolbar);

        toolbar.setTitle("Añadir nuevo bar");
        toolbar.inflateMenu(R.menu.menu_addbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        //TODO guardar

                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    private void cargarListadoDeBares(){
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<ArrayList<Bar>> call = restClient.getBaresNombre();

        call.enqueue(new Callback<ArrayList<Bar>>() {
            @Override
            public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {
                mProgressBarContainer.setVisibility(View.GONE);
                mFormulario.setVisibility(View.VISIBLE);
                switch (response.code()) {
                    case 200:
                        mBares = response.body();
                        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;
                        mNombreACTV.setAdapter(new ArrayAdapter<>(getActivity(), layoutItemId, mBares));
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
                AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        //TODO finish();
                    }
                }).show();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveBarListener) {
            mListener = (OnSaveBarListener) context;
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

    private void rellenarFormulario(){

    }

    private void limpiarFormulario(){

    }

    public interface OnSaveBarListener {
        void onSaveBar(Bar bar);
    }
}
