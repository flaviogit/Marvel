package marvelapi;

import model.Comics;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Lillo on 21/10/2018.
 */

public interface ComicApi {
    @GET("characters/{id}/comics")
    Call<MarvelResponse<Comics>> getCharacterComics(
            @Path("id") String characterId);

    @GET("characters/{id}/comics")
    Call<MarvelResponse<Comics>> getCharacterComics(@Path("id") String characterId, @Query("limit") String limit, @Query("offset") String offset);
}
