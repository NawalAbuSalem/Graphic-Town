
package com.nns.graphictown.Model.Cart;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nns.graphictown.Model.Product.Product;

public class Item {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cart_id")
    @Expose
    private Integer cartId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("images_urls")
    @Expose
    private List<String> imagesUrls = null;
    @SerializedName("product")
    @Expose
    private CartProduct product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public CartProduct getProduct() {
        return product;
    }

    public void setProduct(CartProduct product) {
        this.product = product;
    }

}
