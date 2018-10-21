package model;

/**
 * Created by Lillo on 21/10/2018.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Character implements Serializable {
    @SerializedName("id") private String id;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("thumbnail") private MarvelImage thumbnail;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MarvelImage getThumbnail() {
        return thumbnail;
    }

    @Override public String toString() {
        return "Character{"
                + "id='"
                + id
                + '\''
                + ", name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", thumbnail="
                + thumbnail
                +
                '}';

    }
}
