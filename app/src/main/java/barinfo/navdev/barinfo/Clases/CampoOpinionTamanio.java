package barinfo.navdev.barinfo.Clases;

import java.io.Serializable;

public class CampoOpinionTamanio implements Serializable {

    int id;
    int campo_opinion_id;
    String tamanio;

    public CampoOpinionTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCampo_opinion_id() {
        return campo_opinion_id;
    }

    public void setCampo_opinion_id(int campo_opinion_id) {
        this.campo_opinion_id = campo_opinion_id;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }
}
