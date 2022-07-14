
package com.nns.graphictown.Model.advertisement;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Advertisement {

    @SerializedName("data")
    @Expose
    private List<AdvertisementData> data = null;

    public List<AdvertisementData> getData() {
        return data;
    }

    public void setData(List<AdvertisementData> data) {
        this.data = data;
    }

}
