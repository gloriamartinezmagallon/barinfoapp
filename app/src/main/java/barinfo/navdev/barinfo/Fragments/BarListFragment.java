package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Adapters.BarAdapter;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;

public class BarListFragment extends BaseFragment {


    public static final String TAG = "BarListFragment";
    private OnListFragmentInteractionListener mListener;

    ArrayList<Bar> mBares;

    public BarListFragment() { }

    public static BarListFragment newInstance(ArrayList<Bar> bares) {
        BarListFragment fragment = new BarListFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_BARES, bares);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBares = (ArrayList<Bar>) getArguments().getSerializable(Constants.EXTRA_BARES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_list_item, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new BarAdapter(mBares, getActivity(), new BarAdapter.OnItemClick() {
            @Override
            public void onClick(Bar bar) {
                mListener.onListBarSelected(bar);
            }
        }));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListBarSelected(Bar bar);
    }
}
