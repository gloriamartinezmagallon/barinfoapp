package barinfo.navdev.barinfo.Utils;


import org.json.JSONArray;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestClient {

    public static final String URL_BASE = "http://192.168.1.32:88/barinfo/public/api/";

    @GET("buscador")
    Call<Buscador> getBuscador();

    @POST("bares")
    @FormUrlEncoded
    Call<ArrayList<Bar>> getBares(@Field("Latitud") double Latitud, @Field("Longitud") double Longitud);

    @POST("bares")
    @FormUrlEncoded
    Call<ArrayList<Bar>> getBares(@Field("conOpiniones") boolean conOpiniones,
                                  @Field("NombreLocalidad") String NombreLocalidad,
                                  @Field("Tipo") String Tipo,
                                  @Field("Especialidad") String Especialidad,
                                  @Field("DescripZona") String DescripZona,
                                  @Field("Campo") JSONArray Campo,
                                  @Field("Latitud") double Latitud,
                                  @Field("Longitud") double Longitud);

}
