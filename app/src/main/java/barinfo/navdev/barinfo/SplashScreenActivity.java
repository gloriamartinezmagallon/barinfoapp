package barinfo.navdev.barinfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.PermissionUtils;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreenActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    Buscador buscador;
    ArrayList<Bar> bares;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    boolean calledInitbares = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,android.Manifest.permission.ACCESS_FINE_LOCATION,getString(R.string.permission_rationale_location)
                    , true);
        }else{
            initBuscador();
        }
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
                        calledInitbares = false;
                        if (!calledInitbares){
                            calledInitbares = true;
                            initBares();
                        }
                        /*SingleShotLocationProvider.LocationCallback locationCallback = new SingleShotLocationProvider.LocationCallback() {
                            @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                buscador.setLatitud(location.latitude);
                                buscador.setLongitud(location.longitude);

                            }
                        };
                        SingleShotLocationProvider.requestSingleUpdate(SplashScreenActivity.this,locationCallback);*/
                        break;
                    default:
                        AlertUtils.errorDialog(SplashScreenActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onFailure(Call<Buscador> call, Throwable t) {
                Log.e("error", t.toString());
                AlertUtils.errorDialog(SplashScreenActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
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

        Call<ArrayList<Bar>> call = restClient.getBares(buscador.getLatitud(),buscador.getLongitud());

        call.enqueue(new Callback<ArrayList<Bar>>() {
            @Override
            public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {
                switch (response.code()) {
                    case 200:
                        bares = response.body();
                        cargarMainActivity();
                        break;

                    default:

                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bar>> call, Throwable t) {
                Log.e("error", t.toString());
                AlertUtils.errorDialog(SplashScreenActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                    @Override
                    public void run() {
                        finish();
                    }
                }).show();
            }
        });
    }

    private void cargarMainActivity(){
        if (buscador != null && bares != null){
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
            bundle.putSerializable(Constants.EXTRA_BARES, bares);
            intent.putExtras(bundle);

            startActivity(intent);
            //finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(true).show(getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // enableMyLocation();
        initBuscador();
    }
}
