package th.ac.dusit.dbizcom.bistro.model;

import com.google.gson.annotations.SerializedName;

public class Beverage {

    @SerializedName("id")
    public final int id;
    @SerializedName("name")
    public final String name;
    @SerializedName("category_id")
    public final int categoryId;
    @SerializedName("price_hot")
    public final int priceHot;
    @SerializedName("price_iced")
    public final int priceIced;
    @SerializedName("price_frappe")
    public final int priceFrappe;
    @SerializedName("price_smoothie")
    public final int priceSmoothie;
    @SerializedName("created_at")
    public final String createdAt;

    public Beverage(int id, String name, int categoryId, int priceHot, int priceIced, int priceFrappe, int priceSmoothie, String createdAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.priceHot = priceHot;
        this.priceIced = priceIced;
        this.priceFrappe = priceFrappe;
        this.priceSmoothie = priceSmoothie;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
