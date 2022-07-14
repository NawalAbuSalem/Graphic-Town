
package com.nns.graphictown.Model.category;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("data")
    @Expose
    private List<CategoryData> data = null;

    public List<CategoryData> getData() {
        return data;
    }

    public void setData(List<CategoryData> data) {
        this.data = data;
    }

}
