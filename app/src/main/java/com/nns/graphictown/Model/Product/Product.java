
package com.nns.graphictown.Model.Product;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("data")
    @Expose
    private List<ProductData> data = null;

    public List<ProductData> getData() {
        return data;
    }

    public void setData(List<ProductData> data) {
        this.data = data;
    }

}
