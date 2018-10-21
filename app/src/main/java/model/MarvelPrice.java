package model;

/**
 * Created by Lillo on 21/10/2018.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MarvelPrice implements Serializable {
    @SerializedName("type") private String type;
    @SerializedName("price") private float price;

    public String getType() {
        return type;
    }
    public float getPrice() {
        return price;
    }
}
