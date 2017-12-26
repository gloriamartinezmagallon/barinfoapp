package barinfo.navdev.barinfo.Utils;


import org.json.JSONArray;

import java.util.ArrayList;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.Clases.Buscador;
import barinfo.navdev.barinfo.Clases.Opinion;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestClient {

    public static final String URL_BASE = "http://192.168.1.32:88/barinfo/public/api/";

    @GET("buscador")
    Call<Buscador> getBuscador();


    @GET("barinfo/{id}")
    Call<Bar> getBarInfo(@Path("id") int id);

    @POST("addopinion")
    Call<Bar> addOpinion(@Body Opinion opinion);

    @POST("bares")
    @FormUrlEncoded
    Call<ArrayList<Bar>> getBares(@Field("deviceId") long deviceId,
                                  @Field("Latitud") double Latitud,
                                  @Field("Longitud") double Longitud);

    @POST("bares")
    @FormUrlEncoded
    Call<ArrayList<Bar>> getBares(@Field("deviceId") long deviceId,
                                  @Field("conOpiniones") boolean conOpiniones,
                                  @Field("NombreLocalidad") String NombreLocalidad,
                                  @Field("Tipo") int Tipo,
                                  @Field("Especialidad") String Especialidad,
                                  @Field("DescripZona") String DescripZona,
                                  @Field("Campo") JSONArray Campo,
                                  @Field("Latitud") double Latitud,
                                  @Field("Longitud") double Longitud);

}
