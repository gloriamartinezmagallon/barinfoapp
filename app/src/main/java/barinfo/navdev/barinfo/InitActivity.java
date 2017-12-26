package barinfo.navdev.barinfo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Opinion;
import barinfo.navdev.barinfo.Fragments.AddOpinionFragment;
import barinfo.navdev.barinfo.Fragments.BarDetailsFragment;
import barinfo.navdev.barinfo.Fragments.BarListFragment;
import barinfo.navdev.barinfo.Fragments.FilterFragment;
import barinfo.navdev.barinfo.Fragments.LoadingFragment;
import barinfo.navdev.barinfo.Fragments.MainFragment;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitActivity extends AppCompatActivity  implements LoadingFragment.OnLoadFinishListener,
        MainFragment.OnBarIsSelected, MainFragment.OnFilterButtonClick, BarDetailsFragment.OnAddOpinion,
        AddOpinionFragment.OnSaveOpinion, FilterFragment.OnFilterButtonClick, FilterFragment.OnCancelButtonClick,
        BarListFragment.OnListFragmentInteractionListener{

    Buscador mBuscador;
    Bar mBarSelected;
    ArrayList<Bar> mBares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);



        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            LoadingFragment firstFragment = new LoadingFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    public void onLoadFinish(Buscador buscador, ArrayList<Bar> bares) {
        mBuscador = buscador;
        mBares = bares;

        MainFragment newFragment = MainFragment.newInstance(mBares,mBuscador);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, MainFragment.TAG);
        transaction.commit();
    }

    @Override
    public void onBarIsSelected(Bar bar) {
        final ProgressDialog progressDialog = AlertUtils.progressDialog(this,"Descargando información completa...");
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<Bar> call = restClient.getBarInfo(bar.getId());

        call.enqueue(new Callback<Bar>() {
            @Override
            public void onResponse(Call<Bar> call, Response<Bar> response) {
                progressDialog.dismiss();
                switch (response.code()) {
                    case 200:
                        mBarSelected = response.body();

                        BarDetailsFragment newFragment = BarDetailsFragment.newInstance(mBarSelected);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, newFragment,BarDetailsFragment.TAG);
                        transaction.addToBackStack(MainFragment.TAG);

                        transaction.commit();
                        break;

                    default:
                        AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Bar> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("error", t.toString());
                AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        //TODO finish();
                    }
                }).show();
            }
        });
    }

    @Override
    public void onAddOpinion(Opinion opinion) {
        AddOpinionFragment newFragment = AddOpinionFragment.newInstance(mBarSelected, opinion, mBuscador);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, AddOpinionFragment.TAG);
        transaction.addToBackStack(BarDetailsFragment.TAG);
        transaction.commit();
    }

    @Override
    public void onSave(Opinion opinion) {
        final ProgressDialog progressDialog = AlertUtils.progressDialog(this,"Guardando opinión");
        progressDialog.show();
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<Bar> call = restClient.addOpinion(opinion);

        call.enqueue(new Callback<Bar>() {
            @Override
            public void onResponse(Call<Bar> call, Response<Bar> response) {
                progressDialog.dismiss();
                switch (response.code()) {
                    case 200:
                        mBarSelected = response.body();
                        BarDetailsFragment barDetailsFragment = (BarDetailsFragment) getSupportFragmentManager().findFragmentByTag(BarDetailsFragment.TAG);
                        barDetailsFragment.setBar(mBarSelected);
                        getSupportFragmentManager().popBackStack();
                        break;

                    default:
                        AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Bar> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("error", t.toString());
                AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        //TODO finish();
                    }
                }).show();
            }
        });
    }

    @Override
    public void onClickFilterButton(Buscador buscador, ArrayList<Bar> bares) {
        mBuscador = buscador;
        mBares = bares;

        BarListFragment newFragment = BarListFragment.newInstance(mBares);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment,BarListFragment.TAG);
        transaction.commit();
    }

    @Override
    public void onClickCancelButton() {

    }

    @Override
    public void onFilterButtonClick() {
        FilterFragment newFragment = FilterFragment.newInstance(mBuscador);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment,FilterFragment.TAG);
        transaction.addToBackStack(MainFragment.TAG);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onListBarSelected(Bar bar) {
        final ProgressDialog progressDialog = AlertUtils.progressDialog(this,"Descargando información completa...");
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);

        Call<Bar> call = restClient.getBarInfo(bar.getId());

        call.enqueue(new Callback<Bar>() {
            @Override
            public void onResponse(Call<Bar> call, Response<Bar> response) {
                progressDialog.dismiss();
                switch (response.code()) {
                    case 200:
                        mBarSelected = response.body();

                        BarDetailsFragment newFragment = BarDetailsFragment.newInstance(mBarSelected);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, newFragment,BarDetailsFragment.TAG);
                        transaction.addToBackStack(BarListFragment.TAG);

                        transaction.commit();
                        break;

                    default:
                        AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Bar> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("error", t.toString());
                AlertUtils.errorDialog(InitActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        //TODO finish();
                    }
                }).show();
            }
        });
    }
}
