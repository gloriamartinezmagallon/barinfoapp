package barinfo.navdev.barinfo.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import barinfo.navdev.barinfo.R;
import barinfo.navdev.barinfo.Utils.Constants;


public class AddNuevoCampoDialog extends android.support.v4.app.DialogFragment {

    long mPreferences_uuid;
    static AddNuevoCampoDialog newInstance(long preferences_uuid) {
        AddNuevoCampoDialog f = new AddNuevoCampoDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong(Constants.PREF_UUID, preferences_uuid);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences_uuid = getArguments().getLong(Constants.PREF_UUID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_addnuevocampodialog, container, false);
        final EditText nombre = (EditText) v.findViewById(R.id.nombre);

        final CheckBox indicarmarca = (CheckBox) v.findViewById(R.id.indicarmarca);
        final CheckBox indicartamanio = (CheckBox) v.findViewById(R.id.indicartamanio);

        Button guardarBtn = (Button) v.findViewById(R.id.guardarBtn);
        guardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nombre.getText().toString().length() == 0){
                    Toast.makeText(getContext(),"Introduzca un nombre",Toast.LENGTH_SHORT).show();
                    nombre.requestFocus();

                    guardarNuevoCampo(nombre.getText().toString(), indicarmarca.isChecked(), indicartamanio.isChecked());
                }
            }
        });

        return v;
    }

    private  void guardarNuevoCampo(String nombre, boolean indicarMarca, boolean indicarTamanio){

    }
}
