
package com.nns.graphictown.Model.Cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartItem {

    @SerializedName("data")
    @Expose
    private List<CartData> data;

    public List<CartData> getData() {
        return data;
    }

    public void setData(List<CartData> data) {
        this.data = data;
    }
}
