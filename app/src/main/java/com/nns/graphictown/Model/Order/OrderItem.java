
package com.nns.graphictown.Model.Order;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderItem implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("cart_item_id")
    @Expose
    private Integer cartItemId;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("images_urls")
    @Expose
    private List<String> imagesUrls = null;
    @SerializedName("product")
    @Expose
    private OrderProduct product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public OrderProduct getProduct() {
        return product;
    }

    public void setProduct(OrderProduct product) {
        this.product = product;
    }

}
