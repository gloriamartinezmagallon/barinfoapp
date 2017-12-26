package barinfo.navdev.barinfo.Clases;


import java.io.Serializable;

public class Tipo  implements Serializable {
    int id;
    String nombre;

    public Tipo() {
        nombre = "";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return this.getNombre();
    }
}
