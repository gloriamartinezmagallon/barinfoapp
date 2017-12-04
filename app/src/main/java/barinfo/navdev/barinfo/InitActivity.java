package barinfo.navdev.barinfo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Fragments.AddOpinionFragment;
import barinfo.navdev.barinfo.Fragments.BarDetailsFragment;
import barinfo.navdev.barinfo.Fragments.LoadingFragment;
import barinfo.navdev.barinfo.Fragments.MainFragment;

public class InitActivity extends AppCompatActivity  implements LoadingFragment.OnLoadFinishListener, MainFragment.OnBarIsSelected, BarDetailsFragment.OnAddOpinion, AddOpinionFragment.OnSaveOpinion{

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

        // Create fragment and give it an argument specifying the article it should show
        MainFragment newFragment = MainFragment.newInstance(mBares,mBuscador);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        getFragmentManager().popBackStackImmediate();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(MainFragment.TAG);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBarIsSelected(Bar bar) {

        mBarSelected = bar;
        // Create fragment and give it an argument specifying the article it should show
        BarDetailsFragment newFragment = BarDetailsFragment.newInstance(mBarSelected);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(BarDetailsFragment.TAG);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onAddOpinion() {
        AddOpinionFragment newFragment = AddOpinionFragment.newInstance(mBarSelected, mBuscador);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(AddOpinionFragment.TAG);

        transaction.commit();
    }

    @Override
    public void onSave(Uri uri) {

    }
}
