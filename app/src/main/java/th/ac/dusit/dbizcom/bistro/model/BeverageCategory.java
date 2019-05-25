package th.ac.dusit.dbizcom.bistro.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeverageCategory {

    @SerializedName("id")
    public final int id;
    @SerializedName("name")
    public final String name;
    @SerializedName("created_at")
    public final String createdAt;
    @SerializedName("data_list")
    public final List<Beverage> beverageList;

    public BeverageCategory(int id, String name, String createdAt, List<Beverage> beverageList) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.beverageList = beverageList;
    }

    @Override
    public String toString() {
        return name;
    }
}
