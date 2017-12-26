package barinfo.navdev.barinfo.Clases;


import java.io.Serializable;
import java.util.ArrayList;

public class Opinion implements Serializable {

    int id;
    int bar_id;
    int calidad;
    int precio;
    String texto;
    int tipo_id;
    String deviceid;
    Tipo tipo;
    ArrayList<CampoOpinion> camposopiniones;

    ArrayList<CampoOpinion> campos;

    public Opinion() {
        campos = new ArrayList<>();
        camposopiniones = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBar_id() {
        return bar_id;
    }

    public void setBar_id(int bar_id) {
        this.bar_id = bar_id;
    }

    public int getCalidad() {
        return calidad;
    }

    public void setCalidad(int calidad) {
        this.calidad = calidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getTipo_id() {
        return tipo_id;
    }

    public void setTipo_id(int tipo_id) {
        this.tipo_id = tipo_id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public ArrayList<CampoOpinion> getCampos() {
        return campos;
    }

    public void setCampos(ArrayList<CampoOpinion> campos) {
        this.campos = campos;
    }

    public void addCampoOpinion(CampoOpinion campoOpinion){
        this.campos.add(campoOpinion);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public ArrayList<CampoOpinion> getCamposopiniones() {
        return camposopiniones;
    }
}
