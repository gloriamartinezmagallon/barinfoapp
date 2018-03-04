package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Adapters.BarAdapter;
import barinfo.navdev.barinfo.Adapters.TipoAdapter;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Tipo;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";

    private ArrayList<Bar> mBares;
    private Buscador mBuscador;

    private RecyclerView lista, tipos;

    private OnBarIsSelected mBarSelectedListener;
    private OnFilterButtonClick mFilterButtonClick;

    public MainFragment() { }


    public static MainFragment newInstance(ArrayList<Bar> bares, Buscador buscador) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BARES, bares);
        args.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBares = (ArrayList<Bar>) getArguments().getSerializable(Constants.EXTRA_BARES);
            mBuscador = (Buscador) getArguments().getSerializable(Constants.EXTRA_BUSCADOR_INIT);
        }else{
            throw new ClassCastException(getActivity().toString()
                    + " no envía bien los params");
        }
    }

    private void cargarLista() {

        BarAdapter adapter = new BarAdapter(mBares, getActivity(), new BarAdapter.OnItemClick() {
            @Override
            public void onClick(Bar bar) {
                onBarSelected(bar);
            }
        },true);
        if (lista != null)
            lista.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        tipos = (RecyclerView) v.findViewById(R.id.tiposResturantes);
        lista = (RecyclerView) v.findViewById(R.id.recyclerView);

        TipoAdapter adapter = new TipoAdapter(mBuscador.getTipos(), getActivity(), new TipoAdapter.OnItemClick() {
            @Override
            public void onClick(Tipo tipo) {
                mBuscador.setTipoSeleccionado(tipo);
                mFilterButtonClick.onFilterButtonClick(mBuscador);
            }
        });
        tipos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        tipos.setAdapter(adapter);


        lista.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_filter:
                        if (mFilterButtonClick != null){
                            mFilterButtonClick.onFilterButtonClick(mBuscador);
                        }
                        return true;

                    default:
                        return true;
                }
            }
        });

        cargarLista();

        return v;
    }

    public void onBarSelected(Bar bar) {
        if (mBarSelectedListener != null) {
            mBarSelectedListener.onBarIsSelected(bar);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBarIsSelected) {
            mBarSelectedListener = (OnBarIsSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBarIsSelected");
        }

        if (context instanceof OnFilterButtonClick) {
            mFilterButtonClick = (OnFilterButtonClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterButtonClick");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBarSelectedListener = null;
        mFilterButtonClick = null;
    }

    public interface OnBarIsSelected {
        void onBarIsSelected(Bar bar);
    }

    public interface OnFilterButtonClick {
        void onFilterButtonClick(Buscador buscador);
    }

    public void refreshBares(){
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<ArrayList<Bar>> call = restClient.getBaresMasPopulares();

        call.enqueue(new Callback<ArrayList<Bar>>() {
            @Override
            public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {
                switch (response.code()) {
                    case 200:
                        mBares = response.body();
                        cargarLista();
                        break;

                    default:
                        AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                                getActivity().finish();
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
                        getActivity().finish();
                    }
                }).show();
            }
        });
    }
}
