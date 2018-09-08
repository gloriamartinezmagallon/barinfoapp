package barinfo.navdev.barinfo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
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

public class LoadingFragment extends BaseFragment {

    Buscador buscador;
    ArrayList<Bar> bares;

    long mUUID;

    private OnLoadFinishListener mCallback;
    public interface OnLoadFinishListener {
        void onLoadFinishBares(ArrayList<Bar> bares);
        void onLoadFinishBuscador(Buscador buscador);
    }

    public LoadingFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager.initializeInstance(getContext());

        mUUID = PreferencesManager.getInstance().getValue(Constants.PREF_UUID);
        if (mUUID == 0){
            mUUID = new Date().getTime();
            PreferencesManager.getInstance().setValue(Constants.PREF_UUID,mUUID);
        }

        initBares();
        initBuscador();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loading, container, false);

        return v;
    }

    private void initBuscador(){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<Buscador> call = restClient.getBuscador();

        call.enqueue(new Callback<Buscador>() {
            @Override
            public void onResponse(Call<Buscador> call, Response<Buscador> response) {
                Log.d("SSA","code "+response.code());
                switch (response.code()) {
                    case 200:
                        buscador = response.body();
                        mCallback.onLoadFinishBuscador(buscador);
                        break;
                    default:
                        AlertUtils.errorDialog(getContext(), getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                            getActivity().finish();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onFailure(Call<Buscador> call, Throwable t) {
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

    private void initBares(){
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
                        bares = response.body();
                        mCallback.onLoadFinishBares(bares);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnLoadFinishListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadFinishListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
