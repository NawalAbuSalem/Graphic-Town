
package com.nns.graphictown.Model.advertisement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdvertisementData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
