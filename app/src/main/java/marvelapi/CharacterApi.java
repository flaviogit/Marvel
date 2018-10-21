package marvelapi;


import model.Characters;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lillo on 21/10/2018.
 */

public interface CharacterApi {
    @GET("characters") Call<MarvelResponse<Characters>> getCharacters();

    @GET("characters") Call<MarvelResponse<Characters>> getCharacters(
            @Query("limit") String limit, @Query("offset") String offset, @Query("nameStartsWith") String nameStartsWith);

    @GET("characters") Call<MarvelResponse<Characters>> getCharacters(
            @Query("limit") String limit, @Query("offset") String offset);
}
