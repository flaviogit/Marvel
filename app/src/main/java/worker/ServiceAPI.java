package worker;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import marvelapi.AuthInterceptor;
import marvelapi.CharacterApi;
import marvelapi.ComicApi;
import marvelapi.MarvelResponse;
import marvelapi.TimeProvider;
import model.Character;
import model.Characters;
import model.Comic;
import model.Comics;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServiceAPI extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    /*private static final String ACTION_FOO = "com.retrofit.app.testretrofit.action.FOO";
    private static final String ACTION_BAZ = "com.retrofit.app.testretrofit.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.retrofit.app.testretrofit.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.retrofit.app.testretrofit.extra.PARAM2";


    public static final String FILEPATH = "path";
*/


    public static final String NOTIFICATION = "com.marvel.receiver";
    final String privateKey = "f2a80e641e2dcd43bf9fa9e1ee8385eb712ef962";
    final String publicKey = "12ab92182cc7a48bf1c8d21a2e1295df";
    private Response<MarvelResponse<Characters>> heroList;
    private Response<MarvelResponse<Comics>> comicList;
    public static final String CHARACTERLIST = "characterlist";
    public static final String CHARACTERGRID = "charactergrid";
    public static final String CHARACTERDETAIL = "characterdetail";

    public ServiceAPI() {
        super("ServiceAPI");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    /*public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServiceAPI.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    /*public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServiceAPI.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            /*if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
*/
            if(CHARACTERLIST.equals(action))
            {
                String limit = intent.getExtras().getString("limit");
                String offset = intent.getExtras().getString("offset");
                String nameStartsWith = intent.getExtras().getString("nameStartsWith");

                if(limit == null || limit.isEmpty())
                    limit = "20";
                if(offset == null || offset.isEmpty())
                    offset="0";
                if(nameStartsWith == null || nameStartsWith.isEmpty())
                    nameStartsWith="";


                InitializeCharacterListMaven(limit, offset, nameStartsWith);
            }
            else if(CHARACTERGRID.equals(action))
                InitializeCharacterGridMaven();
            else if(CHARACTERDETAIL.equals(action)) {
                String id = intent.getExtras().getString("id");
                InitializeCharacterDetailMaven(id);
            }
        }
    }

    private void InitializeCharacterListMaven(String limit, String offset, String nameStartsWith)
    {
        TimeProvider timeProvider = new TimeProvider();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(publicKey, privateKey, timeProvider));

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gateway.marvel.com/v1/public/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        CharacterApi c = retrofit.create(CharacterApi.class);
        Call<MarvelResponse<Characters>> call; // = c.getCharacters(limit, offset, nameStartsWith);
        if(nameStartsWith.isEmpty())
            call = c.getCharacters(limit, offset);
        else
            call = c.getCharacters(limit, offset, nameStartsWith);


        try {
            heroList = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            publishCharacterListResults(heroList.body().getResponse().getCharacters());
        }
        catch (Exception ex)
        {
            int a = 0;
            a = 1;
        }



    }

    private void publishCharacterListResults(List<Character> list_val) {
        Intent intent = new Intent(CHARACTERLIST);
        intent.putExtra(CHARACTERLIST, (Serializable) list_val);
        //intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    private void InitializeCharacterGridMaven()
    {
        TimeProvider timeProvider = new TimeProvider();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(publicKey, privateKey, timeProvider));

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gateway.marvel.com/v1/public/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        CharacterApi c = retrofit.create(CharacterApi.class);
        Call<MarvelResponse<Characters>> call = c.getCharacters();

        try {
            heroList = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            publishCharacterGridResults(heroList.body().getResponse().getCharacters());
        }
        catch (Exception ex)
        {
            int a = 0;
            a = 1;
        }

    }

    private void publishCharacterGridResults(List<Character> list_val) {
        Intent intent = new Intent(CHARACTERGRID);
        intent.putExtra(CHARACTERGRID, (Serializable) list_val);
        //intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    private void InitializeCharacterDetailMaven(String id)
    {
        TimeProvider timeProvider = new TimeProvider();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(publicKey, privateKey, timeProvider));

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gateway.marvel.com/v1/public/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        ComicApi c = retrofit.create(ComicApi.class);
        //Call<List<Character>> call = c.getCharacters();
        Call<MarvelResponse<Comics>> call = c.getCharacterComics(id);//"1009146");

        try {
            comicList = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            publishCharacterDetailResults(comicList.body().getResponse().getComics());
        }
        catch (Exception ex)
        {
            int a = 0;
            a = 1;
        }

    }

    private void publishCharacterDetailResults(List<Comic> list_val) {
        Intent intent = new Intent(CHARACTERDETAIL);
        intent.putExtra(CHARACTERDETAIL, (Serializable) list_val);
        //intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    /*private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }*/

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    /*private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }*/


}
