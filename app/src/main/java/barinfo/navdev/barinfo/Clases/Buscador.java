package barinfo.navdev.barinfo.Clases;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Buscador implements Serializable{

    boolean conOpiniones = true;
    Tipo tipoSeleccionado;
    String localidadSeleccionada;

    ArrayList<String> localidades;
    ArrayList<String> tiposgn;
    ArrayList<String> especialidades;
    ArrayList<String> zonas;
    ArrayList<Campo> campos;
    ArrayList<Tipo> tipos;

    double latitud;
    double longitud;

    public boolean isConOpiniones() {
        return conOpiniones;
    }

    public void setConOpiniones(boolean conOpiniones) {
        this.conOpiniones = conOpiniones;
    }

    public ArrayList<String> getLocalidades() {
        return localidades;
    }

    public ArrayList<String> getTiposgn() {
        return tiposgn;
    }

    public ArrayList<String> getEspecialidades() {
        return especialidades;
    }

    public ArrayList<String> getZonas() {
        return zonas;
    }

    public ArrayList<Campo> getCampos() {
        return campos;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }


    public ArrayList<Tipo> getTipos() {
        return tipos;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public JSONArray camposToJSON() throws Exception{
        JSONArray json = new JSONArray();
        for(Campo c: campos){
            JSONObject object = new JSONObject();
            object.put("id",c.getId());
            object.put("tiene",c.getTiene());
            object.put("marca",c.getMarcaSeleccionada() == null ? 0 : c.getMarcaSeleccionada());
            object.put("tamanio",c.getTamanioSeleccionada());
            json.put(object);
        }

        return json;
    }

    public Tipo getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(Tipo tipoSeleccionado) {
        this.tipoSeleccionado = tipoSeleccionado;
    }

    public String getLocalidadSeleccionada() {
        return localidadSeleccionada;
    }

    public void setLocalidadSeleccionada(String localidadSeleccionada) {
        this.localidadSeleccionada = localidadSeleccionada;
    }
}
