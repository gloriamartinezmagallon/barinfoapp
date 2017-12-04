package barinfo.navdev.barinfo;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Campo;
import barinfo.navdev.barinfo.Utils.AlertUtils;
import barinfo.navdev.barinfo.Utils.Constants;
import barinfo.navdev.barinfo.Utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilterActivity extends AppCompatActivity{

    MaterialBetterSpinner tipoACTV, localidadACTV, especialidadACTV;//, zonaACTV;
    Switch opinionesSV;
    LinearLayout camposLL,rootView;
    Buscador buscador;
    ArrayList<Bar> bares;

    Button buscar;
    HashMap<Integer,View> camposViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setupActionBar();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        buscador = (Buscador) bundle.getSerializable(Constants.EXTRA_BUSCADOR_INIT);

        rootView = (LinearLayout) findViewById(R.id.rootView);

        buscar = (Button) findViewById(R.id.buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar();
            }
        });

        setupFormulario();
    }

    private void buscar(){
        //COGEMOS LOS VALORES DEL FORMULARIO

        buscador.setConOpiniones(opinionesSV.isChecked());

        for(Campo c: buscador.getCampos()){
            View child = camposViews.get(c.getId());
            if (child != null){
                if (c.getIndicarmarca()==1){
                    MaterialBetterSpinner marcaACTV = (MaterialBetterSpinner) child.findViewById(R.id.marcaACV);
                    c.setMarcaSeleccionada(marcaACTV.getText().toString());
                }

                if (c.getIndicartamanio()==1){
                    MaterialBetterSpinner tamanioACTV = (MaterialBetterSpinner) child.findViewById(R.id.tamanio);
                    c.setTamanioSeleccionada(c.getIdTamanio(tamanioACTV.getText().toString()));
                }
            }
        }

        //LLAMAMOS AL SERVICIO WEB
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestClient.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);
        try{
            Call<ArrayList<Bar>> call = restClient.getBares(buscador.isConOpiniones(),
            "",
            "",
            "",
            "",
            buscador.camposToJSON(),
            buscador.getLatitud(),
            buscador.getLongitud());

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
                    AlertUtils.errorDialog(FilterActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            AlertUtils.errorDialog(FilterActivity.this, getString(R.string.error_initbuscador), new AlertUtils.OnErrorDialog() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

    }

    private void cargarMainActivity(){
        if (buscador != null && bares != null){
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
            bundle.putSerializable(Constants.EXTRA_BARES, bares);
            intent.putExtras(bundle);

            startActivity(intent);
            finish();
        }
    }


    private void setupFormulario(){

        opinionesSV = (Switch) findViewById(R.id.opinionesSwichView);
        if (buscador.isConOpiniones()){
            opinionesSV.setChecked(true);
        }

        int layoutItemId = android.R.layout.simple_spinner_dropdown_item;
        camposViews = new HashMap<>();
        camposLL = (LinearLayout) findViewById(R.id.campos);
        for(Campo c: buscador.getCampos()){
            if (c.getNumopiniones() == 0){
                camposViews.put(c.getId(),null);
                continue;
            }
            View child = getLayoutInflater().inflate(R.layout.item_filtercampo, null);
            TextView nombre = (TextView) child.findViewById(R.id.nombre);
            nombre.setText(c.getNombre());
            TextInputLayout marca = (TextInputLayout) child.findViewById(R.id.marcaACV_id);
            MaterialBetterSpinner marcaACTV = (MaterialBetterSpinner) child.findViewById(R.id.marcaACV);
            if (c.getIndicarmarca()==0){
                marca.setVisibility(View.GONE);
            }else{
                marcaACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, c.getMarcas()));
            }

            TextInputLayout tamanio = (TextInputLayout) child.findViewById(R.id.tamanio_id);
            MaterialBetterSpinner tamanioACTV = (MaterialBetterSpinner) child.findViewById(R.id.tamanio);
            if (c.getIndicartamanio()==0){
                tamanio.setVisibility(View.GONE);
            }else{
                ArrayList<String> tamaniosString = new ArrayList<>();
                for(String tam: c.getTamanios()) tamaniosString.add(Campo.getNombreTamanio(tam));
                tamanioACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, tamaniosString));
            }

            camposLL.addView(child);
            camposViews.put(c.getId(),child);
        }

        localidadACTV = (MaterialBetterSpinner) findViewById(R.id.localidadACView);
        localidadACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, buscador.getLocalidades()));
        tipoACTV = (MaterialBetterSpinner) findViewById(R.id.tipoACView);
        tipoACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, buscador.getTipos()));
        especialidadACTV = (MaterialBetterSpinner) findViewById(R.id.especialidadACView);
        especialidadACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, buscador.getEspecialidades()));
        /*zonaACTV = (MaterialBetterSpinner) findViewById(R.id.zonaACView);
        zonaACTV.setAdapter(new ArrayAdapter<>(this, layoutItemId, buscador.getZonas()));*/
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_actionbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            volverSinBuscar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        volverSinBuscar();
    }

    private void volverSinBuscar(){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootView.clearFocus();

    }
}
