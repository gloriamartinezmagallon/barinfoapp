package barinfo.navdev.barinfo.Clases;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Campo implements Serializable{

    int id;
    String nombre;
    int indicarmarca;
    int indicartamanio;
    int numopiniones;
    ArrayList<String> marcas;
    ArrayList<String> tamanios;

    String marcaSeleccionada;
    int tamanioSeleccionada;

    boolean quetenga;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIndicarmarca() {
        return indicarmarca;
    }

    public void setIndicarmarca(int indicarmarca) {
        this.indicarmarca = indicarmarca;
    }

    public int getIndicartamanio() {
        return indicartamanio;
    }

    public void setIndicartamanio(int indicartamanio) {
        this.indicartamanio = indicartamanio;
    }

    public int getNumopiniones() {
        return numopiniones;
    }

    public void setNumopiniones(int numopiniones) {
        this.numopiniones = numopiniones;
    }

    public ArrayList<String> getMarcas() {
        return marcas;
    }

    public void setMarcas(ArrayList<String> marcas) {
        this.marcas = marcas;
    }

    public String getMarcaSeleccionada() {
        return marcaSeleccionada;
    }

    public void setMarcaSeleccionada(String marcaSeleccionada) {
        this.marcaSeleccionada = marcaSeleccionada;
    }

    public ArrayList<String> getTamanios() {
        return tamanios;
    }

    public void setTamanios(ArrayList<String> tamanios) {
        this.tamanios = tamanios;
    }

    public static String getNombreTamanio(String id){
        switch (id){
            case "1":
                return "Pequeño";
            case "2":
                return "Normal";
            case "3":
                return "Grande";
        }

        return "";
    }

    public static int getIdTamanio(String nombre){
        switch (nombre){
            case "Pequeño":
                return 1;
            case "Normal":
                return 2;
            case "Grande":
                return 3;
        }

        return 0;
    }

    public int getTamanioSeleccionada() {
        return tamanioSeleccionada;
    }

    public void setTamanioSeleccionada(int tamanioSeleccionada) {
        this.tamanioSeleccionada = tamanioSeleccionada;
    }

    public int getTiene(){
        return (tamanioSeleccionada > 0 || (marcaSeleccionada != null && marcaSeleccionada.length() > 0) ? 1 : 0);
    }

    public static ArrayList<String> getDefaultTamanios(){
        return new ArrayList<String>(Arrays.asList("Pequeño","Normal","Grande"));
    }

    public boolean isQuetenga() {
        return quetenga;
    }

    public void setQuetenga(boolean quetenga) {
        this.quetenga = quetenga;
    }
}
