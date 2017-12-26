package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Adapters.BarAdapter;
import barinfo.navdev.barinfo.Adapters.TipoAdapter;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;

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
        });
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

        TipoAdapter adapter = new TipoAdapter( mBuscador.getTipos(),getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        tipos.setLayoutManager(gridLayoutManager);
        tipos.setAdapter(adapter);


        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        lista.setLayoutManager(llm);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_filter:
                        if (mFilterButtonClick != null){
                            mFilterButtonClick.onFilterButtonClick();
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
        void onFilterButtonClick();
    }
}
