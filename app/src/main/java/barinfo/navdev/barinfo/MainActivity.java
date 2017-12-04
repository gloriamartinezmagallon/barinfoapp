package barinfo.navdev.barinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Adapters.BarAdapter;
import barinfo.navdev.barinfo.Adapters.TipoAdapter;
import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Utils.Constants;

public class MainActivity extends AppCompatActivity {

    private RecyclerView lista, tipos;

    Buscador buscador;
    ArrayList<Bar> bares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        buscador = (Buscador) bundle.getSerializable(Constants.EXTRA_BUSCADOR_INIT);
        bares = (ArrayList<Bar>) bundle.getSerializable(Constants.EXTRA_BARES);


        tipos = (RecyclerView) findViewById(R.id.tiposResturantes);
        TipoAdapter adapter = new TipoAdapter( buscador.getTipos(),this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        tipos.setLayoutManager(gridLayoutManager);
        tipos.setAdapter(adapter);

        lista = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lista.setLayoutManager(llm);

        cargarLista();
    }

    private void cargarLista() {

        BarAdapter adapter = new BarAdapter(bares, this, new BarAdapter.OnItemClick() {
            @Override
            public void onClick(Bar bar) {
                irAFicha(bar);
            }
        });
        if (lista != null)
            lista.setAdapter(adapter);
    }

   private void filtrar() {
        Intent intent = new Intent(this, FilterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_BUSCADOR_INIT, buscador);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                filtrar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void irAFicha(Bar b){
        Intent intent = new Intent(this, FichaBarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_BAR, b);
        intent.putExtras(bundle);

        startActivity(intent);


    }


}
