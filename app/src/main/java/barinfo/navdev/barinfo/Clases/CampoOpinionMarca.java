package barinfo.navdev.barinfo.Clases;

import java.io.Serializable;

public class CampoOpinionMarca implements Serializable {

    int id;
    int campo_opinion_id;
    String nombre;

    public CampoOpinionMarca(String nombre) {
        this.nombre = nombre;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
