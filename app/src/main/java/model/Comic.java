package model;

/**
 * Created by Lillo on 21/10/2018.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Comic implements Serializable {
    @SerializedName("id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("prices") private List<MarvelPrice> prices;
    @SerializedName("thumbnail") private MarvelImage thumbnail;

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public List<MarvelPrice> getPrices() {
        return prices;
    }
    public MarvelImage getThumbnail() {
        return thumbnail;
    }

    @Override public String toString() {
        return "Comic{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", prices=" + prices +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
