package barinfo.navdev.barinfo.Clases;


import java.io.Serializable;
import java.util.ArrayList;

public class Bar implements Serializable{
    int id;
    int codrecursoGN;
    String nombre;
    String nombreLocalidad;
    String tipo;
    String especialidad;
    String imgFicheroGN;
    String descripZona;
    String direccion;
    double latitud;
    double longitud;
    int opiniones_count;
    String deviceid;

    double distance;

    ArrayList<Opinion> opiniones;

    public ArrayList<Opinion> getOpiniones() {
        return opiniones;
    }




    public int getId() {
        return id;
    }

    public int getCodrecursoGN() {
        return codrecursoGN;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreLocalidad() {
        return nombreLocalidad;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getImgFicheroGN() {
        return imgFicheroGN;
    }

    public String getDescripZona() {
        return descripZona;
    }
    public String getDireccion() {
        return direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public double getDistance() {
        return distance;
    }

    public String getDistanceWithFormat(){
        if (getDistance() < 1){
            return (String.format("%.0f", getDistance()*100)+"0 m");
        }else if (getDistance() < 10){
            return (String.format("%.2f", getDistance())+"0 km");
        }else{
            return (String.format("%.0f", getDistance())+" km");
        }
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNombreLocalidad(String nombreLocalidad) {
        this.nombreLocalidad = nombreLocalidad;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getOpiniones_count() {
        return opiniones_count;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
