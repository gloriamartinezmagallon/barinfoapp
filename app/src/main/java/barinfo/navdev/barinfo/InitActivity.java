package barinfo.navdev.barinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Opinion;
import barinfo.navdev.barinfo.Fragments.AddOpinionFragment;
import barinfo.navdev.barinfo.Fragments.BarDetailsFragment;
import barinfo.navdev.barinfo.Fragments.FilterFragment;
import barinfo.navdev.barinfo.Fragments.LoadingFragment;
import barinfo.navdev.barinfo.Fragments.MainFragment;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.PreferencesManager;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InitActivity extends AppCompatActivity  implements LoadingFragment.OnLoadFinishListener,
        MainFragment.OnBarIsSelected, MainFragment.OnFilterButtonClick, BarDetailsFragment.OnAddOpinion,
        AddOpinionFragment.OnSaveOpinion, FilterFragment.OnListFragmentInteractionListener, FilterFragment.OnButtonAddBarListener{

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
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
    public void onLoadFinishBuscador(Buscador buscador) {
        mBuscador = buscador;
    }

    @Override
    public void onLoadFinishBares(ArrayList<Bar> bares) {
        mBares = bares;

        MainFragment newFragment = MainFragment.newInstance(mBares);
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
                        Toast.makeText(InitActivity.this,"Opinión guardada correctamente",Toast.LENGTH_LONG).show();
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
    public void onFilterButtonClick() {
        if (mBuscador != null){
            FilterFragment newFragment = FilterFragment.newInstance(mBuscador);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment,FilterFragment.TAG);
            transaction.addToBackStack(MainFragment.TAG);
            transaction.commit();
        }else{
            Toast.makeText(this,R.string.pendiente_initbuscador,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f.getTag().equalsIgnoreCase(FilterFragment.TAG)){
            ((FilterFragment) f).filtrar();
        }else if (f.getTag().equalsIgnoreCase(MainFragment.TAG)){
            ((MainFragment) f).refreshBares();
        }
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
                        transaction.addToBackStack(FilterFragment.TAG);

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
    public void onAddBar() {
        /*AddBarFragment newFragment = AddBarFragment.newInstance(mBuscador);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment,AddBarFragment.TAG);
        transaction.addToBackStack(FilterFragment.TAG);
        transaction.commit();*/

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .setCountry("ES")
                .build();

        try {
            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(41.9098937,-2.4999443),new LatLng(43.3149461,-0.7233368));
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .setBoundsBias(latLngBounds)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Bar nuevoBar = new Bar();
                nuevoBar.setNombre(place.getName().toString());
                nuevoBar.setDireccion(place.getAddress().toString());
                nuevoBar.setLongitud(place.getLatLng().longitude);
                nuevoBar.setLatitud(place.getLatLng().latitude);
                nuevoBar.setDeviceid(PreferencesManager.getInstance().getValue(Constants.PREF_UUID)+"");
                nuevoBar.setGoogle_phonenumber((String)place.getPhoneNumber());
                nuevoBar.setGoogle_website(place.getWebsiteUri().toString());
                nuevoBar.setGoogle_pricelevel(place.getPriceLevel());
                nuevoBar.setGoogle_rating(place.getRating());


                final ProgressDialog progressDialog = AlertUtils.progressDialog(this,"Guardando opinión");
                progressDialog.show();
                Gson gson = new GsonBuilder()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RestClient.URL_BASE)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RestClient restClient = retrofit.create(RestClient.class);

                Call<Bar> call = restClient.addBar(nuevoBar);

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



                Log.e("InitActivity", "Place: nombre = " + place.getName().toString());



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("InitActivity", "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f.getTag().equalsIgnoreCase(FilterFragment.TAG)){
            ((FilterFragment) f).comprobarPermisoUbicacion();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
