package barinfo.navdev.barinfo.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class CampoOpinion implements Serializable {

    int id;
    int bar_id;
    int campo_id;
    int tiene;

    Campo campo;
    ArrayList<CampoOpinionMarca> marcas;
    ArrayList<CampoOpinionTamanio> tamanios;

    public CampoOpinion(int bar_id, int campo_id, int tiene) {
        this.bar_id = bar_id;
        this.campo_id = campo_id;
        this.tiene = tiene;
    }

    public int geId() {
        return id;
    }

    public int getBar_id() {
        return bar_id;
    }

    public void setBar_id(int bar_id) {
        this.bar_id = bar_id;
    }

    public int getCampo_id() {
        return campo_id;
    }

    public void setCampo_id(int campo_id) {
        this.campo_id = campo_id;
    }

    public int getTiene() {
        return tiene;
    }

    public void setTiene(int tiene) {
        this.tiene = tiene;
    }

    public ArrayList<CampoOpinionMarca> getMarcas() {
        return marcas;
    }

    public void setMarcas(ArrayList<CampoOpinionMarca> marcas) {
        this.marcas = marcas;
    }

    public ArrayList<CampoOpinionTamanio> getTamanios() {
        return tamanios;
    }

    public void setTamanios(ArrayList<CampoOpinionTamanio> tamanios) {
        this.tamanios = tamanios;
    }

    public Campo getCampo() {
        return campo;
    }
}
